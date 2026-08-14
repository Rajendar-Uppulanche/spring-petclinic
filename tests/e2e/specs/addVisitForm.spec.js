describe('Add Visit Form UI and Functionality', () => {
  beforeEach(() => {
    // Assuming a base URL and a way to navigate to the add visit form
    // For example, visiting an owner's page and clicking 'Add New Visit'
    cy.visit('/owners/1/pets/1/visits/new'); // Adjust URL as per application routing
  });

  it('FR-010: Should display "Appointment Date" as the label for the date field', () => {
    cy.get('.add-visit-form-card label').contains('Appointment Date').should('be.visible');
  });

  it('FR-011: Should display "Reason for Visit" as the label for the description field', () => {
    cy.get('.add-visit-form-card label').contains('Reason for Visit').should('be.visible');
  });

  it('NFR-006: Should style the submit button with correct background and text color', () => {
    cy.get('.add-visit-submit-button')
      .should('have.css', 'background-color', 'rgb(40, 167, 69)') // #28A745
      .and('have.css', 'color', 'rgb(255, 255, 255)'); // white
  });

  it('NFR-007: Should style the cancel link as plain text with correct color', () => {
    cy.get('.add-visit-cancel-link')
      .should('have.css', 'color', 'rgb(108, 117, 125)') // #6C757D
      .and('have.css', 'text-decoration-line', 'none'); // plain text
  });

  it('NFR-008: Should style the form card/panel with the correct background color', () => {
    cy.get('.add-visit-form-card')
      .should('have.css', 'background-color', 'rgb(235, 245, 251)'); // #EBF5FB
  });

  it('NFR-009: Should ensure all form field labels are bold', () => {
    cy.get('.add-visit-form-card label')
      .each(($label) => {
        cy.wrap($label).should('have.css', 'font-weight', '600');
      });
  });

  // Basic functional test to ensure form submission still works
  it('Should successfully add a new visit', () => {
    const futureDate = new Date();
    futureDate.setDate(futureDate.getDate() + 7);
    const formattedDate = futureDate.toISOString().split('T')[0]; // YYYY-MM-DD

    cy.get('input[name="date"]').type(formattedDate);
    cy.get('input[name="description"]').type('Routine check-up');
    cy.get('.add-visit-submit-button').click();

    // Assuming redirection to owner details page and visit appears
    cy.url().should('include', '/owners/1');
    cy.contains('Routine check-up').should('be.visible');
    cy.contains('Your visit has been booked').should('be.visible'); // Flash message
  });
});
