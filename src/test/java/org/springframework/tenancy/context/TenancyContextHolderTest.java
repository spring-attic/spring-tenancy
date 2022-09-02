package org.springframework.tenancy.context;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.tenancy.provider.DefaultTenant;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class TenancyContextHolderTest {

    @Before
    public void setUp() throws Exception {
        TenancyContextHolder.setStrategyName(TenancyContextHolder.MODE_INHERITABLETHREADLOCAL);
    }

    @After
    public void tearDown() {
        TenancyContextHolder.setStrategyName(TenancyContextHolder.MODE_THREADLOCAL);
    }

    @Test
    public void testContextHolderGetterSetterClearer() {
        TenancyContext tc = new DefaultTenancyContext();
        tc.setTenant(new DefaultTenant("id", "myTenant"));
        TenancyContextHolder.setContext(tc);
        assertThat(TenancyContextHolder.getContext(), is(tc));
        TenancyContextHolder.clearContext();
        assertThat(TenancyContextHolder.getContext(), is(not(tc)));
        TenancyContextHolder.clearContext();
    }

    @Test
    public void testNeverReturnsNull() {
        assertNotNull(TenancyContextHolder.getContext());
        TenancyContextHolder.clearContext();
    }

    @Test
    public void testRejectsNulls() {
        try {
            TenancyContextHolder.setContext(null);
            fail("Should have rejected null");
        } catch (IllegalArgumentException expected) {
        }
    }

    static class StatusHolder {

        private Boolean success;

        private String message;

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public void assertEquals(Object expected, Object result) {
            if (alreadyFailed()) {
                return;
            }
            success = expected == result;
            addMessageIfFailed("'" + result + "' should be equals to expected value: '" + expected + "'");
        }

        public void assertNotEquals(Object expected, Object result) {
            if (alreadyFailed()) {
                return;
            }
            success = expected != result;
            addMessageIfFailed("'" + result + "' should be different from expected value: '" + expected + "'");
        }

        public void assertNull(Object expected, Object result) {
            if (alreadyFailed()) {
                return;
            }
            success = expected != result;
            addMessageIfFailed("'" + result + "' should be different from expected value: '" + expected + "'");
        }

        public void assertTrue(String message, boolean expression) {
            if (alreadyFailed()) {
                return;
            }
            success = expression;
            if (!success) {
                this.message = message;
            }
        }

        public void addMessageIfFailed(String message) {
            if (!success) {
                this.message = message;
            }
        }

        public void assertSuccess() {
            assertTrue(message, success);
        }

        private boolean alreadyFailed() {
            return success != null && !success;
        }
    }
}
