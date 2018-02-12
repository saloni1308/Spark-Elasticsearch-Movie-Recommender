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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AndroidBrowserUserAgentSelector implements IUserAgentSelector {

    private static final Random RANDOM = new Random();
    private static final List<String> USER_AGENTS = new ArrayList<>();

    static {
        USER_AGENTS.add("Mozilla/5.0 (Linux; U; Android {v}; fr-fr; Nexus One Build/FRF91) AppleWebKit/5{b}.{c} (KHTML, like Gecko) Version/{a}.{a} Mobile Safari/5{b}.{c}");
        USER_AGENTS.add("Mozilla/5.0 (Linux; U; Android {v}; fr-fr; Dell Streak Build/Donut AppleWebKit/5{b}.{c}+ (KHTML, like Gecko) Version/3.{a}.2 Mobile Safari/ 5{b}.{c}.1");
        USER_AGENTS.add("Mozilla/5.0 (Linux; U; Android 4.{v}; fr-fr; LG-L160L Build/IML74K) AppleWebkit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30");
        USER_AGENTS.add("Mozilla/5.0 (Linux; U; Android 4.{v}; fr-fr; HTC Sensation Build/IML74K) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30");
        USER_AGENTS.add("Mozilla/5.0 (Linux; U; Android {v}; en-gb) AppleWebKit/999+ (KHTML, like Gecko) Safari/9{b}.{a}");
        USER_AGENTS.add("Mozilla/5.0 (Linux; U; Android {v}.5; fr-fr; HTC_IncredibleS_S710e Build/GRJ{b}) AppleWebKit/5{b}.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/5{b}.1");
        USER_AGENTS.add("Mozilla/5.0 (Linux; U; Android 2.{v}; fr-fr; HTC Vision Build/GRI{b}) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1");
        USER_AGENTS.add("Mozilla/5.0 (Linux; U; Android {v}.4; fr-fr; HTC Desire Build/GRJ{b}) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1");
        USER_AGENTS.add("Mozilla/5.0 (Linux; U; Android 2.{v}; fr-fr; T-Mobile myTouch 3G Slide Build/GRI40) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1");
        USER_AGENTS.add("Mozilla/5.0 (Linux; U; Android {v}.3; fr-fr; HTC_Pyramid Build/GRI40) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1");
        USER_AGENTS.add("Mozilla/5.0 (Linux; U; Android 2.{v}; fr-fr; HTC_Pyramid Build/GRI40) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari");
        USER_AGENTS.add("Mozilla/5.0 (Linux; U; Android 2.{v}; fr-fr; HTC Pyramid Build/GRI40) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/5{b}.1");
        USER_AGENTS.add("Mozilla/5.0 (Linux; U; Android 2.{v}; fr-fr; LG-LU3000 Build/GRI40) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/5{b}.1");
        USER_AGENTS.add("Mozilla/5.0 (Linux; U; Android 2.{v}; fr-fr; HTC_DesireS_S510e Build/GRI{a}) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/{c}.1");
        USER_AGENTS.add("Mozilla/5.0 (Linux; U; Android 2.{v}; fr-fr; HTC_DesireS_S510e Build/GRI40) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile");
        USER_AGENTS.add("Mozilla/5.0 (Linux; U; Android {v}.3; fr-fr; HTC Desire Build/GRI{a}) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1");
        USER_AGENTS.add("Mozilla/5.0 (Linux; U; Android 2.{v}; fr-fr; HTC Desire Build/FRF{a}) AppleWebKit/533.1 (KHTML, like Gecko) Version/{a}.0 Mobile Safari/533.1");
        USER_AGENTS.add("Mozilla/5.0 (Linux; U; Android {v}; fr-lu; HTC Legend Build/FRF91) AppleWebKit/533.1 (KHTML, like Gecko) Version/{a}.{a} Mobile Safari/{c}.{a}");
        USER_AGENTS.add("Mozilla/5.0 (Linux; U; Android {v}; fr-fr; HTC_DesireHD_A9191 Build/FRF91) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1");
        USER_AGENTS.add("Mozilla/5.0 (Linux; U; Android {v}.1; fr-fr; HTC_DesireZ_A7{c} Build/FRG83D) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/{c}.{a}");
        USER_AGENTS.add("Mozilla/5.0 (Linux; U; Android {v}.1; en-gb; HTC_DesireZ_A7272 Build/FRG83D) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/{c}.1");
        USER_AGENTS.add("Mozilla/5.0 (Linux; U; Android {v}; fr-fr; LG-P5{b} Build/FRG83) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1");
    }

    /**
     * Get a random user agent to use for web requests
     *
     * @return a random user agent
     */
    @Override
    public String getUserAgent() {
        final int i = RANDOM.nextInt(USER_AGENTS.size());
        return USER_AGENTS.get(i);
    }
}
