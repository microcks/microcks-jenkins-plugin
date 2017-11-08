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

import io.github.microcks.jenkins.plugin.model.ITimedMicrocksPlugin;
import jenkins.tasks.SimpleBuildStep;

import java.io.Serializable;

/**
 * @author laurent
 */
public abstract class TimedMicrocksBaseStep extends MicrocksBaseStep implements SimpleBuildStep, Serializable, ITimedMicrocksPlugin {

   protected final String waitTime;
   protected final String waitUnit;

   protected TimedMicrocksBaseStep(String apiURL, String verbose, String waitTime, String waitUnit) {
      super(apiURL, verbose);
      this.waitTime = waitTime != null ? waitTime.trim() : null;
      this.waitUnit = waitUnit != null ? waitUnit.trim() : null;
   }

   public final String getWaitTime() {
      return waitTime;
   }

   public final String getWaitUnit() {
      return TimeoutUnit.normalize(waitUnit);
   }
}
