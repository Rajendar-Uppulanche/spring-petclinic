package com.example.petclinic.selenium;

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
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
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
    void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--window-size=1920,1080");
        driver = new ChromeDriver(options);
        baseUrl = "http://localhost:" + port;
        driver.get(baseUrl + "/visits");
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void testFilterAppointmentsByPetId() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        WebElement petIdInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("petId")));
        petIdInput.sendKeys("1");

        WebElement applyFiltersButton = driver.findElement(By.cssSelector(".appointment-filter button[type='submit']"));
        applyFiltersButton.click();

        wait.until(ExpectedConditions.numberOfElementsToBe(By.cssSelector("table tbody tr"), 1));

        List<WebElement> rows = driver.findElements(By.cssSelector("table tbody tr"));
        assertThat(rows).hasSize(1);
        assertThat(rows.get(0).getText()).contains("Leo");
    }

    @Test
    void testClearFilters() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        WebElement petIdInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("petId")));
        petIdInput.sendKeys("1");
        driver.findElement(By.cssSelector(".appointment-filter button[type='submit']")).click();
        wait.until(ExpectedConditions.numberOfElementsToBe(By.cssSelector("table tbody tr"), 1));

        WebElement clearFiltersButton = driver.findElement(By.cssSelector(".appointment-filter button.btn-secondary"));
        clearFiltersButton.click();

        wait.until(ExpectedConditions.numberOfElementsToBe(By.cssSelector("table tbody tr"), 4));

        List<WebElement> rows = driver.findElements(By.cssSelector("table tbody tr"));
        assertThat(rows).hasSize(4);
        assertThat(petIdInput.getAttribute("value")).isEmpty();
    }

    @Test
    void testFilterByDateRangeAndDescription() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        WebElement startDateInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("startDate")));
        startDateInput.sendKeys("2013-01-01");
        WebElement endDateInput = driver.findElement(By.id("endDate"));
        endDateInput.sendKeys("2013-01-31");
        WebElement descriptionKeywordInput = driver.findElement(By.id("descriptionKeyword"));
        descriptionKeywordInput.sendKeys("checkup");

        driver.findElement(By.cssSelector(".appointment-filter button[type='submit']")).click();

        wait.until(ExpectedConditions.numberOfElementsToBe(By.cssSelector("table tbody tr"), 1));
        List<WebElement> rows = driver.findElements(By.cssSelector("table tbody tr"));
        assertThat(rows).hasSize(1);
        assertThat(rows.get(0).getText()).contains("routine checkup");
        assertThat(rows.get(0).getText()).contains("2013-01-01");
    }
}