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

package org.onehippo.intellij.groovy.config.metadata.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.onehippo.intellij.groovy.config.metadata.ExecuteType;
import org.onehippo.intellij.groovy.config.metadata.Location;

import com.google.common.base.Strings;

/**
 * @version "$Id$"
 */
public class FileDialog extends JComponent {


    private static final long serialVersionUID = 1L;
    private JPanel mainPanel;
    private JTextField scriptName;
    private JComboBox<String> queryOrPath;
    private JSpinner batchSize;
    private JSpinner throttle;
    private JTextField queryString;
    private JComboBox<String> location;
    private JCheckBox dryRunCheckBox;
    private JTextField path;

    private FileDialogData existingData;
    private final ComboBoxModel<String> queryOrPathModel = new DefaultComboBoxModel<>(new String[]{ExecuteType.QUERY.getType(), ExecuteType.PATH.getType()});
    private final ComboBoxModel<String> locationModel = new DefaultComboBoxModel<>(new String[]{Location.REGISTRY.getName(), Location.QUEUE.getName(), Location.HISTORY.getName()});

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public void initialize(final ScheduleDialog parent, final FileDialogData existingData) {
        //############################################
        // SPINNER LIMIT
        //############################################
        this.existingData =  existingData;
        batchSize.setModel(new SpinnerNumberModel(10, 1, Integer.MAX_VALUE, 10));
        throttle.setModel(new SpinnerNumberModel(100, 0, Integer.MAX_VALUE, 10));
        queryOrPath.setModel(queryOrPathModel);
        queryOrPath.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                parent.inputChanged();
            }
        });
        location.setModel(locationModel);

        final DocumentListener listener = new DocumentListener() {
            @Override
            public void insertUpdate(final DocumentEvent e) {
                parent.inputChanged();
            }

            @Override
            public void removeUpdate(final DocumentEvent e) {
                parent.inputChanged();
            }

            @Override
            public void changedUpdate(final DocumentEvent e) {
                parent.inputChanged();
            }
        };
        scriptName.getDocument().addDocumentListener(listener);
        queryString.getDocument().addDocumentListener(listener);
        setData(existingData);

    }

    public boolean valid() {
        if(Strings.isNullOrEmpty(scriptName.getText())){
            return false;
        }
        if(getDropdownValue().equals(ExecuteType.PATH.getType())) {
            return !Strings.isNullOrEmpty(path.getText());
        }
        return !Strings.isNullOrEmpty(queryString.getText());
    }

    public void setData(FileDialogData data) {

        path.setText(data.getPathScript());
        dryRunCheckBox.setSelected(data.isDryRun());
        scriptName.setText(data.getScriptName());
        queryString.setText(data.getQueryScript());
        throttle.setValue(data.getThrottle());
        batchSize.setValue(data.getBatchSize());
        queryOrPath.setModel(queryOrPathModel);
        queryOrPathModel.setSelectedItem(data.getQueryOrPath());
        locationModel.setSelectedItem(data.getLocation());
    }

    public void getData(FileDialogData data) {
        data.setPathScript(path.getText());
        data.setDryRun(dryRunCheckBox.isSelected());
        data.setScriptName(scriptName.getText());
        data.setQueryScript(queryString.getText());
        data.setThrottle(Integer.parseInt(String.valueOf(throttle.getModel().getValue())));
        data.setBatchSize(Integer.parseInt(String.valueOf(batchSize.getModel().getValue())));
        data.setQueryOrPath(getDropdownValue());
        data.setLocation(String.valueOf(location.getSelectedItem()));
        data.setContent(existingData.getContent());
    }

    private String getDropdownValue() {
        return String.valueOf(queryOrPath.getSelectedItem());
    }

    public boolean isModified(FileDialogData data) {
        return true;
    }
}
