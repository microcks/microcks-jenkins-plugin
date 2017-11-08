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

import io.github.microcks.jenkins.plugin.model.IMicrocksPluginDescriptor;
import io.github.microcks.jenkins.plugin.model.ITimedMicrocksPlugin;
import hudson.model.AbstractProject;
import hudson.model.Describable;
import hudson.tasks.BuildStep;
import hudson.tasks.BuildStepDescriptor;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.StaplerRequest;

/**
 * Extending this descriptor imbues subclasses with a global Jenkins
 * timeout setting.
 *
 * Theory of operation:
 * 1. Each timed operation which exposes a global timeout extends this descriptor.
 * 2. The descriptor will persist/restore a global wait/waitUnit for the operation type.
 * 3. Classes wishing to share the same timeout value (e.g. DSL steps) should access the timeout value through GlobalConfig.
 * 4. GlobalConfig will load the correct descriptor to read the currently configured timeout value.
 * 5. The host for the global config option must have a global.jelly for setting the value from Jenkins Configure.
 *      See examples like: src/main/resources/com/github/lbroudoux/microcks/jenkins/plugin/MicrocksTester/global.jelly
 * @author laurent
 */
public abstract class TimedBuildStepDescriptor<T extends BuildStep & Describable<T>> extends BuildStepDescriptor<T> implements IMicrocksPluginDescriptor {

   protected String wait;
   protected String waitUnit;

   TimedBuildStepDescriptor() {
      load();
   }

   @Override
   public synchronized void load() {
      super.load();
      if (wait == null || wait.trim().isEmpty()) {
         wait = "" + getStaticDefaultWaitTime();
      }

      long w = Long.parseLong(wait);

      if (waitUnit == null || waitUnit.trim().isEmpty()) {
         if (w > 1000 && (w % ITimedMicrocksPlugin.TimeoutUnit.SECONDS.multiplier == 0)) {
            // We are loading a new or an existing config without time units
            waitUnit = ITimedMicrocksPlugin.TimeoutUnit.SECONDS.name;
            // Convert existing timeout to seconds
            w /= ITimedMicrocksPlugin.TimeoutUnit.SECONDS.multiplier;
         } else {
            waitUnit = ITimedMicrocksPlugin.TimeoutUnit.MILLISECONDS.name;
         }
      }

      wait = "" + w;
   }

   @Override
   public boolean isApplicable(Class<? extends AbstractProject> aClass) {
      // Indicates that this builder can be used with all kinds of project types
      return true;
   }

   @Override
   public synchronized boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
      wait = formData.getString("wait");
      waitUnit = ITimedMicrocksPlugin.TimeoutUnit.normalize(formData.getString("waitUnit"));
      if (wait == null || wait.isEmpty()) {
         // If someone clears the value, go back to default and use seconds
         wait = "" + getStaticDefaultWaitTime() / ITimedMicrocksPlugin.TimeoutUnit.SECONDS.multiplier;
         waitUnit = ITimedMicrocksPlugin.TimeoutUnit.SECONDS.name;
      }
      wait = wait.trim();
      save();
      return true;
   }

   public synchronized long getConfiguredDefaultWaitTime() {
      ITimedMicrocksPlugin.TimeoutUnit unit = ITimedMicrocksPlugin.TimeoutUnit.getByName(waitUnit);
      return unit.toMilliseconds("" + wait, getStaticDefaultWaitTime());
   }


   public synchronized String getWait() {
      return wait;
   }

   public synchronized String getWaitUnit() {
      return waitUnit;
   }

   /**
    * @return Return the non-configurable default for this build step. This will
    * populate the global default wait time for the operation the first time Jenkins
    * loads this plugin. Once a global configuration with a value exists, this
    * value will no longer be used. However, this value will be re-populated if the
    * user clears the global timeout form and saves the configuration.
    */
   protected abstract long getStaticDefaultWaitTime();
}
