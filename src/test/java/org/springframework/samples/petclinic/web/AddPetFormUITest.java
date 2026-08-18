package org.springframework.samples.petclinic.web;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.Select;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AddPetFormUITest {

    @LocalServerPort
    private int port;

    private WebDriver driver;
    private String baseUrl;

    @BeforeEach
    void setUp() {
        // Assuming chromedriver is in your PATH or specified via system property
        // System.setProperty("webdriver.chrome.driver", "path/to/chromedriver");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // Run in headless mode for CI/CD
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--window-size=1920,1080");
        this.driver = new ChromeDriver(options);
        this.baseUrl = "http://localhost:" + port;
    }

    @AfterEach
    void tearDown() {
        if (this.driver != null) {
            this.driver.quit();
        }
    }

    @Test
    void testFutureBirthDateClientValidation() throws InterruptedException {
        driver.get(baseUrl + "/owners/1/pets/new");

        WebElement nameInput = driver.findElement(By.id("name"));
        nameInput.sendKeys("TestPet");

        WebElement birthDateInput = driver.findElement(By.id("birthDate"));
        // Set a future date
        LocalDate futureDate = LocalDate.now().plusDays(1);
        birthDateInput.sendKeys(futureDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        Select typeSelect = new Select(driver.findElement(By.id("type")));
        typeSelect.selectByVisibleText("cat");

        // Click outside to trigger validation (or simulate blur)
        nameInput.click();

        WebElement birthDateError = driver.findElement(By.id("birthDateError"));
        assertTrue(birthDateError.isDisplayed(), "Birth date error message should be displayed");
        assertTrue(birthDateError.getText().contains("Birth date cannot be in the future"), "Error message should indicate future date");

        WebElement submitButton = driver.findElement(By.cssSelector("button[type='submit']"));
        submitButton.click();

        // Verify that the form was NOT submitted (still on the same page or URL hasn't changed)
        // This is a simple check; a more robust check would be to look for a success message or URL change
        assertTrue(driver.getCurrentUrl().contains("/owners/1/pets/new"), "Form should not be submitted with future birth date");
        assertTrue(birthDateError.isDisplayed(), "Error message should still be displayed after attempted submission");
    }

    @Test
    void testValidBirthDateClientValidation() {
        driver.get(baseUrl + "/owners/1/pets/new");

        WebElement nameInput = driver.findElement(By.id("name"));
        nameInput.sendKeys("ValidPet");

        WebElement birthDateInput = driver.findElement(By.id("birthDate"));
        // Set a past date
        LocalDate pastDate = LocalDate.now().minusDays(1);
        birthDateInput.sendKeys(pastDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        Select typeSelect = new Select(driver.findElement(By.id("type")));
        typeSelect.selectByVisibleText("dog");

        // Click outside to trigger validation (or simulate blur)
        nameInput.click();

        WebElement birthDateError = driver.findElement(By.id("birthDateError"));
        assertFalse(birthDateError.isDisplayed(), "Birth date error message should not be displayed for valid date");

        WebElement submitButton = driver.findElement(By.cssSelector("button[type='submit']"));
        submitButton.click();

        // Verify that the form was submitted (redirected to owner details page)
        assertTrue(driver.getCurrentUrl().contains("/owners/1"), "Form should be submitted with valid birth date");
    }
}
