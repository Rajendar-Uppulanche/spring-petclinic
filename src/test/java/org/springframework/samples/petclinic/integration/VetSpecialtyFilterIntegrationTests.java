package org.springframework.samples.petclinic.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.samples.petclinic.vet.Specialty;
import org.springframework.samples.petclinic.vet.SpecialtyRepository;
import org.springframework.samples.petclinic.vet.Vet;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
class VetSpecialtyFilterIntegrationTests {

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
        Specialty radiology = new Specialty();
        radiology.setId(1);
        radiology.setName("radiology");

        Specialty surgery = new Specialty();
        surgery.setId(2);
        surgery.setName("surgery");

        Specialty dentistry = new Specialty();
        dentistry.setId(3);
        dentistry.setName("dentistry");

        Vet vet1 = new Vet();
        vet1.setId(1);
        vet1.setFirstName("James");
        vet1.setLastName("Carter");
        vet1.addSpecialty(radiology);

        Vet vet2 = new Vet();
        vet2.setId(2);
        vet2.setFirstName("Helen");
        vet2.setLastName("Leary");
        vet2.addSpecialty(surgery);
        vet2.addSpecialty(dentistry);

        vets = Arrays.asList(vet1, vet2);
        specialties = Arrays.asList(radiology, surgery, dentistry);

        Page<Vet> vetPage = new PageImpl<>(vets, PageRequest.of(0, 5), vets.size());
        given(this.vetRepository.findAll(any(Pageable.class))).willReturn(vetPage);
        given(this.specialtyRepository.findAll()).willReturn(specialties);
    }

    @Test
    void testVetsPageContainsSpecialtyFilterAndScript() throws Exception {
        MvcResult result = mockMvc.perform(get("/vets.html"))
            .andExpect(status().isOk())
            .andExpect(view().name("vets/vetList"))
            .andReturn();

        String content = result.getResponse().getContentAsString();

        // Verify specialty filter dropdown exists
        assertThat(content).contains("<select id=\"specialtyFilter\"");
        assertThat(content).contains("<option value=\"\">All specialties</option>");
        assertThat(content).contains("<option th:each=\"specialty : ${specialties}\" th:value=\"${specialty.name}\" th:text=\"${specialty.name}\"></option>");
        assertThat(content).contains("radiology</option>");
        assertThat(content).contains("surgery</option>");
        assertThat(content).contains("dentistry</option>");

        // Verify vet rows have data-specialties attribute
        assertThat(content).contains("<tr th:each=\"vet : ${listVets}\" th:data-specialties=\"${\#strings.listJoin(vet.specialties.![name], ',')}\">");
        assertThat(content).contains("data-specialties=\"radiology\"");
        assertThat(content).contains("data-specialties=\"surgery,dentistry\"");

        // Verify JavaScript file is included
        assertThat(content).contains("<script src=\"/resources/js/vet-filter.js\"></script>");

        // Verify "No vets found" message exists
        assertThat(content).contains("<div id=\"noVetsFound\" style=\"display: none;\"");
    }

    @Test
    void testVetsPageWithNoSpecialties() throws Exception {
        given(this.specialtyRepository.findAll()).willReturn(List.of()); // No specialties

        MvcResult result = mockMvc.perform(get("/vets.html"))
            .andExpect(status().isOk())
            .andExpect(view().name("vets/vetList"))
            .andReturn();

        String content = result.getResponse().getContentAsString();

        // Verify specialty filter dropdown still exists but will only have "All specialties"
        assertThat(content).contains("<select id=\"specialtyFilter\"");
        assertThat(content).contains("<option value=\"\">All specialties</option>");
        assertThat(content).doesNotContain("radiology</option>"); // No actual specialties
    }
}
