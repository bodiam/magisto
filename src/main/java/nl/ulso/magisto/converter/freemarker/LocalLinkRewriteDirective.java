/*
 * Copyright 2015 Vincent Oostindie
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package nl.ulso.magisto.converter.freemarker;

import freemarker.core.Environment;
import freemarker.ext.beans.StringModel;
import freemarker.template.*;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.util.Map;

/**
 * FreeMarker directive that rewrites local links of the form {@code /path/to/file} to {@code ../../path/to/file} in
 * the template; inserting as many parent directory navigation steps as needed. This allows templates to include static
 * files that are always resolved correctly, no matter at what level the template is processed.
 */
public class LocalLinkRewriteDirective implements TemplateDirectiveModel {

    private static final String MODEL_PARAMETER_NAME = "path";
    private static final String DIRECTIVE_PARAMETER_NAME = "path";

    private static final String SUPER = "..";
    private static final String SLASH = "/";

    @Override
    public void execute(Environment environment, Map parameters, TemplateModel[] loopVariables,
                        TemplateDirectiveBody body) throws TemplateException, IOException {
        if (parameters.size() != 1) {
            throw new TemplateException("Directive requires a link as a single parameter", environment);
        }
        final Object parameter = parameters.get(DIRECTIVE_PARAMETER_NAME);
        if (parameter == null) {
            throw new TemplateException("Missing parameter " + DIRECTIVE_PARAMETER_NAME, environment);
        }
        if (!(parameter instanceof SimpleScalar)) {
            throw new TemplateException("Expected a SimpleScalar. Got a(n) " + parameter.getClass().getSimpleName(),
                    environment);
        }
        final String link = ((SimpleScalar) parameter).getAsString();
        final Path filePath = (Path) ((StringModel) environment.getDataModel().get(MODEL_PARAMETER_NAME)).getWrappedObject();
        final Writer writer = environment.getOut();
        writer.write(rewriteLink(filePath, link));
    }

    String rewriteLink(Path filePath, String link) {
        final StringBuilder rewrite = new StringBuilder();
        final int pathParts = filePath.getNameCount() - 1;
        if (pathParts == 0) {
            rewrite.append(".");
        } else {
            for (int i = 0; i < pathParts; i++) {
                if (i > 0) {
                    rewrite.append(SLASH);
                }
                rewrite.append(SUPER);
            }
        }
        if (!link.startsWith(SLASH)) {
            rewrite.append(SLASH);
        }
        rewrite.append(link);
        return rewrite.toString();
    }
}
