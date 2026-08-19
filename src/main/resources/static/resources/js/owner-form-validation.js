$(document).ready(function() {
    const telephoneInput = $('#telephone');
    const telephoneClientError = $('#telephone-client-error');
    const form = $('#add-owner-form');

    function validateTelephone() {
        const value = telephoneInput.val();
        const isValid = /^[0-9]{10,15}$/.test(value);

        if (!isValid && value.length > 0) {
            telephoneInput.addClass('is-invalid');
            telephoneClientError.text('Telephone number must be 10-15 digits and contain only numbers.').show();
            return false;
        } else {
            telephoneInput.removeClass('is-invalid');
            telephoneClientError.hide();
            return true;
        }
    }

    // Validate on input change
    telephoneInput.on('input', validateTelephone);

    // Validate on form submission
    form.on('submit', function(event) {
        if (!validateTelephone()) {
            event.preventDefault(); // Prevent form submission if validation fails
        }
    });
});