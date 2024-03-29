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
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * @author Din Music
 * @since 3.13.0
 */
@RequestScoped
public class BookService {

    @PersistenceContext
    private EntityManager em;

    public Book getBook(String id) {
        return em.find(Book.class, id);
    }

    public List<Book> getBooks() {
        return em.createNamedQuery(Book.FIND_ALL, Book.class).getResultList();
    }

    public Book addBook(Book book) {

        try {
            beginTx();
            em.persist(book);
            commitTx();

        } catch (Exception e) {
            rollbackTx();
        }
        return book;
    }

    public void deleteBook(String id) {

        Book book = getBook(id);

        if (book != null) {

            try {
                beginTx();
                em.remove(book);
                commitTx();

            } catch (Exception e) {
                rollbackTx();
            }
        }
    }

    private void beginTx() {
        if (!em.getTransaction().isActive())
            em.getTransaction().begin();
    }

    private void commitTx() {
        if (em.getTransaction().isActive())
            em.getTransaction().commit();
    }

    private void rollbackTx() {
        if (em.getTransaction().isActive())
            em.getTransaction().rollback();
    }

}
