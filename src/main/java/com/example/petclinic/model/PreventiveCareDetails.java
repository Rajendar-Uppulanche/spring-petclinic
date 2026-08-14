package com.example.petclinic.model;

import javax.persistence.Embeddable;
import java.time.LocalDate;

@Embeddable
public class PreventiveCareDetails {

    private String vaccineName;
    private String dosage;
    private LocalDate nextDueDate;

    public String getVaccineName() {
        return vaccineName;
    }

    public void setVaccineName(String vaccineName) {
        this.vaccineName = vaccineName;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public LocalDate getNextDueDate() {
        return nextDueDate;
    }

    public void setNextDueDate(LocalDate nextDueDate) {
        this.nextDueDate = nextDueDate;
    }
}