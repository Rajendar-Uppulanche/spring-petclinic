/**
 * @jest-environment jsdom
 */

// Re-define the script logic in a testable way
const setupCharCounter = () => {
    const descriptionTextarea = document.getElementById('description');
    const charCounter = document.getElementById('visit-description-char-counter');
    const maxLength = 500;
    const warningThreshold = 50;

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
};

describe('Visit Form Character Counter', () => {
    let descriptionTextarea;
    let charCounter;

    beforeEach(() => {
        // Reset DOM and re-run setup before each test
        document.body.innerHTML = `
            <textarea id="description" maxlength="500"></textarea>
            <span id="visit-description-char-counter" class="char-counter"></span>
        `;
        descriptionTextarea = document.getElementById('description');
        charCounter = document.getElementById('visit-description-char-counter');
        setupCharCounter(); // Initialize the counter logic
    });

    test('should initialize with correct remaining characters', () => {
        expect(charCounter.textContent).toBe('500 characters remaining');
        expect(charCounter.classList.contains('char-counter--warning')).toBe(false);
    });

    test('should update remaining characters on input', () => {
        descriptionTextarea.value = 'Hello';
        descriptionTextarea.dispatchEvent(new Event('input'));
        expect(charCounter.textContent).toBe('495 characters remaining');
        expect(charCounter.classList.contains('char-counter--warning')).toBe(false);
    });

    test('should apply warning class when remaining characters are below threshold', () => {
        descriptionTextarea.value = 'a'.repeat(451); // 49 characters remaining
        descriptionTextarea.dispatchEvent(new Event('input'));
        expect(charCounter.textContent).toBe('49 characters remaining');
        expect(charCounter.classList.contains('char-counter--warning')).toBe(true);
    });

    test('should remove warning class when remaining characters are above threshold', () => {
        descriptionTextarea.value = 'a'.repeat(450); // 50 characters remaining
        descriptionTextarea.dispatchEvent(new Event('input'));
        expect(charCounter.textContent).toBe('50 characters remaining');
        expect(charCounter.classList.contains('char-counter--warning')).toBe(false);

        descriptionTextarea.value = 'a'.repeat(449); // 51 characters remaining
        descriptionTextarea.dispatchEvent(new Event('input'));
        expect(charCounter.textContent).toBe('51 characters remaining');
        expect(charCounter.classList.contains('char-counter--warning')).toBe(false);
    });

    test('should handle empty input correctly', () => {
        descriptionTextarea.value = '';
        descriptionTextarea.dispatchEvent(new Event('input'));
        expect(charCounter.textContent).toBe('500 characters remaining');
        expect(charCounter.classList.contains('char-counter--warning')).toBe(false);
    });

    test('should handle input exactly at max length', () => {
        descriptionTextarea.value = 'a'.repeat(500);
        descriptionTextarea.dispatchEvent(new Event('input'));
        expect(charCounter.textContent).toBe('0 characters remaining');
        expect(charCounter.classList.contains('char-counter--warning')).toBe(true);
    });
});
