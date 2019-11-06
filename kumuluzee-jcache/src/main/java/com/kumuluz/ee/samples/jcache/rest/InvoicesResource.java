/*
 *  Copyright (c) 2014-2019 Kumuluz and/or its affiliates
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

package com.kumuluz.ee.samples.jcache.rest;

import com.kumuluz.ee.samples.jcache.rest.lib.InvoiceData;
import com.kumuluz.ee.samples.jcache.rest.services.InvoiceService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author cen1
 * @since 3.6.0
 */
@RequestScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("/invoices")
public class InvoicesResource {

    @Inject
    private InvoiceService invoiceService;

    //JCache annotations
    @POST
    @Path("/{id}")
    public Response addData(@PathParam("id") String id, InvoiceData in) {
        InvoiceData data = invoiceService.putInvoice(id, in);
        return Response.ok(data).build();
    }

    @GET
    @Path("/{id}")
    public Response getData(@PathParam("id") String id) {
        InvoiceData data = invoiceService.getInvoice(id);
        return Response.ok(data).build();
    }

    //Programmatic API
    @GET
    @Path("/{id}/default")
    public Response getInvoicesDefault(@PathParam("id") String id) {
        InvoiceData data = invoiceService.getInvoiceDefault(id);
        return Response.ok(data).build();
    }

    @GET
    @Path("/{id}/my")
    public Response getInvoicesMy(@PathParam("id") String id) {
        InvoiceData data = invoiceService.getInvoiceMy(id);
        return Response.ok(data).build();
    }
}
