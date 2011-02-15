package org.springframework.tenancy.datasource;

import java.sql.SQLException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.dbcp.BasicDataSource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "/applicationContext-testDBSwitching.xml" })
@Transactional()
public class DatabaseSwitchingDataSourceTest {

	@PersistenceContext
	protected EntityManager entityManager;

	@Autowired
	private BasicDataSource dataSource;

	@Autowired
	private ThreadlocalDatabaseSwitchingDataSource switchingDataSource;

	private final String DB1 = "DB1";
	private final String DB2 = "DB2";

	private final String CREATE_TABLE_STMT = "CREATE TABLE DOMAINOBJECT (ID INTEGER PRIMARY KEY, STRING CHAR(50)) GRANT ALL ON DOMAINOBJECT TO SA;";

	// I don't really want this to be a test, just a before that does not run under a transaction, however, I can't make
	// that work (@BeforeTransaction). This means that we must run all tests in the class.
	@Rollback(false)
	@Test
	public void setupDB1() throws SQLException {
		dataSource.getConnection().createStatement().execute("create schema " + DB1 + " " + CREATE_TABLE_STMT);

		switchingDataSource.setDatabaseName(DB1);
		DomainObject do1 = getMockDomainObject("db1");
		entityManager.persist(do1);
		DomainObject result = (DomainObject) entityManager.createQuery("select do from DomainObject do;")
				.getResultList().get(0);
		Assert.assertNotNull(result);
		Assert.assertEquals("db1", result.getString());
		entityManager.flush();

	}

	// I don't really want this to be a test, just a before that does not run under a transaction, however, I can't make
	// that work (@BeforeTransaction). This means that we must run all tests in the class.
	@Rollback(false)
	@Test
	public void setupDB2() throws SQLException {
		dataSource.getConnection().createStatement().execute("create schema " + DB2 + " " + CREATE_TABLE_STMT);

		switchingDataSource.setDatabaseName(DB2);
		entityManager.persist(getMockDomainObject("db2"));
		DomainObject result = (DomainObject) entityManager.createQuery("select do from DomainObject do;")
				.getResultList().get(0);
		Assert.assertNotNull(result);
		Assert.assertEquals("db2", result.getString());
		entityManager.flush();

	}

	@Test
	public void testGetFromD2() {
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

	private DomainObject getMockDomainObject(String data) {
		DomainObject result = new DomainObject();
		result.setId(nextId++);
		result.setString(data);
		return result;
	}
}
