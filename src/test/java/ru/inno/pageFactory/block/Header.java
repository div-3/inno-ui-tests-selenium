package ru.inno.pageFactory.block;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import ru.inno.pageFactory.other.NotChangeTextForXSecond;
import ru.inno.pageFactory.page.SearchResultPage;

import static java.time.Duration.ofSeconds;
import static org.openqa.selenium.By.cssSelector;

public class Header {
    private final WebDriver driver;
    @FindBy(css = "#search-field")
    private WebElement searchInput;
    private final By cartIconLocator = cssSelector(".basket-in-cart-a");
    @FindBy(css = ".basket-in-cart-a")
    private WebElement cartIcon;

    public Header(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public SearchResultPage search(String text) {
        searchInput.clear();
        searchInput.sendKeys(text, Keys.RETURN);
        return new SearchResultPage(driver);
    }

    public Header awaitCartCounter() {
        new WebDriverWait(driver, ofSeconds(10))
                .until(new NotChangeTextForXSecond(cartIconLocator, 2, 500));
        return this;
    }

    public int getCartCounter() {
        String text = cartIcon.getText();
        return Integer.parseInt(text);
    }

}
