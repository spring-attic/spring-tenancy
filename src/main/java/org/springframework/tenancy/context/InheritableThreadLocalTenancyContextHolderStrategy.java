package org.springframework.tenancy.context;

import org.springframework.util.Assert;

/**
 * Similar to <code>InheritableThreadLocalSecurityContextHolderStrategy</code>
 * from Spring Security.
 */
final class InheritableThreadLocalTenancyContextHolderStrategy implements TenancyContextHolderStrategy {

    private static final ThreadLocal<TenancyContext> CONTEXT_HOLDER = new InheritableThreadLocal<TenancyContext>();

    public void clearContext() {
        CONTEXT_HOLDER.remove();
    }

    public TenancyContext getContext() {
        TenancyContext ctx = CONTEXT_HOLDER.get();

        if (ctx == null) {
            ctx = createEmptyContext();
            CONTEXT_HOLDER.set(ctx);
        }

        return ctx;
    }

    public void setContext(TenancyContext context) {
        Assert.notNull(context, "Only non-null TenancyContext instances are permitted");
        CONTEXT_HOLDER.set(context);
    }

    public TenancyContext createEmptyContext() {
        return new DefaultTenancyContext();
    }
}
