package com.kumuluz.ee.samples.tutorial.orders.api.v1.producers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

@Provider
public class JacksonProducer implements ContextResolver<ObjectMapper> {

    private final ObjectMapper mapper;

    public JacksonProducer() {

        mapper = new ObjectMapper();

        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        mapper.setDateFormat(dateFormat);
    }

    @Override
    public ObjectMapper getContext(Class<?> aClass) {
        return mapper;
    }
}
