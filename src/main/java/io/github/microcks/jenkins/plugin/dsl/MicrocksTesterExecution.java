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

import hudson.AbortException;
import hudson.EnvVars;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Computer;
import hudson.model.Executor;
import hudson.model.Run;
import hudson.model.TaskListener;
import org.jenkinsci.plugins.workflow.steps.AbstractSynchronousNonBlockingStepExecution;
import org.jenkinsci.plugins.workflow.steps.StepContextParameter;

import javax.inject.Inject;

/**
 * @author laurent
 */
public class MicrocksTesterExecution extends AbstractSynchronousNonBlockingStepExecution<Void> {

   private static final long serialVersionUID = 1L;

   @StepContextParameter
   private transient TaskListener listener;
   @StepContextParameter
   private transient Launcher launcher;
   @StepContextParameter
   private transient EnvVars envVars;
   @StepContextParameter
   private transient Run<?, ?> runObj;
   @StepContextParameter
   private transient FilePath filePath; // included as ref of what can be included for future use
   @StepContextParameter
   private transient Executor executor; // included as ref of what can be included for future use
   @StepContextParameter
   private transient Computer computer; // included as ref of what can be included for future use

   @Inject
   private transient MicrocksTester step;

   @Override
   protected Void run() throws Exception {
      boolean success = step.doItCore(listener, envVars, runObj, null, launcher);
      if (!success) {
         throw new AbortException("\"" + step.getDescriptor().getDisplayName() + "\" failed");
      }
      return null;
   }
}
