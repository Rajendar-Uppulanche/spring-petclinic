const OwnerContactInfoComponent = {
    render: (owner) => {
        if (!owner) {
            return '<p>No owner contact information available.</p>';
        }
        return `
            <div class="owner-contact-info">
                <h3>Owner Contact Information</h3>
                <p><strong>Name:</strong> ${owner.firstName} ${owner.lastName}</p>
                <p><strong>Telephone:</strong> ${owner.telephone}</p>
                <p><strong>Email:</strong> ${owner.email}</p>
                <p><a href="/owners/${owner.ownerId}">View Full Owner Profile</a></p>
            </div>
        `;
    }
};