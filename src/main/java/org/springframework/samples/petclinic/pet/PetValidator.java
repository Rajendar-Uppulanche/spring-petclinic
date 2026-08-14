package org.springframework.samples.petclinic.pet;

import java.time.LocalDate;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * `Validator` for `Pet` forms.
 * <p>
 * We're not using Bean Validation annotations here because it is easier to define such validation
 * logic in a class.
 * </p>
 *
 * @author Ken Krebs
 * @author Juergen Hoeller
 * @author Maciej Walkowiak
 */
public class PetValidator implements Validator {

	private static final String REQUIRED = "required";

	@Override
	public void validate(Object obj, Errors errors) {
		Pet pet = (Pet) obj;
		String name = pet.getName();
		// name validation
		if (name == null || name.isBlank()) {
			errors.rejectValue("name", REQUIRED, REQUIRED);
		}

		// type validation
		if (pet.isNew() && pet.getType() == null) {
			errors.rejectValue("type", REQUIRED, REQUIRED);
		}

		// birth date validation
		if (pet.getBirthDate() == null) {
			errors.rejectValue("birthDate", REQUIRED, REQUIRED);
		} else if (pet.getBirthDate().isAfter(LocalDate.now())) {
			// Modified: Use a configurable message key
			errors.rejectValue("birthDate", "pet.birthDate.future", "Birth date cannot be in the future");
		}
	}

	/**
	 * This Validator validates just Pet instances
	 */
	@Override
	public boolean supports(Class<?> clazz) {
		return Pet.class.isAssignableFrom(clazz);
	}

}
