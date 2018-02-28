/*
 * Copyright 2018 Hippo B.V. (http://www.onehippo.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *         http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.onehippo.forge.pageflow.core.def.impl;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.onehippo.forge.pageflow.core.def.PageStateDefinition;
import org.onehippo.forge.pageflow.core.def.PageTransitionDefinition;

public class DefaultPageStateDefinition implements PageStateDefinition {

    private static final long serialVersionUID = 1L;

    private final String id;

    private final String path;

    private List<PageTransitionDefinition> pageTransitionDefs;

    public DefaultPageStateDefinition(final String id, final String path) {
        this.id = id;
        this.path = path;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public List<PageTransitionDefinition> getPageTransitionDefinitions() {
        if (pageTransitionDefs == null) {
            return Collections.emptyList();
        }

        return Collections.unmodifiableList(pageTransitionDefs);
    }

    public void addPageStateTransitionDefinition(PageTransitionDefinition pageTransitionDef) {
        if (pageTransitionDefs == null) {
            pageTransitionDefs = new LinkedList<>();
        }

        pageTransitionDefs.add(pageTransitionDef);
    }

    public boolean removePageStateTransitionDefinition(PageTransitionDefinition pageTransitionDef) {
        if (pageTransitionDefs == null) {
            return false;
        }

        return pageTransitionDefs.remove(pageTransitionDef);
    }

    public void removeAllPageStateTransitionDefinitions() {
        if (pageTransitionDefs != null) {
            pageTransitionDefs.clear();
        }
    }
}
