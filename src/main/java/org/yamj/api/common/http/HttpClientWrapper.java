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

import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;

@SuppressWarnings("deprecation")
public class HttpClientWrapper implements CommonHttpClient, Closeable {

    private final HttpClient httpClient;
    private IUserAgentSelector userAgentSelector;

    public HttpClientWrapper(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public HttpClientWrapper(HttpClient httpClient, IUserAgentSelector userAgentSelector) {
        this.httpClient = httpClient;
        this.userAgentSelector = userAgentSelector;
    }

    protected static HttpHost determineTarget(HttpUriRequest request) throws ClientProtocolException {
        HttpHost target = null;
        URI requestURI = request.getURI();
        if (requestURI.isAbsolute()) {
            target = URIUtils.extractHost(requestURI);
            if (target == null) {
                throw new ClientProtocolException("URI does not specify a valid host name: " + requestURI);
            }
        }
        return target;
    }

    protected static URI toURI(URL url) {
        try {
            return url.toURI();
        } catch (URISyntaxException ex) {
            throw new IllegalArgumentException("Invalid URL: " + url, ex);
        }
    }

    @Override
    public void setUserAgentSelector(IUserAgentSelector userAgentSelector) {
        this.userAgentSelector = userAgentSelector;
    }

    @SuppressWarnings("unused")
    protected void prepareRequest(HttpUriRequest request) throws ClientProtocolException {
        if (userAgentSelector != null) {
            final Header[] headers = request.getHeaders(HTTP.USER_AGENT);
            if (headers == null || headers.length == 0) {
                request.setHeader(HTTP.USER_AGENT, userAgentSelector.getUserAgent());
            }
        }
    }

    @SuppressWarnings("unused")
    protected void prepareRequest(HttpHost target, HttpRequest request) throws ClientProtocolException {
        if (userAgentSelector != null) {
            final Header[] headers = request.getHeaders(HTTP.USER_AGENT);
            if (headers == null || headers.length == 0) {
                request.setHeader(HTTP.USER_AGENT, userAgentSelector.getUserAgent());
            }
        }
    }

    @Override
    public DigestedResponse requestContent(URL url) throws IOException {
        return requestContent(url, null);
    }

    @Override
    public DigestedResponse requestContent(URL url, Charset charset) throws IOException {
        return requestContent(toURI(url), charset);
    }

    @Override
    public DigestedResponse requestContent(String uri) throws IOException {
        return requestContent(uri, null);
    }

    @Override
    public DigestedResponse requestContent(String uri, Charset charset) throws IOException {
        final HttpGet httpGet = new HttpGet(uri);
        return requestContent(httpGet, charset);
    }

    @Override
    public DigestedResponse requestContent(URI uri) throws IOException {
        return requestContent(uri, null);
    }

    @Override
    public DigestedResponse requestContent(URI uri, Charset charset) throws IOException {
        final HttpGet httpGet = new HttpGet(uri);
        return requestContent(httpGet, charset);
    }

    @Override
    public DigestedResponse requestContent(HttpGet httpGet) throws IOException {
        return requestContent(httpGet, null);
    }

    @Override
    public DigestedResponse requestContent(HttpGet httpGet, Charset charset) throws IOException {
        return DigestedResponseReader.requestContent(this, httpGet, charset);
    }

    @Override
    public DigestedResponse postContent(URL url, HttpEntity entity) throws IOException {
        return postContent(url, entity, null);
    }

    @Override
    public DigestedResponse postContent(URL url, HttpEntity entity, Charset charset) throws IOException {
        return postContent(toURI(url), entity, charset);
    }

    @Override
    public DigestedResponse postContent(String uri, HttpEntity entity) throws IOException {
        return postContent(uri, entity, null);
    }

    @Override
    public DigestedResponse postContent(String uri, HttpEntity entity, Charset charset) throws IOException {
        final HttpPost httpPost = new HttpPost(uri);
        httpPost.setEntity(entity);
        return postContent(httpPost, charset);
    }

    @Override
    public DigestedResponse postContent(URI uri, HttpEntity entity) throws IOException {
        return postContent(uri, entity, null);
    }

    @Override
    public DigestedResponse postContent(URI uri, HttpEntity entity, Charset charset) throws IOException {
        final HttpPost httpPost = new HttpPost(uri);
        httpPost.setEntity(entity);
        return postContent(httpPost, charset);
    }

    @Override
    public DigestedResponse postContent(HttpPost httpPost) throws IOException {
        return postContent(httpPost, null);
    }

    @Override
    public DigestedResponse postContent(HttpPost httpPost, Charset charset) throws IOException {
        return DigestedResponseReader.postContent(this, httpPost, charset);
    }

    @Override
    public DigestedResponse deleteContent(URL url) throws IOException {
        return deleteContent(url, null);
    }

    @Override
    public DigestedResponse deleteContent(URL url, Charset charset) throws IOException {
        return deleteContent(toURI(url), charset);
    }

    @Override
    public DigestedResponse deleteContent(String uri) throws IOException {
        return deleteContent(uri, null);
    }

    @Override
    public DigestedResponse deleteContent(String uri, Charset charset) throws IOException {
        final HttpDelete httpDelete = new HttpDelete(uri);
        return deleteContent(httpDelete, charset);
    }

    @Override
    public DigestedResponse deleteContent(URI uri) throws IOException {
        return deleteContent(uri, null);
    }

    @Override
    public DigestedResponse deleteContent(URI uri, Charset charset) throws IOException {
        final HttpDelete httpDelete = new HttpDelete(uri);
        return deleteContent(httpDelete, charset);
    }

    @Override
    public DigestedResponse deleteContent(HttpDelete httpDelete) throws IOException {
        return deleteContent(httpDelete, null);
    }

    @Override
    public DigestedResponse deleteContent(HttpDelete httpDelete, Charset charset) throws IOException {
        return DigestedResponseReader.deleteContent(this, httpDelete, charset);
    }

    @Override
    public HttpEntity requestResource(URL url) throws IOException {
        return requestResource(toURI(url));
    }

    @Override
    public HttpEntity requestResource(String uri) throws IOException {
        final HttpGet httpGet = new HttpGet(uri);
        return requestResource(httpGet);
    }

    @Override
    public HttpEntity requestResource(URI uri) throws IOException {
        final HttpGet httpGet = new HttpGet(uri);
        return requestResource(httpGet);
    }

    @Override
    public HttpEntity requestResource(HttpGet httpGet) throws IOException {
        return execute(httpGet).getEntity();
    }

    @Override
    public HttpEntity postResource(URL url, HttpEntity entity) throws IOException {
        return postResource(toURI(url), entity);
    }

    @Override
    public HttpEntity postResource(String uri, HttpEntity entity) throws IOException {
        final HttpPost httpPost = new HttpPost(uri);
        httpPost.setEntity(entity);
        return postResource(httpPost);
    }

    @Override
    public HttpEntity postResource(URI uri, HttpEntity entity) throws IOException {
        final HttpPost httpPost = new HttpPost(uri);
        httpPost.setEntity(entity);
        return postResource(httpPost);
    }

    @Override
    public HttpEntity postResource(HttpPost httpPost) throws IOException {
        return execute(httpPost).getEntity();
    }

    @Override
    public HttpEntity deleteResource(URL url) throws IOException {
        return deleteResource(toURI(url));
    }

    @Override
    public HttpEntity deleteResource(String uri) throws IOException {
        final HttpDelete httpDelete = new HttpDelete(uri);
        return deleteResource(httpDelete);
    }

    @Override
    public HttpEntity deleteResource(URI uri) throws IOException {
        final HttpDelete httpDelete = new HttpDelete(uri);
        return deleteResource(httpDelete);
    }

    @Override
    public HttpEntity deleteResource(HttpDelete httpDelete) throws IOException {
        return execute(httpDelete).getEntity();
    }

    @Override
    public HttpResponse execute(HttpUriRequest request) throws IOException {
        prepareRequest(request);
        return httpClient.execute(request);
    }

    @Override
    public HttpResponse execute(HttpUriRequest request, HttpContext context) throws IOException {
        prepareRequest(request);
        return httpClient.execute(request, context);
    }

    @Override
    public HttpResponse execute(HttpHost target, HttpRequest request) throws IOException {
        prepareRequest(target, request);
        return httpClient.execute(target, request);
    }

    @Override
    public <T> T execute(HttpUriRequest request, ResponseHandler<? extends T> responseHandler) throws IOException, ClientProtocolException {
        prepareRequest(request);
        return httpClient.execute(request, responseHandler);
    }

    @Override
    public HttpResponse execute(HttpHost target, HttpRequest request, HttpContext context) throws IOException {
        prepareRequest(target, request);
        return httpClient.execute(target, request, context);
    }

    @Override
    public <T> T execute(HttpUriRequest request, ResponseHandler<? extends T> responseHandler, HttpContext context) throws IOException, ClientProtocolException {
        prepareRequest(request);
        return httpClient.execute(request, responseHandler, context);
    }

    @Override
    public <T> T execute(HttpHost target, HttpRequest request, ResponseHandler<? extends T> responseHandler) throws IOException, ClientProtocolException {
        prepareRequest(target, request);
        return httpClient.execute(target, request, responseHandler);
    }

    @Override
    public <T> T execute(HttpHost target, HttpRequest request, ResponseHandler<? extends T> responseHandler, HttpContext context) throws IOException, ClientProtocolException {
        prepareRequest(target, request);
        return httpClient.execute(target, request, responseHandler, context);
    }

    /**
     * Will be removed when removed in HttpClient interface.
     *
     * @return
     * @deprecated
     */
    @Override
    @Deprecated
    public ClientConnectionManager getConnectionManager() {
        return httpClient.getConnectionManager();
    }

    /**
     * Will be removed when removed in HttpClient interface.
     *
     * @return
     * @deprecated
     */
    @Override
    @Deprecated
    public HttpParams getParams() {
        return httpClient.getParams();
    }

    @Override
    public void close() throws IOException {
        if (httpClient instanceof Closeable) {
            ((Closeable) this.httpClient).close();
        }
    }
}
