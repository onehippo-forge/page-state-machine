/*
 *  Copyright 2018 Hippo B.V. (http://www.onehippo.com)
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.onehippo.forge.pageflow.hst.sitemapitemhandler;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.hippoecm.hst.core.request.ResolvedSiteMapItem;
import org.hippoecm.hst.core.sitemapitemhandler.AbstractFilterChainAwareHstSiteMapItemHandler;
import org.hippoecm.hst.core.sitemapitemhandler.HstSiteMapItemHandlerException;
import org.hippoecm.hst.util.PathUtils;
import org.onehippo.forge.pageflow.core.PageFlowNotFoundException;
import org.onehippo.forge.pageflow.core.rt.PageFlow;
import org.onehippo.forge.pageflow.core.rt.PageFlowControl;
import org.onehippo.forge.pageflow.core.rt.PageState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PageFlowControlHstSiteMapItemHandler extends AbstractFilterChainAwareHstSiteMapItemHandler {

    private static Logger log = LoggerFactory.getLogger(PageFlowControlHstSiteMapItemHandler.class);

    private boolean pageFlowControlSetInServletContext;

    private volatile PageFlowControl pageFlowControl;

    @Override
    public ResolvedSiteMapItem process(ResolvedSiteMapItem resolvedSiteMapItem, HttpServletRequest request,
            HttpServletResponse response, FilterChain filterChain) throws HstSiteMapItemHandlerException {
        final PageFlowControl flowControl = getPageFlowControl(request);

        if (!pageFlowControlSetInServletContext) {
            request.getServletContext().setAttribute(PageFlowControl.PAGE_FLOW_CONTROL_ATTR_NAME, flowControl);
            pageFlowControlSetInServletContext = true;
        }

        request.setAttribute(PageFlowControl.PAGE_FLOW_CONTROL_ATTR_NAME, flowControl);

        PageFlow pageFlow = null;

        try {
            pageFlow = flowControl.getPageFlow(request);
        } catch (PageFlowNotFoundException e) {
            log.warn("Page Flow not found for the current request.");
        }

        if (pageFlow != null) {
            if (!pageFlow.isStarted()) {
                pageFlow.start();
            }

            final PageState pageState = pageFlow.getPageState();

            if (!StringUtils.equals(PathUtils.normalizePath(StringUtils.defaultString(pageState.getPath())),
                    PathUtils.normalizePath(StringUtils.defaultString(request.getPathInfo())))) {
                try {
                    flowControl.sendRedirect(request, response, pageState);
                    return null;
                } catch (Exception e) {
                    log.warn("Failed to redirect to the pageState: {}", pageState, e);
                }
            }
        }

        if (resolvedSiteMapItem == null) {
            try {
                filterChain.doFilter(request, response);
            } catch (Exception e) {
                log.warn("Failed to filterChain.doFilter(...).", e);
            }
        }

        return resolvedSiteMapItem;
    }

    protected PageFlowControl getPageFlowControl(HttpServletRequest request) {
        PageFlowControl flowControl = pageFlowControl;

        if (flowControl == null) {
            synchronized (this) {
                flowControl = pageFlowControl;

                if (flowControl == null) {
                    flowControl = createPageFlowControl(request);
                    pageFlowControl = flowControl;
                }
            }
        }

        return flowControl;
    }

    protected PageFlowControl createPageFlowControl(HttpServletRequest request) {
        return new DefaultHstPageFlowControl();
    }
}
