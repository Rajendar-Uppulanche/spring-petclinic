package org.springframework.samples.petclinic.owner;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class OwnerTests {

	@Test
	void setAndGetUserId() {
		Owner owner = new Owner();
		String userId = "testUser123";
		owner.setUserId(userId);
		assertThat(owner.getUserId()).isEqualTo(userId);
	}

	@Test
	void userIdIsNotBlank() {
		Owner owner = new Owner();
		owner.setUserId("someId");
		assertThat(owner.getUserId()).isNotBlank();
	}

	@Test
	void toStringIncludesUserId() {
		Owner owner = new Owner();
		owner.setId(1);
		owner.setFirstName("George");
		owner.setLastName("Franklin");
		owner.setAddress("110 W. Liberty St.");
		owner.setCity("Madison");
		owner.setTelephone("6085551023");
		owner.setUserId("georgef123");

		String toString = owner.toString();
		assertThat(toString).contains("id=1", "firstName=George", "lastName=Franklin", "userId=georgef123");
	}
}
