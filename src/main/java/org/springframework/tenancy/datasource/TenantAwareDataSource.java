package org.springframework.tenancy.datasource;

import org.springframework.tenancy.context.TenancyContextHolder;

public class TenantAwareDataSource extends AbstractDatabaseSwitchingDataSource {

	@Override
	protected String getDatabaseName() {
		String dbName = null;
		if (TenancyContextHolder.getContext() != null && TenancyContextHolder.getContext().getTenant() != null) {
			dbName = TenancyContextHolder.getContext().getTenant().getIdentity().toString();
		}
		return dbName;
	}
}
