package com.kumuluz.ee.samples.swagger.v1;

import com.kumuluz.ee.samples.swagger.v2.CustomerResourceV2;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;

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
@SwaggerDefinition(info = @Info(title = "CustomersAPI", version = "v1.0.0"), basePath = "v1", host = "localhost:8080")
public class CustomerApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {

        Set<Class<?>> classes = new HashSet<>();

        classes.add(CustomerResource.class);
        return classes;
    }
}