package ru.inno.labirint;

import io.github.bonigarcia.seljup.SeleniumJupiter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.chrome.ChromeDriver;

import java.time.Duration;

@ExtendWith(SeleniumJupiter.class)
public class LabirintUITest {

    @Test
    public void byuJavaBooks(ChromeDriver browser){

        //Установка неявного ожидания для всех команд 4 секунды
        browser.manage().timeouts().implicitlyWait(Duration.ofSeconds(4));

        //Открытие страницы
        browser.get("https://www.labirint.ru/");

        //Установка cookie для отключения показа плашки "Принять Cookie"
        Cookie cookiePolicy = new Cookie("cookie_policy", "1");
        browser.manage().addCookie(cookiePolicy);
        browser.navigate().refresh();

        //Ввод в строку поиска текста Java и выполнение запроса
        browser.findElement(By.cssSelector("#search-field")).sendKeys("Java", );

        System.out.println("END TEST");
    }
}
