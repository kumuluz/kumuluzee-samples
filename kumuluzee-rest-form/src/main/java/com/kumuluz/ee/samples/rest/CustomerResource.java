/*
 *  Copyright (c) 2014-2021 Kumuluz and/or its affiliates
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
package com.kumuluz.ee.samples.rest;

import org.apache.commons.codec.binary.Hex;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * @author cen1
 * @since 3.13.0
 */
@RequestScoped
@Path("/customers")
public class CustomerResource {

    @Context
    protected UriInfo uriInfo;

    @POST
    @Path("/pdf")
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    @Produces(MediaType.APPLICATION_JSON)
    public Response uploadPdfFile(@FormDataParam("file") InputStream fileInputStream,
                                  @FormDataParam("file") FormDataContentDisposition fileMetaData) throws Exception {

        String tmpdir = System.getProperty("java.io.tmpdir");

        java.nio.file.Path finalPath = Paths.get(tmpdir, fileMetaData.getFileName());

        try (final OutputStream out = Files.newOutputStream(finalPath)) {
            fileInputStream.transferTo(out);
        }

        //Re-read file from disk to verify
        final MessageDigest digest = MessageDigest.getInstance("SHA-1");
        final byte[] hashbytes = digest.digest(Files.readAllBytes(finalPath));
        String hash = Hex.encodeHexString(hashbytes);

        return Response.ok(new PathResponse(finalPath.toString(), hash)).build();
    }
}
