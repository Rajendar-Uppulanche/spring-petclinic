/*
 * Copyright 2012-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.owner;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for the {@link Pet} entity.
 */
class PetTests {

	@Test
	void testGetNameWhenNameIsNull() {
		Pet pet = new Pet();
		// Name is null by default for a new Pet (inherited from NamedEntity)
		assertEquals("", pet.getName(), "getName() should return an empty string when name is null");
	}

	@Test
	void testGetNameWhenNameIsNotNull() {
		Pet pet = new Pet();
		String petName = "Max";
		pet.setName(petName);
		assertEquals(petName, pet.getName(), "getName() should return the correct name when not null");
	}

}
