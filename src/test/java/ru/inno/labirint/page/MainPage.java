package ru.inno.labirint.page;

import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;

public class MainPage extends Page{
    public MainPage(WebDriver driver) {
        super(driver);
    }

    public void open(){
        driver.get("https://www.labirint.ru/");
        Cookie cookiePolicy = new Cookie("cookie_policy", "1");
        driver.manage().addCookie(cookiePolicy);
    }


}
