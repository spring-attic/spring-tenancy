/*******************************************************************************
 * Copyright (c) 2010, 2011 SpringSource, a division of VMware 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
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

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Statement;
import java.util.logging.Logger;

import javax.sql.DataSource;

/**
 * Abstract implementation of a data source that switches the database name whenever a 
 * connection is provided.
 * 
 * @author Clint Morgan (Tasktop Technologies Inc.)
 * @author Lucas Panjer (Tasktop Technologies Inc.)
 */
public abstract class AbstractDatabaseSwitchingDataSource implements DataSource {

	public static enum Language {
		HSQL("SET SCHEMA ", "'"), MYSQL("USE ", "`"), ORACLE("ALTER SESSION SET CURRENT_SCHEMA=", "\"");

		private final String switchCommand;
		private final String quoteChar;

		private Language(String switchCommand, String quoteChar) {
			this.switchCommand = switchCommand;
			this.quoteChar = quoteChar;
		}

		public String switchDatabase(String dbName) {
			String query = switchCommand + quoteChar + dbName + quoteChar; 
			if(this.name().equals(ORACLE.name())) {
				return query;
			}
			else {
				return query + ";";
			}
		}
	};

	protected DataSource wrappedDataSource;
	protected Language language = Language.MYSQL;

	public void setLanguage(Language l) {
		this.language = l;
	}

	@Override
	public PrintWriter getLogWriter() throws SQLException {
		return wrappedDataSource.getLogWriter();
	}

	@Override
	public int getLoginTimeout() throws SQLException {
		return wrappedDataSource.getLoginTimeout();
	}

	@Override
	public void setLogWriter(PrintWriter arg0) throws SQLException {
		wrappedDataSource.setLogWriter(arg0);
	}

	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return wrappedDataSource.getParentLogger();
	}

	@Override
	public void setLoginTimeout(int arg0) throws SQLException {
		wrappedDataSource.setLoginTimeout(arg0);
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return wrappedDataSource.isWrapperFor(iface);
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return wrappedDataSource.unwrap(iface);
	}

	@Override
	public Connection getConnection() throws SQLException {
		Connection con = wrappedDataSource.getConnection();
		return switchDatabase(con);
	}

	protected Connection switchDatabase(Connection con) throws SQLException {
		String databaseName = getDatabaseName();
		if (databaseName != null) {
			Statement s = con.createStatement();
			try {
				s.execute(language.switchDatabase(databaseName));
			} catch (SQLException e) { 
				con.close();
				throw e;
			} finally {
				s.close();
			}
		}
		return con;
	}

	@Override
	public Connection getConnection(String username, String password) throws SQLException {
		Connection con = wrappedDataSource.getConnection(username, password);
		return switchDatabase(con);
	}

	public void setWrappedDataSource(DataSource wrappedDataSource) {
		this.wrappedDataSource = wrappedDataSource;
	}

	abstract protected String getDatabaseName();
}
