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

import hudson.model.Project;
import io.github.microcks.jenkins.plugin.CommonParamsHelper;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import io.github.microcks.jenkins.plugin.MicrocksGlobalConfiguration;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.QueryParameter;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * @author laurent
 */
public interface IMicrocksPluginDescriptor {

   default FormValidation doCheckServer(@QueryParameter String value)
         throws IOException, ServletException {
      return CommonParamsHelper.doCheckServer(value);
   }

   default ListBoxModel doFillServerItems() {
      ListBoxModel items = new ListBoxModel();
      items.add(" - None -");
      for (MicrocksInstallation installation : MicrocksGlobalConfiguration.get().getMicrocksInstallations()) {
         items.add(installation.getMicrocksDisplayName());
      }
      return items;
   }

   default ListBoxModel doFillWaitUnitItems() {
      ListBoxModel items = new ListBoxModel();
      items.add("Seconds", "sec");
      items.add("Minutes", "min");
      items.add("Milliseconds", "milli");
      return items;
   }
}
