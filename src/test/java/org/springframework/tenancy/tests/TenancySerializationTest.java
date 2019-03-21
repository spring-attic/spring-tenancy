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
package org.springframework.tenancy.tests;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.tenancy.context.DefaultTenancyContext;
import org.springframework.tenancy.provider.DefaultTenant;

/**
 * Test serialization of tenancy components
 * 
 * @author David Green (Tasktop Technologies Inc.)
 */
public class TenancySerializationTest {

	@Test
	public void testDefaultTenantSerializable() {
		DefaultTenant defaultTenant = new DefaultTenant("id", "data");
		assertSerializable(defaultTenant);
	}
	
	@Test
	public void testDefaultTenancyContextSerializable() {
		DefaultTenancyContext context = new DefaultTenancyContext();
		assertSerializable(context);
	}

	private void assertSerializable(Object o) {
		Assert.assertTrue(o instanceof Serializable);
		Object copy;
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(out);
			objectOutputStream.writeObject(o);
			objectOutputStream.close();

			ObjectInputStream objectInputStream = new ObjectInputStream(
					new ByteArrayInputStream(out.toByteArray()));
			copy = objectInputStream.readObject();

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Cannot serialize object " + o);
			throw new IllegalStateException(e);
		}
		Assert.assertNotNull(copy);
		Assert.assertEquals(o, copy);
	}
}
