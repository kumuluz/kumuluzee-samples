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
package com.kumuluz.ee.samples;

import com.kumuluz.ee.samples.models.Customer;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;


/**
 * @author Urban Malc
 * @author Jan Meznarič
 * @since 2.5.0
 */
@WebServlet(urlPatterns = {"/configServlet"})
@RequestScoped
public class ConfigServlet extends HttpServlet {

    @Inject
    private Config injectedConfig;

    @Inject
    @ConfigProperty(name = "mp.exampleString")
    private String injectedString;

    @Inject
    @ConfigProperty(name = "mp.non-existent-string", defaultValue = "Property does not exist!")
    private String nonExistentString;

    @Inject
    @ConfigProperty(name = "mp.non-existent-string")
    private Optional<String> nonExistentStringOpt;

    @Inject
    @ConfigProperty(name = "mp.exampleCustomer")
    private Customer customer;

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter res = response.getWriter();

        Config config = ConfigProvider.getConfig();
        res.println(config.getValue("mp.exampleString", String.class));

        res.println(injectedConfig.getValue("mp.exampleBoolean", boolean.class));

        res.println(injectedString);
        res.println(nonExistentString);

        res.println(nonExistentStringOpt.orElse("Empty Optional"));

        res.println(customer);
    }
}
