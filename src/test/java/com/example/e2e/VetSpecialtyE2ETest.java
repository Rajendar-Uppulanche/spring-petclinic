package com.example.e2e;

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
import org.springframework.boot.test.web.server.LocalServerPort;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class VetSpecialtyE2ETest {

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
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        baseUrl = "http://localhost:" + port;
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void testSpecialtyFilterDisplaysCorrectVets() {
        driver.get(baseUrl + "/vets/vetList.html");
        List<WebElement> initialVetRows = driver.findElements(By.cssSelector("#vetTable tbody tr"));
        assertThat(initialVetRows).isNotEmpty();
        WebElement specialtyFilter = driver.findElement(By.id("specialtyFilter"));
        Select select = new Select(specialtyFilter);
        select.selectByVisibleText("Radiology");
        List<WebElement> filteredVetRows = driver.findElements(By.cssSelector("#vetTable tbody tr"));
        assertThat(filteredVetRows).isNotEmpty();
        for (WebElement row : filteredVetRows) {
            String specialtiesText = row.findElements(By.tagName("td")).get(2).getText();
            assertThat(specialtiesText).contains("Radiology");
            assertThat(specialtiesText).doesNotContain("Dentistry");
        }
        select.selectByValue("all");
        List<WebElement> allVetRowsAfterFilter = driver.findElements(By.cssSelector("#vetTable tbody tr"));
        assertThat(allVetRowsAfterFilter).hasSameSizeAs(initialVetRows);
    }

    @Test
    void testNoVetsFoundMessageForNonExistentSpecialty() {
        driver.get(baseUrl + "/vets/vetList.html");
        WebElement specialtyFilter = driver.findElement(By.id("specialtyFilter"));
        Select select = new Select(specialtyFilter);
        select.selectByVisibleText("Cardiology");
        WebElement noVetsMessage = driver.findElement(By.id("noVetsMessage"));
        assertThat(noVetsMessage.isDisplayed()).isTrue();
        assertThat(noVetsMessage.getText()).isEqualTo("No veterinarians found for the selected specialty.");
        List<WebElement> vetRows = driver.findElements(By.cssSelector("#vetTable tbody tr"));
        assertThat(vetRows).isEmpty();
    }

    @Test
    void testNoSpecialtiesAvailableMessage() {

    }
}