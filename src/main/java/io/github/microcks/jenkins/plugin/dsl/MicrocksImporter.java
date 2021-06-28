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
import io.github.microcks.jenkins.plugin.model.IMicrocksImporter;
import io.github.microcks.jenkins.plugin.model.IMicrocksPluginDescriptor;
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
import java.util.Map;

/**
 * @author laurent
 */
public class MicrocksImporter extends MicrocksBaseStep implements IMicrocksImporter {

   protected String specificationFiles;

   // Fields in config.jelly must match the parameter names in the "DataBoundConstructor"
   @DataBoundConstructor
   public MicrocksImporter() {
   }

   @Override
   public String getSpecificationFiles() {
      return specificationFiles;
   }

   @DataBoundSetter
   public void setSpecificationFiles(String specificationFiles) {
      this.specificationFiles = specificationFiles != null ? specificationFiles.trim() : null;
   }

   @Extension
   public static class DescriptorImpl extends AbstractStepDescriptorImpl implements IMicrocksPluginDescriptor {

      public DescriptorImpl() {
         super(MicrocksImporterExecution.class);
      }

      @Override
      public String getFunctionName() {
         return "microcksImport";
      }

      @Override
      public Step newInstance(Map<String, Object> arguments) throws Exception {
         MicrocksImporter step = new MicrocksImporter();
         // Assign arguments if provided.
         if (arguments.containsKey("specificationFiles")) {
            Object specificationFiles = arguments.get("specificationFiles");
            if (specificationFiles != null) {
               step.setSpecificationFiles(specificationFiles.toString());
            }
         }
         // Fill common parameters before returning step.
         CommonParamsHelper.updateDSLBaseStep(arguments, step);
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
