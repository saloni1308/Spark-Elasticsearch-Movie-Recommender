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

import java.io.Serializable;

/**
 * Contains the content of the digested response stream body and its HTTP status code.
 */
public class DigestedResponse implements Serializable {

    private static final long serialVersionUID = 2L;

    private int statusCode;
    private String content;

    public DigestedResponse() {
        super();
    }

    public DigestedResponse(final int statusCode, final String content) {
        super();
        this.statusCode = statusCode;
        this.content = content;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(final int statusCode) {
        this.statusCode = statusCode;
    }

    public String getContent() {
        return content;
    }

    public void setContent(final String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append(this.statusCode)
                .append(" ")
                .append(this.content)
                .toString();
    }
}
