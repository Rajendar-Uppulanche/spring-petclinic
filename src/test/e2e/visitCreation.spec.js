// Assuming Playwright or Cypress syntax
describe('Visit Creation E2E Tests', () => {
    beforeEach(() => {
        // Navigate to the pet details page where visits can be added
        // This would typically involve logging in and navigating
        cy.visit('/owners/1/pets/1/visits/new'); // Example path
    });

    it('should display the character counter and update it correctly', () => {
        const descriptionTextarea = '#description';
        const charCounter = '#visit-description-char-counter';

        cy.get(charCounter).should('contain', '500 characters remaining');
        cy.get(charCounter).should('not.have.class', 'char-counter--warning');

        cy.get(descriptionTextarea).type('Short description');
        cy.get(charCounter).should('contain', '483 characters remaining');
        cy.get(charCounter).should('not.have.class', 'char-counter--warning');

        // Type a long description to trigger warning
        const longText = 'a'.repeat(451); // 49 remaining
        cy.get(descriptionTextarea).clear().type(longText);
        cy.get(charCounter).should('contain', '49 characters remaining');
        cy.get(charCounter).should('have.class', 'char-counter--warning');

        // Type even longer, up to max
        const maxText = 'a'.repeat(500); // 0 remaining
        cy.get(descriptionTextarea).clear().type(maxText);
        cy.get(charCounter).should('contain', '0 characters remaining');
        cy.get(charCounter).should('have.class', 'char-counter--warning');
    });

    it('should allow creating a visit with a valid description length', () => {
        const descriptionTextarea = '#description';
        const dateInput = '#date'; // Assuming a date input field
        const submitButton = 'button[type="submit"]';

        cy.get(dateInput).type('2023/01/01'); // Example date
        cy.get(descriptionTextarea).type('This is a valid visit description within the 500 character limit.');
        cy.get(submitButton).click();

        // Assert redirection to pet details page or success message
        cy.url().should('include', '/owners/1/pets/1');
        cy.contains('This is a valid visit description').should('exist'); // Verify visit is displayed
    });

    it('should prevent creating a visit with a description exceeding 500 characters', () => {
        const descriptionTextarea = '#description';
        const dateInput = '#date';
        const submitButton = 'button[type="submit"]';
        const errorMessage = '.help-inline'; // Assuming error messages appear here

        cy.get(dateInput).type('2023/01/01');
        const tooLongText = 'a'.repeat(501);
        cy.get(descriptionTextarea).type(tooLongText);
        cy.get(submitButton).click();

        // Assert that the form remains on the same page or shows an error
        cy.url().should('include', '/visits/new'); // Still on the new visit page
        cy.get(errorMessage).should('contain', 'Description must not exceed 500 characters.');
    });
});