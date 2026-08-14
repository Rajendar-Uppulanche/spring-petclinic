package org.springframework.samples.petclinic.ui;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PetDetailsFormUITests {

    @LocalServerPort
    private int port;

    private WebDriver driver;
    private String baseUrl;

    @BeforeEach
    void setUp() {
        // Ensure ChromeDriver is available in PATH or set via System.setProperty
        // Example: System.setProperty("webdriver.chrome.driver", "/path/to/chromedriver");
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        baseUrl = "http://localhost:" + port;
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    private void navigateToPetCreationForm(int ownerId) {
        driver.get(baseUrl + "/owners/" + ownerId + "/pets/new");
        new WebDriverWait(driver, Duration.ofSeconds(10))
            .until(ExpectedConditions.visibilityOfElementLocated(By.tagName("h2")));
        assertEquals("New Pet", driver.findElement(By.tagName("h2")).getText().trim());
    }

    @Test
    void testPetNameInputFieldHasTooltip() {
        navigateToPetCreationForm(1); // Assuming owner with ID 1 exists
        WebElement nameInput = driver.findElement(By.id("name"));
        assertNotNull(nameInput);
        assertEquals("Enter the pet's name", nameInput.getAttribute("title"));
    }

    @Test
    void testPetBirthDateInputFieldHasTooltip() {
        navigateToPetCreationForm(1);
        WebElement birthDateInput = driver.findElement(By.id("birthDate"));
        assertNotNull(birthDateInput);
        assertEquals("Enter the pet's birth date (YYYY-MM-DD)", birthDateInput.getAttribute("title"));
    }

    @Test
    void testPetTypeSelectFieldHasTooltip() {
        navigateToPetCreationForm(1);
        WebElement typeSelect = driver.findElement(By.id("type"));
        assertNotNull(typeSelect);
        assertEquals("Select the pet's type", typeSelect.getAttribute("title"));
    }

    @Test
    void testPetNameInputFieldFocusStyling() {
        navigateToPetCreationForm(1);
        WebElement nameInput = driver.findElement(By.id("name"));
        nameInput.click(); // Give focus
        // Verify computed style for border-color (rgb(174, 214, 241) for #AED6F1)
        assertEquals("rgb(174, 214, 241)", nameInput.getCssValue("border-color"));
    }

    @Test
    void testPetBirthDateInputFieldFocusStyling() {
        navigateToPetCreationForm(1);
        WebElement birthDateInput = driver.findElement(By.id("birthDate"));
        birthDateInput.click();
        assertEquals("rgb(174, 214, 241)", birthDateInput.getCssValue("border-color"));
    }

    @Test
    void testPetTypeSelectFieldFocusStyling() {
        navigateToPetCreationForm(1);
        WebElement typeSelect = driver.findElement(By.id("type"));
        typeSelect.click();
        assertEquals("rgb(174, 214, 241)", typeSelect.getCssValue("border-color"));
    }
}