#region Apache License, Version 2.0
//
// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.
//
#endregion
using System;
using System.Collections.Generic;
using System.IO;
using NPanday.Model.Pom;
using NPanday.ProjectImporter.Digest.Model;
using NPanday.Utils;

namespace NPanday.ProjectImporter.Converter.Algorithms
{
    public class AzurePomConverter : AbstractPomConverter
    {

        public AzurePomConverter(ProjectDigest projectDigest, string mainPomFile, NPanday.Model.Pom.Model parent, string groupId) 
            : base(projectDigest,mainPomFile,parent, groupId)
        {
        }

        public override void ConvertProjectToPomModel(bool writePom, string scmTag)
        {
            GenerateHeader("azure-cloud-service");

            //Add SCM Tag
            if (scmTag != null && scmTag != string.Empty && Model.parent==null)
            {
                Scm scmHolder = new Scm();
                scmHolder.connection = string.Format("scm:svn:{0}", scmTag);
                scmHolder.developerConnection = string.Format("scm:svn:{0}", scmTag);
                scmHolder.url = scmTag;

                Model.scm = scmHolder;
            }

            // Add Com Reference Dependencies
            if (projectDigest.ComReferenceList.Length > 0)
            {
                AddComReferenceDependency();
            }
            
			//Add Project WebReferences
            AddWebReferences();

            //Add EmbeddedResources maven-resgen-plugin
            AddEmbeddedResources();
            
            // Add Project Inter-dependencies
            foreach (ProjectReference projectRef in projectDigest.ProjectReferences)
            {
                AddProjectReference(projectRef);
            }

            // Add Project Reference Dependencies
            // override the one from the parent to add new types for Azure
            AddProjectReferenceDependenciesToList();

            AddPlugin("org.apache.npanday.plugins", "azure-maven-plugin");

            if (writePom)
            {
                PomHelperUtility.WriteModelToPom(new FileInfo(Path.Combine(projectDigest.FullDirectoryName, "pom.xml")), Model);
            }
        }

        private void AddProjectReference(ProjectReference projectRef)
        {
            Dependency dependency = new Dependency();

            dependency.artifactId = projectRef.Name;
            dependency.groupId = !string.IsNullOrEmpty(groupId) ? groupId : projectRef.Name;
            dependency.version = string.IsNullOrEmpty(version) ? "1.0-SNAPSHOT" : version;

            dependency.type = "dotnet-library";
            if (projectRef.ProjectReferenceDigest != null
                && !string.IsNullOrEmpty(projectRef.ProjectReferenceDigest.OutputType))
            {
                dependency.type = projectRef.ProjectReferenceDigest.OutputType.ToLower();
            }
            if (projectRef.RoleType != null)
            {
                switch (projectRef.RoleType)
                {
                    case "Web":
                        dependency.type = "msdeploy-package";
                        break;
                    case "Worker":
                        dependency.type = "dotnet-application";
                        break;
                    default:
                        Console.WriteLine("Unknown role type '" + projectRef.RoleType + "' - treating as a dotnet-library");
                        break;
                }
            }

            AddDependency(dependency);
        }
    }
}
