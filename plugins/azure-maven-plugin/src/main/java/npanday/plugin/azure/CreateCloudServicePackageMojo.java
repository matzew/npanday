package npanday.plugin.azure;

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

import com.google.common.collect.Lists;
import npanday.ArtifactType;
import npanday.PathUtil;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.util.List;
import java.util.Set;

/**
 * Packages roles specified in the dependencies into a common cloud service package (*.cspkg), accompanied
 * with a configuration template (*.cscfg).
 *
 * @author <a href="mailto:lcorneliussen@apache.org">Lars Corneliussen</a>
 * @goal create-cloud-service-package
 * @requiresDependencyResolution runtime
 */
public class CreateCloudServicePackageMojo
    extends AbstractCSPackDeployMojo
{
    private File packageFile, templateConfigurationFile;

    /**
     * The service definition file that get passed along to cspack.
     *
     * @parameter expression="${azure.serviceDefinition}" default-value="${basedir}/ServiceDefinition.csdef"
     */
    private File serviceDefinitionFile;

    /**
     * If a vanilla cloud service configuration file should be generated and attached along with the package.
     *
     * @parameter expression="${azure.generateConfigurationFile}" default-value="true"
     */
    private boolean generateConfigurationFile;

    @Override
    protected void afterCommandExecution() throws MojoExecutionException
    {
        attachPackageFile();

        if ( generateConfigurationFile )
        {
            attachConfigurationFile();
        }
    }

    private void attachPackageFile() throws MojoExecutionException
    {
        if ( !packageFile.exists() )
        {
            throw new MojoExecutionException(
                "NPANDAY-123-001: CSPack seemed to fail on creating the package " + packageFile.getAbsolutePath()
            );
        }

        project.getArtifact().setFile( packageFile );
    }

    private void attachConfigurationFile() throws MojoExecutionException
    {
        if ( !templateConfigurationFile.exists() )
        {
            throw new MojoExecutionException(
                "NPANDAY-123-002: CSPack seemed to fail on creating the template configuration file "
                    + packageFile.getAbsolutePath()
            );

        }

        projectHelper.attachArtifact(
            project, ArtifactType.AZURE_CLOUD_SERVICE_CONFIGURATION.getPackagingType(), "generated",
            templateConfigurationFile
        );
    }

    @Override
    protected void beforeCommandExecution()
    {
        packageFile = new File(
            project.getBuild().getDirectory(),
            project.getArtifactId() + "." + ArtifactType.AZURE_CLOUD_SERVICE.getExtension()
        );

        templateConfigurationFile = new File(
            project.getBuild().getDirectory(), project.getArtifactId() + "-configtemplate" + "."
            + ArtifactType.AZURE_CLOUD_SERVICE_CONFIGURATION.getExtension()
        );
    }

    @Override
    protected List<String> getCommands() throws MojoExecutionException, MojoFailureException
    {
        List<String> commands = Lists.newArrayList();

        commands.add( serviceDefinitionFile.getAbsolutePath() );

        if ( generateConfigurationFile )
        {
            commands.add( "/generateConfigurationFile:" + templateConfigurationFile.getAbsolutePath() );
        }

        commands.add( "/out:" + packageFile.getAbsolutePath() );

        final Set projectDependencyArtifacts = project.getDependencyArtifacts();
        for ( Object artifactAsObject : projectDependencyArtifacts )
        {
            Artifact artifact = (Artifact) artifactAsObject;
            final boolean isWebRole = artifact.getType().equals(
                ArtifactType.MSDEPLOY_PACKAGE.getPackagingType()
            ) || artifact.getType().equals(
                ArtifactType.MSDEPLOY_PACKAGE.getExtension()
            );

            final boolean isWorkerRole = artifact.getType().equals(
                ArtifactType.DOTNET_APPLICATION.getPackagingType()
            ) || artifact.getType().equals(
                ArtifactType.DOTNET_APPLICATION.getExtension()
            );

            if ( !isWebRole && !isWorkerRole )
            {
                throw new MojoExecutionException(
                    "NPANDAY-123-005: Artifact type " + artifact.getType() + " of artifact " + artifact.getArtifactId()
                        + " is not supported for azure cloud services.\n\nPlease use "
                        + ArtifactType.DOTNET_APPLICATION.getPackagingType() + " for worker roles, and "
                        + ArtifactType.MSDEPLOY_PACKAGE.getPackagingType() + " for web roles"
                );
            }

            final File roleRoot = new File(
                PathUtil.getPreparedPackageFolder( project ), artifact.getArtifactId()
            );

            if ( isWebRole )
            {
                getLog().debug( "NPANDAY-123-003: Found web role " + artifact.getArtifactId() );
            }
            else if ( isWorkerRole )
            {
                getLog().debug( "NPANDAY-123-004: Found worker role " + artifact.getArtifactId() );
            }

            if ( !roleRoot.exists() )
            {
                throw new MojoExecutionException(
                    "NPANDAY-123-006: Could not find worker/web role root for " + artifact.getArtifactId() + ": "
                        + roleRoot
                );
            }

            if ( isWebRole )
            {
                commands.add(
                    "/role:" + artifact.getArtifactId() + ";" + roleRoot.getAbsolutePath()
                );
                // TODO: 'Web/' is hardcoded here; where to get it from?
                commands.add(
                    "/sitePhysicalDirectories:" + artifact.getArtifactId() + ";Web;" + roleRoot.getAbsolutePath()
                );
            }
            else if ( isWorkerRole )
            {
                File entryPoint = new File( roleRoot, artifact.getArtifactId() + ".dll" );

                if ( !entryPoint.exists() )
                {
                    throw new MojoExecutionException(
                        "NPANDAY-123-007: Could not find entry point dll for " + artifact.getArtifactId() + ": "
                            + entryPoint
                    );
                }

                commands.add(
                    "/role:" + artifact.getArtifactId() + ";" + roleRoot.getAbsolutePath() + ";"
                        + entryPoint.getAbsolutePath()
                );
            }

            // TODO: enable configuration of different framework pr. role; default to frameworkVersion
            // TODO: save roleprops file somewhere else?
            File rolePropertiesFile = new File(project.getBuild().getDirectory(), artifact.getArtifactId() + ".roleproperties");
            try
            {
                FileUtils.fileWrite( rolePropertiesFile.getAbsolutePath(), "TargetFrameWorkVersion=v4.0" );
                commands.add(
                    "/rolePropertiesFile:" + artifact.getArtifactId() + ";" + rolePropertiesFile.getAbsolutePath()
                );
            } catch (java.io.IOException e) {
                throw new MojoFailureException( "NPANDAY-123-008: Error while creating role properties file for " + artifact.getArtifactId(), e );
            }
        }

        return commands;
    }

}
