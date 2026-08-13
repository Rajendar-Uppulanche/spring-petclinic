// cypress/e2e/primary_color.cy.ts

describe('Primary UI Color Verification', () => {
  it('should display the primary button with the new grey accent color', () => {
    cy.visit('/'); // Assuming the application runs at the root

    // Find a primary button or element that uses the primary theme color
    // This selector might need adjustment based on actual application structure
    cy.get('.btn-primary').first().should('exist').then(($btn) => {
      // Get the computed background color
      const backgroundColor = $btn.css('background-color');

      // Convert hex #6c757d to RGB for comparison (Cypress gets computed styles as RGB)
      // #6c757d is rgb(108, 117, 125)
      expect(backgroundColor).to.equal('rgb(108, 117, 125)');
    });

    // Optionally, check text color for accessibility if it's also affected
    cy.get('.btn-primary').first().should('exist').then(($btn) => {
      const textColor = $btn.css('color');
      // Assuming white text on dark grey for contrast
      expect(textColor).to.equal('rgb(255, 255, 255)');
    });
  });

  it('should display primary navigation links with the new grey accent color', () => {
    cy.visit('/');
    // Assuming there's a navigation link that uses the primary color
    cy.get('nav .nav-link.active').first().should('exist').then(($link) => {
      const color = $link.css('color');
      // Assuming the primary color is used for active links or hover states
      // This might be the text color, not background
      expect(color).to.equal('rgb(108, 117, 125)');
    });
  });
});