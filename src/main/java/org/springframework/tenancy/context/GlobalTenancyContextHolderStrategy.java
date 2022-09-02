package org.springframework.tenancy.context;

import org.springframework.util.Assert;

/**
 * Similar to <code>GlobalSecurityContextHolderStrategy</code>
 * from Spring Security.
 */
public class GlobalTenancyContextHolderStrategy implements TenancyContextHolderStrategy {

    private static TenancyContext contextHolder;

    public void clearContext() {
        contextHolder = null;
    }

    public TenancyContext getContext() {
        if (contextHolder == null) {
            contextHolder = new DefaultTenancyContext();
        }

        return contextHolder;
    }

    public void setContext(TenancyContext context) {
        Assert.notNull(context, "Only non-null TenancyContext instances are permitted");
        contextHolder = context;
    }

    public TenancyContext createEmptyContext() {
        return new DefaultTenancyContext();
    }
}
