describe('Owner Registration Form Telephone Validation', () => {
    beforeEach(() => {
        // Assuming the application is running on localhost:8080
        cy.visit('/owners/new');
    });

    it('should display client-side validation error for invalid telephone (non-numeric)', () => {
        cy.get('#firstName').type('John');
        cy.get('#lastName').type('Doe');
        cy.get('#address').type('123 Main St');
        cy.get('#city').type('Anytown');
        cy.get('#telephone').type('abcde12345'); // Non-numeric input

        cy.get('#telephone').blur(); // Trigger validation

        cy.get('#telephone').should('have.class', 'is-invalid');
        cy.get('#telephone-client-error').should('be.visible').and('contain', 'Telephone number must be 10-15 digits and contain only numbers.');
        cy.get('button[type="submit"]').should('be.enabled'); // HTML5 pattern prevents submission, but JS might not.
        // For Cypress, we'd typically check if form submission is prevented.
        // Let's try to submit and see if the error persists or if server-side validation kicks in.
        cy.get('button[type="submit"]').click();
        cy.url().should('include', '/owners/new'); // Should stay on the same page due to client-side validation
        cy.get('#telephone-client-error').should('be.visible');
    });

    it('should display client-side validation error for invalid telephone (too short)', () => {
        cy.get('#firstName').type('John');
        cy.get('#lastName').type('Doe');
        cy.get('#address').type('123 Main St');
        cy.get('#city').type('Anytown');
        cy.get('#telephone').type('12345'); // Too short

        cy.get('#telephone').blur(); // Trigger validation

        cy.get('#telephone').should('have.class', 'is-invalid');
        cy.get('#telephone-client-error').should('be.visible').and('contain', 'Telephone number must be 10-15 digits and contain only numbers.');
        cy.get('button[type="submit"]').click();
        cy.url().should('include', '/owners/new');
        cy.get('#telephone-client-error').should('be.visible');
    });

    it('should display client-side validation error for invalid telephone (too long)', () => {
        cy.get('#firstName').type('John');
        cy.get('#lastName').type('Doe');
        cy.get('#address').type('123 Main St');
        cy.get('#city').type('Anytown');
        cy.get('#telephone').type('1234567890123456'); // Too long

        cy.get('#telephone').blur(); // Trigger validation

        cy.get('#telephone').should('have.class', 'is-invalid');
        cy.get('#telephone-client-error').should('be.visible').and('contain', 'Telephone number must be 10-15 digits and contain only numbers.');
        cy.get('button[type="submit"]').click();
        cy.url().should('include', '/owners/new');
        cy.get('#telephone-client-error').should('be.visible');
    });

    it('should allow form submission with a valid telephone number', () => {
        cy.get('#firstName').type('Jane');
        cy.get('#lastName').type('Smith');
        cy.get('#address').type('456 Oak Ave');
        cy.get('#city').type('Otherville');
        cy.get('#telephone').type('9876543210'); // Valid telephone

        cy.get('#telephone').blur(); // Trigger validation

        cy.get('#telephone').should('not.have.class', 'is-invalid');
        cy.get('#telephone-client-error').should('not.be.visible');

        cy.get('button[type="submit"]').click();

        // After successful submission, it should redirect to the owner details page
        cy.url().should('match', /owners\/\d+/);
        cy.contains('Owner Information');
        cy.contains('Jane Smith');
        cy.contains('9876543210');
    });

    it('should display server-side validation error if client-side is bypassed or incomplete', () => {
        // Simulate bypassing client-side validation (e.g., by disabling JS or using an old browser)
        // For this test, we'll submit an invalid value and expect the server to catch it.
        // We'll use a value that might pass basic HTML5 pattern but fail server-side length/content.
        // Or, more simply, just submit an empty field if @NotEmpty is on server.
        cy.get('#firstName').type('Server');
        cy.get('#lastName').type('Error');
        cy.get('#address').type('789 Pine Ln');
        cy.get('#city').type('Cloud City');
        // Intentionally leave telephone empty to trigger @NotEmpty on server
        // Or type something that passes HTML5 but fails server-side (e.g., 9 digits if pattern was less strict)
        // For now, let's test the @NotEmpty server-side validation.
        cy.get('#telephone').clear(); // Ensure it's empty

        cy.get('button[type="submit"]').click();

        // Should stay on the form page and display server-side error
        cy.url().should('include', '/owners/new');
        cy.get('#telephone').should('have.class', 'is-invalid');
        cy.get('span[th:if="${#fields.hasErrors(\'telephone\')}"]').should('be.visible').and('contain', 'must not be empty');
    });
});