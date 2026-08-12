package org.springframework.samples.petclinic.visit;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.pet.Pet;
import org.springframework.samples.petclinic.util.CsvExportService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Controller
class VisitController {

    private final VisitService visitService;
    private final CsvExportService csvExportService;

    public VisitController(VisitService visitService, CsvExportService csvExportService) {
        this.visitService = visitService;
        this.csvExportService = csvExportService;
    }

    // API for paginated and sorted visit history
    @GetMapping("/api/pets/{petId}/visits")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getPaginatedVisits(
        @PathVariable("petId") Integer petId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "date,desc") String[] sort) {

        Sort.Direction direction = Sort.Direction.fromString(sort[1]);
        String property = sort[0];
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, property));

        Page<Visit> visitPage = visitService.findVisitsByPetId(petId, pageable);

        return new ResponseEntity<>(
            Map.of(
                "visits", visitPage.getContent(),
                "currentPage", visitPage.getNumber(),
                "totalItems", visitPage.getTotalElements(),
                "totalPages", visitPage.getTotalPages()
            ),
            HttpStatus.OK
        );
    }

    // API for CSV export
    @GetMapping("/api/pets/{petId}/visits/export")
    public ResponseEntity<byte[]> exportVisitsToCsv(@PathVariable("petId") Integer petId) {
        List<Visit> visits = visitService.findAllVisitsByPetId(petId);
        Pet pet = visitService.findPetById(petId); // Needed for pet name in filename

        if (pet == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (PrintWriter writer = new PrintWriter(baos, true, StandardCharsets.UTF_8)) {
            csvExportService.writeVisitsToCsv(writer, visits);
        }

        String petName = pet.getName().replace(" ", "_"); // Sanitize pet name for filename
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        // BR-018: Filename format: petclinic_visits_{petName}_{YYYY-MM-DD}.csv
        String filename = String.format("petclinic_visits_%s_%s.csv", petName, date);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");

        return new ResponseEntity<>(baos.toByteArray(), headers, HttpStatus.OK);
    }
}
