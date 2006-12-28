/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.maven.dotnet.plugin.compile;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.DirectoryScanner;

import java.io.File;
import java.io.IOException;

/**
 * Copies source files to target directory.
 *
 * @author Shane Isbell
 * @goal process-sources
 * @phase process-sources
 */

public class SourceProcessorMojo extends AbstractMojo {

    /**
     * Source directory
     *
     * @parameter expression = "${sourceDirectory}" default-value="${project.build.sourceDirectory}"
     * @required
     */
    private String sourceDirectory;

    /**
     * Output directory
     *
     * @parameter expression = "${outputDirectory}" default-value="${project.build.directory}${file.separator}build-sources"
     * @required
     */
    private String outputDirectory;

    /**
     * @parameter expression = "${includes}"
     */
    private String[] includes;

    /**
     * @parameter expression = "${excludes}"
     */
    private String[] excludes;

    public void execute() throws MojoExecutionException {
        if (!new File(sourceDirectory).exists()) {
            getLog().info("NMAVEN-904-001: No source files to copy");
            return;
        }
        DirectoryScanner directoryScanner = new DirectoryScanner();
        directoryScanner.setBasedir(sourceDirectory);

        if(includes != null && includes.length > 0 ) directoryScanner.setIncludes(includes);
        if(excludes != null && excludes.length > 0) directoryScanner.setExcludes(excludes);
        directoryScanner.addDefaultExcludes();
        directoryScanner.scan();
        String[] files = directoryScanner.getIncludedFiles();
        getLog().info("NMAVEN-904-002: Copying source files: From = " + sourceDirectory + ",  To = " + outputDirectory);
        for (String file : files) {
            try {
                FileUtils.copyFile(new File(sourceDirectory + File.separator + file),
                        new File(outputDirectory + File.separator + file));
            } catch (IOException e) {
                throw new MojoExecutionException("NMAVEN-904-000: Unable to process sources", e);
            }
        }
    }
}