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

package org.onehippo.intellij.groovy.utils;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.ui.awt.RelativePoint;

/**
 * @version "$Id$"
 */
public final class Util {

    public static void showMessage(final Project project, String html) {
        StatusBar statusBar = WindowManager.getInstance().getStatusBar(project);
        if (statusBar == null) {
            return;
        }
        JBPopupFactory.getInstance()
                .createHtmlTextBalloonBuilder(html, MessageType.INFO, null)
                .setFadeoutTime(4000)
                .createBalloon()
                .show(RelativePoint.getNorthWestOf(statusBar.getComponent()), Balloon.Position.atRight);
    }

    public static void showError(final Project project, String html) {
        StatusBar statusBar = WindowManager.getInstance().getStatusBar(project);
        if (statusBar == null) {
            return;
        }
        JBPopupFactory.getInstance()
                .createHtmlTextBalloonBuilder(html, MessageType.ERROR, null)
                .setFadeoutTime(4000)
                .createBalloon()
                .show(RelativePoint.getCenterOf(statusBar.getComponent()), Balloon.Position.atRight);
    }


    public static boolean readBooleanProperty(final Node node, final String name) throws RepositoryException {
        if (node.hasProperty(name)) {
            final Property property = node.getProperty(name);
            return property.getBoolean();
        }
        return false;
    }

    @SuppressWarnings("NumericCastThatLosesPrecision")
    public static int readIntProperty(final Node node, final String name) throws RepositoryException {
        if (node.hasProperty(name)) {
            final Property property = node.getProperty(name);
            final long aLong = property.getLong();
            return (int) aLong;
        }
        return 0;
    }

    public static String readStringProperty(final Node node, final String name) throws RepositoryException {

        if (node.hasProperty(name)) {
            final Property property = node.getProperty(name);
            return property.getString();
        }
        return null;
    }
    private Util() {
    }

}
