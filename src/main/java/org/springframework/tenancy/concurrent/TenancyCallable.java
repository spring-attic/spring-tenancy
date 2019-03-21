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
package org.springframework.tenancy.concurrent;

import java.util.concurrent.Callable;

import org.springframework.tenancy.context.DefaultTenancyContext;
import org.springframework.tenancy.context.TenancyContext;
import org.springframework.tenancy.context.TenancyContextHolder;
import org.springframework.tenancy.core.Tenant;

/**
 * A callable that sets the tenancy context for the duration of the call.
 *  
 * @author David Green (Tasktop Technologies Inc.)
 */
public abstract class TenancyCallable<T> implements Callable<T> {

	private final Tenant tenant;

	/**
	 * create the callable with the given tenant as the context
	 */
	public TenancyCallable(Tenant tenant) {
		this.tenant = tenant;
	}

	/**
	 * create the callable with the current context tenant as the context of the callable
	 */
	public TenancyCallable() {
		this(TenancyContextHolder.getContext().getTenant());
	}

	@Override
	public final T call() throws Exception {
		final TenancyContext previousTenancyContext = TenancyContextHolder.getContext();
		
		DefaultTenancyContext context = new DefaultTenancyContext();
		context.setTenant(tenant);
		TenancyContextHolder.setContext(context);
		try {
			return callAsTenant();
		} finally {
			TenancyContextHolder.setContext(previousTenancyContext);
		}
	}

	protected abstract T callAsTenant() throws Exception;
}