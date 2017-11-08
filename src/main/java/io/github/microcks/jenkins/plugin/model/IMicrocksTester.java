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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.microcks.jenkins.plugin.Messages;
import hudson.EnvVars;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.Run;
import hudson.model.TaskListener;
import okhttp3.*;

import java.io.IOException;
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

      // Build a new Http client to
      OkHttpClient client = new OkHttpClient();
      StringBuilder builder = new StringBuilder("{");
      builder.append("\"serviceId\": \"").append(getServiceId()).append("\", ");
      builder.append("\"testEndpoint\": \"").append(getTestEndpoint()).append("\", ");
      builder.append("\"runnerType\": \"").append(getRunnerType()).append("\"");
      builder.append("}");

      if (chatty) {
         listener.getLogger().println("\n MicrocksTester launching new test with " + builder.toString());
      }
      String testResultId = null;
      try {
         String response = this.createTestResult(client, builder.toString());
         if (chatty) {
            listener.getLogger().println("\n MicrocksTester got response: " + response);
         }
         // Convert response to Node using Jackson object mapper.
         ObjectMapper mapper = new ObjectMapper();
         JsonNode responseNode = mapper.readTree(response);
         testResultId = responseNode.path("id").asText();
      } catch (IOException e) {
         e.printStackTrace();
      }

      // Now waiting on test completion or cancelling.
      boolean testResult = waitOnTest(client, testResultId, listener, startTime, wait, chatty);

      if (testResult) {
         listener.getLogger().println(String.format(Messages.EXIT_TEST_GOOD, getTestConfig(), testResultId));
      } else {
         listener.getLogger().println(String.format(Messages.EXIT_TEST_BAD, getTestConfig(), testResultId, wait));
      }
      return testResult;
   }

   default boolean waitOnTest(OkHttpClient client, String testResultId, TaskListener listener, long startTime, long wait, boolean chatty) throws InterruptedException {
      boolean success = false;
      ObjectMapper mapper = new ObjectMapper();

      while (TimeUnit.NANOSECONDS.toMillis(System.nanoTime()) < (startTime + wait)) {
         if (chatty) {
            listener.getLogger().println(String.format("\n MicrocksTester checking if test \"%s\" is still in progress.", testResultId));
         }
         try {
            String response = this.getTestResult(client, testResultId);
            JsonNode responseNode = mapper.readTree(response);

            String successString = responseNode.path("success").asText();
            String inProgressString = responseNode.path("inProgress").asText();
            success = Boolean.parseBoolean(successString);
            boolean inProgress = Boolean.parseBoolean(inProgressString);


            if (chatty) {
               listener.getLogger().println(String.format("\n MicrocksTester got status for test \"%s\" - success: %s, inProgress: %s",
                     testResultId, successString, inProgressString));
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

   default String createTestResult(OkHttpClient client, String json) throws IOException {
      MediaType JSON = MediaType.parse("application/json; charset=utf-8");
      RequestBody body = RequestBody.create(JSON, json);
      Request request = new Request.Builder()
            .url(getApiURL() + "/tests")
            .post(body)
            .build();
      Response response = client.newCall(request).execute();
      return response.body().string();
   }

   default String getTestResult(OkHttpClient client, String testResultId) throws IOException {
      Request request = new Request.Builder()
            .url(getApiURL() + "/tests/" + testResultId)
            .addHeader("Accept", "application/json")
            .get()
            .build();
      Response response = client.newCall(request).execute();
      return response.body().string();
   }
}
