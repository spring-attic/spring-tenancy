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

package org.springframework.tenancy.context;

import org.springframework.util.Assert;

/**
 * A <code>ThreadLocal</code>-based implementation of {@link TenancyContextHolderStrategy}.
 * 
 * @author Clint Morgan (Tasktop Technologies Inc.)
 * 
 * @see java.lang.ThreadLocal
 */
public class ThreadLocalTenancyContextHolderStrategy implements TenancyContextHolderStrategy {
	private static final ThreadLocal<TenancyContext> contextHolder = new ThreadLocal<TenancyContext>();

	public void clearContext() {
		contextHolder.set(null);
	}

	public TenancyContext getContext() {
		TenancyContext ctx = contextHolder.get();

		if (ctx == null) {
			ctx = createEmptyContext();
			contextHolder.set(ctx);
		}

		return ctx;
	}

	public void setContext(TenancyContext context) {
		Assert.notNull(context, "Only non-null SecurityContext instances are permitted");
		contextHolder.set(context);
	}

	public TenancyContext createEmptyContext() {
		return new DefaultTenancyContext();
	}
}
