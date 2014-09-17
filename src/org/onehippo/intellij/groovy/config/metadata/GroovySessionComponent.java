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

package org.onehippo.intellij.groovy.config.metadata;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.onehippo.intellij.groovy.config.metadata.gui.FileDialogData;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.components.StoragePathMacros;
import com.intellij.openapi.components.StorageScheme;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

@State(
        name = "GroovySessionComponent",
        storages = {
                @Storage(id = "default", file = StoragePathMacros.PROJECT_FILE),
                @Storage(id = "dir", file = StoragePathMacros.PROJECT_CONFIG_DIR + "/hippo_groovy_session_storage.xml", scheme = StorageScheme.DIRECTORY_BASED)
        }
)
public class GroovySessionComponent implements ProjectComponent, PersistentStateComponent<SessionState> {
    private SessionState sessionState;
    private Project project;

    public GroovySessionComponent(Project project) {
        this.project = project;
        sessionState = new SessionState();
    }

    @Override
    public void initComponent() {

    }

    @Override
    public void disposeComponent() {

    }

    @Override
    @NotNull
    public String getComponentName() {
        return "GroovySessionComponent";
    }

    @Override
    public void projectOpened() {

    }

    @Override
    public void projectClosed() {

    }

    @Nullable
    @Override
    public SessionState getState() {
        return sessionState;
    }

    @Override
    public void loadState(final SessionState sessionState) {
        this.sessionState = sessionState;

    }

    //############################################
    // CUSTOM LOADING /SAVING
    //############################################

    public void saveState(final FileDialogData data, final VirtualFile file) {
        sessionState.put(file.getCanonicalPath(), data);

    }


}
