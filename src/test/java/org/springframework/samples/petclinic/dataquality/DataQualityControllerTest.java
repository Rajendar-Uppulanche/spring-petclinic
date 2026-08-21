package org.springframework.samples.petclinic.dataquality;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Test class for {@link DataQualityController}
 */
@WebMvcTest(DataQualityController.class)
@Import(TestSecurityConfig.class) // Assuming a minimal security config for tests
class DataQualityControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private DataQualityService dataQualityService;

	private DataQualityReport mockReport;

	@BeforeEach
	void setUp() {
		mockReport = new DataQualityReport();
		mockReport.setGenerationTimestamp(LocalDateTime.of(2023, 1, 1, 10, 0));
		mockReport.setTotalOwners(1);
		mockReport.setOwnersWithIncompleteAddress(0);
		mockReport.setOwnersWithInvalidTelephone(0);
		mockReport.setTotalPets(1);
		mockReport.setPetsWithIncompleteBirthDate(0);

		OwnerQualityMetrics ownerMetrics = new OwnerQualityMetrics();
		ownerMetrics.setOwnerId(1);
		ownerMetrics.setOwnerName("John Doe");
		ownerMetrics.setAddressComplete(true);
		ownerMetrics.setTelephoneValid(true);

		PetQualityMetrics petMetrics = new PetQualityMetrics();
		petMetrics.setPetId(101);
		petMetrics.setPetName("Buddy");
		petMetrics.setBirthDateComplete(true);
		petMetrics.setTypeComplete(true);
		petMetrics.setHasVisits(true);

		ownerMetrics.getPetMetrics().add(petMetrics);
		mockReport.getDetailedOwnerMetrics().add(ownerMetrics);
	}

	@Test
	@WithMockUser(roles = { "ADMIN" })
	void shouldReturnDataQualityReportForAdmin() throws Exception {
		when(dataQualityService.generateDataQualityReport()).thenReturn(mockReport);

		mockMvc.perform(get("/api/admin/dataquality/report").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.totalOwners").value(1))
			.andExpect(jsonPath("$.detailedOwnerMetrics[0].ownerName").value("John Doe"))
			.andExpect(jsonPath("$.detailedOwnerMetrics[0].petMetrics[0].petName").value("Buddy"));
	}

	@Test
	@WithMockUser(roles = { "USER" })
	void shouldForbidDataQualityReportForNonAdmin() throws Exception {
		mockMvc.perform(get("/api/admin/dataquality/report").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isForbidden());
	}

	@Test
	void shouldRequireAuthenticationForDataQualityReport() throws Exception {
		mockMvc.perform(get("/api/admin/dataquality/report").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnauthorized());
	}

	// A minimal security config for testing purposes
	// In a real application, this would be part of the main SecurityConfig
	static class TestSecurityConfig {

		// This class is intentionally left empty for @Import to work.
		// Spring Security's default configuration (e.g., form login, basic auth)
		// will be applied, allowing @WithMockUser to function.
		// For more complex security setups, a full @Configuration class would be needed.
	}

}
