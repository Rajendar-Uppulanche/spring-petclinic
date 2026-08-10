/**
	 * Handles the submission of a new visit form.
	 * @param pet The pet to add a visit to.
	 * @param visit The visit data to save.
	 * @param result the binding result, which validates the data
	 * @return a String representing the view to show next
	 */
	@PostMapping("/owners/{ownerId}/pets/{petId}/visits/new")
	public String showNewVisitForm(@PathVariable("ownerId") int ownerId, @PathVariable("petId") int petId, Map<String, Object> model) {
		return "owners/createOrUpdateVisitForm";
	}

	@PostMapping("/owners/{ownerId}/pets/{petId}/visits/save")
	public String processNewVisitForm(@Valid Visit visit, BindingResult result, @PathVariable int petId) {
		if (result.hasErrors()) {
			return "owners/createOrUpdateVisitForm";
		}
		else {
			this.clinicService.saveNewVisit(petId, visit);
			return "redirect:/owners/{ownerId}";
		}
	}