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

/**
 * A strategy for storing tenancy context information against an execution.
 * 
 * @author Clint Morgan (Tasktop Technologies Inc.)
 */
public interface TenancyContextHolderStrategy {

	/**
	 * Clears the current context.
	 */
	void clearContext();

	/**
	 * Obtains the current context.
	 * 
	 * @return a context (never <code>null</code> - create a default implementation if necessary)
	 */
	TenancyContext getContext();

	/**
	 * Sets the current context.
	 * 
	 * @param context
	 *            to the new argument (should never be <code>null</code>, although implementations must check if
	 *            <code>null</code> has been passed and throw an <code>IllegalArgumentException</code> in such cases)
	 */
	void setContext(TenancyContext context);

	/**
	 * Creates a new, empty context implementation, for use when creating a new context for the first time.
	 * 
	 * @return the empty context.
	 */
	TenancyContext createEmptyContext();

}
