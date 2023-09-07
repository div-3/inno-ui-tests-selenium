package ru.inno.pageFactory.block;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class BookCard {
    private final WebElement element;
    @FindBy(css = ".product-card__name")
    private WebElement title;
    @FindBy(css = ".btn-tocart.buy-link")
    WebElement addToCartButton;

    public BookCard(WebElement element) {
        this.element = element;
        PageFactory.initElements(element, this);
    }

    public String getTitle(){
        return title.getText();
    }

    public void addToCart(){
        addToCartButton.click();
    }
}
