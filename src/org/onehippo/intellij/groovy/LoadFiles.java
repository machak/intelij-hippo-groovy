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


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

import org.onehippo.intellij.groovy.config.ApplicationComponent;
import org.onehippo.intellij.groovy.config.ProjectComponent;
import org.onehippo.intellij.groovy.config.metadata.ExecuteType;
import org.onehippo.intellij.groovy.config.metadata.GroovySessionComponent;
import org.onehippo.intellij.groovy.config.metadata.Location;
import org.onehippo.intellij.groovy.config.metadata.gui.FileDialogData;
import org.onehippo.intellij.groovy.utils.Util;

import com.google.common.base.Strings;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;

/**
 * @version "$Id$"
 */
public class LoadFiles extends AnAction {


    private static final java.util.regex.Pattern PATTERN = java.util.regex.Pattern.compile("\\.groovy");

    @Override
    public void actionPerformed(final AnActionEvent event) {
        final Project project = CommonDataKeys.PROJECT.getData(event.getDataContext());
        if (project != null) {
            RepositoryConnector connector = new RepositoryConnector(project);
            final Set<FileDialogData> templates = fetchTemplates(project, connector);
            if (templates.size() == 0) {
                Util.showMessage(project, "No existing groovy scripts found");
                return;
            }
            final VirtualFile baseDir = project.getBaseDir();
            final String projectPath = baseDir.getPath();
            String groovyFolder = projectPath + File.separator + RepositoryConnector.GROOVY_FOLDER;
            final ApplicationComponent component = project.getComponent(ProjectComponent.class);
            if (component != null) {
                final String groovyDirectory = component.getGroovyDirectory();
                if (!Strings.isNullOrEmpty(groovyDirectory)) {
                    groovyFolder = groovyDirectory;
                }

            }
            if (!new File(groovyFolder).exists()) {
                Util.showError(project, "Root folder doesn't exist:" + groovyFolder + ". Change it in Settings > Hippo Groovy editor");
                return;
            }
            final boolean done = saveTemplates(connector, project, templates, groovyFolder);
            if (done) {
                Util.showMessage(project, "Successfully loaded " + templates.size() + " groovy scripts");
            }
        }


    }

    public boolean saveTemplates(final RepositoryConnector connector, final Project project, final Set<FileDialogData> templates, final String groovyFolder) {
        final GroovySessionComponent sessionComponent = project.getComponent(GroovySessionComponent.class);
        try {


            final File file = new File(groovyFolder);
            if (!file.exists()) {
                file.mkdir();
            }

            final FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);

            for (FileDialogData template : templates) {
                final RepositoryConnector.TemplateData path = connector.createFilePath(template.getNodePath(), file, template.getContent());
                final String fileName = path.getFileName();
                final File templateFile = new File(fileName);
                if (!templateFile.exists()) {
                    templateFile.createNewFile();
                }
                final Writer writer = new BufferedWriter(new FileWriter(templateFile));
                try {
                    writer.write(template.getContent());
                    writer.flush();
                    writer.close();
                } finally {
                    writer.close();
                }
                final LocalFileSystem instance = LocalFileSystem.getInstance();
                instance.refresh(true);
                final VirtualFile vf = instance.findFileByPath(templateFile.getAbsolutePath());
                if (vf != null) {
                    fileEditorManager.openFile(vf, true, true);
                    template.setScriptName(PATTERN.matcher(vf.getName()).replaceAll(""));
                    template.setLocation(path.getLocation().getName());
                    sessionComponent.saveState(template, vf);
                }

            }
            return true;
        } catch (IOException e) {
            GroovyEditor.error(e.getMessage(), project);
        }

        return false;

    }

    public Set<FileDialogData> fetchTemplates(final Project project, final RepositoryConnector connector) {
        final Set<FileDialogData> templates = new HashSet<>();
        Session session = null;
        try {
            session = connector.getSession();
            final QueryManager queryManager = session.getWorkspace().getQueryManager();
            final Query query = queryManager.createQuery(Location.Constants.FETCH_QUERY, Query.XPATH);
            final QueryResult result = query.execute();
            final NodeIterator nodes = result.getNodes();

            while (nodes.hasNext()) {
                final FileDialogData data = new FileDialogData();
                final Node node = nodes.nextNode();
                data.setNodePath(node.getPath());
                data.setNodeId(node.getIdentifier());
                data.setQueryScript(Util.readStringProperty(node, "hipposys:query"));
                data.setPathScript(Util.readStringProperty(node, "hipposys:path"));
                data.setDryRun(Util.readBooleanProperty(node, "hipposys:dryrun"));
                final String queryOrPath = Strings.isNullOrEmpty(Util.readStringProperty(node, "hipposys:nodetype")) ? ExecuteType.QUERY.getType() : ExecuteType.PATH.getType();
                data.setQueryOrPath(queryOrPath);
                data.setThrottle(Util.readIntProperty(node, "hipposys:batchsize"));
                data.setBatchSize(Util.readIntProperty(node, "hipposys:throttle"));
                data.setPathScript(Util.readStringProperty(node, "hipposys:path"));
                String content = Util.readStringProperty(node, "hipposys:script");
                if (Strings.isNullOrEmpty(content)) {
                    content = "";
                }
                data.setContent(content);
                templates.add(data);
            }
        } catch (RepositoryException e) {
            GroovyEditor.error("Error loading templates", project);
        } finally {
            if (session != null) {
                session.logout();
            }
        }


        return templates;

    }

}
