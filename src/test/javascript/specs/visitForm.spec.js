describe('Visit Form Character Counter', () => {
    let descriptionTextarea;
    let charCounter;

    beforeEach(() => {
        // Set up a mock DOM for testing
        document.body.innerHTML = `
            <textarea id="description" maxlength="500"></textarea>
            <span id="visit-description-char-counter" class="char-counter"></span>
        `;
        descriptionTextarea = document.getElementById('description');
        charCounter = document.getElementById('visit-description-char-counter');

        // Manually trigger the script logic for testing purposes
        const scriptContent = `
            document.addEventListener('DOMContentLoaded', function() {
                const descriptionTextarea = document.getElementById('description');
                const charCounter = document.getElementById('visit-description-char-counter');
                const maxLength = 500;
                const warningThreshold = 50;

                if (descriptionTextarea && charCounter) {
                    function updateCharCounter() {
                        const currentLength = descriptionTextarea.value.length;
                        const remaining = maxLength - currentLength;

                        charCounter.textContent = \
                            \
                            \
                            `\
                            ${remaining} characters remaining\
                            `\
                            ;

                        if (remaining < warningThreshold) {
                            charCounter.classList.add('char-counter--warning');
                        } else {
                            charCounter.classList.remove('char-counter--warning');
                        }
                    }

                    updateCharCounter();
                    descriptionTextarea.addEventListener('input', updateCharCounter);
                }
            });
        `;
        const script = document.createElement('script');
        script.textContent = scriptContent;
        document.body.appendChild(script);
        document.dispatchEvent(new Event('DOMContentLoaded'));
    });

    afterEach(() => {
        document.body.innerHTML = ''; // Clean up
    });

    it('should initialize the counter correctly', () => {
        expect(charCounter.textContent).toBe('500 characters remaining');
        expect(charCounter.classList.contains('char-counter--warning')).toBeFalse();
    });

    it('should update the counter on input', () => {
        descriptionTextarea.value = 'Hello';
        descriptionTextarea.dispatchEvent(new Event('input'));
        expect(charCounter.textContent).toBe('495 characters remaining');
        expect(charCounter.classList.contains('char-counter--warning')).toBeFalse();
    });

    it('should apply warning class when remaining characters are below threshold', () => {
        descriptionTextarea.value = 'a'.repeat(451); // 49 remaining
        descriptionTextarea.dispatchEvent(new Event('input'));
        expect(charCounter.textContent).toBe('49 characters remaining');
        expect(charCounter.classList.contains('char-counter--warning')).toBeTrue();
    });

    it('should remove warning class when remaining characters are above threshold', () => {
        descriptionTextarea.value = 'a'.repeat(450); // 50 remaining
        descriptionTextarea.dispatchEvent(new Event('input'));
        expect(charCounter.textContent).toBe('50 characters remaining');
        expect(charCounter.classList.contains('char-counter--warning')).toBeFalse();

        descriptionTextarea.value = 'a'.repeat(449); // 51 remaining
        descriptionTextarea.dispatchEvent(new Event('input'));
        expect(charCounter.textContent).toBe('51 characters remaining');
        expect(charCounter.classList.contains('char-counter--warning')).toBeFalse();
    });

    it('should handle maximum length correctly', () => {
        descriptionTextarea.value = 'a'.repeat(500);
        descriptionTextarea.dispatchEvent(new Event('input'));
        expect(charCounter.textContent).toBe('0 characters remaining');
        expect(charCounter.classList.contains('char-counter--warning')).toBeTrue(); // 0 < 50
    });
});