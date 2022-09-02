package org.springframework.tenancy.context;

import org.junit.After;
import org.junit.Before;
import org.springframework.tenancy.provider.DefaultTenant;

public class GlobalTenancyContextHolderStrategyTest extends InheritableThreadLocalTenancyContextHolderStrategyTest {

    @Before
    @Override
    public void setUp() throws Exception {
        TenancyContextHolder.setStrategyName(TenancyContextHolder.MODE_GLOBAL);
        TenancyContext tc = new DefaultTenancyContext();
        tc.setTenant(new DefaultTenant("id", "myTenant"));
        TenancyContextHolder.setContext(tc);

    }

    @After
    @Override
    public void tearDown() {
        TenancyContextHolder.setStrategyName(TenancyContextHolder.MODE_THREADLOCAL);
    }
}
