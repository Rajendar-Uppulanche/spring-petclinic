import React, { useState } from 'react';
import './AppointmentFilter.css'; // Assuming some basic styling

const AppointmentFilter = ({ onApplyFilters, onClearFilters }) => {
    const [filters, setFilters] = useState({
        startDate: '',
        endDate: '',
        petName: '',
        ownerName: '',
        veterinarianId: '',
        status: '',
    });

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFilters(prevFilters => ({
            ...prevFilters,
            [name]: value,
        }));
    };

    const handleApply = () => {
        // Basic client-side validation for dates
        if (filters.startDate && filters.endDate && new Date(filters.startDate) > new Date(filters.endDate)) {
            alert('Start date cannot be after end date.');
            return;
        }
        onApplyFilters(filters);
    };

    const handleClear = () => {
        setFilters({
            startDate: '',
            endDate: '',
            petName: '',
            ownerName: '',
            veterinarianId: '',
            status: '',
        });
        onClearFilters();
    };

    return (
        <div className="appointment-filter-card">
            <h3>Filter Appointments</h3>
            <div className="filter-grid">
                <div className="filter-group">
                    <label htmlFor="startDate">Start Date:</label>
                    <input
                        type="date"
                        id="startDate"
                        name="startDate"
                        value={filters.startDate}
                        onChange={handleChange}
                    />
                </div>
                <div className="filter-group">
                    <label htmlFor="endDate">End Date:</label>
                    <input
                        type="date"
                        id="endDate"
                        name="endDate"
                        value={filters.endDate}
                        onChange={handleChange}
                    />
                </div>
                <div className="filter-group">
                    <label htmlFor="petName">Pet Name:</label>
                    <input
                        type="text"
                        id="petName"
                        name="petName"
                        value={filters.petName}
                        onChange={handleChange}
                        placeholder="e.g., Buddy"
                    />
                </div>
                <div className="filter-group">
                    <label htmlFor="ownerName">Owner Name:</label>
                    <input
                        type="text"
                        id="ownerName"
                        name="ownerName"
                        value={filters.ownerName}
                        onChange={handleChange}
                        placeholder="e.g., Jane Doe"
                    />
                </div>
                <div className="filter-group">
                    <label htmlFor="veterinarianId">Veterinarian ID:</label>
                    <input
                        type="number"
                        id="veterinarianId"
                        name="veterinarianId"
                        value={filters.veterinarianId}
                        onChange={handleChange}
                        placeholder="e.g., 1"
                    />
                </div>
                <div className="filter-group">
                    <label htmlFor="status">Status:</label>
                    <select
                        id="status"
                        name="status"
                        value={filters.status}
                        onChange={handleChange}
                    >
                        <option value="">All</option>
                        <option value="SCHEDULED">Scheduled</option>
                        <option value="COMPLETED">Completed</option>
                        <option value="CANCELLED">Cancelled</option>
                        <option value="PENDING">Pending</option>
                    </select>
                </div>
            </div>
            <div className="filter-actions">
                <button onClick={handleApply} className="btn-primary">Apply Filters</button>
                <button onClick={handleClear} className="btn-secondary">Clear Filters</button>
            </div>
        </div>
    );
};

export default AppointmentFilter;
