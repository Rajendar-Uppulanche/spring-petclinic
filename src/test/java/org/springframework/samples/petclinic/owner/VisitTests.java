package org.springframework.samples.petclinic.owner;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

public class VisitTests {

    @Test
    void shouldSetDefaultStatusToScheduled() {
        Visit visit = new Visit();
        assertThat(visit.getStatus()).isEqualTo(VisitStatus.SCHEDULED);
    }

    @Test
    void shouldAllowScheduledToInProgress() {
        Visit visit = new Visit(); // Default SCHEDULED
        assertThat(visit.canTransitionTo(VisitStatus.IN_PROGRESS)).isTrue();
        visit.transitionTo(VisitStatus.IN_PROGRESS);
        assertThat(visit.getStatus()).isEqualTo(VisitStatus.IN_PROGRESS);
    }

    @Test
    void shouldAllowScheduledToCancelled() {
        Visit visit = new Visit(); // Default SCHEDULED
        assertThat(visit.canTransitionTo(VisitStatus.CANCELLED)).isTrue();
        visit.transitionTo(VisitStatus.CANCELLED);
        assertThat(visit.getStatus()).isEqualTo(VisitStatus.CANCELLED);
    }

    @Test
    void shouldAllowInProgressToCompleted() {
        Visit visit = new Visit();
        visit.transitionTo(VisitStatus.IN_PROGRESS); // Transition to IN_PROGRESS
        assertThat(visit.canTransitionTo(VisitStatus.COMPLETED)).isTrue();
        visit.transitionTo(VisitStatus.COMPLETED);
        assertThat(visit.getStatus()).isEqualTo(VisitStatus.COMPLETED);
    }

    @Test
    void shouldNotAllowScheduledToCompletedDirectly() {
        Visit visit = new Visit(); // Default SCHEDULED
        assertThat(visit.canTransitionTo(VisitStatus.COMPLETED)).isFalse();
        assertThrows(IllegalArgumentException.class, () -> visit.transitionTo(VisitStatus.COMPLETED));
    }

    @Test
    void shouldNotAllowCompletedToAnyOtherStatus() {
        Visit visit = new Visit();
        visit.transitionTo(VisitStatus.IN_PROGRESS);
        visit.transitionTo(VisitStatus.COMPLETED); // Now COMPLETED

        assertThat(visit.canTransitionTo(VisitStatus.SCHEDULED)).isFalse();
        assertThat(visit.canTransitionTo(VisitStatus.IN_PROGRESS)).isFalse();
        assertThat(visit.canTransitionTo(VisitStatus.CANCELLED)).isFalse();

        assertThrows(IllegalArgumentException.class, () -> visit.transitionTo(VisitStatus.SCHEDULED));
        assertThrows(IllegalArgumentException.class, () -> visit.transitionTo(VisitStatus.IN_PROGRESS));
        assertThrows(IllegalArgumentException.class, () -> visit.transitionTo(VisitStatus.CANCELLED));
    }

    @Test
    void shouldNotAllowCancelledToAnyOtherStatus() {
        Visit visit = new Visit();
        visit.transitionTo(VisitStatus.CANCELLED); // Now CANCELLED

        assertThat(visit.canTransitionTo(VisitStatus.SCHEDULED)).isFalse();
        assertThat(visit.canTransitionTo(VisitStatus.IN_PROGRESS)).isFalse();
        assertThat(visit.canTransitionTo(VisitStatus.COMPLETED)).isFalse();

        assertThrows(IllegalArgumentException.class, () -> visit.transitionTo(VisitStatus.SCHEDULED));
        assertThrows(IllegalArgumentException.class, () -> visit.transitionTo(VisitStatus.IN_PROGRESS));
        assertThrows(IllegalArgumentException.class, () -> visit.transitionTo(VisitStatus.COMPLETED));
    }

    @Test
    void shouldNotAllowInProgressToScheduledOrCancelled() {
        Visit visit = new Visit();
        visit.transitionTo(VisitStatus.IN_PROGRESS); // Now IN_PROGRESS

        assertThat(visit.canTransitionTo(VisitStatus.SCHEDULED)).isFalse();
        assertThat(visit.canTransitionTo(VisitStatus.CANCELLED)).isFalse();

        assertThrows(IllegalArgumentException.class, () -> visit.transitionTo(VisitStatus.SCHEDULED));
        assertThrows(IllegalArgumentException.class, () -> visit.transitionTo(VisitStatus.CANCELLED));
    }

    @Test
    void shouldSetAndGetDateDescriptionAndPet() {
        Visit visit = new Visit();
        LocalDate testDate = LocalDate.of(2024, 1, 1);
        String testDescription = "Routine checkup";
        Pet testPet = new Pet();
        testPet.setName("Fido");

        visit.setDate(testDate);
        visit.setDescription(testDescription);
        visit.setPet(testPet);

        assertThat(visit.getDate()).isEqualTo(testDate);
        assertThat(visit.getDescription()).isEqualTo(testDescription);
        assertThat(visit.getPet()).isEqualTo(testPet);
    }
}
