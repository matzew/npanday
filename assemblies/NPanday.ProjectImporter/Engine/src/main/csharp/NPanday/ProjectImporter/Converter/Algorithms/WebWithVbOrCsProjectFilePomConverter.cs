using System;
using System.Collections.Generic;
using System.Text;
using System.Xml;
using System.IO;

using NPanday.ProjectImporter.Digest;
using NPanday.ProjectImporter.Digest.Model;
using NPanday.Utils;
using NPanday.Model.Pom;

using NPanday.Artifact;

using System.Reflection;

using NPanday.ProjectImporter.Converter;

using NPanday.ProjectImporter.Validator;


/// Author: Leopoldo Lee Agdeppa III

namespace NPanday.ProjectImporter.Converter.Algorithms
{
    public class WebWithVbOrCsProjectFilePomConverter : NormalPomConverter
    {
        public WebWithVbOrCsProjectFilePomConverter(ProjectDigest projectDigest, string mainPomFile, NPanday.Model.Pom.Model parent, string groupId) 
            : base(projectDigest,mainPomFile,parent, groupId)
        {
        }

        public override void ConvertProjectToPomModel(bool writePom)
        {
            // just call the base, but dont write it we still need some minor adjustments for it
            base.ConvertProjectToPomModel(false);
            Model.packaging = "asp";

            Model.build.sourceDirectory = ".";

            // change the outputDirectory of the plugin
            Plugin compilePlugin = FindPlugin("org.apache.maven.dotnet.plugins", "maven-compile-plugin");
            AddPluginConfiguration(compilePlugin, "outputDirectory", "bin");

            if (writePom)
            {
                PomHelperUtility.WriteModelToPom(new FileInfo(Path.GetDirectoryName(projectDigest.FullFileName) + @"\pom.xml"), Model);
            }


        }


    }
}