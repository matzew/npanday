//------------------------------------------------------------------------------
// <auto-generated>
//     This code was generated by a tool.
//     Runtime Version:2.0.50727.42
//
//     Changes to this file may cause incorrect behavior and will be lost if
//     the code is regenerated.
// </auto-generated>
//------------------------------------------------------------------------------

//
// This source code was auto-generated by wsdl, Version=2.0.50727.42.
//
namespace NMaven.Service {
    using System.Diagnostics;
    using System.Web.Services;
    using System.ComponentModel;
    using System.Web.Services.Protocols;
    using System;
    using System.Xml.Serialization;


    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "2.0.50727.42")]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Web.Services.WebServiceBindingAttribute(Name="MavenEmbedderServiceHttpBinding", Namespace="http://incubator.apache.org/nmaven/MavenEmbedderService")]
    public partial class MavenEmbedderService : System.Web.Services.Protocols.SoapHttpClientProtocol {

        private System.Threading.SendOrPostCallback getMavenProjectsForOperationCompleted;

        private System.Threading.SendOrPostCallback executeOperationCompleted;

        /// <remarks/>
        public MavenEmbedderService() {
            this.Url = "http://localhost:9191/dotnet-service-embedder/services/MavenEmbedderService";
        }

        /// <remarks/>
        public event getMavenProjectsForCompletedEventHandler getMavenProjectsForCompleted;

        /// <remarks/>
        public event executeCompletedEventHandler executeCompleted;

        /// <remarks/>
        [System.Web.Services.Protocols.SoapDocumentMethodAttribute("", RequestNamespace="http://incubator.apache.org/nmaven/MavenEmbedderService", ResponseNamespace="http://incubator.apache.org/nmaven/MavenEmbedderService", Use=System.Web.Services.Description.SoapBindingUse.Literal, ParameterStyle=System.Web.Services.Protocols.SoapParameterStyle.Wrapped)]
        [return: System.Xml.Serialization.XmlArrayAttribute("out", IsNullable=true)]
        [return: System.Xml.Serialization.XmlArrayItemAttribute(Namespace="urn:maven-embedder")]
        public MavenProject[] getMavenProjectsFor([System.Xml.Serialization.XmlElementAttribute(IsNullable=true)] string in0) {
            object[] results = this.Invoke("getMavenProjectsFor", new object[] {
                        in0});
            return ((MavenProject[])(results[0]));
        }

        /// <remarks/>
        public System.IAsyncResult BegingetMavenProjectsFor(string in0, System.AsyncCallback callback, object asyncState) {
            return this.BeginInvoke("getMavenProjectsFor", new object[] {
                        in0}, callback, asyncState);
        }

        /// <remarks/>
        public MavenProject[] EndgetMavenProjectsFor(System.IAsyncResult asyncResult) {
            object[] results = this.EndInvoke(asyncResult);
            return ((MavenProject[])(results[0]));
        }

        /// <remarks/>
        public void getMavenProjectsForAsync(string in0) {
            this.getMavenProjectsForAsync(in0, null);
        }

        /// <remarks/>
        public void getMavenProjectsForAsync(string in0, object userState) {
            if ((this.getMavenProjectsForOperationCompleted == null)) {
                this.getMavenProjectsForOperationCompleted = new System.Threading.SendOrPostCallback(this.OngetMavenProjectsForOperationCompleted);
            }
            this.InvokeAsync("getMavenProjectsFor", new object[] {
                        in0}, this.getMavenProjectsForOperationCompleted, userState);
        }

        private void OngetMavenProjectsForOperationCompleted(object arg) {
            if ((this.getMavenProjectsForCompleted != null)) {
                System.Web.Services.Protocols.InvokeCompletedEventArgs invokeArgs = ((System.Web.Services.Protocols.InvokeCompletedEventArgs)(arg));
                this.getMavenProjectsForCompleted(this, new getMavenProjectsForCompletedEventArgs(invokeArgs.Results, invokeArgs.Error, invokeArgs.Cancelled, invokeArgs.UserState));
            }
        }

        /// <remarks/>
        [System.Web.Services.Protocols.SoapDocumentMethodAttribute("", RequestNamespace="http://incubator.apache.org/nmaven/MavenEmbedderService", ResponseNamespace="http://incubator.apache.org/nmaven/MavenEmbedderService", Use=System.Web.Services.Description.SoapBindingUse.Literal, ParameterStyle=System.Web.Services.Protocols.SoapParameterStyle.Wrapped)]
        public void execute([System.Xml.Serialization.XmlElementAttribute(IsNullable=true)] MavenExecutionRequest in0) {
            this.Invoke("execute", new object[] {
                        in0});
        }

        /// <remarks/>
        public System.IAsyncResult Beginexecute(MavenExecutionRequest in0, System.AsyncCallback callback, object asyncState) {
            return this.BeginInvoke("execute", new object[] {
                        in0}, callback, asyncState);
        }

        /// <remarks/>
        public void Endexecute(System.IAsyncResult asyncResult) {
            this.EndInvoke(asyncResult);
        }

        /// <remarks/>
        public void executeAsync(MavenExecutionRequest in0) {
            this.executeAsync(in0, null);
        }

        /// <remarks/>
        public void executeAsync(MavenExecutionRequest in0, object userState) {
            if ((this.executeOperationCompleted == null)) {
                this.executeOperationCompleted = new System.Threading.SendOrPostCallback(this.OnexecuteOperationCompleted);
            }
            this.InvokeAsync("execute", new object[] {
                        in0}, this.executeOperationCompleted, userState);
        }

        private void OnexecuteOperationCompleted(object arg) {
            if ((this.executeCompleted != null)) {
                System.Web.Services.Protocols.InvokeCompletedEventArgs invokeArgs = ((System.Web.Services.Protocols.InvokeCompletedEventArgs)(arg));
                this.executeCompleted(this, new System.ComponentModel.AsyncCompletedEventArgs(invokeArgs.Error, invokeArgs.Cancelled, invokeArgs.UserState));
            }
        }

        /// <remarks/>
        public new void CancelAsync(object userState) {
            base.CancelAsync(userState);
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "2.0.50727.42")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace="urn:maven-embedder")]
    public partial class MavenProject {

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute(IsNullable=true)]
        public string artifactId;

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute(IsNullable=true)]
        public string groupId;

        /// <remarks/>
        [System.Xml.Serialization.XmlArrayAttribute(IsNullable=true)]
        public MavenProject[] mavenProjects;

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute(IsNullable=true)]
        public System.Nullable<bool> isOrphaned;

        /// <remarks/>
        [System.Xml.Serialization.XmlIgnoreAttribute()]
        public bool isOrphanedSpecified;

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute(IsNullable=true)]
        public string pomPath;

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute(IsNullable=true)]
        public string version;

        /// <remarks/>
        [System.Xml.Serialization.XmlAnyElementAttribute()]
        public System.Xml.XmlElement[] Any;

        /// <remarks/>
        [System.Xml.Serialization.XmlAnyAttributeAttribute()]
        public System.Xml.XmlAttribute[] AnyAttr;
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "2.0.50727.42")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace="urn:maven-embedder")]
    public partial class MavenExecutionRequest {

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute(IsNullable=true)]
        public string goal;

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute(IsNullable=true)]
        public System.Nullable<int> loggerPort;

        /// <remarks/>
        [System.Xml.Serialization.XmlIgnoreAttribute()]
        public bool loggerPortSpecified;

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute(IsNullable=true)]
        public string pomFile;

        /// <remarks/>
        [System.Xml.Serialization.XmlAnyElementAttribute()]
        public System.Xml.XmlElement[] Any;

        /// <remarks/>
        [System.Xml.Serialization.XmlAnyAttributeAttribute()]
        public System.Xml.XmlAttribute[] AnyAttr;
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "2.0.50727.42")]
    public delegate void getMavenProjectsForCompletedEventHandler(object sender, getMavenProjectsForCompletedEventArgs e);

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "2.0.50727.42")]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    public partial class getMavenProjectsForCompletedEventArgs : System.ComponentModel.AsyncCompletedEventArgs {

        private object[] results;

        internal getMavenProjectsForCompletedEventArgs(object[] results, System.Exception exception, bool cancelled, object userState) :
                base(exception, cancelled, userState) {
            this.results = results;
        }

        /// <remarks/>
        public MavenProject[] Result {
            get {
                this.RaiseExceptionIfNecessary();
                return ((MavenProject[])(this.results[0]));
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "2.0.50727.42")]
    public delegate void executeCompletedEventHandler(object sender, System.ComponentModel.AsyncCompletedEventArgs e);
}
