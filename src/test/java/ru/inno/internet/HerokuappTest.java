package ru.inno.internet;

//Работа с web-элементами на сайте https://the-internet.herokuapp.com/

import io.github.bonigarcia.seljup.SeleniumJupiter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SeleniumJupiter.class)
public class HerokuappTest {
    private final static String BASE_URL = "https://the-internet.herokuapp.com";

    //1. Add/Remove Elements
    @Test
    public void addRemoveTest(ChromeDriver browser){
        browser.manage().timeouts().implicitlyWait(Duration.ofSeconds(4));
        browser.get(BASE_URL + "/add_remove_elements/");

        //Click "Add Element" button by cssSelector
        browser.findElement(By.cssSelector("button[onclick='addElement()']")).click();

        //Click "Add Element" button by xpathSelector
        browser.findElement(By.xpath("//div[contains(@class,'example')]/button")).click();

        //"Delete" buttons search by cssSelector
        List<WebElement> deleteBtnByCss = browser.findElements(By.cssSelector("button.added-manually"));
        assertEquals(2, deleteBtnByCss.size());

        //"Delete" buttons search by xpathSelector
        List<WebElement> deleteBtnByXpath = browser.findElements(By.xpath("//button[contains(@class,'added-manually')]"));
        assertEquals(2, deleteBtnByXpath.size());

        //"Delete" buttons click
        for (WebElement e : deleteBtnByCss) {
            e.click();
        }

        //Check the buttons List size after deletion
        deleteBtnByCss = browser.findElements(By.cssSelector("button.added-manually"));
        assertEquals(0, deleteBtnByCss.size());
    }


}
