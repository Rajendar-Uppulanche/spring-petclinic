package org.springframework.samples.petclinic.owner;

public class PetDetails extends Pet {

    private String formattedVisitCount;

    public PetDetails() {
        super();
    }

    public String getFormattedVisitCount() {
        return formattedVisitCount;
    }

    public void setFormattedVisitCount(String formattedVisitCount) {
        this.formattedVisitCount = formattedVisitCount;
    }
}
