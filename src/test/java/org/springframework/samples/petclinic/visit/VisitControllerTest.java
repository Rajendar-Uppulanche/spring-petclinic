package org.springframework.samples.petclinic.visit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.pet.Pet;
import org.springframework.samples.petclinic.util.CsvExportService;
import org.springframework.test.web.servlet.MockMvc;

import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VisitController.class)
class VisitControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VisitService visitService;

    @MockBean
    private CsvExportService csvExportService;

    private Pet testPet;
    private Visit visit1;
    private Visit visit2;

    @BeforeEach
    void setup() {
        testPet = new Pet();
        testPet.setId(1);
        testPet.setName("Leo");

        visit1 = new Visit();
        visit1.setId(10);
        visit1.setDate(LocalDate.of(2023, 1, 15));
        visit1.setDescription("Routine checkup");
        visit1.setPet(testPet);
        visit1.setStatus("Completed");
        visit1.setDurationMinutes(30);
        visit1.setDiagnosisCode("A01");
        visit1.setTreatmentTags("Vaccination,Deworming");

        visit2 = new Visit();
        visit2.setId(11);
        visit2.setDate(LocalDate.of(2023, 2, 20));
        visit2.setDescription("Follow-up");
        visit2.setPet(testPet);
        visit2.setStatus("Scheduled");
        visit2.setDurationMinutes(45);
        visit2.setDiagnosisCode("B02");
        visit2.setTreatmentTags("Medication");
    }

    @Test
    void testGetPaginatedVisits() throws Exception {
        List<Visit> visits = Arrays.asList(visit2, visit1); // Sorted by date desc
        Page<Visit> visitPage = new PageImpl<>(visits, PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "date")), 2);

        given(visitService.findVisitsByPetId(eq(1), any(Pageable.class))).willReturn(visitPage);

        mockMvc.perform(get("/api/pets/{petId}/visits", 1)
                .param("page", "0")
                .param("size", "10")
                .param("sort", "date,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.visits[0].id").value(11))
            .andExpect(jsonPath("$.visits[0].description").value("Follow-up"))
            .andExpect(jsonPath("$.visits[0].diagnosisCode").value("B02"))
            .andExpect(jsonPath("$.visits[0].treatmentTags").value("Medication"))
            .andExpect(jsonPath("$.visits[1].id").value(10))
            .andExpect(jsonPath("$.visits[1].description").value("Routine checkup"))
            .andExpect(jsonPath("$.visits[1].diagnosisCode").value("A01"))
            .andExpect(jsonPath("$.visits[1].treatmentTags").value("Vaccination,Deworming"))
            .andExpect(jsonPath("$.currentPage").value(0))
            .andExpect(jsonPath("$.totalItems").value(2))
            .andExpect(jsonPath("$.totalPages").value(1));

        verify(visitService).findVisitsByPetId(eq(1), any(Pageable.class));
    }

    @Test
    void testExportVisitsToCsv() throws Exception {
        List<Visit> visits = Arrays.asList(visit1, visit2);
        given(visitService.findAllVisitsByPetId(1)).willReturn(visits);
        given(visitService.findPetById(1)).willReturn(testPet);

        // Mock the CsvExportService to write some content
        Mockito.doAnswer(invocation -> {
            PrintWriter writer = invocation.getArgument(0);
            List<Visit> visitsArg = invocation.getArgument(1);
            writer.println("Header1,Header2");
            writer.println("Value1,Value2");
            return null;
        }).when(csvExportService).writeVisitsToCsv(any(PrintWriter.class), eq(visits));

        mockMvc.perform(get("/api/pets/{petId}/visits/export", 1))
            .andExpect(status().isOk())
            .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, org.hamcrest.Matchers.startsWith("attachment; filename=\"petclinic_visits_Leo_")))
            .andExpect(header().string(HttpHeaders.CONTENT_TYPE, "text/csv"))
            .andExpect(content().string("Header1,Header2\nValue1,Value2\n")); // Verify content written by mock

        verify(visitService).findAllVisitsByPetId(1);
        verify(visitService).findPetById(1);
        verify(csvExportService).writeVisitsToCsv(any(PrintWriter.class), eq(visits));
    }

    @Test
    void testExportVisitsToCsv_PetNotFound() throws Exception {
        given(visitService.findAllVisitsByPetId(1)).willReturn(Collections.emptyList());
        given(visitService.findPetById(1)).willReturn(null);

        mockMvc.perform(get("/api/pets/{petId}/visits/export", 1))
            .andExpect(status().isNotFound());

        verify(visitService).findPetById(1);
    }
}
