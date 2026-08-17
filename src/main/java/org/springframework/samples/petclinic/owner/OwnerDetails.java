package org.springframework.samples.petclinic.owner;

import java.util.List;

public class OwnerDetails extends Owner {

    private List<PetDetails> petDetails;

    public OwnerDetails() {
        super();
    }

    public List<PetDetails> getPetDetails() {
        return petDetails;
    }

    public void setPetDetails(List<PetDetails> petDetails) {
        this.petDetails = petDetails;
    }
}
