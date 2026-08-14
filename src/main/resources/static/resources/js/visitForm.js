document.addEventListener('DOMContentLoaded', function() {
    const descriptionTextarea = document.getElementById('description');
    const charCounter = document.getElementById('visit-description-char-counter');
    const maxLength = 500;
    const warningThreshold = 50; // FR-012: warning when remaining < 50

    if (descriptionTextarea && charCounter) {
        function updateCharCounter() {
            const currentLength = descriptionTextarea.value.length;
            const remaining = maxLength - currentLength;

            charCounter.textContent = `${remaining} characters remaining`;

            if (remaining < warningThreshold) {
                charCounter.classList.add('char-counter--warning');
            } else {
                charCounter.classList.remove('char-counter--warning');
            }
        }

        // Initial update on page load
        updateCharCounter();

        // Update on input
        descriptionTextarea.addEventListener('input', updateCharCounter);
    }
});
