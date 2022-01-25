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

import com.kumuluz.ee.database.schema.migrations.liquibase.LiquibaseContainer;
import com.kumuluz.ee.database.schema.migrations.liquibase.annotations.LiquibaseChangelog;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;

/**
 * @author Din Music
 * @since 3.13.0
 */
@RequestScoped
public class LiquibaseService {

    private static final Logger LOG = LogManager.getLogger(LiquibaseService.class.getName());

    @Inject
    @LiquibaseChangelog(jndiName = "jdbc/BooksDS")
    private LiquibaseContainer liquibaseContainer;

    public void reset() {

        Liquibase liquibase = liquibaseContainer.createLiquibase();

        // Retrieves contexts and labels from Liquibase configuration in KumuluzEE config file
        Contexts contexts = liquibaseContainer.getContexts();
        LabelExpression labels = liquibaseContainer.getLabels();

        try {
            liquibase.dropAll();
            liquibase.update(contexts, labels);
            liquibase.validate();

        } catch (Exception e) {
            LOG.error("Error while resetting database.", e);
        }
    }

    public void populate() {

        Liquibase liquibase = liquibaseContainer.createLiquibase();

        try {
            liquibase.update("populate");
        } catch (Exception e) {
            LOG.error("Error while populating database.", e);
        }
    }
}
