package ru.inno.labirint.block;

import org.openqa.selenium.WebElement;

import static org.openqa.selenium.By.cssSelector;

public class BookCard {
    private final WebElement element;

    public BookCard(WebElement element) {
        this.element = element;
    }

    public String getTitle(){
        return element.findElement(cssSelector(".product-card__name")).getText();
    }

    public void addToCart(){
        element.findElement(cssSelector(".btn-tocart.buy-link")).click();
    }
}
