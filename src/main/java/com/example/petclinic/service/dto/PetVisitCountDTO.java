package com.example.petclinic.service.dto;

public class PetVisitCountDTO {
    private Long petId;
    private Long visitCount;

    public PetVisitCountDTO(Long petId, Long visitCount) {
        this.petId = petId;
        this.visitCount = visitCount;
    }

    public Long getPetId() {
        return petId;
    }

    public void setPetId(Long petId) {
        this.petId = petId;
    }

    public Long getVisitCount() {
        return visitCount;
    }

    public void setVisitCount(Long visitCount) {
        this.visitCount = visitCount;
    }
}