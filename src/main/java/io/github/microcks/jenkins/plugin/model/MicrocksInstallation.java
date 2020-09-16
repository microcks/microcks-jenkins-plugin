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

import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.common.StandardListBoxModel;
import com.cloudbees.plugins.credentials.common.StandardUsernameCredentials;
import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials;
import com.cloudbees.plugins.credentials.common.UsernamePasswordCredentials;
import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import hudson.model.Project;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;

import io.github.microcks.jenkins.plugin.util.KeycloakConnector;
import io.github.microcks.jenkins.plugin.util.MicrocksConfigException;
import io.github.microcks.jenkins.plugin.util.MicrocksConnector;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import java.io.IOException;
import java.util.List;

import static com.cloudbees.plugins.credentials.CredentialsMatchers.instanceOf;

/**
 * @author laurent
 */
public class MicrocksInstallation extends AbstractDescribableImpl<MicrocksInstallation> {

   private final String microcksDisplayName;
   private final String microcksApiURL;
   private final String microcksCredentialsId;
   private final boolean disableSSLValidation;

   @DataBoundConstructor
   public MicrocksInstallation(String microcksDisplayName, String microcksApiURL, String microcksCredentialsId, boolean disableSSLValidation) {
      this.microcksDisplayName = microcksDisplayName;
      this.microcksApiURL = microcksApiURL;
      this.microcksCredentialsId = microcksCredentialsId;
      this.disableSSLValidation = disableSSLValidation;
   }


   public String getMicrocksDisplayName() {
      return microcksDisplayName;
   }

   public String getMicrocksApiURL() {
      return microcksApiURL;
   }

   public String getMicrocksCredentialsId() {
      return microcksCredentialsId;
   }

   public MicrocksConnector getMicrocksConnector() throws MicrocksConfigException {
      KeycloakConnector keycloakConnector = getKeycloakConnector();
      String oAuthToken = keycloakConnector.connectAndGetToken();
      return new MicrocksConnector(this.microcksApiURL, oAuthToken, this.disableSSLValidation);
   }

   public KeycloakConnector getKeycloakConnector() throws MicrocksConfigException {
      String microcksKeycloakURL;
      try {
         microcksKeycloakURL = MicrocksConnector.getKeycloakURL(this.microcksApiURL, this.disableSSLValidation);
      } catch (IOException ioe) {
         throw new MicrocksConfigException("Could not connect to Microcks: " + ioe.getMessage());
      }
      return getKeycloakConnector(microcksKeycloakURL, this.microcksCredentialsId, this.disableSSLValidation);
   }

   public static KeycloakConnector getKeycloakConnector(String microcksKeycloakURL, String microcksCredentialsId, boolean disableSSLValidation) {
      String username = null;
      String password = null;

      if (microcksCredentialsId != null && microcksCredentialsId.length() > 0) {
         List<StandardUsernamePasswordCredentials> credsList = CredentialsProvider.lookupCredentials(StandardUsernamePasswordCredentials.class);
         for (StandardUsernamePasswordCredentials creds : credsList) {
            if (creds.getId().equals(microcksCredentialsId)) {
               username = creds.getUsername();
               password = creds.getPassword().getPlainText();
            }
         }
      }
      return new KeycloakConnector(microcksKeycloakURL, username, password, disableSSLValidation);
   }

   @Extension
   public static class MicrocksInstallationDescriptor extends Descriptor<MicrocksInstallation> {

      public FormValidation doTestMicrocksConnection(
            @QueryParameter("microcksApiURL") final String microcksApiURL,
            @QueryParameter("microcksCredentialsId") final String microcksCredentialsId,
            @QueryParameter("disableSSLValidation") final boolean disableSSLValidation
      ) {
         try {
            String microcksKeycloakURL = MicrocksConnector.getKeycloakURL(microcksApiURL, disableSSLValidation);
            KeycloakConnector testConnector = MicrocksInstallation.getKeycloakConnector(microcksKeycloakURL, microcksCredentialsId, disableSSLValidation);
            testConnector.connectAndGetToken();
            return FormValidation.ok("Success");
         } catch (Exception e) {
            return FormValidation.error(e.getMessage());
         }
      }

      public ListBoxModel doFillMicrocksCredentialsIdItems(@AncestorInPath Project project) {
         return new StandardListBoxModel().withEmptySelection()
               .withMatching(
                     instanceOf(UsernamePasswordCredentials.class),
                     CredentialsProvider.lookupCredentials(StandardUsernameCredentials.class, project)
               );
      }

      @Override
      public String getDisplayName() {
         return "Microcks Installation";
      }
   }
}
