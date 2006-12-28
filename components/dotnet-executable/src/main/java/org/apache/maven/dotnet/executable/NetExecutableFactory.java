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
package org.apache.maven.dotnet.executable;

import org.apache.maven.project.MavenProject;

import java.util.List;
import java.io.File;

import org.apache.maven.dotnet.vendor.VendorInfo;
import org.apache.maven.dotnet.executable.compiler.CompilerRequirement;
import org.apache.maven.dotnet.executable.compiler.CompilerConfig;
import org.apache.maven.dotnet.executable.compiler.CompilerExecutable;
import org.apache.maven.dotnet.PlatformUnsupportedException;

/**
 * Provides services to obtain executables. This interface is intended to be used by <code>AbstractMojo</code>
 * implementations to obtain the appropriate executable or compiler.
 *
 * @author Shane Isbell
 */
public interface NetExecutableFactory
{

    /**
     * Role used to register component implementations with the container.
     */
    String ROLE = NetExecutableFactory.class.getName();

    /**
     * Returns an executable that matches the vendor, framework version and profile. These are typically framework-vendor
     * executables.
     *
     * @param vendor           the vendor of the executable
     * @param frameworkVersion the version of the .NET framework that the executable works under
     * @param profile          the profile of the executable plugin. This is used to differentiate an executable that
     *                         matches the same platform and framework version.
     * @param project          the maven project
     * @param commands         the user-defined command options to use with the executable
     * @param netHome          the install root of the .NET framework
     * @return the executable that matches the vendor, framework version and profile
     * @throws org.apache.maven.dotnet.PlatformUnsupportedException if no executable is found
     */
    NetExecutable getNetExecutableFor( String vendor, String frameworkVersion, String profile, MavenProject project,
                                       List<String> commands, File netHome )
        throws PlatformUnsupportedException;

    /**
     * Returns an executable that resides within a maven repository. These are typically user-implemented executables.
     *
     * @param groupId         the group ID of the executable artifact (as specified within the maven repo)
     * @param artifactId      the artifact ID of the executable artifact (as specified within the maven repo)
     * @param vendorInfo      the additional vendor information used to decide how to execute the net executable
     * @param localRepository the local maven repository where the executable resides.
     * @return the executable that resides within a maven repository.
     * @throws PlatformUnsupportedException if no executable is found
     */
    NetExecutable getNetExecutableFromRepository( String groupId, String artifactId, VendorInfo vendorInfo,
                                                  MavenProject project, String localRepository, List<String> commands )
        throws PlatformUnsupportedException;

    /**
     * Returns an executable for compiling .NET applications.
     *
     * @param compilerRequirement   the requirements for the compiler
     * @param compilerConfig        the configuration for the compiler
     * @param project               the maven project
     * @param assemblyPath          an optional parameter for replacing the system assemblies
     * @return the executable for compiling .NET applications
     * @throws org.apache.maven.dotnet.PlatformUnsupportedException if no executable is found
     *
     */
    CompilerExecutable getCompilerExecutableFor( CompilerRequirement compilerRequirement, CompilerConfig compilerConfig,
                                                 MavenProject project, File assemblyPath )
        throws PlatformUnsupportedException;

}