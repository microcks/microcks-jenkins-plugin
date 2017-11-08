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

import io.github.microcks.jenkins.plugin.model.GlobalConfig;
import io.github.microcks.jenkins.plugin.model.IMicrocksTester;
import hudson.Extension;
import hudson.tasks.Builder;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * @author laurent
 */
public class MicrocksTester extends TimedMicrocksBaseStep implements IMicrocksTester {

   protected final String serviceId;
   protected final String testEndpoint;
   protected final String runnerType;

   // Fields in config.jelly must match the parameter names in the "DataBoundConstructor"
   @DataBoundConstructor
   public MicrocksTester(String apiURL, String serviceId, String testEndpoint, String runnerType, String verbose, String waitTime, String waitUnit) {
      super(apiURL, verbose, waitTime, waitUnit);
      this.serviceId = serviceId != null ? serviceId.trim() : null;
      this.testEndpoint = testEndpoint != null ? testEndpoint.trim() : null;
      this.runnerType = runnerType != null ? runnerType.trim() : null;
   }

   @Override
   public String getServiceId() {
      return serviceId;
   }

   @Override
   public String getTestEndpoint() {
      return testEndpoint;
   }

   @Override
   public String getRunnerType() {
      return runnerType;
   }

   /**
    * Descriptor for {@link MicrocksTester}. Used as a singleton.
    * The class is marked as public so that it can be accessed from views.
    */
   @Extension // This indicates to Jenkins that this is an implementation of an extension point.
   public static final class DescriptorImpl extends TimedBuildStepDescriptor<Builder> {

      public String getDisplayName() {
         return DISPLAY_NAME;
      }

      @Override
      protected long getStaticDefaultWaitTime() {
         return GlobalConfig.DEFAULT_TEST_WAIT;
      }
   }
}
