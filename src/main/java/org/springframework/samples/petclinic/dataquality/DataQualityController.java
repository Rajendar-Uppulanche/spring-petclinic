package org.springframework.samples.petclinic.dataquality;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for exposing data quality reports.
 * This endpoint is secured and requires ADMIN role access (NFR-075).
 */
@RestController
@RequestMapping("/api/admin/dataquality")
public class DataQualityController {

	private final DataQualityService dataQualityService;

	public DataQualityController(DataQualityService dataQualityService) {
		this.dataQualityService = dataQualityService;
	}

	/**
	 * Retrieves the comprehensive data quality report.
	 * Requires ADMIN role for access (NFR-075).
	 * @return A {@link DataQualityReport} containing aggregated and detailed metrics.
	 */
	@GetMapping("/report")
	@PreAuthorize("hasRole('ADMIN')")
	public DataQualityReport getDataQualityReport() {
		return dataQualityService.generateDataQualityReport();
	}

}
