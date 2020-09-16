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

import java.util.Date;

/**
 * This is a lightweight representation of a Microcks TestResult.
 * @author laurent
 */
public class TestResultSummary {

   private String id;
   private Long version;
   private Long testNumber;
   private Date testDate;
   private String testedEndpoint;
   private String serviceId;
   private long elapsedTime;
   private boolean success = false;
   private boolean inProgress = true;

   public String getId() {
      return id;
   }

   public Long getVersion() {
      return version;
   }

   public Long getTestNumber() {
      return testNumber;
   }

   public Date getTestDate() {
      return testDate;
   }

   public String getTestedEndpoint() {
      return testedEndpoint;
   }

   public String getServiceId() {
      return serviceId;
   }

   public long getElapsedTime() {
      return elapsedTime;
   }

   public boolean isSuccess() {
      return success;
   }

   public boolean isInProgress() {
      return inProgress;
   }
}
