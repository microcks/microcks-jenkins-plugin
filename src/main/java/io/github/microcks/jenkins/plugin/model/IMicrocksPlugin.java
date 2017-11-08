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
package io.github.microcks.jenkins.plugin.model;

import hudson.AbortException;
import hudson.EnvVars;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.Run;
import hudson.model.TaskListener;

import java.io.IOException;

/**
 * @author laurent
 */
public interface IMicrocksPlugin {

   // states of note
   public static final String STATE_RUNNING = "Running";
   public static final String STATE_COMPLETE = "Complete";
   public static final String STATE_CANCELLED = "Cancelled";
   public static final String STATE_ERROR = "Error";
   public static final String STATE_FAILED = "Failed";

   public String getApiURL();

   public String getVerbose();

   public String getDisplayName();

   boolean doItCore(TaskListener listener, EnvVars env, Run<?, ?> run, AbstractBuild<?, ?> build, Launcher launcher) throws InterruptedException;

   default void doIt(Run<?, ?> run, FilePath workspace, Launcher launcher,
                     TaskListener listener) throws InterruptedException, IOException {
      boolean successful = this.doItCore(listener, null, run, null, launcher);
      if (!successful)
         throw new AbortException("\"" + getDisplayName() + "\" failed");
   }

   default boolean doIt(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws IOException, InterruptedException {
      boolean successful = this.doItCore(listener, null, null, build, launcher);
      if (!successful)
         throw new AbortException("\"" + getDisplayName() + "\" failed");
      return successful;
   }

   default boolean isTestRunning(String testState) {
      if (testState != null && (testState.equals(STATE_RUNNING)))
         return true;
      return false;
   }

   default boolean isTestFinished(String testState) {
      if (testState != null && (testState.equals(STATE_COMPLETE) || testState.equals(STATE_FAILED) || testState.equals(STATE_ERROR) || testState.equals(STATE_CANCELLED)))
         return true;
      return false;
   }
}
