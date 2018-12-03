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
package io.github.microcks.jenkins.plugin.util;

import javax.net.ssl.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.*;
import java.security.cert.X509Certificate;

/**
 * @author laurent
 */
public class TrustingSSLSocketFactory extends SSLSocketFactory {

   private X509TrustManager trustManager;
   private SSLSocketFactory internalSSLSocketFactory;

   public TrustingSSLSocketFactory() throws KeyManagementException, NoSuchAlgorithmException {
      SSLContext context = SSLContext.getInstance("TLS");

      // Create a trust manager that does not validate certificate chains
      this.trustManager = new X509TrustManager() {
         public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
         }
         public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
         }
         public void checkServerTrusted(X509Certificate[] certs, String authType) {
         }
      };

      TrustManager[] trustAllCerts = new TrustManager[] { this.trustManager };

      context.init(null, trustAllCerts, new java.security.SecureRandom());
      internalSSLSocketFactory = context.getSocketFactory();
   }

   public X509TrustManager getTrustManager() {
      return this.trustManager;
   }

   @Override
   public String[] getDefaultCipherSuites() {
      return internalSSLSocketFactory.getDefaultCipherSuites();
   }

   @Override
   public String[] getSupportedCipherSuites() {
      return internalSSLSocketFactory.getSupportedCipherSuites();
   }

   @Override
   public Socket createSocket() throws IOException {
      return internalSSLSocketFactory.createSocket();
   }

   @Override
   public Socket createSocket(Socket s, String host, int port, boolean autoClose) throws IOException {
      return internalSSLSocketFactory.createSocket(s, host, port, autoClose);
   }

   @Override
   public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
      return internalSSLSocketFactory.createSocket(host, port);
   }

   @Override
   public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException, UnknownHostException {
      return internalSSLSocketFactory.createSocket(host, port, localHost, localPort);
   }

   @Override
   public Socket createSocket(InetAddress host, int port) throws IOException {
      return internalSSLSocketFactory.createSocket(host, port);
   }

   @Override
   public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
      return internalSSLSocketFactory.createSocket(address, port, localAddress, localPort);
   }
}
