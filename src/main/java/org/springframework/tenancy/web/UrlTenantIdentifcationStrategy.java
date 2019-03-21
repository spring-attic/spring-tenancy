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

package org.springframework.tenancy.web;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Required;

/**
 * A {@link TenantIdentificationStrategy strategy} which matches a request URI against a provided regular expression.
 * The first group of the expression will be used as the identification.
 * 
 * @author Clint Morgan (Tasktop Technologies Inc.)
 * 
 */
public class UrlTenantIdentifcationStrategy implements TenantIdentificationStrategy {

	private Pattern pattern;

	@Override
	public Object identifyTenant(HttpServletRequest request) {
		Matcher matcher = pattern.matcher(request.getRequestURI());
		if (!matcher.matches()) {
			return null;
		}
		return matcher.group(1);
	}

	/**
	 * Set the url pattern to use. This pattern should contain at least one group, the first of which will be used for
	 * the tenant identity.
	 * 
	 * @param urlPattern
	 */
	@Required
	public void setUrlPattern(String urlPattern) {
		this.pattern = Pattern.compile(urlPattern);
	}

}
