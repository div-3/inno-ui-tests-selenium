package ru.inno.internet;

import io.github.bonigarcia.seljup.SeleniumJupiter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chromium.ChromiumDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@ExtendWith(SeleniumJupiter.class)
public class AuthFormTest {
    @Test
    public void succAuth(FirefoxDriver driver){
//        WebDriver driver = new FirefoxDriver();
//        WebDriver driver = new ChromeDriver();
        driver.get("http://the-internet.herokuapp.com/login");

        System.out.println(driver.manage().timeouts().getImplicitWaitTimeout());

        driver.manage().timeouts().implicitlyWait(Duration.of(10, ChronoUnit.SECONDS));

        System.out.println(driver.manage().timeouts().getImplicitWaitTimeout());

    }

}
