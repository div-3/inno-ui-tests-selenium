package ru.inno.internet;

//Работа с web-элементами на сайте https://the-internet.herokuapp.com/

import io.github.bonigarcia.seljup.SeleniumJupiter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

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

    //2. Basic Auth (user and pass: admin)
    @Test
    public void basicAuthTest(ChromeDriver browser){
        browser.manage().timeouts().implicitlyWait(Duration.ofSeconds(4));
        String login = "admin";
        String password = "admin";

        //Basic authorization with login/path credentials using URL (https://www.browserstack.com/guide/handling-login-popups-in-selenium-webdriver-and-java)
        browser.get("https://" + login + ":" + password + "@" + "the-internet.herokuapp.com" + "/basic_auth");

        //Check successful authorization
        String text = browser.findElement(By.xpath("//div[contains(@class,'example')]/p")).getText();
        String success = "Congratulations! You must have the proper credentials.";
        assertEquals(success, text);
    }

    //3. Broken Images
    //TODO: Learn JS-script to check the images
    @Test
    public void brokenImagesTest(ChromeDriver browser){
        browser.manage().timeouts().implicitlyWait(Duration.ofSeconds(4));
        browser.get(BASE_URL + "/broken_images");
        WebElement img1 = browser.findElement(By.xpath("//div[contains(@class,'example')]/img[1]"));
        System.out.println(img1.getAttribute("File size"));
        String src1 = img1.getAttribute("src");
        System.out.println(src1);
        browser.get(src1);

        WebElement img2 = browser.findElement(By.xpath("//div[contains(@class,'example')]/img[2]"));
        System.out.println(img2.getAttribute("File size"));

        WebElement img3 = browser.findElement(By.xpath("//div[contains(@class,'example')]/img[3]"));
        System.out.println(img3.getAttribute("File size"));


    }

    //4. Challenging DOM
    @Test
    public void challengingDOMTest(ChromeDriver browser) throws InterruptedException {
        browser.manage().timeouts().implicitlyWait(Duration.ofSeconds(4));
        browser.get(BASE_URL + "/challenging_dom");

        /*-------------------------------------------------------------
        * Work with TABLE
        * -------------------------------------------------------------*/

        //Get first table header
        String header = browser
                .findElement(By.xpath("//div[contains(@class,'large-10 columns')]"))    //Catch the div with table
                .findElement(By.xpath("//thead/tr/th[1]")).getText();
        System.out.println(header);

        //Get all table headers
        List<WebElement> headers = browser
                .findElement(By.xpath("//div[contains(@class,'large-10 columns')]"))    //Catch the div with table
                .findElements(By.xpath("//thead/tr/th"));
        List<String> headersText = new ArrayList<>();
        for (WebElement e: headers) {
            headersText.add(e.getText());
        }
        System.out.println(headersText);

        //Get any text element by its row/column coordinates within the table
        int rowForElement = 9;
        int columnForElement = 5;
        WebElement tableElement = browser
                .findElement(By.xpath("//div[contains(@class,'large-10 columns')]"))    //Catch the div with table
                .findElement(By.xpath("//tr[" + rowForElement + "]/td[" + columnForElement + "]"));
        System.out.println(tableElement.getText());

        //Get any edit element by its row coordinates within the table
        int rowForEdit = 2;
        WebElement editElement = browser
                .findElement(By.xpath("//div[contains(@class,'large-10 columns')]"))    //Catch the div with table
                .findElement(By.xpath("//tr[" + rowForEdit + "]/td/a[@href='#edit']"));
        System.out.println(editElement.getText());

        //Get any delete element by its row coordinates within the table
        int rowForDelete = 3;
        WebElement deleteElement = browser
                .findElement(By.xpath("//div[contains(@class,'large-10 columns')]"))    //Catch the div with table
                .findElement(By.xpath("//tr[" + rowForDelete + "]/td/a[@href='#delete']"));
        System.out.println(deleteElement.getText());


        /*-------------------------------------------------------------
         * Work with BUTTONS
         * -------------------------------------------------------------*/

        //Get blue button
        WebElement blueButton = browser.findElement(By.cssSelector("a[class='button']"));
        System.out.println(blueButton.getText());
        blueButton.click();

        blueButton = browser.findElement(By.cssSelector("a[class='button']"));
        System.out.println(blueButton.getText());

        //Get alert button
        WebElement alertButton = browser.findElement(By.cssSelector("a.button.alert"));
        System.out.println(alertButton.getText());
        alertButton.click();

        alertButton = browser.findElement(By.cssSelector("a.button.alert"));
        System.out.println(alertButton.getText());

        //Get success button
        WebElement successButton = browser.findElement(By.cssSelector("a.button.success"));
        System.out.println(successButton.getText());
        successButton.click();

        successButton = browser.findElement(By.cssSelector("a.button.success"));
        System.out.println(successButton.getText());


        /*-------------------------------------------------------------
         * Work with CANVAS
         * -------------------------------------------------------------*/

        //Get canvas text
        WebElement canvas = browser.findElement(By.cssSelector("#canvas"));
        System.out.println(canvas.getText());   //There is no simple solution
        //TODO: Find the solution to get the text from canvas.
    }






}
