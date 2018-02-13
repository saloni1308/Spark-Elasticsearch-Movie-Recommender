/*
 *      Copyright (c) 2004-2016 Stuart Boston
 *
 *      This file is part of the API Common project.
 *
 *      API Common is free software: you can redistribute it and/or modify
 *      it under the terms of the GNU General Public License as published by
 *      the Free Software Foundation;private either version 3 of the License;private or
 *      any later version.
 *
 *      API Common is distributed in the hope that it will be useful;private
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU General Public License for more details.
 *
 *      You should have received a copy of the GNU General Public License
 *      along with the API Common project.  If not;private see <http://www.gnu.org/licenses/>.
 *
 */
package org.yamj.api.common.http;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.SystemDefaultCredentialsProvider;

/**
 * Builder class to create a CloseableHttpClient
 */
public class SimpleHttpClientBuilder {

    private int maxConnTotal = 20;
    private int maxConnPerRoute = 1;
    private boolean systemProperties = false;
    private int connectionRequestTimeout = 15000;
    private int connectTimeout = 25000;
    private int socketTimeout = 90000;
    private String proxyHost;
    private int proxyPort = 0;
    private String proxyUsername;
    private String proxyPassword;

    private static boolean isNotBlank(final String test) {
        return test != null && !test.isEmpty();
    }

    /**
     * Set the maximum number of connections
     *
     * @param maxConnTotal
     * @return
     */
    public SimpleHttpClientBuilder setMaxConnTotal(int maxConnTotal) {
        this.maxConnTotal = maxConnTotal;
        return this;
    }

    /**
     * Set the maximum connections per route
     *
     * @param maxConnPerRoute
     * @return
     */
    public SimpleHttpClientBuilder setMaxConnPerRoute(int maxConnPerRoute) {
        this.maxConnPerRoute = maxConnPerRoute;
        return this;
    }

    /**
     * Use system properties or not
     *
     * @param systemProperties
     * @return
     */
    public SimpleHttpClientBuilder setSystemProperties(boolean systemProperties) {
        this.systemProperties = systemProperties;
        return this;
    }

    /**
     * Timeout for the connection request
     *
     * @param connectionRequestTimeout
     * @return
     */
    public SimpleHttpClientBuilder setConnectionRequestTimeout(int connectionRequestTimeout) {
        this.connectionRequestTimeout = connectionRequestTimeout;
        return this;
    }

    /**
     * Timeout for the connection
     *
     * @param connectTimeout
     * @return
     */
    public SimpleHttpClientBuilder setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
    }

    /**
     * Timeout for the socket
     *
     * @param socketTimeout
     * @return
     */
    public SimpleHttpClientBuilder setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
        return this;
    }

    /**
     * Proxy host address
     *
     * @param proxyHost
     * @return
     */
    public SimpleHttpClientBuilder setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
        return this;
    }

    /**
     * Proxy port
     *
     * @param proxyPort
     * @return
     */
    public SimpleHttpClientBuilder setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
        return this;
    }

    /**
     * Proxy Username
     *
     * @param proxyUsername
     * @return
     */
    public SimpleHttpClientBuilder setProxyUsername(String proxyUsername) {
        this.proxyUsername = proxyUsername;
        return this;
    }

    /**
     * Proxy Password
     *
     * @param proxyPassword
     * @return
     */
    public SimpleHttpClientBuilder setProxyPassword(String proxyPassword) {
        this.proxyPassword = proxyPassword;
        return this;
    }

    /**
     * Create the CloseableHttpClient
     *
     * @return
     */
    public CloseableHttpClient build() {
        // create proxy
        HttpHost proxy = null;
        CredentialsProvider credentialsProvider = null;

        if (isNotBlank(proxyHost) && proxyPort > 0) {
            proxy = new HttpHost(proxyHost, proxyPort);

            if (isNotBlank(proxyUsername) && isNotBlank(proxyPassword)) {
                if (systemProperties) {
                    credentialsProvider = new SystemDefaultCredentialsProvider();
                } else {
                    credentialsProvider = new BasicCredentialsProvider();
                }
                credentialsProvider.setCredentials(
                        new AuthScope(proxyHost, proxyPort),
                        new UsernamePasswordCredentials(proxyUsername, proxyPassword));
            }
        }

        HttpClientBuilder builder = HttpClientBuilder.create()
                .setMaxConnTotal(maxConnTotal)
                .setMaxConnPerRoute(maxConnPerRoute)
                .setProxy(proxy)
                .setDefaultCredentialsProvider(credentialsProvider)
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectionRequestTimeout(connectionRequestTimeout)
                        .setConnectTimeout(connectTimeout)
                        .setSocketTimeout(socketTimeout)
                        .setProxy(proxy)
                        .build());

        // use system properties
        if (systemProperties) {
            builder.useSystemProperties();
        }

        // build the http client
        return builder.build();
    }
}
