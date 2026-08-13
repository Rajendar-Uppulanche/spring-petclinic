document.addEventListener('DOMContentLoaded', function() {
    const specialtyFilter = document.getElementById('specialtyFilter');
    const vetTableBody = document.querySelector('#vets tbody');
    const vetRows = vetTableBody ? Array.from(vetTableBody.querySelectorAll('tr')) : [];
    const noVetsFoundMessage = document.getElementById('noVetsFound');

    if (!specialtyFilter) {
        // If no specialty filter dropdown, perhaps no specialties were passed or it's not the vets.html page
        return;
    }

    // Check if there are any specialties other than "All specialties"
    // If only one option (All specialties) is present, hide the filter.
    if (specialtyFilter.options.length <= 1) {
        specialtyFilter.style.display = 'none';
        specialtyFilter.previousElementSibling.style.display = 'none'; // Hide the label too
        return;
    }

    specialtyFilter.addEventListener('change', function() {
        const selectedSpecialty = this.value;
        let vetsFound = 0;

        vetRows.forEach(row => {
            const specialtiesData = row.getAttribute('data-specialties');
            const specialties = specialtiesData ? specialtiesData.split(',').map(s => s.trim()) : [];

            if (selectedSpecialty === '' || specialties.includes(selectedSpecialty)) {
                row.style.display = '';
                vetsFound++;
            } else {
                row.style.display = 'none';
            }
        });

        if (noVetsFoundMessage) {
            if (vetsFound === 0) {
                noVetsFoundMessage.style.display = 'block';
            } else {
                noVetsFoundMessage.style.display = 'none';
            }
        }
    });
});
