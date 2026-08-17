package com.example.petclinic.e2e;

import org.junit.jupiter.api.AfterEach;
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
import org.springframework.boot.web.server.LocalServerPort;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AppointmentFilterE2ETest {

    @LocalServerPort
    private int port;

    private WebDriver driver;
    private String baseUrl;

    @BeforeEach
    void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--window-size=1920,1080");
        driver = new ChromeDriver(options);
        baseUrl = "http://localhost:" + port;
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void testFilterAppointmentsByDateRange() {
        driver.get(baseUrl + "/appointments");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".appointment-filter")));

        driver.findElement(By.id("startDate")).sendKeys("2023-01-01");
        driver.findElement(By.id("endDate")).sendKeys("2023-01-31");
        driver.findElement(By.cssSelector(".appointment-filter button[type='submit']")).click();

        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.cssSelector(".table tbody tr:first-child td:nth-child(2)"), "2023-01-15"));
        List<WebElement> rows = driver.findElements(By.cssSelector(".table tbody tr"));
        assertThat(rows).hasSize(1);
        assertThat(rows.get(0).getText()).contains("Routine checkup");
    }

    @Test
    void testFilterAppointmentsByPetIdAndVeterinarianId() {
        driver.get(baseUrl + "/appointments");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".appointment-filter")));

        driver.findElement(By.id("petId")).sendKeys("1");
        driver.findElement(By.id("veterinarianId")).sendKeys("1");
        driver.findElement(By.cssSelector(".appointment-filter button[type='submit']")).click();

        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.cssSelector(".table tbody tr:first-child td:nth-child(4)"), "Buddy"));
        List<WebElement> rows = driver.findElements(By.cssSelector(".table tbody tr"));
        assertThat(rows).hasSize(2);
        assertThat(rows.get(0).getText()).contains("Routine checkup");
        assertThat(rows.get(1).getText()).contains("Vaccination");
    }

    @Test
    void testClearFilters() {
        driver.get(baseUrl + "/appointments");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".appointment-filter")));

        driver.findElement(By.id("startDate")).sendKeys("2023-01-01");
        driver.findElement(By.cssSelector(".appointment-filter button[type='submit']")).click();
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.cssSelector(".table tbody tr:first-child td:nth-child(2)"), "2023-01-15"));
        assertThat(driver.findElements(By.cssSelector(".table tbody tr"))).hasSize(1);

        driver.findElement(By.cssSelector(".appointment-filter button.btn-secondary")).click();
        wait.until(ExpectedConditions.numberOfElementsToBe(By.cssSelector(".table tbody tr"), 3));
        List<WebElement> rows = driver.findElements(By.cssSelector(".table tbody tr"));
        assertThat(rows).hasSize(3);
    }
}
