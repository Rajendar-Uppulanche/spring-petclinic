package org.springframework.samples.petclinic.owner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
class VisitController {

    private final OwnerRepository owners;
    private final PetRepository pets;
    private final VisitRepository visits;

    @Autowired
    public VisitController(OwnerRepository owners, PetRepository pets, VisitRepository visits) {
        this.owners = owners;
        this.pets = pets;
        this.visits = visits;
    }

    @InitBinder
    public void setAllowedFields(WebDataBinder dataBinder) {
        dataBinder.setDisallowedFields("id");
    }

    /**
     * Called before each and every @RequestMapping annotated method.
     * 2 goals:
     * - Make sure that owner has been loaded and is not null
     * - Make sure that pet has been loaded and is not null
     * @param petId
     * @param ownerId
     * @param model
     * @return
     */
    @ModelAttribute("visit")
    public Visit loadPetWithVisit(@PathVariable("petId") int petId, @PathVariable("ownerId") int ownerId, Model model) {
        Pet pet = this.pets.findById(petId);
        pet.setOwner(this.owners.findById(ownerId));
        model.addAttribute("pet", pet);
        Visit visit = new Visit();
        pet.addVisit(visit);
        return visit;
    }

    // Spring MVC calls method loadPetWithVisit(...) before initNewVisitForm method
    @GetMapping("/owners/{ownerId}/pets/{petId}/visits/new")
    public String initNewVisitForm(@PathVariable("petId") int petId, Map<String, Object> model) {
        return "pets/createOrUpdateVisitForm";
    }

    // Spring MVC calls method loadPetWithVisit(...) before processNewVisitForm method
    @PostMapping("/owners/{ownerId}/pets/{petId}/visits/new")
    public String processNewVisitForm(@Valid Visit visit, BindingResult result) {
        if (result.hasErrors()) {
            return "pets/createOrUpdateVisitForm";
        } else {
            this.visits.save(visit);
            return "redirect:/owners/{ownerId}";
        }
    }

    @GetMapping("/owners/{ownerId}/visits")
    public ModelAndView showVisits(@PathVariable("ownerId") int ownerId) {
        ModelAndView mav = new ModelAndView("owners/ownerDetails");
        Owner owner = this.owners.findById(ownerId);
        mav.addObject(owner);
        return mav;
    }

    // New API endpoint for filtered visits
    @GetMapping("/api/owners/{ownerId}/visits")
    public ResponseEntity<?> getFilteredVisits(
        @PathVariable("ownerId") int ownerId,
        @RequestParam(value = "fromDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
        @RequestParam(value = "toDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
        @RequestParam(value = "keyword", required = false) String keyword) {

        // Server-side validation: fromDate should not be later than toDate (BR-004)
        if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
            return ResponseEntity.badRequest().body("From Date cannot be after To Date.");
        }

        List<Visit> filteredVisits = this.visits.findByOwnerIdAndFilters(ownerId, fromDate, toDate, keyword);

        // Map visits to a DTO for API response
        List<VisitDto> visitDtos = filteredVisits.stream()
            .map(visit -> new VisitDto(visit.getId(), visit.getDate(), visit.getDescription(), visit.getPet().getName()))
            .collect(Collectors.toList());

        return new ResponseEntity<>(visitDtos, HttpStatus.OK);
    }

    // DTO for visits to avoid exposing full entities
    static class VisitDto {
        public Integer id;
        public LocalDate date;
        public String description;
        public String petName;

        public VisitDto(Integer id, LocalDate date, String description, String petName) {
            this.id = id;
            this.date = date;
            this.description = description;
            this.petName = petName;
        }
    }
}
