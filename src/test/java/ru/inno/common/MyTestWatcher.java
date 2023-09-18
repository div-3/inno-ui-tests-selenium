package ru.inno.common;

import io.qameta.allure.Attachment;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;

import static org.openqa.selenium.By.cssSelector;
import static ru.inno.common.BaseUITest.getDriver;

//Чтобы получать WebDriver от теста при сработке assert надо:
//https://davy.ai/testwatcher-api-junit5-allure-how-to-take-a-browser-when-aftereach-happens-earlier-than-testfailed/

public class MyTestWatcher implements TestWatcher {

    public void testFailed(ExtensionContext context, Throwable cause) {
        getScreenshot(getDriver());
    }

    //Получение скриншота и добавление его в Allure как Attachment
    @Attachment(value = "screen.png", type = "image/png")
    private byte[] getScreenshot(WebDriver driver) {
        return driver.findElement(cssSelector(".body-main-content-wrapper")).getScreenshotAs(OutputType.BYTES);
    }
}