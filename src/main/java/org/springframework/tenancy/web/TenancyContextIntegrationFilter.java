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

package org.springframework.tenancy.web;

import java.io.IOException;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.tenancy.context.TenancyContext;
import org.springframework.tenancy.context.TenancyContextHolder;
import org.springframework.tenancy.core.Tenant;
import org.springframework.tenancy.provider.DefaultTenantProvider;
import org.springframework.tenancy.provider.TenantProvider;
import org.springframework.web.filter.GenericFilterBean;

/**
 * Responsible for setting and removing the {@link TenancyContext tenancy context} for the scope of every request. This
 * filter should be installed before any components that need access to the {@link TenancyContext tenancy context}.
 * 
 * @author Clint Morgan (Tasktop Technologies Inc.)
 * 
 * @see TenancyContext
 * @see TenancyContextHolder
 */
// TODO fail fast if no idStratagies
public class TenancyContextIntegrationFilter extends GenericFilterBean {

	private List<TenantIdentificationStrategy> tenantIdentificationStrategyChain;

	private TenantProvider tenantProvider = new DefaultTenantProvider();

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException,
			ServletException {
		HttpServletRequest request = (HttpServletRequest) req;

		TenancyContext contextBeforeChainExecution = determineTenancyContext(request);

		try {
			TenancyContextHolder.setContext(contextBeforeChainExecution);

			chain.doFilter(req, res);

		} finally {
			// Crucial removal of ContextHolder contents - do this
			// before anything else.
			TenancyContextHolder.clearContext();
		}

	}

	private TenancyContext determineTenancyContext(HttpServletRequest request) {
		TenancyContext tenancyContext = TenancyContextHolder.createEmptyContext();
		tenancyContext.setTenant(determineTenant(request));
		return tenancyContext;
	}

	/**
	 * Determine the tenant based on a given request. Default implementation goes through the identification strategies
	 * in order. The first identification strategy to find a tenant identification in the request will be used, via the
	 * tenantProvider, to locate the tenant.
	 * 
	 * @param request
	 * @return the tenant context for the given httpRequest. <code>null</code> is a valid return value.
	 */
	protected Tenant determineTenant(HttpServletRequest request) {
		for (TenantIdentificationStrategy tenantIdentificationStrategy : tenantIdentificationStrategyChain) {
			Object tenantId = tenantIdentificationStrategy.identifyTenant(request);
			if (tenantId != null) {
				return tenantProvider.findTenant(tenantId);
			}
		}
		return null;
	}

	/**
	 * Set the tenant identification strategy chain. The first member of the chain to identify a tenant will be used.
	 * 
	 * @param tenantIdentificationStrategyChain
	 */
	public void setTenantIdentificationStrategyChain(
			List<TenantIdentificationStrategy> tenantIdentificationStrategyChain) {
		this.tenantIdentificationStrategyChain = tenantIdentificationStrategyChain;
	}

	public void setTenantProvider(TenantProvider tenantProvider) {
		this.tenantProvider = tenantProvider;
	}
}