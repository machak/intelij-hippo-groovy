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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.options.BaseConfigurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.TextAccessor;

public class PluginConfiguration extends BaseConfigurable {
    private JLabel label;
    private JPanel mainPanel;
    private JPasswordField password;

    private TextFieldWithBrowseButton groovyDirectory;
    private JLabel groovyLabel;
    private JTextField rmiAddress;
    private JTextField username;


    public PluginConfiguration() {

        final DocumentListener listener = new DocumentAdapter() {
            @Override
            protected void textChanged(DocumentEvent documentEvent) {
                groovyDirectory.getText();
            }
        };
        groovyDirectory.getChildComponent().getDocument().addDocumentListener(listener);
        groovyDirectory.setTextFieldPreferredWidth(50);
        groovyDirectory.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chooseFolder(groovyDirectory, false);
            }
        });


    }

    @Nls
    @Override
    public String getDisplayName() {
        return "Hippo groovy editor";
    }

    @Override
    public String getHelpTopic() {
        return null;
    }

    @Override
    public JComponent createComponent() {
        return mainPanel;
    }

    @Override
    public void apply() throws ConfigurationException {

    }

    @Override
    public void reset() {
    }

    @Override
    public void disposeUIResources() {
    }

    public boolean isModified(ApplicationComponent component) {

        final String groovyText = groovyDirectory.getText();
        final String groovyDir = component.getGroovyDirectory();
        boolean textChanged = isTextChanged(groovyText, groovyDir);
        if (textChanged) {
            return true;
        }
        final String usernameText = username.getText();
        final String usernameComp = component.getUsername();
        textChanged = isTextChanged(usernameText, usernameComp);
        if (textChanged) {
            return true;
        }
        final String passwordText = String.valueOf(password.getPassword());
        final String passwordComp = component.getPassword();
        textChanged = isTextChanged(passwordText, passwordComp);
        if (textChanged) {
            return true;
        }
        final String rmiText = rmiAddress.getText();
        final String rmiComp = component.getRmiAddress();
        textChanged = isTextChanged(rmiText, rmiComp);
        return textChanged;


    }

    public void storeDataTo(ApplicationComponent component) {

        component.setGroovyDirectory(groovyDirectory.getText());
        component.setUsername(username.getText());
        component.setPassword(password.getText());
        component.setRmiAddress(rmiAddress.getText());

    }

    public void readDataFrom(ApplicationComponent component) {

        groovyDirectory.setText(component.getGroovyDirectory());
        password.setText(component.getPassword());
        username.setText(component.getUsername());
        rmiAddress.setText(component.getRmiAddress());
    }

    public Project getProject(final Component component) {
        Project project = CommonDataKeys.PROJECT.getData(DataManager.getInstance().getDataContext(component));
        if (project != null) {
            return project;
        }
        return ProjectManager.getInstance().getDefaultProject();
    }

    private boolean isTextChanged(final String text, final String dir) {
        if (text == null) {
            if (dir == null) {
                return false;
            }
        } else if (text.equals(dir)) {
            return false;
        }
        return true;
    }

    private void createUIComponents() {

    }

    //############################################
    //
    //############################################

    private void chooseFolder(final TextAccessor field, final boolean chooseFiles) {
        final FileChooserDescriptor descriptor = new FileChooserDescriptor(chooseFiles, !chooseFiles, false, false, false, false) {
            @Override
            public String getName(VirtualFile virtualFile) {
                return virtualFile.getName();
            }

            @Override
            @Nullable
            public String getComment(VirtualFile virtualFile) {
                return virtualFile.getPresentableUrl();
            }
        };
        descriptor.setTitle("Select Groovy Root Folder");

        final String selectedPath = field.getText();
        final VirtualFile preselectedFolder = LocalFileSystem.getInstance().findFileByPath(selectedPath);

        final VirtualFile[] files = FileChooser.chooseFiles(descriptor, mainPanel, getProject(mainPanel), preselectedFolder);
        if (files.length > 0) {
            field.setText(files[0].getPath());
        }
    }
}