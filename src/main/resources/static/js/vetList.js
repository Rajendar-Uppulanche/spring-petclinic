document.addEventListener('DOMContentLoaded', function() {
    const specialtyFilter = document.getElementById('specialtyFilter');
    const vetTableBody = document.querySelector('#vetTable tbody');
    const noVetsMessage = document.getElementById('noVetsMessage');
    const noSpecialtiesMessage = document.getElementById('noSpecialtiesMessage');

    async function fetchAndDisplayVets(specialty = 'all') {
        try {
            const url = specialty === 'all' ? '/api/vets' : `/api/vets?specialty=${encodeURIComponent(specialty)}`;
            const response = await fetch(url);
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            const vets = await response.json();
            renderVets(vets);
        } catch (error) {
            console.error('Error fetching veterinarians:', error);
            vetTableBody.innerHTML = '<tr><td colspan="3" style="color: red;">Failed to load veterinarians.</td></tr>';
            noVetsMessage.style.display = 'none';
        }
    }

    function renderVets(vets) {
        vetTableBody.innerHTML = '';
        if (vets.length === 0) {
            noVetsMessage.style.display = 'block';
        } else {
            noVetsMessage.style.display = 'none';
            vets.forEach(vet => {
                const row = vetTableBody.insertRow();
                row.insertCell().textContent = vet.firstName;
                row.insertCell().textContent = vet.lastName;
                const specialtiesCell = row.insertCell();
                if (vet.specialties && vet.specialties.length > 0) {
                    specialtiesCell.textContent = vet.specialties.map(s => s.name).join(', ');
                } else {
                    specialtiesCell.textContent = 'None';
                }
            });
        }
    }

    async function fetchAndPopulateSpecialties() {
        try {
            const response = await fetch('/api/specialties');
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            const specialties = await response.json();
            if (specialties.length === 0) {
                noSpecialtiesMessage.style.display = 'block';
                specialtyFilter.disabled = true;
            } else {
                noSpecialtiesMessage.style.display = 'none';
                specialtyFilter.disabled = false;
                while (specialtyFilter.options.length > 1) {
                    specialtyFilter.remove(1);
                }
                specialties.sort((a, b) => a.name.localeCompare(b.name)).forEach(specialty => {
                    const option = document.createElement('option');
                    option.value = specialty.name;
                    option.textContent = specialty.name;
                    specialtyFilter.appendChild(option);
                });
            }
        } catch (error) {
            console.error('Error fetching specialties:', error);
            noSpecialtiesMessage.style.display = 'block';
            noSpecialtiesMessage.textContent = 'Failed to load specialties for filtering.';
            specialtyFilter.disabled = true;
        }
    }

    specialtyFilter.addEventListener('change', function() {
        const selectedSpecialty = this.value;
        fetchAndDisplayVets(selectedSpecialty);
    });

    fetchAndPopulateSpecialties();
    fetchAndDisplayVets();
});