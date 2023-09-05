package ru.inno.labirint.block;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import ru.inno.labirint.page.SearchResultPage;

import static org.openqa.selenium.By.cssSelector;

public class Header {
    private final WebDriver driver;
    private final static By SEARCH_INPUT_LOCATOR = cssSelector("#search-field");

    public Header(WebDriver driver) {
        this.driver = driver;
    }

    public SearchResultPage search(String text){
        driver.findElement(SEARCH_INPUT_LOCATOR).clear();
        driver.findElement(SEARCH_INPUT_LOCATOR).sendKeys(text, Keys.RETURN);
        return new SearchResultPage(driver);
    }
}
