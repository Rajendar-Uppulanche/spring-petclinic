package org.springframework.samples.petclinic.dataquality;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class ScheduledDataQualityTasks {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledDataQualityTasks.class);

    private final DataQualityService dataQualityService;
    // Assuming a NotificationService exists for alerting
    // private final NotificationService notificationService;

    public ScheduledDataQualityTasks(DataQualityService dataQualityService /*, NotificationService notificationService*/) {
        this.dataQualityService = dataQualityService;
        // this.notificationService = notificationService;
    }

    // Schedule to run every hour
    @Scheduled(fixedRateString = "${dataquality.schedule.fixedRate:3600000}") // 1 hour in milliseconds
    public void performDataQualityChecks() {
        logger.info("Executing scheduled data quality checks...");

        DataQualityService.DataQualityReport ownerReport = dataQualityService.checkOwnerContactDetailsQuality();
        logger.info("Owner Data Quality Report: {}", ownerReport);
        // if (!ownerReport.isPassed()) {
        //     notificationService.sendAlert("Owner Data Quality Issue", ownerReport.getSummary());
        // }

        DataQualityService.DataQualityReport petReport = dataQualityService.checkPetRecordsQuality();
        logger.info("Pet Data Quality Report: {}", petReport);
        // if (!petReport.isPassed()) {
        //     notificationService.sendAlert("Pet Data Quality Issue", petReport.getSummary());
        // }

        logger.info("Scheduled data quality checks completed.");
    }
}
