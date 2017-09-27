package com.kumuluz.ee.samples.tutorial.customers.api.v1;

import javax.annotation.security.DeclareRoles;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/v1")
@DeclareRoles({"user", "admin"})
public class CustomerApplication extends Application {
}
