package org.springframework.tenancy.datasource;

public class ThreadlocalDatabaseSwitchingDataSource extends AbstractDatabaseSwitchingDataSource {

	private final ThreadLocal<String> databaseName = new ThreadLocal<String>();

	public void setDatabaseName(String databaseName) {
		this.databaseName.set(databaseName);
	}

	public void clearDatabaseName() {
		this.databaseName.set(null);
	}

	@Override
	protected String getDatabaseName() {
		return databaseName.get();
	}

}
