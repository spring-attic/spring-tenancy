package org.springframework.tenancy.datasource;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

public abstract class AbstractDatabaseSwitchingDataSource implements DataSource {

	public static enum Language {
		HSQL("SET SCHEMA", "'"), MYSQL("USE", "`");

		private final String switchCommand;
		private final String quoteChar;

		private Language(String switchCommand, String quoteChar) {
			this.switchCommand = switchCommand;
			this.quoteChar = quoteChar;
		}

		public String switchDatabase(String dbName) {
			return switchCommand + " " + quoteChar + dbName + quoteChar + ";";
		}

	};

	private DataSource wrappedDataSource;
	private Language language = Language.MYSQL;

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
