package ru.inno.factory;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.time.Duration;

public class WebDriverFactory {
    public WebDriver getDriver(DriverType name) {
        WebDriver driver = null;
        switch (name) {
            case EDGE -> {
                driver = new EdgeDriver();
            }
            case CHROME -> {
                driver = new ChromeDriver();
            }
            case FIREFOX -> {
                driver = new FirefoxDriver();
            }
            default -> throw new RuntimeException("Wrong WebDriver type!");
        }

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(4));
        return driver;
    }

    public WebDriver getDriver(DriverType name, String... args) {
        WebDriver driver = null;
        switch (name) {
            case EDGE -> {
                EdgeOptions options = new EdgeOptions();
                for (String s : args) {
                    options.addArguments(s);
                }
                driver = new EdgeDriver(options);
            }
            case CHROME -> {
                ChromeOptions options = new ChromeOptions();
                for (String s : args) {
                    options.addArguments(s);
                }
                driver = new ChromeDriver(options);
            }
            case FIREFOX -> {
                FirefoxOptions options = new FirefoxOptions();
                for (String s : args) {
                    options.addArguments(s);
                }
                driver = new FirefoxDriver(options);
            }
            default -> throw new RuntimeException("Wrong WebDriver type!");
        }

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(4));
        return driver;
    }
}
