package org.springframework.samples.petclinic.web;

import org.springframework.samples.petclinic.model.Pet;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDate;

/**
 * <code>Validator</code> for <code>Pet</code> forms.
 * <p>
 * We're using a custom <code>Validator</code> here because it's more efficient and cleaner
 * to put all of our validations in one class than to clutter up our
 * <code>PetController</code> with multiple <code>if</code> statements.
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
        if (!StringUtils.hasLength(name)) {
            errors.rejectValue("name", REQUIRED, REQUIRED);
        }

        // birth date validation
        if (pet.getBirthDate() == null) {
            errors.rejectValue("birthDate", REQUIRED, REQUIRED);
        } else if (pet.getBirthDate().isAfter(LocalDate.now())) {
            errors.rejectValue("birthDate", "futureDate", "Birth date cannot be in the future");
        }

        // pet type validation
        if (pet.isNew() && pet.getType() == null) {
            errors.rejectValue("type", REQUIRED, REQUIRED);
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
