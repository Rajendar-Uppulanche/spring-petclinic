package org.springframework.samples.petclinic.appointment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.samples.petclinic.model.BaseEntity;
import org.springframework.samples.petclinic.owner.Pet;
import org.springframework.samples.petclinic.vet.Vet;

import java.time.LocalDate;

@Entity
@Table(name = "appointments")
public class Appointment extends BaseEntity {

	@Column(name = "appointment_date")
	@DateTimeFormat(pattern = "yyyy/MM/dd")
	@NotNull
	private LocalDate date;

	@Column(name = "description")
	private String description;

	@ManyToOne
	@JoinColumn(name = "pet_id")
	@NotNull
	private Pet pet;

	@ManyToOne
	@JoinColumn(name = "vet_id")
	private Vet vet;

	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	@NotNull
	private AppointmentStatus status;

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Pet getPet() {
		return pet;
	}

	public void setPet(Pet pet) {
		this.pet = pet;
	}

	public Vet getVet() {
		return vet;
	}

	public void setVet(Vet vet) {
		this.vet = vet;
	}

	public AppointmentStatus getStatus() {
		return status;
	}

	public void setStatus(AppointmentStatus status) {
		this.status = status;
	}

}
