package com.kumuluz.ee.samples.openapi;

import io.swagger.oas.annotations.info.Info;
import io.swagger.oas.annotations.servers.Server;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * CustomerApplication class
 *
 * @author Zvone Gazvoda
 * @since 2.5.0
 */
@ApplicationPath("v1")
@Info(title = "CustomerApi", version = "v1.0.0")
public class CustomerApplication extends Application {

}
