// Assuming a global 'app' object or similar for managing views/components
const PreventiveCareForm = {
    render: (initialData = {}) => {
        const { vaccineName = '', dosage = '', nextDueDate = '' } = initialData;
        return `
            <div id="preventive-care-form-container">
                <h3>Preventive Care Details</h3>
                <label for="vaccineName">Vaccine Name:</label>
                <input type="text" id="vaccineName" name="vaccineName" value="${vaccineName}" required><br>

                <label for="dosage">Dosage:</label>
                <input type="text" id="dosage" name="dosage" value="${dosage}" required><br>

                <label for="nextDueDate">Next Due Date:</label>
                <input type="date" id="nextDueDate" name="nextDueDate" value="${nextDueDate}" required><br>
            </div>
        `;
    },
    getData: () => {
        const container = document.getElementById('preventive-care-form-container');
        if (!container) return null; // Form not rendered

        return {
            vaccineName: container.querySelector('#vaccineName').value,
            dosage: container.querySelector('#dosage').value,
            nextDueDate: container.querySelector('#nextDueDate').value
        };
    },
    isValid: () => {
        const container = document.getElementById('preventive-care-form-container');
        if (!container) return true; // If form not rendered, it's valid (not required)

        const vaccineName = container.querySelector('#vaccineName').value;
        const dosage = container.querySelector('#dosage').value;
        const nextDueDate = container.querySelector('#nextDueDate').value;

        return vaccineName.trim() !== '' && dosage.trim() !== '' && nextDueDate.trim() !== '';
    }
};