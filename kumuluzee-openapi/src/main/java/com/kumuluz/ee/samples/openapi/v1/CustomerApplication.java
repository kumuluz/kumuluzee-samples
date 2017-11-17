package com.kumuluz.ee.samples.openapi.v1;

import io.swagger.oas.annotations.OpenAPIDefinition;
import io.swagger.oas.annotations.info.Info;
import io.swagger.oas.annotations.servers.Server;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * CustomerApplication class
 *
 * @author Zvone Gazvoda
 * @since 2.5.0
 */
@ApplicationPath("v1")
@OpenAPIDefinition(info = @Info(title = "CustomerApi", version = "v1.0.0"), servers = @Server(url = "http://localhost:8080/v1"))
public class CustomerApplication extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();

        classes.add(CustomerResource.class);

        return classes;
    }
}
