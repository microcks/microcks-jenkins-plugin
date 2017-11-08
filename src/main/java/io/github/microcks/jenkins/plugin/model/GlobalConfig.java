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

import io.github.microcks.jenkins.plugin.MicrocksTester;

/**
 * @author laurent
 */
public class GlobalConfig {

   private static final long SECOND = 1000;

   public static final long DEFAULT_TEST_WAIT = 5 * SECOND;

   public static long getTestWait() {
      return new MicrocksTester.DescriptorImpl().getConfiguredDefaultWaitTime();
   }
}
