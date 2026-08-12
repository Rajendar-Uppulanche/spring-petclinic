/*
 * Copyright 2012-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.owner;

import java.io.IOException;
import java.io.StringWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

/**
 * @author Vitaliy Fedoriv
 */
@Service
public class VisitService {

	private final VisitRepository visits;

	public VisitService(VisitRepository visits) {
		this.visits = visits;
	}

	public VisitPage findVisitsByPetId(int petId, int page, int size, String sort, String direction) {
		Sort sortOrder = Sort.by(Sort.Direction.fromString(direction), sort);
		Pageable pageable = PageRequest.of(page - 1, size, sortOrder);
		Page<Visit> visitPage = this.visits.findByPetId(petId, pageable);
		return new VisitPage(visitPage.getContent(), visitPage.getTotalElements(), visitPage.getNumber(),
				visitPage.getSize());
	}

	public byte[] exportVisitsAsCsv(String petName, Collection<Visit> visits) {
		try (StringWriter writer = new StringWriter();
			 CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.builder()
						 .setHeader("Visit ID", "Appointment Date", "Vet Name", "Status", "Duration (minutes)",
								 "Diagnosis Code", "Treatment Tags", "Description")
						 .build())) {

			for (Visit visit : visits) {
				csvPrinter.printRecord(visit.getId(),
						visit.getDate().format(DateTimeFormatter.ISO_LOCAL_DATE),
						"N/A", // Vet Name placeholder
						"N/A", // Status placeholder
						"N/A", // Duration placeholder
						"N/A", // Diagnosis Code placeholder
						"N/A", // Treatment Tags placeholder
						visit.getDescription());
			}
			return writer.toString().getBytes();
		} catch (IOException e) {
			throw new RuntimeException("Failed to generate CSV file", e);
		}
	}

	public String generateCsvFilename(String petName) {
		String dateString = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
		return String.format("petclinic_visits_%s_%s.csv", petName.replace(" ", "_"), dateString);
	}

}
