package org.springframework.samples.petclinic.owner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationPreferenceServiceTests {

    @Mock
    private OwnerRepository ownerRepository;

    @InjectMocks
    private NotificationPreferenceService notificationPreferenceService;

    private Owner owner;

    @BeforeEach
    void setUp() {
        owner = new Owner();
        owner.setId(1);
        owner.setFirstName("Jane");
        owner.setLastName("Doe");
        owner.setReceivesVaccinationReminders(true);
    }

    @Test
    void shouldUnsubscribeOwner() {
        when(ownerRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(ownerRepository.save(any(Owner.class))).thenReturn(owner);

        boolean result = notificationPreferenceService.unsubscribe(owner.getId());

        assertThat(result).isTrue();
        assertThat(owner.getReceivesVaccinationReminders()).isFalse();
        verify(ownerRepository, times(1)).save(owner);
    }

    @Test
    void shouldSubscribeOwner() {
        owner.setReceivesVaccinationReminders(false); 
        when(ownerRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(ownerRepository.save(any(Owner.class))).thenReturn(owner);

        boolean result = notificationPreferenceService.subscribe(owner.getId());

        assertThat(result).isTrue();
        assertThat(owner.getReceivesVaccinationReminders()).isTrue();
        verify(ownerRepository, times(1)).save(owner);
    }

    @Test
    void shouldReturnFalseIfOwnerNotFoundForUnsubscribe() {
        when(ownerRepository.findById(anyInt())).thenReturn(Optional.empty());

        boolean result = notificationPreferenceService.unsubscribe(999);

        assertThat(result).isFalse();
        verify(ownerRepository, never()).save(any(Owner.class));
    }

    @Test
    void shouldReturnFalseIfOwnerNotFoundForSubscribe() {
        when(ownerRepository.findById(anyInt())).thenReturn(Optional.empty());

        boolean result = notificationPreferenceService.subscribe(999);

        assertThat(result).isFalse();
        verify(ownerRepository, never()).save(any(Owner.class));
    }
}