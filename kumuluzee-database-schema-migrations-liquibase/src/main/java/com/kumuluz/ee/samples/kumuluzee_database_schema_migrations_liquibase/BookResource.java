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
package com.kumuluz.ee.samples.kumuluzee_database_schema_migrations_liquibase;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * @author Din Music
 * @since 3.13.0
 */
@Path("books")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped
public class BookResource {

    @Inject
    private BookService bookService;

    @GET
    @Path("{bookId}")
    public Response getBook(@PathParam("bookId") String bookId) {
        Book book = bookService.getBook(bookId);
        return (book != null)
                ? Response.ok(book).build()
                : Response.status(Response.Status.NOT_FOUND).build();
    }

    @GET
    public Response getBooks() {
        List<Book> books = bookService.getBooks();
        return Response.ok(books).build();
    }

    @POST
    public Response addBook(Book book) {
        Book addedBook = bookService.addBook(book);
        return Response.ok(addedBook).build();
    }

    @DELETE
    @Path("{bookId}")
    public Response deleteBook(@PathParam("bookId") String bookId) {
        bookService.deleteBook(bookId);
        return Response.noContent().build();
    }

}
