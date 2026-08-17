import React, { useState, useEffect } from 'react';
import AppointmentFilter from '../components/AppointmentFilter';
import './AppointmentListPage.css'; // Assuming some basic styling

const AppointmentListPage = () => {
    const [appointments, setAppointments] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const [currentFilters, setCurrentFilters] = useState({});

    const fetchAppointments = async (filters = {}) => {
        setLoading(true);
        setError(null);
        try {
            const queryParams = new URLSearchParams();
            if (filters.startDate) queryParams.append('start_date', new Date(filters.startDate).toISOString());
            if (filters.endDate) queryParams.append('end_date', new Date(filters.endDate).toISOString());
            if (filters.petName) queryParams.append('pet_name', filters.petName);
            if (filters.ownerName) queryParams.append('owner_name', filters.ownerName);
            if (filters.veterinarianId) queryParams.append('veterinarian_id', filters.veterinarianId);
            if (filters.status) queryParams.append('status', filters.status);

            const response = await fetch(`/api/appointments?${queryParams.toString()}`);
            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.error || 'Failed to fetch appointments');
            }
            const data = await response.json();
            setAppointments(data);
        } catch (err) {
            setError(err.message);
            setAppointments([]); // Clear appointments on error
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchAppointments(currentFilters);
    }, [currentFilters]); // Re-fetch when filters change

    const handleApplyFilters = (filters) => {
        setCurrentFilters(filters);
    };

    const handleClearFilters = () => {
        setCurrentFilters({});
    };

    return (
        <div className="appointment-list-page">
            <h1>Appointments</h1>
            <AppointmentFilter
                onApplyFilters={handleApplyFilters}
                onClearFilters={handleClearFilters}
            />

            {loading && <p>Loading appointments...</p>}
            {error && <p className="error-message">Error: {error}</p>}

            {!loading && !error && appointments.length === 0 && (
                <p className="no-results-message">No appointments found matching your criteria.</p>
            )}

            {!loading && !error && appointments.length > 0 && (
                <div className="appointment-list">
                    {appointments.map(appointment => (
                        <div key={appointment.id} className="appointment-card">
                            <h3>{appointment.pet_name} - {appointment.owner_name}</h3>
                            <p>Date: {new Date(appointment.appointment_date).toLocaleString()}</p>
                            <p>Reason: {appointment.reason}</p>
                            <p>Veterinarian ID: {appointment.veterinarian_id || 'N/A'}</p>
                            <p>Status: {appointment.status}</p>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
};

export default AppointmentListPage;
