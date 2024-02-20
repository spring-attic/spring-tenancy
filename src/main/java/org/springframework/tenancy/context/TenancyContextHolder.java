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
 * Associates a given {@link TenancyContext} with the current execution.
 * <p>
 * This class provides a series of static methods that delegate to an instance of {@link TenancyContextHolderStrategy}.
 * The purpose of the class is to provide a convenient way to specify the strategy that should be used for a given JVM.
 * This is a JVM-wide setting, since everything in this class is <code>static</code> to facilitate ease of use in
 * calling code.
 * 
 * @author Clint Morgan (Tasktop Technologies Inc.)
 * 
 */
public class TenancyContextHolder {

	private static TenancyContextHolderStrategy strategy = new ThreadLocalTenancyContextHolderStrategy();

	/**
	 * Explicitly clear the tenacy context.
	 * 
	 */
	public static void clearContext() {
		strategy.clearContext();
	}

	/**
	 * Obtain the current <code>TenancyContext</code>.
	 * 
	 * @return the tenancy context (never <code>null</code>)
	 */
	public static TenancyContext getContext() {
		return strategy.getContext();
	}

	/**
	 * Associates a new <code>TenancyContext</code> with the current context of execution.
	 * 
	 * @param context
	 *            the new <code>TenancyContext</code> (may not be <code>null</code>)
	 */

	public static void setContext(TenancyContext context) {
		strategy.setContext(context);
	}

	/**
	 * Delegates the creation of a new, empty context to the configured strategy.
	 */
	public static TenancyContext createEmptyContext() {
		return strategy.createEmptyContext();
	}

	/**
	 * Allows retrieval of the context strategy.
	 * 
	 * @return the configured strategy for storing the tenancy context.
	 */
	public static TenancyContextHolderStrategy getStrategy() {
		return strategy;
	}

	/**
	 * Set the context strategy.
	 * 
	 * @param strategy
	 *            the configured strategy for storing the tenancy context.
	 */
	public static void setStrategy(TenancyContextHolderStrategy strategy) {
		if (strategy == null) {
			throw new IllegalArgumentException();
		}
		TenancyContextHolder.strategy = strategy;
	}

}
