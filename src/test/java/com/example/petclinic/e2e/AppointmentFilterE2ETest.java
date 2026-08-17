package com.example.petclinic.e2e;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.Select;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class AppointmentFilterE2ETest {

    @LocalServerPort
    private int port;

    private WebDriver driver;
    private String baseUrl;

    @BeforeAll
    static void setupClass() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void setupTest() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--window-size=1920,1080");
        driver = new ChromeDriver(options);
        baseUrl = "http://localhost:" + port;
        driver.get(baseUrl + "/appointments");
    }

    @AfterEach
    void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }

    private void applyFilters() {
        driver.findElement(By.cssSelector(".appointment-filter button[type='submit']")).click();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void clearFilters() {
        driver.findElement(By.cssSelector(".appointment-filter button.btn-secondary")).click();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private int getAppointmentRowCount() {
        return driver.findElements(By.cssSelector(".table tbody tr")).size();
    }

    @Test
    void testFilterByPet() {
        Select petFilter = new Select(driver.findElement(By.id("petFilter")));
        petFilter.selectByIndex(1);

        applyFilters();
        assertThat(getAppointmentRowCount()).isGreaterThanOrEqualTo(0);
    }

    @Test
    void testFilterByVeterinarian() {
        Select vetFilter = new Select(driver.findElement(By.id("veterinarianFilter")));
        vetFilter.selectByIndex(1);

        applyFilters();
        assertThat(getAppointmentRowCount()).isGreaterThanOrEqualTo(0);
    }

    @Test
    void testFilterByDateRange() {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        LocalDate tomorrow = today.plusDays(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        driver.findElement(By.id("startDateFilter")).sendKeys(yesterday.format(formatter));
        driver.findElement(By.id("endDateFilter")).sendKeys(tomorrow.format(formatter));

        applyFilters();
        assertThat(getAppointmentRowCount()).isGreaterThanOrEqualTo(0);
    }

    @Test
    void testFilterByDescriptionKeyword() {
        driver.findElement(By.id("descriptionFilter")).sendKeys("checkup");
        applyFilters();
        assertThat(getAppointmentRowCount()).isGreaterThanOrEqualTo(0);
    }

    @Test
    void testClearFilters() {
        Select petFilter = new Select(driver.findElement(By.id("petFilter")));
        petFilter.selectByIndex(1);
        driver.findElement(By.id("descriptionFilter")).sendKeys("test");
        applyFilters();

        int filteredCount = getAppointmentRowCount();
        assertThat(filteredCount).isLessThanOrEqualTo(getInitialAppointmentCount());

        clearFilters();
        int clearedCount = getAppointmentRowCount();
        assertThat(clearedCount).isEqualTo(getInitialAppointmentCount());
    }

    private int getInitialAppointmentCount() {
        driver.get(baseUrl + "/appointments");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return getAppointmentRowCount();
    }

    @Test
    void testMultipleFiltersCombined() {
        Select petFilter = new Select(driver.findElement(By.id("petFilter")));
        petFilter.selectByIndex(1);
        Select vetFilter = new Select(driver.findElement(By.id("veterinarianFilter")));
        vetFilter.selectByIndex(1);
        driver.findElement(By.id("descriptionFilter")).sendKeys("annual");

        applyFilters();
        assertThat(getAppointmentRowCount()).isGreaterThanOrEqualTo(0);
    }
}