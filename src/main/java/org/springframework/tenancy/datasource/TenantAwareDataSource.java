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

import org.springframework.tenancy.context.TenancyContext;
import org.springframework.tenancy.context.TenancyContextHolder;
import org.springframework.tenancy.core.Tenant;

/**
 * A data source that is tenant aware, that switches the database name based on the current tenant.
 * 
 * @author Clint Morgan (Tasktop Technologies Inc.)
 * @author David Green (Tasktop Technologies Inc.)
 */
public class TenantAwareDataSource extends AbstractDatabaseSwitchingDataSource {

	@Override
	protected String getDatabaseName() {
		String dbName = null;
		TenancyContext tenancyContext = TenancyContextHolder.getContext();
		if (tenancyContext.getTenant() != null) {
			dbName = computeDatabaseName(tenancyContext.getTenant());
		}
		return dbName;
	}

	/**
	 * Compute the database name based on a tenant.  The default implementation uses the {@link Tenant#getIdentity() tenant identity}
	 * as the database name.
	 * @param tenant the tenant, must not be null
	 * @return the database name
	 */
	protected String computeDatabaseName(Tenant tenant) {
		return tenant.getIdentity().toString();
	}
}
