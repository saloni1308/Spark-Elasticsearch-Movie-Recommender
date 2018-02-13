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
package org.yamj.api.common.tools;

import org.yamj.api.common.exception.ApiException;
import org.yamj.api.common.http.DigestedResponse;

public final class ResponseTools {

    private ResponseTools() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static boolean isOK(final DigestedResponse response) {
        return isOK(response.getStatusCode());
    }

    public static boolean isOK(final ApiException apiException) {
        return isOK(apiException.getResponseCode());
    }


    public static boolean isOK(final int statusCode) {
        return statusCode == 200;
    }

    public static boolean isNotOK(final DigestedResponse response) {
        return !isOK(response);
    }

    public static boolean isNotOK(final ApiException apiException) {
        return isNotOK(apiException.getResponseCode());
    }

    public static boolean isNotOK(final int statusCode) {
        return !isOK(statusCode);
    }

    public static boolean isTemporaryError(final DigestedResponse response) {
        return isTemporaryError(response.getStatusCode());
    }

    public static boolean isTemporaryError(final ApiException apiException) {
        return isTemporaryError(apiException.getResponseCode());
    }

    public static boolean isTemporaryError(final int statusCode) { //NOSONAR
        switch (statusCode) {
            case 408:
            case 419:
            case 423:
            case 429:
            case 500:
            case 502:
            case 503:
            case 504:
            case 507:
            case 509:
            case 598:
            case 599:
                return true;
            default:
                return false;
        }
    }
}
