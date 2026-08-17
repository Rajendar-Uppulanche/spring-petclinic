package org.springframework.samples.petclinic.owner;

import java.util.ArrayList;
import java.util.List;

/**
 * A Data Transfer Object (DTO) for Owner details, including a list of PetDetailsDTOs.
 */
public class OwnerDetailsDTO {
    private Integer id;
    private String firstName;
    private String lastName;
    private String address;
    private String city;
    private String telephone;
    private List<PetDetailsDTO> pets = new ArrayList<>();

    public OwnerDetailsDTO(Owner owner) {
        this.id = owner.getId();
        this.firstName = owner.getFirstName();
        this.lastName = owner.getLastName();
        this.address = owner.getAddress();
        this.city = owner.getCity();
        this.telephone = owner.getTelephone();
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    public List<PetDetailsDTO> getPets() { return pets; }
    public void setPets(List<PetDetailsDTO> pets) { this.pets = pets; }
}
