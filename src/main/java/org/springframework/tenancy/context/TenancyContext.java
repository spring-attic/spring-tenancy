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

package org.springframework.tenancy.context;

import java.io.Serializable;

import org.springframework.tenancy.core.Tenant;

/**
 * a context in which tenancy can be defined
 * 
 * @author Clint Morgan (Tasktop Technologies Inc.)
 */
public interface TenancyContext extends Serializable {
	/**
	 * Obtains the current tenant.
	 * 
	 * @return the <code>Tenant</code> or <code>null</code> if no tenancy information is available.
	 */
	Tenant getTenant();

	/**
	 * Changes the current tenant, or removes the tenancy information.
	 * 
	 * @param tenant
	 *            the new <code>Tenant</code>, or <code>null</code> if no further tenancy information should be stored
	 */
	void setTenant(Tenant tenant);
}
