/*
 *  Copyright (c) 2014-2017 Kumuluz and/or its affiliates
 *  and other contributors as indicated by the @author tags and
 *  the contributor list.
 *
 *  Licensed under the MIT License (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  https://opensource.org/licenses/MIT
 *
 *  The software is provided "AS IS", WITHOUT WARRANTY OF ANY KIND, express or
 *  implied, including but not limited to the warranties of merchantability,
 *  fitness for a particular purpose and noninfringement. in no event shall the
 *  authors or copyright holders be liable for any claim, damages or other
 *  liability, whether in an action of contract, tort or otherwise, arising from,
 *  out of or in connection with the software or the use or other dealings in the
 *  software. See the License for the specific language governing permissions and
 *  limitations under the License.
*/
package com.kumuluz.ee.samples.discovery.discover.servlet;

import com.kumuluz.ee.discovery.annotations.DiscoverService;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author Urban Malc
 */
@WebServlet("DiscoverServlet")
public class DiscoverServlet extends HttpServlet {

    @Inject
    @DiscoverService(value = "customer-service", version = "*", environment = "dev")
    private URL url;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        response.getWriter().println("Discovered instance on " + url);

        response.getWriter().println("Sending request for customer list ...");
        URL serviceUrl = new URL(url.toString() + "/v1/customers");
        HttpURLConnection conn = (HttpURLConnection) serviceUrl.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder receivedResponse = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            receivedResponse.append(line);
        }
        rd.close();

        response.getWriter().println("Received response: " + receivedResponse.toString());
    }
}
