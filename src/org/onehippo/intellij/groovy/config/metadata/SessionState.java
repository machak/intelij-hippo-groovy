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

import java.util.HashMap;
import java.util.Map;

import org.onehippo.intellij.groovy.config.metadata.gui.FileDialogData;

/**
 * @version "$Id$"
 */
public class SessionState {
    private Map<String, FileDialogData> items = new HashMap<>();

    public Map<String, FileDialogData> getItems() {
        return items;
    }

    public void setItems(final Map<String, FileDialogData> items) {
        this.items = items;
    }

    public void put(final String key, final FileDialogData value) {
        items.put(key, value);
    }
}
