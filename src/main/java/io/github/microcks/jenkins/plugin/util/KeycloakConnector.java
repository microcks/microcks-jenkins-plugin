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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import javax.net.ssl.SSLSession;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Base64;

/**
 * @author laurent
 */
public class KeycloakConnector {

   private String url = null;
   private String username = null;
   private String password = null;
   private boolean disableSSLValidation = false;
   private OkHttpClient client;

   public KeycloakConnector(String url, String username, String password, boolean disableSSLValidation) {
      this.url = url;
      this.username = username;
      this.password = password;
      this.disableSSLValidation = disableSSLValidation;
      if (!disableSSLValidation) {
         this.client = new OkHttpClient();
      } else {
         try {
            TrustingSSLSocketFactory sslSocketFactory = new TrustingSSLSocketFactory();
            this.client = new OkHttpClient.Builder()
                  .sslSocketFactory(sslSocketFactory, sslSocketFactory.getTrustManager())
                  .hostnameVerifier((String s, SSLSession sslSession) -> true)
                  .build();
         } catch (Exception e) {
            throw new NullPointerException("Cannot build an HttpClient with " + e.getMessage());
         }
      }
   }

   public String getUrl() {
      return url;
   }

   public String getUsername() {
      return username;
   }

   public String getPassword() {
      return password;
   }

   public String connectAndGetToken() throws MicrocksConfigException {
      RequestBody body = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"),
            "grant_type=client_credentials");

      byte[] basic = null;
      try {
         basic = (username + ":" + password).getBytes("UTF-8");
      } catch (UnsupportedEncodingException uee) {
         throw new MicrocksConfigException("Could not encode username and password for requesting an OAuth token");
      }

      Response response = null;
      try {
         Request request = new Request.Builder()
               .url(url + "/protocol/openid-connect/token")
               .addHeader("Authorization", "Basic " + Base64.getEncoder().encodeToString(basic))
               .addHeader("Accept", "application/json")
               .post(body)
               .build();
         response = client.newCall(request).execute();
      } catch (IOException ioe) {
         throw new MicrocksConfigException("Could not connect to Keycloak: " + ioe.getMessage());
      }

      if (!response.isSuccessful()) {
         throw new MicrocksConfigException("Could not retrieve an OAuth token from Keycloak: " + response.message());
      }

      // Convert response to Node using Jackson object mapper.
      ObjectMapper mapper = new ObjectMapper();
      JsonNode responseNode = null;
      try {
         responseNode = mapper.readTree(response.body().string());
      } catch (Exception e) {
         throw new MicrocksConfigException("Could not parse the response body from Keycloak: " + e.getMessage());
      }
      return responseNode.path("access_token").asText();
   }

}
