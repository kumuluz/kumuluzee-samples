/*
 *  Copyright (c) 2014-2018 Kumuluz and/or its affiliates
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
package com.kumuluz.ee.samples.jaxws.cxf;


import com.kumuluz.ee.jaxws.cxf.annotations.WsContext;
import com.kumuluz.ee.samples.jaxws.cxf.interceptors.WsInterceptor;
import com.kumuluz.ee.samples.jaxws.cxf.service.CustomersService;
import com.kumuluz.samples.jax_ws.cxf.customers._1.CustomerEndpoint;
import com.kumuluz.samples.jax_ws.cxf.customers._1.GetCustomers;
import com.kumuluz.samples.jax_ws.cxf.customers._1.GetCustomersResponse;
import org.apache.cxf.annotations.SchemaValidation;

import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.jws.HandlerChain;
import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;
import java.util.logging.Logger;

/**
 * @author gpor89
 * @since 3.0.0
 */
@WsContext(contextRoot = "/soap", urlPattern = "/customers/1.0")
@ApplicationScoped
@WebService(serviceName = "CustomerEndpoint", portName = "CustomerEndpointSOAP", targetNamespace = "http://kumuluz.com/samples/jax-ws/cxf/customers/1.0",
        endpointInterface = "com.kumuluz.samples.jax_ws.cxf.customers._1.CustomerEndpoint", wsdlLocation = "/wsdls/customers.wsdl")
@SchemaValidation
@Interceptors(WsInterceptor.class)
@HandlerChain(file = "/META-INF/handler-chains.xml")
public class CustomerEndpointBean implements CustomerEndpoint {

    private static final Logger LOG = Logger.getLogger(CustomerEndpointBean.class.getName());

    @Inject
    private CustomersService customersService;

    @Resource(name = "wsContext")
    private WebServiceContext webServiceContext;

    @Override
    public GetCustomersResponse getCustomers(final GetCustomers parameters) {

        String soapAction = (String) webServiceContext.getMessageContext().get("SOAPAction");
        LOG.info("Soap action: " + soapAction);

        GetCustomersResponse response = new GetCustomersResponse();
        response.getCustomers().addAll(customersService.getCustomers());

        return response;
    }
}
