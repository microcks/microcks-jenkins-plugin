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

import hudson.AbortException;
import hudson.EnvVars;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.Run;
import hudson.model.TaskListener;
import io.github.microcks.jenkins.plugin.Messages;
import io.github.microcks.jenkins.plugin.MicrocksGlobalConfiguration;
import io.github.microcks.jenkins.plugin.util.MicrocksConfigException;
import io.github.microcks.jenkins.plugin.util.MicrocksConnector;

import java.io.IOException;

/**
 * @author laurent
 */
public interface IMicrocksImporter extends IMicrocksPlugin {

   String DISPLAY_NAME = "Import API specification files in Microcks";

   default String getDisplayName() {
      return DISPLAY_NAME;
   }

   String getSpecificationFiles();


   default void doIt(Run<?, ?> run, FilePath workspace, Launcher launcher,
                     TaskListener listener) throws InterruptedException, IOException {
      boolean successful = this.doItCore(listener, workspace, run, null, launcher);
      if (!successful)
         throw new AbortException("\"" + getDisplayName() + "\" failed");
   }

   default boolean doIt(AbstractBuild<?, ?> build, Launcher launcher,
                        BuildListener listener) throws IOException, InterruptedException {
      boolean successful = this.doItCore(listener, build.getWorkspace(), null, build, launcher);
      if (!successful)
         throw new AbortException("\"" + getDisplayName() + "\" failed");
      return successful;
   }

   default boolean doItCore(TaskListener listener, EnvVars env, Run<?, ?> run, AbstractBuild<?, ?> build, Launcher launcher) throws InterruptedException {
      boolean successful = this.doItCore(listener, build.getWorkspace(), null, build, launcher);
      if (!successful)
         throw new InterruptedException("\"" + getDisplayName() + "\" failed");
      return successful;
   }

   default boolean doItCore(TaskListener listener, FilePath workspace, Run<?, ?> run, AbstractBuild<?, ?> build, Launcher launcher) throws InterruptedException {
      boolean chatty = Boolean.parseBoolean(getVerbose());

      MicrocksInstallation microcksServer = MicrocksGlobalConfiguration.get().getMicrocksInstallationByName(getServer());
      MicrocksConnector microcksConnector = null;
      try {
         microcksConnector= microcksServer.getMicrocksConnector();
      } catch (MicrocksConfigException mce) {
         listener.getLogger().println(String.format(Messages.EXIT_CONFIG_BAD, getSpecificationFiles(), mce.getMessage()));
         return false;
      }

      if (chatty) {
         listener.getLogger().println("\n MicrocksImporter connecting to microcks server successful");
      }

      try {
         FilePath sourceDir = workspace;

         for (String specificationFile : getSpecificationFiles().split(",")) {
            boolean mainArtifact = true;
            if (specificationFile.contains(":")) {
               String[] pathAndMainArtifact = specificationFile.split(":");
               specificationFile = pathAndMainArtifact[0];
               // We do not use Boolean.parseBoolean() as we want the default to be 'true'.
               if ("false".equals(pathAndMainArtifact[1].toLowerCase())) {
                  mainArtifact = false;
               }
            }
            FilePath filePath = new FilePath(workspace, specificationFile);
            String discoveredSvc = microcksConnector.uploadSpecification(filePath.getName(), filePath.readToString(), mainArtifact);
            listener.getLogger().println(String.format("\n MicrocksImporter has discovered '%s'", discoveredSvc));
         }
         return true;
      } catch (IOException ioe) {
         listener.getLogger().println(String.format("\n MicrocksImporter got IOException: '%s'", ioe.getMessage()));
      } catch (MicrocksConfigException mce) {
         listener.getLogger().println(String.format("\n MicrocksImporter got MicrocksConfigException: '%s'", mce.getMessage()));
      }
      return false;
   }
}
