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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.microcks.jenkins.plugin.model.GlobalConfig;
import io.github.microcks.jenkins.plugin.model.IMicrocksTester;
import hudson.Extension;
import hudson.tasks.Builder;
import org.kohsuke.stapler.DataBoundConstructor;

import java.util.*;
import java.util.Map.Entry;

/**
 * @author laurent
 */
public class MicrocksTester extends TimedMicrocksBaseStep implements IMicrocksTester {

   protected final String serviceId;
   protected final String testEndpoint;
   protected final String runnerType;
   protected final String secretName;
   protected final String filteredOperations;
   protected final String operationsHeaders;

   // Fields in config.jelly must match the parameter names in the "DataBoundConstructor"
   @DataBoundConstructor
   public MicrocksTester(String server, String serviceId, String testEndpoint, String runnerType, String secretName,
                         String filteredOperations, String operationsHeaders, String verbose, String waitTime, String waitUnit) {
      super(server, verbose, waitTime, waitUnit);
      this.serviceId = serviceId != null ? serviceId.trim() : null;
      this.testEndpoint = testEndpoint != null ? testEndpoint.trim() : null;
      this.runnerType = runnerType != null ? runnerType.trim() : null;
      this.secretName = secretName != null ? secretName.trim() : null;
      this.filteredOperations = filteredOperations != null ? filteredOperations.trim() : null;
      this.operationsHeaders = operationsHeaders != null ? operationsHeaders.trim() : null;
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

   @Override
   public String getSecretName() {
      return secretName;
   }

   @Override
   public List<String> getFilteredOperations() {
      List<String> results = null;

      if (filteredOperations != null && !filteredOperations.isEmpty()) {
         // Add some Jackson parsing and mapping here.
         ObjectMapper mapper = new ObjectMapper();

         try {
            JsonNode rootNode = mapper.readTree(this.filteredOperations);

            // If we've got an array, that's a good start! Initialize results.
            if (rootNode.isArray()) {
               results = new ArrayList<>();

               Iterator<JsonNode> operationNodes = rootNode.elements();
               while (operationNodes.hasNext()) {
                  JsonNode operationNode = operationNodes.next();

                  // If we've got a string, add the operation to the list.
                  if (operationNode.isTextual()) {
                     results.add(operationNode.textValue());
                  }
               }
            }
         } catch (Exception e) {
            System.err.println("Exception while parsing filteredOperations field: " + e.getMessage());
         }
      }
      return results;
   }

   @Override
   public Map<String, List<Map<String, String>>> getOperationsHeaders() {
      Map<String, List<Map<String, String>>> results = null;

      if (operationsHeaders != null && !operationsHeaders.isEmpty()) {
         // Add some Jackson parsing and mapping here.
         ObjectMapper mapper = new ObjectMapper();
         try {
            JsonNode rootNode = mapper.readTree(this.operationsHeaders);

            // If we've got an object, that's a good start! Initialize results.
            if (rootNode.isObject()) {
               results = new HashMap<>();

               Iterator<Entry<String, JsonNode>> operationNodes = rootNode.fields();
               while (operationNodes.hasNext()) {
                  Entry<String, JsonNode> operationNode = operationNodes.next();

                  if (operationNode.getValue().isArray()) {
                     List<Map<String, String>> operationHeadersList = new ArrayList<>();

                     Iterator<JsonNode> headerNodes = operationNode.getValue().iterator();
                     while (headerNodes.hasNext()) {
                        JsonNode headerNode = headerNodes.next();

                        // If we've got correct keys, add the header to the list.
                        if (headerNode.has("name") && headerNode.has("values")) {
                           Map<String, String> header = new HashMap<>();
                           header.put("name", headerNode.get("name").textValue());
                           header.put("values", headerNode.get("values").textValue());
                           operationHeadersList.add(header);
                        }
                     }
                     // If we've got at least one header, record the operation headers in results.
                     if (operationHeadersList.size() > 0) {
                        results.put(operationNode.getKey(), operationHeadersList);
                     }
                  }
               }
            }
         } catch (Exception e) {
            System.err.println("Exception while parsing operationsHeaders field: " + e.getMessage());
         }
      }
      return results;
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
