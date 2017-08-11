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
package com.github.lbroudoux.microcks.jenkins.plugin.model;

import hudson.EnvVars;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.Run;
import hudson.model.TaskListener;

/**
 * @author laurent
 */
public interface IMicrocksTester extends ITimedMicrocksPlugin {

   String DISPLAY_NAME = "Launch Microcks Test Runner";

   default String getDisplayName() {
      return DISPLAY_NAME;
   }

   String getServiceId();

   String getTestEndpoint();

   String getRunnerType();

   default long getGlobalTimeoutConfiguration() {
      return GlobalConfig.getTestWait();
   }

   default boolean doItCore(TaskListener listener, EnvVars env, Run<?, ?> run, AbstractBuild<?, ?> build, Launcher launcher) throws InterruptedException {
      return true;
   }

   default void waitOnTest() {

   }
}