// src/test/e2e/visit_creation.spec.js
const { test, expect } = require('@playwright/test');

test.describe('Visit Creation Form', () => {
    test('should display character counter and enforce max length', async ({ page }) => {
        // Assuming the application runs on localhost:8080 and owner 1, pet 1 exist
        await page.goto('/owners/1/pets/1/visits/new');

        const descriptionTextarea = page.locator('#description');
        const charCounter = page.locator('#visit-description-char-counter');
        const submitButton = page.locator('button[type="submit"]');
        const helpBlock = page.locator('.help-block');

        // 1. Initial state
        await expect(charCounter).toHaveText('500 characters remaining');
        await expect(charCounter).not.toHaveClass(/char-counter--warning/);

        // 2. Type a short description
        await descriptionTextarea.fill('Routine check-up for pet.');
        await expect(charCounter).toHaveText('475 characters remaining'); // 500 - 25 = 475
        await expect(charCounter).not.toHaveClass(/char-counter--warning/);

        // 3. Type a description near the warning threshold
        const nearWarningDescription = 'a'.repeat(451); // 49 characters remaining
        await descriptionTextarea.fill(nearWarningDescription);
        await expect(charCounter).toHaveText('49 characters remaining');
        await expect(charCounter).toHaveClass(/char-counter--warning/);

        // 4. Type a description exactly at the warning threshold
        const atWarningDescription = 'a'.repeat(450); // 50 characters remaining
        await descriptionTextarea.fill(atWarningDescription);
        await expect(charCounter).toHaveText('50 characters remaining');
        await expect(charCounter).not.toHaveClass(/char-counter--warning/);

        // 5. Type a description exactly at max length
        const maxDescription = 'a'.repeat(500);
        await descriptionTextarea.fill(maxDescription);
        await expect(charCounter).toHaveText('0 characters remaining');
        await expect(charCounter).toHaveClass(/char-counter--warning/); // 0 is < 50

        // 6. Attempt to type beyond max length (browser should prevent this due to maxlength attribute)
        await descriptionTextarea.fill(maxDescription + 'b');
        await expect(descriptionTextarea).toHaveValue(maxDescription); // Should still be 500 chars
        await expect(charCounter).toHaveText('0 characters remaining');

        // 7. Submit a valid description
        await descriptionTextarea.fill('This is a valid description for the visit.');
        await page.locator('#date').fill('2025-01-01'); // Assuming a future date
        await submitButton.click();
        await expect(page).toHaveURL(/owners\/1$/); // Should redirect to owner details page

        // 8. Attempt to submit an invalid (too long) description (backend validation)
        await page.goto('/owners/1/pets/1/visits/new'); // Go back to form
        const tooLongDescription = 'a'.repeat(501);
        await descriptionTextarea.fill(tooLongDescription);
        await page.locator('#date').fill('2025-01-01'); // Assuming a future date
        await submitButton.click();

        // Expect to stay on the form page and see a validation error
        await expect(page).toHaveURL(/visits\/new$/);
        await expect(helpBlock).toBeVisible();
        await expect(helpBlock).toHaveText('Description must not exceed 500 characters');
    });
});
