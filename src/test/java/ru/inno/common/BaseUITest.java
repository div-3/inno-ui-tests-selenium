package ru.inno.common;

import org.openqa.selenium.WebDriver;

public class BaseUITest {
    public static WebDriver webDriver;

    public static WebDriver getDriver() {
        return webDriver;
    }
}
