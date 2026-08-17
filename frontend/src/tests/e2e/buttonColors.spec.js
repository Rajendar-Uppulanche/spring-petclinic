describe('Test Suite 27: Button Color Verification', () => {
  beforeEach(() => {
    // Assuming a page with buttons exists, e.g., a login page or component demo
    cy.visit('/components/buttons'); // Or a relevant URL
  });

  it('should verify the primary button has the correct background and text color (FR-100)', () => {
    cy.get('.btn-primary').should('have.css', 'background-color', 'rgb(76, 175, 80)'); // #4CAF50
    cy.get('.btn-primary').should('have.css', 'color', 'rgb(255, 255, 255)'); // #fff
  });

  it('should verify the secondary button has the correct background and text color (FR-100)', () => {
    cy.get('.btn-secondary').should('have.css', 'background-color', 'rgb(255, 152, 0)'); // #FF9800
    cy.get('.btn-secondary').should('have.css', 'color', 'rgb(255, 255, 255)'); // #fff
  });

  it('should ensure button colors are visually distinct for accessibility (NFR-087)', () => {
    cy.get('.btn-primary').should('not.have.css', 'background-color', 'rgb(255, 152, 0)');
    cy.get('.btn-secondary').should('not.have.css', 'background-color', 'rgb(76, 175, 80)');
  });
});