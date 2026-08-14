package org.springframework.samples.petclinic.visit;

import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * <code>Validator</code> for <code>Visit</code> forms.
 */
public class VisitValidator implements Validator {

	private static final String REQUIRED = "required";
	private static final int MAX_DESCRIPTION_LENGTH = 500; // BR-004

	@Override
	public void validate(Object obj, Errors errors) {
		Visit visit = (Visit) obj;
		String description = visit.getDescription();

		// description validation
		if (!StringUtils.hasText(description)) {
			errors.rejectValue("description", REQUIRED, REQUIRED);
		} else if (description.length() > MAX_DESCRIPTION_LENGTH) {
			errors.rejectValue("description", "tooLong", "Description must not exceed " + MAX_DESCRIPTION_LENGTH + " characters.");
		}

		// date validation (assuming date is always set by default or handled elsewhere)
		if (visit.getDate() == null) {
			errors.rejectValue("date", REQUIRED, REQUIRED);
		}
	}

	/**
	 * This Validator validates *just* Visit instances
	 */
	@Override
	public boolean supports(Class<?> clazz) {
		return Visit.class.isAssignableFrom(clazz);
	}

}