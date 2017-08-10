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
package com.github.lbroudoux.microcks.jenkins.plugin;

import com.github.lbroudoux.microcks.jenkins.plugin.model.IMicrocksPluginDescriptor;
import hudson.model.AbstractProject;
import hudson.model.Describable;
import hudson.tasks.BuildStep;
import hudson.tasks.BuildStepDescriptor;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.StaplerRequest;

/**
 * @author laurent
 */
public class TimedBuildStepDescriptor<T extends BuildStep & Describable<T>> extends BuildStepDescriptor<T> implements IMicrocksPluginDescriptor {

   protected String wait;
   protected String waitUnit;

   TimedBuildStepDescriptor() {
      load();
   }

   @Override
   public synchronized void load() {
      super.load();
      if (wait == null || wait.trim().isEmpty()) {
         //wait = "" + getStaticDefaultWaitTime();
      }
   }

   @Override
   public boolean isApplicable(Class<? extends AbstractProject> aClass) {
      // Indicates that this builder can be used with all kinds of project types
      return true;
   }

   @Override
   public synchronized boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
      return true;
   }
}
