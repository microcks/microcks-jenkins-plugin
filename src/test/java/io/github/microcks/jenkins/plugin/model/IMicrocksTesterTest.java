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
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author laurent
 */
public class IMicrocksTesterTest {


   @Test
   public void testResponseParsing() {
      String response = "{\"id\":\"59947684b2799d34d12fb189\",\"testNumber\":null,\"testDate\":1502901892216,\"testedEndpoint\":\"http://localhost:8088/mockHelloServiceSoapBinding\",\"serviceId\":\"57f4dacc6588793c093f94a5\",\"elapsedTime\":0,\"success\":false,\"inProgress\":true,\"runnerType\":\"HTTP\",\"testCaseResults\":[]}";
      // Convert response to Node using Jackson object mapper.
      ObjectMapper mapper = new ObjectMapper();

      try {
         JsonNode responseNode = mapper.readTree(response);
         String testResultId = responseNode.path("id").asText();

         assertEquals("59947684b2799d34d12fb189", testResultId);
      } catch (Exception e) {
         e.printStackTrace();
         fail("No exception should be thrown");
      }
   }
}