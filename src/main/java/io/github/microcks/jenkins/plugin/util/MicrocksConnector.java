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
package io.github.microcks.jenkins.plugin.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.microcks.jenkins.plugin.model.TestResultSummary;
import okhttp3.*;

import javax.net.ssl.SSLSession;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author laurent
 */
public class MicrocksConnector {

   private String apiURL;
   private String oAuthToken;
   private boolean disableSSLValidation = false;

   private OkHttpClient client;

   public MicrocksConnector(String apiURL, String oAuthToken, boolean disableSSLValidation) {
      this.apiURL = apiURL;
      this.oAuthToken = oAuthToken;
      this.disableSSLValidation = disableSSLValidation;
      this.client = buildHttpClient(disableSSLValidation);
   }

   public static String getKeycloakURL(String apiURL, boolean disableSSLValidation) throws IOException {
      OkHttpClient client = buildHttpClient(disableSSLValidation);

      Request request = new Request.Builder().url(apiURL + "/keycloak/config")
            .addHeader("Accept", "application/json").get().build();
      Response response = client.newCall(request).execute();

      // Convert response to Node using Jackson object mapper.
      JsonNode responseNode = new ObjectMapper().readTree(response.body().string());

      String authServerURL = responseNode.path("auth-server-url").asText();
      String realmName = responseNode.path("realm").asText();
      return authServerURL + "/realms/" + realmName;
   }

   public String createTestResult(String serviceId, String testEndpoint, String runnerType, String secretName,
                                  Long timeout, Map<String, List<Map<String, String>>> operationsHeaders) throws IOException {
      // Prepare a Jackson object mapper.
      ObjectMapper mapper = new ObjectMapper();

      StringBuilder builder = new StringBuilder("{");
      builder.append("\"serviceId\": \"").append(serviceId).append("\", ");
      builder.append("\"testEndpoint\": \"").append(testEndpoint).append("\", ");
      builder.append("\"runnerType\": \"").append(runnerType).append("\", ");
      builder.append("\"timeout\": ").append(timeout);
      if (secretName != null && !secretName.isEmpty()) {
         builder.append(", \"secretName\": \"").append(secretName).append("\"");
      }
      if (operationsHeaders != null && !operationsHeaders.isEmpty()) {
         builder.append(", \"operationsHeaders\": ").append(mapper.writeValueAsString(operationsHeaders));
      }
      builder.append("}");

      System.err.println("Microcks test creation request: " + builder.toString());

      MediaType JSON = MediaType.parse("application/json; charset=utf-8");
      RequestBody body = RequestBody.create(JSON, builder.toString());
      Request request = new Request.Builder()
            .url(apiURL + "/tests")
            .addHeader("Authorization", "Bearer " + oAuthToken)
            .post(body)
            .build();
      Response response = client.newCall(request).execute();

      // Convert response to Node using Jackson object mapper.
      JsonNode responseNode = mapper.readTree(response.body().string());
      return responseNode.path("id").asText();
   }

   public TestResultSummary getTestResult(String testResultId) throws IOException {
      Request request = new Request.Builder()
            .url(apiURL + "/tests/" + testResultId)
            .addHeader("Accept", "application/json")
            .addHeader("Authorization", "Bearer " + oAuthToken)
            .get()
            .build();
      Response response = client.newCall(request).execute();

      // Convert response to Node using Jackson object mapper.
      ObjectMapper mapper = new ObjectMapper();
      mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      return mapper.readValue(response.body().string(), TestResultSummary.class);
   }

   private static OkHttpClient buildHttpClient(boolean disableSSLValidation) {
      OkHttpClient client;
      if (!disableSSLValidation) {
         client = new OkHttpClient();
      } else {
         try {
            TrustingSSLSocketFactory sslSocketFactory = new TrustingSSLSocketFactory();
            client = new OkHttpClient.Builder()
                  .sslSocketFactory(sslSocketFactory, sslSocketFactory.getTrustManager())
                  .hostnameVerifier((String s, SSLSession sslSession) -> true)
                  .build();
         } catch (Exception e) {
            throw new NullPointerException("Cannot build an HttpClient with " + e.getMessage());
         }
      }
      return client;
   }
}
