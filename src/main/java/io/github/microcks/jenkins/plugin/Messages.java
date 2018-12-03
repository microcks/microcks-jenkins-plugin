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

/**
 * @author laurent
 */
public class Messages {

   public static final String EXIT_CONFIG_BAD = "\n\nExiting \"%s\" unsuccessfully; configuration is not correct: \"%s\"";
   public static final String EXIT_TEST_BAD = "\n\nExiting \"%s\" unsuccessfully; test \"%s\" did not complete successfully within the configured timeout of \"%s\" ms.";
   public static final String EXIT_TEST_GOOD = "\n\nExiting \"%s\" successfully; test \"%s\" has completed successfully with success.";
   public static final String START_TEST_RELATED_PLUGINS = "\n\nStarting the \"%s\" step with test config \"%s\".";
}
