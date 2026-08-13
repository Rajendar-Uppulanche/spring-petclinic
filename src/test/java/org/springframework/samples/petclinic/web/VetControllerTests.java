package org.springframework.samples.petclinic.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.samples.petclinic.vet.Specialty;
import org.springframework.samples.petclinic.vet.SpecialtyRepository;
import org.springframework.samples.petclinic.vet.Vet;
import org.springframework.samples.petclinic.vet.VetController;
import org.springframework.samples.petclinic.vet.VetRepository;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(VetController.class)
class VetControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VetRepository vetRepository;

    @MockBean
    private SpecialtyRepository specialtyRepository;

    private List<Vet> vets;
    private List<Specialty> specialties;

    @BeforeEach
    void setup() {
        Vet vet1 = new Vet();
        vet1.setId(1);
        vet1.setFirstName("James");
        vet1.setLastName("Carter");
        Specialty radiology = new Specialty();
        radiology.setId(1);
        radiology.setName("radiology");
        vet1.addSpecialty(radiology);

        Vet vet2 = new Vet();
        vet2.setId(2);
        vet2.setFirstName("Helen");
        vet2.setLastName("Leary");
        Specialty surgery = new Specialty();
        surgery.setId(2);
        surgery.setName("surgery");
        vet2.addSpecialty(surgery);
        Specialty dentistry = new Specialty();
        dentistry.setId(3);
        dentistry.setName("dentistry");
        vet2.addSpecialty(dentistry);

        vets = Arrays.asList(vet1, vet2);
        specialties = Arrays.asList(radiology, surgery, dentistry);

        Page<Vet> vetPage = new PageImpl<>(vets, PageRequest.of(0, 5), vets.size());
        given(this.vetRepository.findAll(any(Pageable.class))).willReturn(vetPage);
        given(this.specialtyRepository.findAll()).willReturn(specialties);
    }

    @Test
    void testShowVetList() throws Exception {
        mockMvc.perform(get("/vets.html"))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("listVets"))
            .andExpect(model().attributeExists("specialties"))
            .andExpect(view().name("vets/vetList"));
    }

    @Test
    void testShowResourcesVetList() throws Exception {
        given(this.vetRepository.findAll()).willReturn(vets);
        mockMvc.perform(get("/vets"))
            .andExpect(status().isOk())
            .andExpect(model().attributeDoesNotExist("specialties"))
            .andExpect(model().attributeExists("vets"));
    }
}
