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
import java.util.concurrent.ThreadFactory;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.tenancy.context.TenancyContext;
import org.springframework.tenancy.context.TenancyContextHolder;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.async.AsyncWebRequest;
import org.springframework.web.context.request.async.CallableProcessingInterceptorAdapter;
import org.springframework.web.context.request.async.WebAsyncManager;
import org.springframework.web.context.request.async.WebAsyncUtils;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TenancyWebAsyncFilterTest {
    @Mock
    private TenancyContext tenancyContext;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private AsyncWebRequest asyncWebRequest;

    private WebAsyncManager asyncManager;

    private JoinableThreadFactory threadFactory;

    private MockFilterChain filterChain;

    private TenancyWebAsyncFilter filter;

    @Before
    public void setUp() {
        when(asyncWebRequest.getNativeRequest(HttpServletRequest.class)).thenReturn(
                request);
        when(request.getRequestURI()).thenReturn("/");
        filterChain = new MockFilterChain();

        threadFactory = new JoinableThreadFactory();
        SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor();
        executor.setThreadFactory(threadFactory);

        asyncManager = WebAsyncUtils.getAsyncManager(request);
        asyncManager.setAsyncWebRequest(asyncWebRequest);
        asyncManager.setTaskExecutor(executor);
        when(request.getAttribute(WebAsyncUtils.WEB_ASYNC_MANAGER_ATTRIBUTE)).thenReturn(
                asyncManager);

        filter = new TenancyWebAsyncFilter();
    }

    @After
    public void clearTenancyContext() {
        TenancyContextHolder.clearContext();
    }

    @Test
    public void doFilterInternalRegistersTenancyContextCallableProcessor()
            throws Exception {
        TenancyContextHolder.setContext(tenancyContext);
        asyncManager
                .registerCallableInterceptors(new CallableProcessingInterceptorAdapter() {
                    @Override
                    public <T> void postProcess(NativeWebRequest request,
                                                Callable<T> task, Object concurrentResult) throws Exception {
                        assertThat(TenancyContextHolder.getContext(), is(not(tenancyContext)));
                    }
                });
        filter.doFilterInternal(request, response, filterChain);

        VerifyingCallable verifyingCallable = new VerifyingCallable();
        asyncManager.startCallableProcessing(verifyingCallable);
        threadFactory.join();
        assertThat(asyncManager.getConcurrentResult(), is((Object) tenancyContext));
    }

    @Test
    public void doFilterInternalRegistersTenancyContextCallableProcessorContextUpdated()
            throws Exception {
        TenancyContextHolder.setContext(TenancyContextHolder.createEmptyContext());
        asyncManager
                .registerCallableInterceptors(new CallableProcessingInterceptorAdapter() {
                    @Override
                    public <T> void postProcess(NativeWebRequest request,
                                                Callable<T> task, Object concurrentResult) throws Exception {
                        assertThat(TenancyContextHolder.getContext(), is(not(tenancyContext)));
                    }
                });
        filter.doFilterInternal(request, response, filterChain);
        TenancyContextHolder.setContext(tenancyContext);

        VerifyingCallable verifyingCallable = new VerifyingCallable();
        asyncManager.startCallableProcessing(verifyingCallable);
        threadFactory.join();
        assertThat(asyncManager.getConcurrentResult(), is((Object) tenancyContext));
    }

    private static final class JoinableThreadFactory implements ThreadFactory {
        private Thread t;

        public Thread newThread(Runnable r) {
            t = new Thread(r);
            return t;
        }

        public void join() throws InterruptedException {
            t.join();
        }
    }

    private class VerifyingCallable implements Callable<TenancyContext> {

        public TenancyContext call() throws Exception {
            return TenancyContextHolder.getContext();
        }

    }
}
