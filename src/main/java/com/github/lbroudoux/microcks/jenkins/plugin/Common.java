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

import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Functionless stub class to establish a path to common
 * *.jelly files (e.g. st:include page="cluster.jelly" class="com.github.lbroudoux.microcks.jenkins.plugin.Common" )
 * @author laurent
 */
public class Common extends AbstractDescribableImpl<Common> {

   @DataBoundConstructor
   public Common() {
   }

   @Extension
   public static class DescriptorImpl extends Descriptor<Common> {
      public String getDisplayName() {
         return "Common";
      }
   }
}
