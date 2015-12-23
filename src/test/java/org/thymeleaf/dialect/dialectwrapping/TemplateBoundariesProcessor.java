/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2014, The THYMELEAF team (http://www.thymeleaf.org)
 * 
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 * 
 * =============================================================================
 */
package org.thymeleaf.dialect.dialectwrapping;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.ITemplateEnd;
import org.thymeleaf.model.ITemplateStart;
import org.thymeleaf.processor.templateboundaries.AbstractTemplateBoundariesProcessor;
import org.thymeleaf.processor.templateboundaries.ITemplateBoundariesStructureHandler;
import org.thymeleaf.templatemode.TemplateMode;

public class TemplateBoundariesProcessor extends AbstractTemplateBoundariesProcessor {

    public TemplateBoundariesProcessor() {
        super(TemplateMode.HTML, 100);
    }

    @Override
    public void doProcessTemplateStart(final ITemplateContext context, final ITemplateStart templateStart, final ITemplateBoundariesStructureHandler structureHandler) {

    }

    @Override
    public void doProcessTemplateEnd(final ITemplateContext context, final ITemplateEnd templateEnd, final ITemplateBoundariesStructureHandler structureHandler) {

    }

}