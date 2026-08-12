package org.springframework.samples.petclinic.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.samples.petclinic.pet.Pet;
import org.springframework.samples.petclinic.vet.Vet;
import org.springframework.samples.petclinic.visit.Visit;

import java.io.StringWriter;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CsvExportServiceTest {

    private CsvExportService csvExportService;

    @BeforeEach
    void setUp() {
        csvExportService = new CsvExportService();
    }

    @Test
    void testWriteVisitsToCsv_emptyList() {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);

        csvExportService.writeVisitsToCsv(printWriter, Collections.emptyList());

        String expectedCsv = "Visit ID,Date,Description,Pet Name,Vet Name,Status,Duration (minutes),Diagnosis Code,Treatment Tags\n";
        assertThat(stringWriter.toString()).isEqualTo(expectedCsv);
    }

    @Test
    void testWriteVisitsToCsv_singleVisit() {
        Pet pet = new Pet();
        pet.setId(1);
        pet.setName("Leo");

        Vet vet = new Vet();
        vet.setId(1);
        vet.setFirstName("James");
        vet.setLastName("Carter");

        Visit visit = new Visit();
        visit.setId(1);
        visit.setDate(LocalDate.of(2023, 1, 15));
        visit.setDescription("Routine checkup for Leo");
        visit.setPet(pet);
        visit.setVet(vet);
        visit.setStatus("Completed");
        visit.setDurationMinutes(30);
        visit.setDiagnosisCode("A01");
        visit.setTreatmentTags("Vaccination,Deworming");

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);

        csvExportService.writeVisitsToCsv(printWriter, Collections.singletonList(visit));

        String expectedCsv = "Visit ID,Date,Description,Pet Name,Vet Name,Status,Duration (minutes),Diagnosis Code,Treatment Tags\n" +
                             "1,2023-01-15,Routine checkup for Leo,Leo,James Carter,Completed,30,A01,Vaccination,Deworming\n";
        assertThat(stringWriter.toString()).isEqualTo(expectedCsv);
    }

    @Test
    void testWriteVisitsToCsv_multipleVisits() {
        Pet pet1 = new Pet();
        pet1.setId(1);
        pet1.setName("Leo");

        Pet pet2 = new Pet();
        pet2.setId(2);
        pet2.setName("Max");

        Vet vet1 = new Vet();
        vet1.setId(1);
        vet1.setFirstName("James");
        vet1.setLastName("Carter");

        Vet vet2 = new Vet();
        vet2.setId(2);
        vet2.setFirstName("Helen");
        vet2.setLastName("Leary");

        Visit visit1 = new Visit();
        visit1.setId(1);
        visit1.setDate(LocalDate.of(2023, 1, 15));
        visit1.setDescription("Routine checkup");
        visit1.setPet(pet1);
        visit1.setVet(vet1);
        visit1.setStatus("Completed");
        visit1.setDurationMinutes(30);
        visit1.setDiagnosisCode("A01");
        visit1.setTreatmentTags("Vaccination,Deworming");

        Visit visit2 = new Visit();
        visit2.setId(2);
        visit2.setDate(LocalDate.of(2023, 2, 20));
        visit2.setDescription("Follow-up for Max, with some notes.");
        visit2.setPet(pet2);
        visit2.setVet(vet2);
        visit2.setStatus("Scheduled");
        visit2.setDurationMinutes(45);
        visit2.setDiagnosisCode("B02");
        visit2.setTreatmentTags("Medication,Diet");

        List<Visit> visits = Arrays.asList(visit1, visit2);

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);

        csvExportService.writeVisitsToCsv(printWriter, visits);

        String expectedCsv = "Visit ID,Date,Description,Pet Name,Vet Name,Status,Duration (minutes),Diagnosis Code,Treatment Tags\n" +
                             "1,2023-01-15,Routine checkup,Leo,James Carter,Completed,30,A01,Vaccination,Deworming\n" +
                             "2,2023-02-20,\"Follow-up for Max, with some notes.\",Max,Helen Leary,Scheduled,45,B02,Medication,Diet\n";
        assertThat(stringWriter.toString()).isEqualTo(expectedCsv);
    }

    @Test
    void testWriteVisitsToCsv_withSpecialCharacters() {
        Pet pet = new Pet();
        pet.setId(1);
        pet.setName("Leo");

        Vet vet = new Vet();
        vet.setId(1);
        vet.setFirstName("James");
        vet.setLastName("Carter");

        Visit visit = new Visit();
        visit.setId(1);
        visit.setDate(LocalDate.of(2023, 3, 10));
        visit.setDescription("Description with, commas and \"quotes\" and\nnewlines.");
        visit.setPet(pet);
        visit.setVet(vet);
        visit.setStatus("Completed");
        visit.setDurationMinutes(60);
        visit.setDiagnosisCode("C03");
        visit.setTreatmentTags("Complex,Tags,\"Special\"");

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);

        csvExportService.writeVisitsToCsv(printWriter, Collections.singletonList(visit));

        String expectedCsv = "Visit ID,Date,Description,Pet Name,Vet Name,Status,Duration (minutes),Diagnosis Code,Treatment Tags\n" +
                             "1,2023-03-10,\"Description with, commas and \"\"quotes\"\" and\nnewlines.\",Leo,James Carter,Completed,60,C03,\"Complex,Tags,\"\"Special\"\"\"\n";
        assertThat(stringWriter.toString()).isEqualTo(expectedCsv);
    }
}
