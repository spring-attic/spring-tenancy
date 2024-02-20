/*******************************************************************************
 * Copyright (c) 2010, 2011 SpringSource, a division of VMware 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Tasktop Technologies Inc. - initial API and implementation
 *******************************************************************************/
package org.springframework.tenancy.datasource;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.dbcp.BasicDataSource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * @author Clint Morgan (Tasktop Technologies Inc.)
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@Transactional
@DirtiesContext
public class DatabaseSwitchingDataSourceTest {

	@PersistenceContext
	protected EntityManager entityManager;

	@Autowired
	private BasicDataSource dataSource;

	@Autowired
	private PlatformTransactionManager transactionManager;

	@Autowired
	private ThreadlocalDatabaseSwitchingDataSource switchingDataSource;

	private static boolean initialized;

	private final String DB1 = "DB1";
	private final String DB2 = "DB2";

	private final String CREATE_TABLE_STMT = "CREATE TABLE DOMAINOBJECT (ID INTEGER PRIMARY KEY, STRING CHAR(50)) GRANT ALL ON DOMAINOBJECT TO SA;";

	@BeforeTransaction
	public void setUpDatabases() throws Exception {

		if (!initialized) {

			initialized = true;

			dataSource.getConnection().createStatement().execute("create schema " + DB1 + " " + CREATE_TABLE_STMT);
			dataSource.getConnection().createStatement().execute("create schema " + DB2 + " " + CREATE_TABLE_STMT);

			new TransactionTemplate(transactionManager).execute(new TransactionCallback<Void>() {
				public Void doInTransaction(TransactionStatus status) {
					setUpData(DB1, "db1");
					return null;
				}
			});
			new TransactionTemplate(transactionManager).execute(new TransactionCallback<Void>() {
				public Void doInTransaction(TransactionStatus status) {
					setUpData(DB2, "db2");
					return null;
				}
			});

		}

	}

	@Test
	public void testGetFromDB2() {
		switchingDataSource.setDatabaseName(DB2);
		DomainObject result = (DomainObject) entityManager.createQuery("select do from DomainObject do;")
				.getResultList().get(0);
		Assert.assertNotNull(result);
		Assert.assertEquals("db2", result.getString());
	}

	@Test
	public void testGetFromDB1() {
		switchingDataSource.setDatabaseName(DB1);
		DomainObject result = (DomainObject) entityManager.createQuery("select do from DomainObject do;")
				.getResultList().get(0);
		Assert.assertNotNull(result);
		Assert.assertEquals("db1", result.getString());
	}

	private static long nextId = 1;

	private void setUpData(String databaseName, String objectName) {

		switchingDataSource.setDatabaseName(databaseName);
		DomainObject do1 = getMockDomainObject(objectName);
		entityManager.persist(do1);
		DomainObject result = (DomainObject) entityManager.createQuery("select do from DomainObject do;")
				.getResultList().get(0);
		Assert.assertNotNull(result);
		Assert.assertEquals(objectName, result.getString());
		entityManager.flush();

	}

	private DomainObject getMockDomainObject(String data) {
		DomainObject result = new DomainObject();
		result.setId(nextId++);
		result.setString(data);
		return result;
	}
}
