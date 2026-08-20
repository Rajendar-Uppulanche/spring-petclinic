package org.springframework.samples.petclinic.vaccination;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.samples.petclinic.model.BaseEntity;

@Entity
@Table(name = "vaccine_types")
public class VaccineType extends BaseEntity {

    @Column(name = "name")
    @NotEmpty
    private String name;

    @Column(name = "default_next_due_interval_days")
    private Integer defaultNextDueIntervalDays; // Interval in days for next due date calculation

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getDefaultNextDueIntervalDays() {
        return defaultNextDueIntervalDays;
    }

    public void setDefaultNextDueIntervalDays(Integer defaultNextDueIntervalDays) {
        this.defaultNextDueIntervalDays = defaultNextDueIntervalDays;
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
