// src/main/resources/static/js/addVaccination.js
document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('add-vaccination-form');
    if (form) {
        form.addEventListener('submit', function(event) {
            let isValid = true;

            const dateAdministeredInput = document.getElementById('dateAdministered');
            const nextDueDateInput = document.getElementById('nextDueDate');

            // Clear previous errors
            clearError(dateAdministeredInput);
            clearError(nextDueDateInput);

            // Validate Date Administered not in future
            if (dateAdministeredInput && dateAdministeredInput.value) {
                const administeredDate = new Date(dateAdministeredInput.value);
                const today = new Date();
                today.setHours(0, 0, 0, 0); // Compare dates only, ignore time

                if (administeredDate > today) {
                    displayError(dateAdministeredInput, 'Date administered cannot be in the future');
                    isValid = false;
                }
            }

            // Validate Next Due Date not before Date Administered
            if (dateAdministeredInput && dateAdministeredInput.value && nextDueDateInput && nextDueDateInput.value) {
                const administeredDate = new Date(dateAdministeredInput.value);
                const nextDueDate = new Date(nextDueDateInput.value);

                if (nextDueDate < administeredDate) {
                    displayError(nextDueDateInput, 'Next due date cannot be before date administered');
                    isValid = false;
                }
            }

            if (!isValid) {
                event.preventDefault(); // Prevent form submission
            }
        });
    }

    function displayError(inputElement, message) {
        const formGroup = inputElement.closest('.form-group');
        if (formGroup) {
            formGroup.classList.add('has-error');
            let helpBlock = formGroup.querySelector('.help-block');
            if (!helpBlock) {
                helpBlock = document.createElement('span');
                helpBlock.classList.add('help-block');
                inputElement.parentNode.appendChild(helpBlock);
            }
            helpBlock.textContent = message;
        }
    }

    function clearError(inputElement) {
        const formGroup = inputElement.closest('.form-group');
        if (formGroup) {
            formGroup.classList.remove('has-error');
            const helpBlock = formGroup.querySelector('.help-block');
            if (helpBlock) {
                helpBlock.textContent = '';
            }
        }
    }
});
