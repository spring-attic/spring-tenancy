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

package org.springframework.tenancy.provider;

import org.springframework.tenancy.core.Tenant;

/**
 * A means of providing a tenant for any given tenant identity
 * 
 * @author Clint Morgan (Tasktop Technologies Inc.)
 */
public interface TenantProvider {

	/**
	 * Attempt to find a tenant based on a given tenant identity identity.
	 * 
	 * @param identity
	 *            the identify of a tenant, or null if there is none
	 * @return a tenant for the corresponding identity, or <code>null</code> if the given identity has no corresponding
	 *         tenant.
	 */
	Tenant findTenant(Object identity);
}
