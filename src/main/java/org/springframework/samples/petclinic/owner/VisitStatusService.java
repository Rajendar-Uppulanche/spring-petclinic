package org.springframework.samples.petclinic.owner;

import org.springframework.stereotype.Service;

@Service
public class VisitStatusService {

    /**
     * Validates if a status transition is allowed based on business rules.
     * BR-008: A visit cannot transition from 'Completed' to 'Scheduled' or 'In Progress'.
     * BR-009: A visit cannot transition from 'Cancelled' to any other status.
     * BR-010: Allowed transitions: Scheduled -> In Progress, Scheduled -> Cancelled, In Progress -> Completed.
     *
     * @param currentStatus The current status of the visit.
     * @param newStatus     The desired new status for the visit.
     * @return true if the transition is allowed, false otherwise.
     */
    public boolean isValidTransition(VisitStatus currentStatus, VisitStatus newStatus) {
        if (currentStatus == null || newStatus == null) {
            // Should not happen with @NotNull, but defensive check
            return false;
        }

        if (currentStatus.equals(newStatus)) {
            return true; // No change, always valid
        }

        // BR-009: A visit cannot transition from 'Cancelled' to any other status.
        if (currentStatus == VisitStatus.CANCELLED) {
            return false;
        }

        // BR-008: A visit cannot transition from 'Completed' to 'Scheduled' or 'In Progress'.
        if (currentStatus == VisitStatus.COMPLETED) {
            return false; // Cannot change from completed to anything else (except completed itself, handled above)
        }

        // BR-010: Allowed transitions
        switch (currentStatus) {
            case SCHEDULED:
                return newStatus == VisitStatus.IN_PROGRESS || newStatus == VisitStatus.CANCELLED;
            case IN_PROGRESS:
                return newStatus == VisitStatus.COMPLETED;
            default:
                return false; // Should not reach here for other statuses
        }
    }
}