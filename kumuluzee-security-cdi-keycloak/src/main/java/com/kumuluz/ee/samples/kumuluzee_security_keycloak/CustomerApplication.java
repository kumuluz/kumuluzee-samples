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
package com.kumuluz.ee.samples.kumuluzee_security_keycloak;

import javax.annotation.security.DeclareRoles;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * @author Benjamin Kastelic
 * @since 2.3.0
 */
@DeclareRoles({"user", "admin"})
//@Keycloak(json =
//        "{" +
//        "  \"realm\": \"customers\"," +
//        "  \"bearer-only\": true," +
//        "  \"auth-server-url\": \"https://localhost:8082/auth\"," +
//        "  \"ssl-required\": \"external\"," +
//        "  \"resource\": \"customers-api\"" +
//        "}"
//)
@ApplicationPath("v1")
public class CustomerApplication extends Application {
}
