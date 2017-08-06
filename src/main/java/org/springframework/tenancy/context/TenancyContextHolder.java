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

package org.springframework.tenancy.context;


import java.lang.reflect.Constructor;

import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

/**
 * Associates a given {@link TenancyContext} with the current execution.
 * <p>
 * This class provides a series of static methods that delegate to an instance of {@link TenancyContextHolderStrategy}.
 * The purpose of the class is to provide a convenient way to specify the strategy that should be used for a given JVM.
 * This is a JVM-wide setting, since everything in this class is <code>static</code> to facilitate ease of use in
 * calling code.
 * <p>
 * To specify which strategy should be used, you must provide a mode setting. A mode
 * setting is one of the three valid <code>MODE_</code> settings defined as
 * <code>static final</code> fields, or a fully qualified classname to a concrete
 * implementation of
 * {@link TenancyContextHolderStrategy} that provides a public no-argument constructor.
 * <p>
 * There are two ways to specify the desired strategy mode <code>String</code>. The first
 * is to specify it via the system property keyed on {@link #SYSTEM_PROPERTY}. The second
 * is to call {@link #setStrategyName(String)} before using the class. If neither approach
 * is used, the class will default to using {@link #MODE_THREADLOCAL}, which is backwards
 * compatible, has fewer JVM incompatibilities and is appropriate on servers (whereas
 * {@link #MODE_GLOBAL} is definitely inappropriate for server use).
 *
 * @author Clint Morgan (Tasktop Technologies Inc.)
 */
public class TenancyContextHolder {

    public static final String MODE_THREADLOCAL = "MODE_THREADLOCAL";

    public static final String MODE_INHERITABLETHREADLOCAL = "MODE_INHERITABLETHREADLOCAL";

    public static final String MODE_GLOBAL = "MODE_GLOBAL";

    public static final String SYSTEM_PROPERTY = "spring.tenancy.strategy";

    private static TenancyContextHolderStrategy strategy = new ThreadLocalTenancyContextHolderStrategy();

    private static String strategyName = System.getProperty(SYSTEM_PROPERTY);

    private static int initializeCount;

    static {
        initialize();
    }

    /**
     * Explicitly clear the tenacy context.
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
     * @param context the new <code>TenancyContext</code> (may not be <code>null</code>)
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
     * @param strategy the configured strategy for storing the tenancy context.
     */
    public static void setStrategy(TenancyContextHolderStrategy strategy) {
        if (strategy == null) {
            throw new IllegalArgumentException();
        }
        TenancyContextHolder.strategy = strategy;
    }

    /**
     * Primarily for troubleshooting purposes, this method shows how many times the class
     * has re-initialized its <code>TenancyContextHolderStrategy</code>.
     *
     * @return the count (should be one unless you've called
     * {@link #setStrategyName(String)} to switch to an alternate strategy.
     */
    public static int getInitializeCount() {
        return initializeCount;
    }

    private static void initialize() {
        if (!StringUtils.hasText(strategyName)) {
            // Set default
            strategyName = MODE_THREADLOCAL;
        }

        if (strategyName.equals(MODE_THREADLOCAL)) {
            strategy = new ThreadLocalTenancyContextHolderStrategy();
        } else if (strategyName.equals(MODE_INHERITABLETHREADLOCAL)) {
            strategy = new InheritableThreadLocalTenancyContextHolderStrategy();
        } else if (strategyName.equals(MODE_GLOBAL)) {
            strategy = new GlobalTenancyContextHolderStrategy();
        } else {
            // Try to load a custom strategy
            try {
                Class<?> clazz = Class.forName(strategyName);
                Constructor<?> customStrategy = clazz.getConstructor();
                strategy = (TenancyContextHolderStrategy) customStrategy.newInstance();
            } catch (Exception ex) {
                ReflectionUtils.handleReflectionException(ex);
            }
        }

        initializeCount++;
    }

    /**
     * Changes the preferred strategy. Do <em>NOT</em> call this method more than once for
     * a given JVM, as it will re-initialize the strategy and adversely affect any
     * existing threads using the old strategy.
     *
     * @param strategyName the fully qualified class name of the strategy that should be
     *                     used.
     */
    public static void setStrategyName(String strategyName) {
        TenancyContextHolder.strategyName = strategyName;
        initialize();
    }

    public String toString() {
        return "TenancyContextHolder[strategy='" + strategyName + "'; initializeCount=" + initializeCount + "]";
    }
}
