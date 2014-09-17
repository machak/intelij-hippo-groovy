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


import java.awt.event.ActionEvent;
import java.io.File;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.onehippo.intellij.groovy.config.ApplicationComponent;
import org.onehippo.intellij.groovy.config.ProjectComponent;

import com.google.common.base.Strings;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.vfs.VirtualFile;

/**
 * @version "$Id$"
 */
public class GroovyEditor extends AnAction {

    public static final NotificationGroup ERROR_GROUP = new NotificationGroup("Hippo Groovy editor error messages", NotificationDisplayType.BALLOON, true);
    public static final NotificationGroup INFO_GROUP = new NotificationGroup("Hippo Groovy editor info messages", NotificationDisplayType.NONE, false);

    private Project project;

    protected static void error(final String message, final Project project) {
        final Notification notification = ERROR_GROUP.createNotification(message, NotificationType.ERROR);
        notification.notify(project);
    }

    private void info(final String message) {
        final Notification notification = INFO_GROUP.createNotification(message, NotificationType.INFORMATION);
        notification.notify(project);
    }

    private void warn(final String message) {
        final Notification notification = INFO_GROUP.createNotification(message, NotificationType.WARNING);
        notification.notify(project);
    }

    @Override
    public void actionPerformed(final AnActionEvent event) {
        project = CommonDataKeys.PROJECT.getData(event.getDataContext());
        if (project != null) {


            Editor editor = CommonDataKeys.EDITOR.getData(event.getDataContext());
            if (editor == null || editor.getDocument() == null) {
                return;
            }
            final VirtualFile canonicalFile = project.getBaseDir().getCanonicalFile();
            if (canonicalFile == null) {
                return;
            }
            final VirtualFile currentFile = CommonDataKeys.VIRTUAL_FILE.getData(event.getDataContext());
            final CharSequence charsSequence = editor.getDocument().getCharsSequence();
            final RepositoryConnector connector = new RepositoryConnector(project);
            final ApplicationComponent component = project.getComponent(ProjectComponent.class);
            String groovyFolder = canonicalFile.getPath() + File.separator + RepositoryConnector.GROOVY_FOLDER;
            if (component != null) {
                final String groovyDirectory = component.getGroovyDirectory();
                if (!Strings.isNullOrEmpty(groovyDirectory)) {
                    groovyFolder = groovyDirectory;
                }
            }
            saveFile(connector, groovyFolder, currentFile, charsSequence.toString());

        }

    }

    public void saveFile(final RepositoryConnector connector, final String groovyFolder, final VirtualFile currentFile, final String content) {
        String path = currentFile.getPath();
        // check if file is within  groovy root folder
        if (!path.startsWith(groovyFolder)) {
            error("File: " + path + " is not within Groovy root folder", project);
            return;
        }

        path = path.substring(groovyFolder.length(), path.length());
        Session session = null;
        try {
            session = connector.getSession();
            final int fileNameIndex = path.lastIndexOf('/');
            String separator = path.startsWith("/") ? "" : "/";
            final String fileName = path.substring(fileNameIndex, path.length());
            final String templateRoot = "/hst:hst/hst:configurations" + separator + path.substring(0, fileNameIndex) + "/hst:templates";
            String absPath = templateRoot + fileName;
            absPath = absPath.replaceAll("hst_default", "hst:default");
            if (session.nodeExists(absPath)) {
                final Node node = session.getNode(absPath);
                node.setProperty("hst:script", content);
                session.save();
            } else {

                showCreatePopup(fileName, templateRoot, content, session);
            }


        } catch (RepositoryException e) {

        } finally {
            if (session != null) {
               session.logout();
            }
        }

    }


    private void showCreatePopup(final String fileName, final String templateRoot, final String content, final Session session) {


        final DialogBuilder dialogBuilder = new DialogBuilder(project);
        dialogBuilder.setTitle("Create new  template node:");
        final JPanel simplePanel = new JPanel();
        simplePanel.add(new JLabel("Node doesn't exist, should we create one? (" + templateRoot + fileName + ')'));
        dialogBuilder.setCenterPanel(simplePanel);

        final Action acceptAction = new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(final ActionEvent e) {

                try {
                    final String root = templateRoot.endsWith("/") ? templateRoot.substring(0, templateRoot.length() - 1) : templateRoot;
                    // check if site node exists:
                    if (!session.nodeExists(root)) {
                        final String configRoot = "/hst:hst/hst:configurations/";
                        final String newSiteName = (root.substring(configRoot.length())).replaceAll("/hst:templates/|/hst:templates", "");
                        final Node configRootNode = session.getNode(configRoot);
                        final Node siteNode = configRootNode.addNode(newSiteName, "hst:configuration");
                        siteNode.addMixin("mix:referenceable");
                        siteNode.addMixin("mix:simpleVersionable");
                        siteNode.addMixin("mix:versionable");
                        final Node templateNode = siteNode.addNode("hst:templates", "hst:templates");
                        templateNode.addMixin("mix:referenceable");
                    }

                    final Node node = session.getNode(root);
                    final String nodeName = fileName.startsWith(File.separator) ? fileName.substring(1, fileName.length()) : fileName;
                    final Node templateNode = node.addNode(nodeName, "hst:template");
                    templateNode.setProperty("hst:script", content);
                    session.save();
                } catch (RepositoryException e1) {
                    GroovyEditor.error("Error writing template, " + e1.getMessage(), project);
                    try {
                        session.refresh(false);
                    } catch (RepositoryException e2) {
                        //
                    }
                }

                dialogBuilder.getDialogWrapper().close(DialogWrapper.OK_EXIT_CODE);


            }
        };
        acceptAction.putValue(Action.NAME, "OK");
        dialogBuilder.addAction(acceptAction);

        dialogBuilder.addCancelAction();
        dialogBuilder.showModal(true);
    }


}
