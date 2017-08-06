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

import java.util.concurrent.Callable;

import org.springframework.tenancy.context.TenancyContext;
import org.springframework.tenancy.context.TenancyContextHolder;
import org.springframework.util.Assert;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.async.CallableProcessingInterceptorAdapter;

public class TenancyContextCallableProcessingInterceptor extends CallableProcessingInterceptorAdapter {

    private TenancyContext tenancyContext;

    /**
     * Create a new {@link TenancyContextCallableProcessingInterceptor} that uses the
     * {@link TenancyContext} from the {@link TenancyContextHolder} at the time
     * {@link #beforeConcurrentHandling(NativeWebRequest, Callable)} is invoked.
     */
    public TenancyContextCallableProcessingInterceptor() {
    }

    /**
     * Creates a new {@link TenancyContextCallableProcessingInterceptor} with the
     * specified {@link TenancyContext}.
     * 
     * @param tenancyContext the {@link TenancyContext} to set on the
     * {@link org.springframework.tenancy.context.TenancyContextHolder} in {@link #preProcess(NativeWebRequest, Callable)}.
     * Cannot be null.
     * @throws IllegalArgumentException if tenancyContext is null.
     */
    public TenancyContextCallableProcessingInterceptor(TenancyContext tenancyContext) {
        Assert.notNull(tenancyContext, "tenancyContext cannot be null");
        setTenancyContext(tenancyContext);
    }

    @Override
    public <T> void beforeConcurrentHandling(NativeWebRequest request, Callable<T> task) throws Exception {
        if (tenancyContext == null) {
            setTenancyContext(TenancyContextHolder.getContext());
        }
    }

    @Override
    public <T> void preProcess(NativeWebRequest request, Callable<T> task) throws Exception {
        TenancyContextHolder.setContext(tenancyContext);
    }

    @Override
    public <T> void postProcess(NativeWebRequest request, Callable<T> task, Object concurrentResult) throws Exception {
        TenancyContextHolder.clearContext();
    }

    private void setTenancyContext(TenancyContext tenancyContext) {
        this.tenancyContext = tenancyContext;
    }
}
