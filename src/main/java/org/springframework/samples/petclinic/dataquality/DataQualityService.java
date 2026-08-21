package org.springframework.samples.petclinic.dataquality;

import org.springframework.stereotype.Service;
import org.springframework.samples.petclinic.owner.OwnerRepository;
import org.springframework.samples.petclinic.pet.PetRepository;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.pet.Pet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DataQualityService {

    private static final Logger logger = LoggerFactory.getLogger(DataQualityService.class);

    private final OwnerRepository ownerRepository;
    private final PetRepository petRepository;

    @Value("${dataquality.owner.email.completeness.threshold:0.95}")
    private double ownerEmailCompletenessThreshold;

    @Value("${dataquality.pet.name.completeness.threshold:0.98}")
    private double petNameCompletenessThreshold;

    public DataQualityService(OwnerRepository ownerRepository, PetRepository petRepository) {
        this.ownerRepository = ownerRepository;
        this.petRepository = petRepository;
    }

    public DataQualityReport checkOwnerContactDetailsQuality() {
        logger.info("Starting data quality check for owner contact details...");
        List<Owner> owners = ownerRepository.findAll();
        long totalOwners = owners.size();
        long ownersWithEmail = owners.stream()
            .filter(owner -> owner.getEmail() != null && !owner.getEmail().trim().isEmpty())
            .count();

        double emailCompleteness = totalOwners > 0 ? (double) ownersWithEmail / totalOwners : 1.0;
        boolean passed = emailCompleteness >= ownerEmailCompletenessThreshold;

        String message = String.format("Owner email completeness: %.2f%% (Threshold: %.2f%%). Status: %s",
            emailCompleteness * 100, ownerEmailCompletenessThreshold * 100, passed ? "PASSED" : "FAILED");
        logger.info(message);

        DataQualityReport report = new DataQualityReport("Owner Contact Details", emailCompleteness, ownerEmailCompletenessThreshold, passed, message);
        if (!passed) {
            report.addIssue("Low email completeness for owners. Current: " + String.format("%.2f%%", emailCompleteness * 100));
        }
        return report;
    }

    public DataQualityReport checkPetRecordsQuality() {
        logger.info("Starting data quality check for pet records...");
        List<Pet> pets = petRepository.findAll();
        long totalPets = pets.size();
        long petsWithName = pets.stream()
            .filter(pet -> pet.getName() != null && !pet.getName().trim().isEmpty())
            .count();

        double nameCompleteness = totalPets > 0 ? (double) petsWithName / totalPets : 1.0;
        boolean passed = nameCompleteness >= petNameCompletenessThreshold;

        String message = String.format("Pet name completeness: %.2f%% (Threshold: %.2f%%). Status: %s",
            nameCompleteness * 100, petNameCompletenessThreshold * 100, passed ? "PASSED" : "FAILED");
        logger.info(message);

        DataQualityReport report = new DataQualityReport("Pet Records", nameCompleteness, petNameCompletenessThreshold, passed, message);
        if (!passed) {
            report.addIssue("Low name completeness for pets. Current: " + String.format("%.2f%%", nameCompleteness * 100));
        }
        return report;
    }

    // Inner class for reporting
    public static class DataQualityReport {
        private String checkName;
        private double metricValue;
        private double threshold;
        private boolean passed;
        private String summary;
        private List<String> issues;

        public DataQualityReport(String checkName, double metricValue, double threshold, boolean passed, String summary) {
            this.checkName = checkName;
            this.metricValue = metricValue;
            this.threshold = threshold;
            this.passed = passed;
            this.summary = summary;
            this.issues = new java.util.ArrayList<>();
        }

        public String getCheckName() { return checkName; }
        public double getMetricValue() { return metricValue; }
        public double getThreshold() { return threshold; }
        public boolean isPassed() { return passed; }
        public String getSummary() { return summary; }
        public List<String> getIssues() { return issues; }
        public void addIssue(String issue) { this.issues.add(issue); }

        @Override
        public String toString() {
            return "DataQualityReport{" +
                   "checkName='" + checkName + '\'' +
                   ", metricValue=" + String.format("%.2f", metricValue * 100) + "%" +
                   ", threshold=" + String.format("%.2f", threshold * 100) + "%" +
                   ", passed=" + passed +
                   ", summary='" + summary + '\'' +
                   ", issues=" + issues +
                   '}';
        }
    }
}
