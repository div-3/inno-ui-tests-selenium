package ru.inno.common;

import io.qameta.allure.Attachment;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;
import org.junit.platform.launcher.TestExecutionListener;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.openqa.selenium.By.cssSelector;

//Чтобы получать WebDriver от теста при сработке assert надо:
//https://davy.ai/testwatcher-api-junit5-allure-how-to-take-a-browser-when-aftereach-happens-earlier-than-testfailed/

public class MyTestWatcher implements TestExecutionListener {

    @Override
    public void afterTest(TestExecutionEvent event) {
        if (event.getOutcome() == Failure) {
            TakesScreenshot ts = (TakesScreenshot) event.getTestClass().getOnlyConstructor().newInstance();
            File source = ts.getScreenshotAs(OutputType.FILE);
        }
    }




//        implements TestWatcher {
//    private WebDriver driver;

    public void testFailed(ExtensionContext context, Throwable cause) {

        TakesScreenshot ts = (TakesScreenshot) context.newInstance();
        File source = ts.getScreenshotAs(OutputType.FILE);
        Path destination = Paths.get(System.getProperty("screenshotFolder"),
                event.getTestMethod().get().getName() + ".png");

        Object test = context.getRequiredTestInstance();
        Field field = null;
        try {
            field = test.getClass().getDeclaredField("driver");
            field.setAccessible(true);
            WebDriver driver = (WebDriver) field.get(test);
            getScreenshot(driver);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }

    public void setDriver(WebDriver driver){
        this.driver = driver;
    }

    //Получение скриншота и добавление его в Allure как Attachment
    @Attachment(value = "screen.png", type = "image/png")
    private byte[] getScreenshot(WebDriver driver) {
        return driver.findElement(cssSelector("body")).getScreenshotAs(OutputType.BYTES);
    }
}
