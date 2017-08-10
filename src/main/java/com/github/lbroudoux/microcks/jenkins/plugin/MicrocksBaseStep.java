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

import com.github.lbroudoux.microcks.jenkins.plugin.model.IMicrocksPlugin;
import hudson.tasks.Builder;
import jenkins.tasks.SimpleBuildStep;

import java.io.Serializable;

/**
 * @author laurent
 */
public abstract class MicrocksBaseStep extends Builder implements SimpleBuildStep, Serializable, IMicrocksPlugin {

   protected final String apiURL;
   protected final String verbose;

   protected MicrocksBaseStep(String apiURL, String verbose) {
      this.apiURL = apiURL != null ? apiURL.trim() : null;
      this.verbose = verbose != null ? verbose.trim() : null;
   }

   public String getApiURL() {
      return apiURL;
   }

   public String getVerbose() {
      return verbose;
   }
}
