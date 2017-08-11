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
package com.github.lbroudoux.microcks.jenkins.plugin.dsl;

import com.github.lbroudoux.microcks.jenkins.plugin.model.ITimedMicrocksPlugin;
import org.kohsuke.stapler.DataBoundSetter;

/**
 * @author laurent
 */
public abstract class TimedMicrocksBaseStep extends MicrocksBaseStep implements ITimedMicrocksPlugin {

   protected String waitTime;

   protected String waitUnit;

   final public String getWaitTime() {
      return waitTime;
   }

   final public String getWaitUnit() {
      return TimeoutUnit.normalize(waitUnit);
   }

   @DataBoundSetter
   final public void setWaitTime(String waitTime) {
      this.waitTime = waitTime != null ? waitTime.trim() : null;
   }

   @DataBoundSetter
   final public void setWaitUnit(String waitUnit) {
      this.waitUnit = waitUnit != null ? waitUnit.trim() : null;
   }
}
