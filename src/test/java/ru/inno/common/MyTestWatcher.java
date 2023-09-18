package ru.inno.common;

import com.codeborne.selenide.WebDriverRunner;
import io.qameta.allure.Attachment;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;
import org.junit.platform.launcher.TestExecutionListener;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import static ru.inno.common.BaseUITest.getDriver;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.openqa.selenium.By.cssSelector;

//Чтобы получать WebDriver от теста при сработке assert надо:
//https://davy.ai/testwatcher-api-junit5-allure-how-to-take-a-browser-when-aftereach-happens-earlier-than-testfailed/

public class MyTestWatcher implements TestWatcher {
//    private WebDriver driver;

    public void testFailed(ExtensionContext context, Throwable cause) {
        getScreenshot(getDriver());
    }

//    public void setDriver(WebDriver driver) {
//        this.driver = driver;
//    }

    //Получение скриншота и добавление его в Allure как Attachment
    @Attachment(value = "screen.png", type = "image/png")
    private byte[] getScreenshot(WebDriver driver) {
        return driver.findElement(cssSelector(".body-main-content-wrapper")).getScreenshotAs(OutputType.BYTES);
    }

}
