/*
 * Copyright 2014 Hippo B.V. (http://www.onehippo.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.onehippo.intellij.groovy;


import java.io.File;
import java.text.MessageFormat;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.hippoecm.repository.HippoRepository;
import org.hippoecm.repository.HippoRepositoryFactory;
import org.onehippo.intellij.groovy.config.ApplicationComponent;
import org.onehippo.intellij.groovy.config.ProjectComponent;
import org.onehippo.intellij.groovy.config.metadata.Location;
import org.onehippo.intellij.groovy.config.metadata.gui.FileDialogData;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.intellij.openapi.project.Project;


/**
 * @version "$Id$"
 */
public class RepositoryConnector {


    public final static String GROOVY_FOLDER = "cms" + File.separator + "src" + File.separator + "main" + File.separator + "java" + File.separator + "groovy";
    private Project project;

    public RepositoryConnector(final Project project) {
        this.project = project;

    }


    public Session getSession() throws RepositoryException {
        try {
            String userName = "admin";
            String password = "admin";
            String address = "rmi://localhost:1099/hipporepository";
            final ApplicationComponent component = project.getComponent(ProjectComponent.class);
            if (component != null) {
                if (!Strings.isNullOrEmpty(component.getUsername())) {
                    userName = component.getUsername();
                }
                if (!Strings.isNullOrEmpty(component.getRmiAddress())) {
                    address = component.getRmiAddress();
                }
                if (!Strings.isNullOrEmpty(component.getPassword())) {
                    password = component.getPassword();
                }
            }
            final HippoRepository repository = HippoRepositoryFactory.getHippoRepository(address);
            return repository.login(userName, password.toCharArray());
        } catch (Exception e) {
            GroovyEditor.error("Error connecting to hippo repository" + e.getMessage(), project);
            throw new RepositoryException("Error connecting to hippo repository templates");
        }


    }


    public TemplateData createFilePath(final String path, final File folder, final String content) {
        Location location = Location.REGISTRY;
        if (path.contains(Location.HISTORY.getPath())) {
            location = Location.HISTORY;
        } else if (path.contains(Location.QUEUE.getPath())) {
            location = Location.QUEUE;
        } else if (path.contains(Location.REGISTRY.getPath())) {
            location = Location.REGISTRY;
        }

        String repoPath = path.replaceAll("/hippo:configuration/hippo:update/", "");
        repoPath = repoPath.replaceAll("hippo:registry", Location.REGISTRY.getName());
        repoPath = repoPath.replaceAll("hippo:history", Location.HISTORY.getName());
        repoPath = repoPath.replaceAll("hippo:queue", Location.QUEUE.getName());


        // get AST and store name as class name, instead of node name:
        /*final GroovyFileBase fragment = new GroovyCodeFragment(project, content);
        final GrTopLevelDefinition[] topLevelDefinitions = fragment.getTopLevelDefinitions();
        if (topLevelDefinitions != null && topLevelDefinitions.length > 0) {
            final String name = topLevelDefinitions[0].getName();
            repoPath = repoPath + '/' + name;
        }else{
            final Iterable<String> pathParts = Splitter.on('/').split(repoPath);
            final List<String> folderParts = Lists.newArrayList(pathParts);
            repoPath = repoPath + '/' + folderParts.remove(folderParts.size() - 1);
        }*/

        if (!repoPath.endsWith(".groovy")) {
            repoPath = repoPath + ".groovy";
        }

        final Iterable<String> pathParts = Splitter.on('/').split(repoPath);
        final List<String> folderParts = Lists.newArrayList(pathParts);
        // remove last item
        String parent = folder.getAbsolutePath();
        folderParts.remove(folderParts.size() - 1);
        File myFolder = folder;
        for (String folderPart : folderParts) {
            final String myFolderPath = MessageFormat.format("{0}{1}{2}", folder.getAbsolutePath(), File.separator, folderPart);

            myFolder = new File(myFolderPath);
            if (!myFolder.exists()) {
                myFolder.mkdir();
            }
            parent = MessageFormat.format("{0}{1}{2}", parent, File.separator, myFolderPath);
        }

        return new TemplateData(location, myFolder, folder.getAbsolutePath() + File.separator + Joiner.on(File.separator).join(pathParts));
    }

    public boolean saveGroovyFile(final String path, final Location location, final FileDialogData data) {
        Session session = null;
        try {

            session = getSession();
            if (session.nodeExists(path)) {
                session.removeItem(path);
            }
            final Node root = session.getNode(location.getPath());
            final Node scriptNode = root.addNode(data.getScriptName(), "hipposys:updaterinfo");
            scriptNode.setProperty("hipposys:batchsize", data.getBatchSize());
            scriptNode.setProperty("hipposys:throttle", data.getThrottle());
            scriptNode.setProperty("hipposys:dryrun", data.isDryRun());
            final String queryScript = data.getQueryScript();
            if (!Strings.isNullOrEmpty(queryScript)) {
                scriptNode.setProperty("hipposys:query", queryScript);
            }
            String content = data.getContent();
            if(Strings.isNullOrEmpty(content)){
               content = "//no script defined";
            }
            scriptNode.setProperty("hipposys:script", content);
            final String pathScript = data.getPathScript();
            if (!Strings.isNullOrEmpty(pathScript)) {
                scriptNode.setProperty("hipposys:path", pathScript);
            }
            session.save();
            return true;
        } catch (RepositoryException e) {
            GroovyEditor.error("Error saving template to hippo repository" + e.getMessage(), project);

        } finally {
            if (session != null) {
                session.logout();
            }
        }
        return false;
    }


    public static class TemplateData {
        final File file;
        final String fileName;
        final Location location;


        private TemplateData(final Location location, final File file, final String fileName) {
            this.file = file;
            this.fileName = fileName;
            this.location = location;
        }

        public File getFile() {
            return file;
        }

        public Location getLocation() {
            return location;
        }

        public String getFileName() {
            return fileName;
        }


    }


}
