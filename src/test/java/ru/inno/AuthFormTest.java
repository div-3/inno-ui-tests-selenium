package ru.inno;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chromium.ChromiumDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class AuthFormTest {
    @Test
    public void succAuth(){
        WebDriver driver = new FirefoxDriver();
        driver.get("http://the-internet.herokuapp.com/login");

        System.out.println(driver.manage().timeouts().getImplicitWaitTimeout());

        driver.manage().timeouts().implicitlyWait(Duration.of(10, ChronoUnit.SECONDS));

        System.out.println(driver.manage().timeouts().getImplicitWaitTimeout());

    }
}
