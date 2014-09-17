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

package org.onehippo.intellij.groovy.config;

import javax.swing.JComponent;

import org.jdom.Element;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.components.StoragePathMacros;
import com.intellij.openapi.components.StorageScheme;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;


@State(name = "HippoGroovyEditor", storages = {@Storage(id = "dir", file = StoragePathMacros.APP_CONFIG + "/hippo_groovy_editor.xml", scheme = StorageScheme.DIRECTORY_BASED)})
public class ApplicationComponent implements com.intellij.openapi.components.ApplicationComponent, Configurable, PersistentStateComponent<Element> {



    public static final String GROOVY_DIR_ATTRIBUTE = "groovyDirectory";
    public static final String USERENAME_ATTRIBUTE = "username";
    public static final String PASSWORD_ATTRIBUTE = "password";
    public static final String RMI_ATTRIBUTE = "rmiAddress";

    public static final String CONFIGURATION_CONFIG_ELEMENT = "hippo-groovy-config";
    private String username;
    private String password;
    private String rmiAddress;
    private String groovyDirectory;
    private PluginConfiguration configPane;


    @Override
    public void initComponent() {
    }

    @Override
    public void disposeComponent() {
    }

    @NotNull
    @Override
    public String getComponentName() {
        return "HippoGroovyEditorComponent";
    }

    @Override
    public Element getState() {
        final Element element = new Element(CONFIGURATION_CONFIG_ELEMENT);
        checkNullSave(element, GROOVY_DIR_ATTRIBUTE, groovyDirectory);
        checkNullSave(element, USERENAME_ATTRIBUTE, username);
        checkNullSave(element, PASSWORD_ATTRIBUTE, password);
        checkNullSave(element, RMI_ATTRIBUTE, rmiAddress);
        checkNullSave(element, GROOVY_DIR_ATTRIBUTE, groovyDirectory);
        return element;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public String getRmiAddress() {
        return rmiAddress;
    }

    public void setRmiAddress(final String rmiAddress) {
        this.rmiAddress = rmiAddress;
    }

    @Override

    public void loadState(final Element element) {
        groovyDirectory = element.getAttributeValue(GROOVY_DIR_ATTRIBUTE);
        username = element.getAttributeValue(USERENAME_ATTRIBUTE);
        password = element.getAttributeValue(PASSWORD_ATTRIBUTE);
        rmiAddress = element.getAttributeValue(RMI_ATTRIBUTE);
    }

    @Nls
    @Override
    public String getDisplayName() {
        return "Hippo groovy editor";
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return null;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        if (configPane == null) {
            configPane = new PluginConfiguration();
        }
        return configPane.createComponent();
    }

    @Override
    public boolean isModified() {
        return configPane != null && configPane.isModified(this);
    }

    @Override
    public void apply() throws ConfigurationException {
        if (configPane != null) {
            configPane.storeDataTo(this);
        }
    }

    @Override
    public void reset() {
        if (configPane != null) {
            configPane.readDataFrom(this);
        }
    }

    @Override
    public void disposeUIResources() {
        configPane = null;
    }


    public String getGroovyDirectory() {
        return groovyDirectory;
    }

    public void setGroovyDirectory(final String groovyDirectory) {
        this.groovyDirectory = groovyDirectory;
    }

    private void checkNullSave(final Element element, final String attr, final String value) {
        if (value == null) {
            return;
        }
        element.setAttribute(attr, value);
    }
}