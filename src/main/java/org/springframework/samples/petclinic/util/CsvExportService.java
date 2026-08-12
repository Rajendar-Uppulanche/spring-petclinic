package org.springframework.samples.petclinic.util;

import org.springframework.samples.petclinic.visit.Visit;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class CsvExportService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public void writeVisitsToCsv(PrintWriter writer, List<Visit> visits) {
        // FR-054: Include 'Diagnosis Code' and 'Treatment Tags'
        writer.println("Visit ID,Date,Description,Pet Name,Vet Name,Status,Duration (minutes),Diagnosis Code,Treatment Tags");

        for (Visit visit : visits) {
            String petName = (visit.getPet() != null) ? visit.getPet().getName() : "";
            String vetName = (visit.getVet() != null) ? visit.getVet().getFirstName() + " " + visit.getVet().getLastName() : "";
            String date = (visit.getDate() != null) ? visit.getDate().format(DATE_FORMATTER) : "";
            String description = (visit.getDescription() != null) ? escapeCsv(visit.getDescription()) : "";
            String status = (visit.getStatus() != null) ? escapeCsv(visit.getStatus()) : "";
            String duration = (visit.getDurationMinutes() != null) ? String.valueOf(visit.getDurationMinutes()) : "";
            String diagnosisCode = (visit.getDiagnosisCode() != null) ? escapeCsv(visit.getDiagnosisCode()) : ""; // New
            String treatmentTags = (visit.getTreatmentTags() != null) ? escapeCsv(visit.getTreatmentTags()) : ""; // New

            writer.printf("%d,%s,%s,%s,%s,%s,%s,%s,%s%n",
                visit.getId(), date, description, petName, vetName, status, duration, diagnosisCode, treatmentTags);
        }
    }

    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        // Enclose in double quotes if the value contains comma, double quote, or newline
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", """") + "\"";
        }
        return value;
    }
}
