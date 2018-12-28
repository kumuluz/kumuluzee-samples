package com.kumuluz.ee.samples.swagger.v1;

import io.swagger.annotations.Info;
import io.swagger.annotations.OAuth2Definition;
import io.swagger.annotations.SecurityDefinition;
import io.swagger.annotations.SwaggerDefinition;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * CustomerApplication class
 *
 * @author Zvone Gazvoda
 * @since 2.5.0
 */
@ApplicationPath("v1/customers")
//@DeclareRoles({"user"})
@SwaggerDefinition(info = @Info(title = "CustomersAPI", version = "v1.0.0"), host = "localhost:8080", securityDefinition =
@SecurityDefinition(
        oAuth2Definitions = {
                @OAuth2Definition(
                        key = "access_code",
                        flow = OAuth2Definition.Flow.ACCESS_CODE,
                        tokenUrl = "https://keycloak.kumuluz.com/auth/realms/customers-realm/protocol/openid-connect/token",
                        authorizationUrl = "https://keycloak.kumuluz.com/auth/realms/customers-realm/protocol/openid-connect/auth",
                        description = "Keycloak Auth",
                        scopes = {}
                ),
                @OAuth2Definition(
                        key = "password",
                        flow = OAuth2Definition.Flow.PASSWORD,
                        tokenUrl = "https://keycloak.kumuluz.com/auth/realms/customers-realm/protocol/openid-connect/token",
                        description = "Keycloak Auth",
                        scopes = {}
                ),
                @OAuth2Definition(
                        key = "application",
                        flow = OAuth2Definition.Flow.APPLICATION,
                        tokenUrl = "https://keycloak.kumuluz.com/auth/realms/customers-realm/protocol/openid-connect/token",
                        description = "Keycloak Auth",
                        scopes = {}
                )
        }
))
public class CustomerApplication extends Application {

}