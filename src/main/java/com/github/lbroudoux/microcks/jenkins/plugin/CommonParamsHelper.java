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
package com.github.lbroudoux.microcks.jenkins.plugin;

import com.github.lbroudoux.microcks.jenkins.plugin.dsl.MicrocksBaseStep;
import com.github.lbroudoux.microcks.jenkins.plugin.dsl.TimedMicrocksBaseStep;
import hudson.util.FormValidation;
import org.kohsuke.stapler.QueryParameter;

import java.util.Map;

/**
 * @author laurent
 */
public class CommonParamsHelper {

   public static FormValidation doCheckApiURL(@QueryParameter String value) {
      if (value.length() == 0)
         return FormValidation.warning("Unless you specify a value here, one of the default API endpoints will be used; see this field's help or https://github.com/microcks/microcks-jenkins-plugin#common-aspects for details");
      return FormValidation.ok();
   }

   public static void updateDSLBaseStep(Map<String, Object> arguments, MicrocksBaseStep step) {
      if (arguments.containsKey("apiURL")) {
         Object apiURL = arguments.get("apiURL");
         if (apiURL != null) {
            step.setApiURL(apiURL.toString());
         }
      }

      if (arguments.containsKey("verbose")) {
         Object verbose = arguments.get("verbose");
         if (verbose != null) {
            step.setVerbose(verbose.toString());
         }
      }
   }

   public static void updateDSLTimedStep(Map<String, Object> arguments, TimedMicrocksBaseStep step) {
      updateDSLBaseStep(arguments, step);

      if (arguments.containsKey("waitTime")) {
         Object waitTime = arguments.get("waitTime");
         if (waitTime != null) {
            step.setWaitTime(waitTime.toString());
         }
      }
      if (arguments.containsKey("waitUnit")) {
         Object waitUnit = arguments.get("waitUnit");
         if (waitUnit != null) {
            step.setWaitUnit(waitUnit.toString());
         }
      }
   }
}
