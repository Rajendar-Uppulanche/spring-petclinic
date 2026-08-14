document.addEventListener('DOMContentLoaded', function() {
    const descriptionTextarea = document.getElementById('description');
    const charCounter = document.getElementById('description-char-counter');
    const maxLength = 500;
    const warningThreshold = 50;

    if (descriptionTextarea && charCounter) {
        function updateCounter() {
            const currentLength = descriptionTextarea.value.length;
            const remaining = maxLength - currentLength;

            charCounter.textContent = remaining + ' characters remaining';

            if (remaining < warningThreshold) {
                charCounter.classList.add('char-counter-warning');
            } else {
                charCounter.classList.remove('char-counter-warning');
            }
        }

        // Initial update
        updateCounter();

        // Update on input
        descriptionTextarea.addEventListener('input', updateCounter);
    }
});
