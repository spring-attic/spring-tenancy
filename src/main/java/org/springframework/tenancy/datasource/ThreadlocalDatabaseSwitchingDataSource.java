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

/**
 * A data source where the database name can be set for the context of the current thread.
 * 
 * @author Clint Morgan (Tasktop Technologies Inc.)
 */
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
