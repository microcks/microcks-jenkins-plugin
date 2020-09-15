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

import io.github.microcks.jenkins.plugin.Messages;
import hudson.EnvVars;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.Run;
import hudson.model.TaskListener;
import io.github.microcks.jenkins.plugin.MicrocksGlobalConfiguration;
import io.github.microcks.jenkins.plugin.util.MicrocksConfigException;
import io.github.microcks.jenkins.plugin.util.MicrocksConnector;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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

   String getSecretName();

   Map<String, List<Map<String, String>>> getOperationsHeaders();


   default long getGlobalTimeoutConfiguration() {
      return GlobalConfig.getTestWait();
   }

   default String getTestConfig() {
      return getServiceId() + "-" + getTestEndpoint() + "-" + getRunnerType();
   }

   default boolean doItCore(TaskListener listener, EnvVars env, Run<?, ?> run, AbstractBuild<?, ?> build, Launcher launcher) throws InterruptedException {
      boolean chatty = Boolean.parseBoolean(getVerbose());
      listener.getLogger().println(String.format(Messages.START_TEST_RELATED_PLUGINS, DISPLAY_NAME, getTestConfig()));

      long startTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime());
      long wait = getTimeout(listener, chatty);

      MicrocksInstallation microcksServer = MicrocksGlobalConfiguration.get().getMicrocksInstallationByName(getServer());
      MicrocksConnector microcksConnector = null;
      try {
         microcksConnector= microcksServer.getMicrocksConnector();
      } catch (MicrocksConfigException mce) {
         listener.getLogger().println(String.format(Messages.EXIT_CONFIG_BAD, getTestConfig(), mce.getMessage()));
         return false;
      }

      if (chatty) {
         listener.getLogger().println("\n MicrocksTester connecting to microcks server successful");
         listener.getLogger().println("\n MicrocksTester launching new test with " + getTestConfig());
      }
      String testResultId = null;
      try {
         testResultId = microcksConnector.createTestResult(getServiceId(), getTestEndpoint(), getRunnerType(),
               getSecretName(), wait, getOperationsHeaders());
         if (chatty) {
            listener.getLogger().println("\n MicrocksTester got response: " + testResultId);
         }
      } catch (IOException e) {
         e.printStackTrace();
      }

      // Now waiting on test completion or cancelling.
      // Add 500ms to wait time as it's now representing the server timeout.
      boolean testResult = waitOnTest(microcksConnector, testResultId, listener, startTime, wait + 500, chatty);

      if (testResult) {
         listener.getLogger().println(String.format(Messages.EXIT_TEST_GOOD, getTestConfig(), testResultId));
      } else {
         listener.getLogger().println(String.format(Messages.EXIT_TEST_BAD, getTestConfig(), testResultId, wait));
      }
      return testResult;
   }

   default boolean waitOnTest(MicrocksConnector connector, String testResultId, TaskListener listener, long startTime, long wait, boolean chatty) throws InterruptedException {
      boolean success = false;

      while (TimeUnit.NANOSECONDS.toMillis(System.nanoTime()) < (startTime + wait)) {
         if (chatty) {
            listener.getLogger().println(String.format("\n MicrocksTester checking if test \"%s\" is still in progress.", testResultId));
         }
         try {
            TestResultSummary testResult = connector.getTestResult(testResultId);

            success = testResult.isSuccess();
            boolean inProgress = testResult.isInProgress();


            if (chatty) {
               listener.getLogger().println(String.format("\n MicrocksTester got status for test \"%s\" - success: %s, inProgress: %s",
                     testResultId, success, inProgress));
            }
            if (!inProgress) {
               break;
            }
         } catch (IOException ioe) {
            listener.getLogger().println("\n MicrocksTester got an IOException while checking test status: " + ioe.getMessage());
         }

         try {
            if (chatty) {
               listener.getLogger().println("\n MicrocksTester waiting for 2 seconds before checking again.");
            }
            Thread.sleep(2000);
         } catch (InterruptedException ie) {
            if (chatty) {
               listener.getLogger().println(String.format("\n MicrocksTester for %s has been interrupted.", getTestConfig()));
            }
            // Need to throw as this indicates the step as been cancelled
            // TODO Attempt to cancel test on Microcks side ?
            throw ie;
         }
      }
      return success;
   }
}
