function initVisitHistory(ownerId) {
    const fromDateInput = document.getElementById('fromDate');
    const toDateInput = document.getElementById('toDate');
    const keywordInput = document.getElementById('keyword');
    const applyFiltersBtn = document.getElementById('applyFilters');
    const clearFiltersBtn = document.getElementById('clearFilters');
    const filteredVisitsBody = document.getElementById('filteredVisitsBody');
    const noVisitsMessage = document.getElementById('noVisitsMessage');

    const STORAGE_KEY_PREFIX = 'visitHistoryFilter_';

    // Function to load filters from session storage (NFR-020)
    function loadFilters() {
        const storedFromDate = sessionStorage.getItem(STORAGE_KEY_PREFIX + ownerId + '_fromDate');
        const storedToDate = sessionStorage.getItem(STORAGE_KEY_PREFIX + ownerId + '_toDate');
        const storedKeyword = sessionStorage.getItem(STORAGE_KEY_PREFIX + ownerId + '_keyword');

        if (storedFromDate) fromDateInput.value = storedFromDate;
        if (storedToDate) toDateInput.value = storedToDate;
        if (storedKeyword) keywordInput.value = storedKeyword;
    }

    // Function to save filters to session storage (NFR-020)
    function saveFilters() {
        sessionStorage.setItem(STORAGE_KEY_PREFIX + ownerId + '_fromDate', fromDateInput.value);
        sessionStorage.setItem(STORAGE_KEY_PREFIX + ownerId + '_toDate', toDateInput.value);
        sessionStorage.setItem(STORAGE_KEY_PREFIX + ownerId + '_keyword', keywordInput.value);
    }

    // Function to clear filters from UI and session storage
    function clearFilters() {
        fromDateInput.value = '';
        toDateInput.value = '';
        keywordInput.value = '';
        sessionStorage.removeItem(STORAGE_KEY_PREFIX + ownerId + '_fromDate');
        sessionStorage.removeItem(STORAGE_KEY_PREFIX + ownerId + '_toDate');
        sessionStorage.removeItem(STORAGE_KEY_PREFIX + ownerId + '_keyword');
        fetchAndDisplayVisits(); // Fetch all visits after clearing filters
    }

    // Client-side date validation (BR-004)
    function validateDates(fromDate, toDate) {
        if (fromDate && toDate && new Date(fromDate) > new Date(toDate)) {
            alert('Error: "From Date" cannot be after "To Date".');
            return false;
        }
        return true;
    }

    // Function to fetch and display visits
    async function fetchAndDisplayVisits() {
        const fromDate = fromDateInput.value;
        const toDate = toDateInput.value;
        const keyword = keywordInput.value;

        if (!validateDates(fromDate, toDate)) {
            return;
        }

        saveFilters(); // Save current filter state

        let url = `/api/owners/${ownerId}/visits?`;
        const params = new URLSearchParams();
        if (fromDate) params.append('fromDate', fromDate);
        if (toDate) params.append('toDate', toDate);
        if (keyword) params.append('keyword', keyword);

        url += params.toString();

        try {
            const response = await fetch(url);
            if (!response.ok) {
                const errorText = await response.text();
                alert(`Error fetching visits: ${errorText}`);
                return;
            }
            const visits = await response.json();
            renderVisits(visits);
        } catch (error) {
            console.error('Failed to fetch visits:', error);
            alert('An error occurred while fetching visits.');
        }
    }

    // Function to render visits in the table
    function renderVisits(visits) {
        filteredVisitsBody.innerHTML = ''; // Clear previous results
        if (visits.length === 0) {
            noVisitsMessage.style.display = 'block';
            filteredVisitsBody.style.display = 'none';
        } else {
            noVisitsMessage.style.display = 'none';
            filteredVisitsBody.style.display = 'table-row-group'; // Show tbody
            visits.forEach(visit => {
                const row = filteredVisitsBody.insertRow();
                row.insertCell().textContent = visit.date;
                row.insertCell().textContent = visit.petName;
                row.insertCell().textContent = visit.description;
            });
        }
    }

    // Event Listeners
    applyFiltersBtn.addEventListener('click', fetchAndDisplayVisits);
    clearFiltersBtn.addEventListener('click', clearFilters);

    // Initial load of filters and visits
    loadFilters();
    fetchAndDisplayVisits();
}
