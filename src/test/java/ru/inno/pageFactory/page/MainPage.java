package ru.inno.pageFactory.page;

import io.qameta.allure.Step;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;

public class MainPage extends Page {
    public MainPage(WebDriver driver) {
        super(driver);
    }

    @Step("Открыть главную страницу")
    public void open() {
        driver.get("https://www.labirint.ru/");
        Cookie cookiePolicy = new Cookie("cookie_policy", "1");
        driver.manage().addCookie(cookiePolicy);
    }


}
