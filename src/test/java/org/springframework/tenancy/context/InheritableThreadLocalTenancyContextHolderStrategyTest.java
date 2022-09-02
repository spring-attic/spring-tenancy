package org.springframework.tenancy.context;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.tenancy.provider.DefaultTenant;

public class InheritableThreadLocalTenancyContextHolderStrategyTest extends TenancyContextHolderTest {

    @Before
    @Override
    public void setUp() throws Exception {
        TenancyContextHolder.setStrategyName(TenancyContextHolder.MODE_INHERITABLETHREADLOCAL);
    }

    @After
    @Override
    public void tearDown() {
        TenancyContextHolder.setStrategyName(TenancyContextHolder.MODE_THREADLOCAL);
    }

    @Test
    public void testSpawnThread() throws InterruptedException {
        TenancyContext tc = new DefaultTenancyContext();
        tc.setTenant(new DefaultTenant("id", "myTenant"));
        TenancyContextHolder.setContext(tc);
        StatusHolder statusHolder = new StatusHolder();
        Runnable runnable = () -> {
            statusHolder.assertEquals(tc, TenancyContextHolder.getContext());
        };
        Thread thread = new Thread(runnable);
        thread.start();
        thread.join(60000L);
        statusHolder.assertSuccess();
    }
}
