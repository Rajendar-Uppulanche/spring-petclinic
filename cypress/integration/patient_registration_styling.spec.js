describe('Patient Registration Form Styling', () => {
  beforeEach(() => {
    cy.visit('/patient-registration');
  });

  it('should display the header with dark blue color, 22px font size, and bold font weight', () => {
    cy.get('.patient-registration-header')
      .should('have.css', 'color', 'rgb(26, 60, 110)')
      .should('have.css', 'font-size', '22px')
      .should('have.css', 'font-weight', '700');
  });

  it('should display the submit button with a green background color', () => {
    cy.get('.patient-registration-submit-button')
      .should('have.css', 'background-color', 'rgb(0, 177, 64)');
  });

  it('should display the cancel link as italic and grey', () => {
    cy.get('.patient-registration-cancel-link')
      .should('have.css', 'font-style', 'italic')
      .should('have.css', 'color', 'rgb(128, 128, 128)');
  });
});