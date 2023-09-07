package ru.inno.labirint.block;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import ru.inno.labirint.other.NotChangeTextForXSecond;
import ru.inno.labirint.page.SearchResultPage;

import static java.time.Duration.ofSeconds;
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

    public Header awaitCartCounter() {
        By cartIcon = cssSelector(".basket-in-cart-a");
        new WebDriverWait(driver, ofSeconds(10))
                .until(new NotChangeTextForXSecond(cartIcon, 2, 500));
        return this;
    }

    public int getCartCounter() {
        String text = driver.findElement(cssSelector(".basket-in-cart-a")).getText();
        return Integer.parseInt(text);
    }

}
