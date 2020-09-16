/*
 * Licensed to Laurent Broudoux (the "Author") under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. Author licenses this
 * file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package io.github.microcks.jenkins.plugin.dsl;

import io.github.microcks.jenkins.plugin.CommonParamsHelper;
import io.github.microcks.jenkins.plugin.model.IMicrocksPluginDescriptor;
import io.github.microcks.jenkins.plugin.model.IMicrocksTester;
import hudson.Extension;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepMonitor;
import org.jenkinsci.plugins.workflow.steps.AbstractStepDescriptorImpl;
import org.jenkinsci.plugins.workflow.steps.Step;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author laurent
 */
public class MicrocksTester extends TimedMicrocksBaseStep implements IMicrocksTester {

   protected String serviceId;
   protected String testEndpoint;
   protected String runnerType;
   protected String secretName;
   protected Map<String, List<Map<String, String>>> operationsHeaders;

   // Fields in config.jelly must match the parameter names in the "DataBoundConstructor"
   @DataBoundConstructor
   public MicrocksTester() {

   }

   @Override
   public String getServiceId() {
      return serviceId;
   }

   @DataBoundSetter
   public void setServiceId(String serviceId) {
      this.serviceId = serviceId != null ? serviceId.trim() : null;
   }

   @Override
   public String getTestEndpoint() {
      return testEndpoint;
   }

   @DataBoundSetter
   public void setTestEndpoint(String testEndpoint) {
      this.testEndpoint = testEndpoint != null ? testEndpoint.trim() : null;
   }

   @Override
   public String getRunnerType() {
      return runnerType;
   }

   @DataBoundSetter
   public void setRunnerType(String runnerType) {
      this.runnerType = runnerType != null ? runnerType.trim() : null;
   }

   @Override
   public String getSecretName() {
      return secretName;
   }

   @DataBoundSetter
   public void setSecretName(String secretName) {
      this.secretName = secretName != null ? secretName.trim() : null;
   }

   @Override
   public Map<String, List<Map<String, String>>> getOperationsHeaders() {
      return operationsHeaders;
   }

   @DataBoundSetter
   public void setOperationsHeaders(Map<String, List<Map<String, String>>> operationsHeaders) {
      this.operationsHeaders = operationsHeaders;
   }

   @Extension
   public static class DescriptorImpl extends AbstractStepDescriptorImpl implements IMicrocksPluginDescriptor {

      public DescriptorImpl() {
         super(MicrocksTesterExecution.class);
      }

      @Override
      public String getFunctionName() {
         return "microcksTest";
      }

      @Override
      public String getDisplayName() {
         return DISPLAY_NAME;
      }

      @Override
      public Step newInstance(Map<String, Object> arguments) throws Exception {
         MicrocksTester step = new MicrocksTester();
         // Assign arguments if provided.
         if (arguments.containsKey("serviceId")) {
            Object serviceId = arguments.get("serviceId");
            if (serviceId != null) {
               step.setServiceId(serviceId.toString());
            }
         }
         if (arguments.containsKey("testEndpoint")) {
            Object testEndpoint = arguments.get("testEndpoint");
            if (testEndpoint != null) {
               step.setTestEndpoint(testEndpoint.toString());
            }
         }
         if (arguments.containsKey("runnerType")) {
            Object runnerType = arguments.get("runnerType");
            if (runnerType != null) {
               step.setRunnerType(runnerType.toString());
            }
         }
         if (arguments.containsKey("secretName")) {
            Object secretName = arguments.get("secretName");
            if (secretName != null) {
               step.setSecretName(secretName.toString());
            }
         }
         if (arguments.containsKey("operationsHeaders")) {
            Object operationsHeaders = arguments.get("operationsHeaders");
            if (operationsHeaders != null && (operationsHeaders instanceof Map)) {
               step.setOperationsHeaders((Map<String, List<Map<String, String>>>)operationsHeaders);
            }
         }
         // Fill common parameters before returning step.
         CommonParamsHelper.updateDSLTimedStep(arguments, step);
         return step;
      }
   }

   @Override
   public boolean prebuild(AbstractBuild<?, ?> build, BuildListener listener) {
      return true;
   }

   @Override
   public Action getProjectAction(AbstractProject<?, ?> project) {
      return null;
   }

   @Override
   public Collection<? extends Action> getProjectActions(
         AbstractProject<?, ?> project) {
      return null;
   }

   @Override
   public BuildStepMonitor getRequiredMonitorService() {
      return null;
   }
}
