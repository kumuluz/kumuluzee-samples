package com.kumuluz.ee.samples.tutorial.orders.api.v1.exceptionmappers;


import com.kumuluz.ee.samples.tutorial.orders.api.v1.dtos.Error;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
@ApplicationScoped
public class NotFoundExceptionMapper implements ExceptionMapper<NotFoundException> {

    @Override
    public Response toResponse(NotFoundException e) {

        Error error = new Error();
        error.setStatus(404);
        error.setCode("resource.not.found");
        error.setMessage(e.getMessage());

        return Response
                .status(Response.Status.NOT_FOUND)
                .entity(error)
                .build();
    }
}
