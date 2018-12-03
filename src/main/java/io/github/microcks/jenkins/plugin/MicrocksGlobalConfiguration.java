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

import hudson.Extension;
import io.github.microcks.jenkins.plugin.model.MicrocksInstallation;
import jenkins.model.GlobalConfiguration;

import java.util.ArrayList;
import java.util.List;

/**
 * @author laurent
 */
@Extension
public class MicrocksGlobalConfiguration extends GlobalConfiguration {

   private List<MicrocksInstallation> microcksInstallations = new ArrayList<>();

   public MicrocksGlobalConfiguration(){
      load();
   }

   public static MicrocksGlobalConfiguration get() {
      return GlobalConfiguration.all().get(MicrocksGlobalConfiguration.class);
   }

   public List<MicrocksInstallation> getMicrocksInstallations() {
      return microcksInstallations;
   }

   public void setMicrocksInstallations(List<MicrocksInstallation> installations) {
      this.microcksInstallations = installations;
      save();
   }

   public MicrocksInstallation getMicrocksInstallationByName(String installationName) {
      for (MicrocksInstallation installation : microcksInstallations) {
         if (installation.getMicrocksDisplayName().equals(installationName)) {
            return installation;
         }
      }
      return null;
   }
}
