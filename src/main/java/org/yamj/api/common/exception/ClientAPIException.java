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
package org.yamj.api.common.exception;

import javax.xml.ws.WebServiceException;
import org.yamj.api.common.http.DigestedResponse;

public class ClientAPIException extends WebServiceException {

    private static final long serialVersionUID = 278660717634380289L;

    private final DigestedResponse response;

    public ClientAPIException(final DigestedResponse response) {
        super();
        this.response = response;
    }

    public ClientAPIException(final DigestedResponse response, final Throwable cause) {
        super(cause);
        this.response = response;
    }

    public DigestedResponse getResponse() {
        return response;
    }
}
