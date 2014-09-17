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

package org.onehippo.intellij.groovy.actions;

import java.util.Map;

import org.onehippo.intellij.groovy.RepositoryConnector;
import org.onehippo.intellij.groovy.config.metadata.GroovySessionComponent;
import org.onehippo.intellij.groovy.config.metadata.Location;
import org.onehippo.intellij.groovy.config.metadata.SessionState;
import org.onehippo.intellij.groovy.config.metadata.gui.FileDialogData;
import org.onehippo.intellij.groovy.config.metadata.gui.ScheduleDialog;
import org.onehippo.intellij.groovy.utils.Util;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.vfs.VirtualFile;

public class ScheduleFileAction extends AnAction {

    private void processFile(final FileDialogData data, final GroovySessionComponent sessionComponent, final Project project) {
        // construct path:
        final String l = data.getLocation();
        final Location location = Location.locationForName(l);
        final String rootPath = location.getPath();
        final String path = rootPath + '/' + data.getScriptName();
        final RepositoryConnector connector = new RepositoryConnector(project);
        final boolean saved = connector.saveGroovyFile(path, location, data);
        if (saved) {
            Util.showMessage(project, "Saved node:  " + path);
        } else {
            Util.showError(project, "Failed to save node:" + path);
        }

    }


    @Override
    public void actionPerformed(AnActionEvent event) {
        final Project project = event.getData(CommonDataKeys.PROJECT);
        if (project == null) {
            return;
        }
        final GroovySessionComponent sessionComponent = project.getComponent(GroovySessionComponent.class);

        final FileEditorManager editorManager = FileEditorManager.getInstance(project);
        final Editor selectedTextEditor = editorManager.getSelectedTextEditor();

        if (selectedTextEditor == null) {
            Util.showError(project, "No document selected");
            return;
        }
        final Document document = selectedTextEditor.getDocument();
        final VirtualFile currentFile = FileDocumentManager.getInstance().getFile(document);
        if (currentFile == null) {
            Util.showError(project, "No document selected");
            return;
        }
        FileDialogData existingData = new FileDialogData();
        final SessionState state = sessionComponent.getState();
        if (state != null) {
            final Map<String, FileDialogData> items = state.getItems();
            final FileDialogData dialogData = items.get(currentFile.getCanonicalPath());
            if (dialogData != null) {
                existingData = dialogData;

            }
        }


        final String content = selectedTextEditor.getDocument().getText();
        final ScheduleDialog dialog = new ScheduleDialog(project, existingData);
        existingData.setContent(content);
        dialog.setTitle("Groovy Script Settings");
        dialog.show();
        switch (dialog.getExitCode()) {
            case DialogWrapper.OK_EXIT_CODE:
                processFile(dialog.getData(), sessionComponent, project);

                break;
            case DialogWrapper.CANCEL_EXIT_CODE:
                Util.showMessage(project, "File not saved");
                break;
        }

    }


}
