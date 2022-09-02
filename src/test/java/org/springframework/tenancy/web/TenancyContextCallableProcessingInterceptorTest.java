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

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.tenancy.context.TenancyContext;
import org.springframework.tenancy.context.TenancyContextHolder;
import org.springframework.web.context.request.NativeWebRequest;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class TenancyContextCallableProcessingInterceptorTest {

    @Mock
    private TenancyContext tenancyContext;

    @Mock
    private Callable<?> callable;

    @Mock
    private NativeWebRequest webRequest;

    @After
    public void clearTenancyContext() {
        TenancyContextHolder.clearContext();
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorNull() {
        new TenancyContextCallableProcessingInterceptor(null);
    }

    @Test
    public void currentTenancyContext() throws Exception {
        TenancyContextCallableProcessingInterceptor interceptor = new TenancyContextCallableProcessingInterceptor();
        TenancyContextHolder.setContext(tenancyContext);
        interceptor.beforeConcurrentHandling(webRequest, callable);
        TenancyContextHolder.clearContext();

        interceptor.preProcess(webRequest, callable);
        assertThat(TenancyContextHolder.getContext(), is(tenancyContext));

        interceptor.postProcess(webRequest, callable, null);
        assertThat(TenancyContextHolder.getContext(), is(not(tenancyContext)));
    }

    @Test
    public void specificTenancyContext() throws Exception {
        TenancyContextCallableProcessingInterceptor interceptor = new TenancyContextCallableProcessingInterceptor(tenancyContext);

        interceptor.preProcess(webRequest, callable);
        assertThat(TenancyContextHolder.getContext(), is(tenancyContext));

        interceptor.postProcess(webRequest, callable, null);
        assertThat(TenancyContextHolder.getContext(), is(not(tenancyContext)));
    }
}
