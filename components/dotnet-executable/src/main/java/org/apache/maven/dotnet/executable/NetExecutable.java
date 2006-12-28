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

import org.apache.maven.dotnet.NMavenContext;

import java.util.List;
import java.io.File;

/**
 * Provides services for executing programs.
 *
 * @author Shane Isbell
 * @see org.apache.maven.dotnet.executable.compiler.CompilerExecutable
 */
public interface NetExecutable
{

    /**
     * Returns the commands that this compiler will use to compile the application. This is not a live list and any changes
     * in it, will not be used by the compiler.
     *
     * @return the commands that this compiler will use to compile the application
     * @throws ExecutionException
     */
    List<String> getCommands()
        throws ExecutionException;

    /**
     * Compiles class files.
     *
     * @throws org.apache.maven.dotnet.executable.ExecutionException
     *          if the compiler writes to the standard error stream.
     *          artifact (module, library, exe, winexe) or the target artifact is not valid for the compiler
     */
    void execute()
        throws ExecutionException;

    /**
     * Returns the executable that this compiler will use to compile the application.
     *
     * @return the executable that this compiler will use to compile the application
     * @throws org.apache.maven.dotnet.executable.ExecutionException
     *
     */
    String getExecutable()
        throws ExecutionException;

    /**
     * @return excution path
     */
    File getExecutionPath();

    /**
     * Initialize this compiler.
     *
     * @param nmavenContext
     */
    void init( NMavenContext nmavenContext );

}