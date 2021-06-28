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
package io.github.microcks.jenkins.plugin;

import io.github.microcks.jenkins.plugin.model.IMicrocksImporter;
import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import io.github.microcks.jenkins.plugin.model.IMicrocksPluginDescriptor;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * @author laurent
 */
public class MicrocksImporter extends MicrocksBaseStep implements IMicrocksImporter {

   protected final String specificationFiles;

   // Fields in config.jelly must match the parameter names in the "DataBoundConstructor"
   @DataBoundConstructor
   public MicrocksImporter(String server, String specificationFiles, String verbose) {
      super(server, verbose);
      this.specificationFiles = specificationFiles != null ? specificationFiles.trim() : null;
   }

   @Override
   public String getSpecificationFiles() {
      return specificationFiles;
   }

   /**
    * Descriptor for {@link MicrocksImporter}. Used as a singleton.
    * The class is marked as public so that it can be accessed from views.
    */
   @Extension // This indicates to Jenkins that this is an implementation of an extension point.
   public static final class DescriptorImpl extends BuildStepDescriptor<Builder> implements IMicrocksPluginDescriptor {

      public String getDisplayName() {
         return DISPLAY_NAME;
      }

      @Override
      public boolean isApplicable(Class<? extends AbstractProject> aClass) {
         // Indicates that this builder can be used with all kinds of project types
         return true;
      }
   }
}
