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
package org.springframework.tenancy.web;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.request.async.WebAsyncManager;
import org.springframework.web.context.request.async.WebAsyncUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * This filter provides Asynchronous Request Processing for multi-tenant
 * application.
 *
 * You need to add this filter to your application to support
 * {@link org.springframework.tenancy.context.TenancyContextHolder} usage
 * in Spring Web MVC Async applications.
 *
 * i.e.
 * <pre>
 * <code>
 @Bean
 @Conditional(TenantCondition.class)
 public FilterRegistrationBean tenancyWebAsyncFilter() {
     FilterRegistrationBean registration = new FilterRegistrationBean();
     TenancyWebAsyncFilter filter = new TenancyWebAsyncFilter();
     registration.setFilter(filter);
     registration.setOrder(5); // whatever order
     return registration;
 }
 * </code>
 * </pre>
 */
public class TenancyWebAsyncFilter extends OncePerRequestFilter {

    private static final Object CALLABLE_INTERCEPTOR_KEY = new Object();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        WebAsyncManager asyncManager = WebAsyncUtils.getAsyncManager(request);

        TenancyWebAsyncFilter tenancyProcessingInterceptor =
                (TenancyWebAsyncFilter) asyncManager.getCallableInterceptor(CALLABLE_INTERCEPTOR_KEY);
        if (tenancyProcessingInterceptor == null) {
            asyncManager.registerCallableInterceptor(CALLABLE_INTERCEPTOR_KEY, new TenancyContextCallableProcessingInterceptor());
        }

        filterChain.doFilter(request, response);
    }
}