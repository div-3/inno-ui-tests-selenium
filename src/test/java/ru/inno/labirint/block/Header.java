package ru.inno.labirint.block;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import ru.inno.labirint.page.SearchResultPage;

import static java.time.Duration.ofSeconds;
import static org.openqa.selenium.By.cssSelector;
import static org.openqa.selenium.support.ui.ExpectedConditions.textToBe;

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

    public Header awaitCartCounterToBe(int number) {
        //TODO: переделать EC для ожидания отсутствия изменений в корзине в течении 3 секунд
        new WebDriverWait(driver, ofSeconds(5))
                .until(textToBe(cssSelector(".basket-in-cart-a"), Integer.toString(number)));
        return this;
    }

    public int getCartCounter() {
        String text = driver.findElement(cssSelector(".basket-in-cart-a")).getText();
        return Integer.parseInt(text);
    }

}
