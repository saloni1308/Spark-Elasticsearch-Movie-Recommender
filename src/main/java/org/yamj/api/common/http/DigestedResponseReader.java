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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ConnectTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Digested Response Reader class to process HTTP requests
 */
public class DigestedResponseReader {

    private static final Logger LOG = LoggerFactory.getLogger(DigestedResponseReader.class);
    private static final int SW_BUFFER_10K = 10240;
    private static final int HTTP_STATUS_503 = 503;

    private DigestedResponseReader() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Read content from the HttpGet
     *
     * @param httpClient
     * @param httpGet
     * @param charset
     * @return
     * @throws IOException
     */
    public static DigestedResponse requestContent(HttpClient httpClient, HttpGet httpGet, Charset charset) throws IOException {
        return processRequest(httpClient, httpGet, charset);
    }

    /**
     * Execute a delete request
     *
     * @param httpClient
     * @param httpDelete
     * @param charset
     * @return
     * @throws IOException
     */
    public static DigestedResponse deleteContent(HttpClient httpClient, HttpDelete httpDelete, Charset charset) throws IOException {
        return processRequest(httpClient, httpDelete, charset);
    }

    /**
     * Execute a post request
     *
     * @param httpClient
     * @param httpPost
     * @param charset
     * @return
     * @throws IOException
     */
    public static DigestedResponse postContent(HttpClient httpClient, HttpPost httpPost, Charset charset) throws IOException {
        return processRequest(httpClient, httpPost, charset);
    }

    /**
     * Process the response and return the content
     *
     * @param httpClient
     * @param httpRequest
     * @param charset
     * @return
     * @throws IOException
     */
    private static DigestedResponse processRequest(HttpClient httpClient, HttpRequestBase httpRequest, Charset charset) throws IOException {
        try {
            final HttpResponse response = httpClient.execute(httpRequest);
            final DigestedResponse digestedResponse = new DigestedResponse();
            digestedResponse.setStatusCode(response.getStatusLine().getStatusCode());

            if (response.getEntity() != null) {
                digestedResponse.setContent(getContent(response, charset));
            }

            return digestedResponse;
        } catch (ConnectTimeoutException | SocketTimeoutException ex) {
            LOG.trace("Timeout exception", ex);
            httpRequest.releaseConnection();

            // a timeout should result in a 503 error
            // to signal that the service is temporarily not available
            return new DigestedResponse(HTTP_STATUS_503, "");
        } catch (IOException ex) {
            httpRequest.releaseConnection();
            throw ex;
        }
    }

    /**
     * Get the content from the response
     *
     * @param response
     * @param charset
     * @return
     * @throws IOException
     */
    private static String getContent(HttpResponse response, Charset charset) throws IOException {
        try (StringWriter content = new StringWriter(SW_BUFFER_10K);
                InputStream is = response.getEntity().getContent();
                InputStreamReader isr = new InputStreamReader(is, charset == null ? Charset.defaultCharset() : charset);
                BufferedReader br = new BufferedReader(isr)) {
            String line = br.readLine();
            while (line != null) {
                content.write(line);
                line = br.readLine();
            }

            content.flush();
            return content.toString();
        }
    }
}
