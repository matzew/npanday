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

package org.apache.maven.dotnet.artifact;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.project.MavenProject;

import java.io.File;

/**
 * Provides information about the location of a .NET executable artifact's exe.config file.
 *
 * @author Shane Isbell
 */
public interface ApplicationConfig
{

    /**
     * Returns the source path of the (original) *.exe.config file
     *
     * @return the source path of the (original) *.exe.config file
     */
    String getConfigSourcePath();

    /**
     * Returns the target path of the (copied) *.exe.config file
     *
     * @return the target path of the (copied) *.exe.config file
     */
    String getConfigDestinationPath();

    /**
     * Factory class for generating default executable configs.
     */
    public static class Factory
    {
        /**
         * Default constructor
         */
        public Factory()
        {
        }

        /**
         * Creates the the application config for the specified artifact. By default, the config source path for the
         * exe.config is located within the project's src/main/config directory. Neither parameter value may be null.
         *
         * @param artifact the executable artifact to which the exe.config file is associated
         * @param project  the maven project
         * @return the application config for the specified artifact
         */
        public static ApplicationConfig createDefaultApplicationConfig( final Artifact artifact,
                                                                        final MavenProject project )
        {
            return new ApplicationConfig()
            {

                public String getConfigSourcePath()
                {
                    return new File(
                        project.getBasedir() + "/src/main/config/" + artifact.getArtifactId() + ".exe.config" )
                        .getAbsolutePath();
                }

                public String getConfigDestinationPath()
                {
                    return project.getBuild().getDirectory() + File.separator + artifact.getArtifactId() +
                        ".exe.config";
                }
            };
        }

    }
}