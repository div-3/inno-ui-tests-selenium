package ru.inno.labirint.page;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import ru.inno.labirint.block.BookCard;
import ru.inno.labirint.block.Chips;
import ru.inno.labirint.block.SortOption;

import java.util.ArrayList;
import java.util.List;

import static java.time.Duration.ofSeconds;
import static org.openqa.selenium.By.cssSelector;
import static org.openqa.selenium.support.ui.ExpectedConditions.stalenessOf;

public class SearchResultPage extends Page{
    public SearchResultPage(WebDriver driver) {
        super(driver);
    }

    public SearchResultPage changeSort(SortOption option){
        driver.findElement(cssSelector("span.sorting-items")).click();
        //TODO: разобраться, почему перестал работать чистый submit по элементу, без предварительного раскрытия списка
//        driver.findElement(cssSelector("[data-event-content='" + option.getTitle() + "']")).submit();
        driver.findElement(cssSelector("[data-event-content='" + option.getTitle() + "']")).click();
        waitLoader();
        return this;
    }

    public List<BookCard> getAllBooks(){
        List<BookCard> books = new ArrayList<>();
        List<WebElement> cards = driver.findElements(cssSelector(".product-card"));
        for (WebElement e : cards) {
            books.add(new BookCard(e));
        }
        return books;
    }

    private void waitLoader() {
        WebElement loader = driver.findElement(cssSelector("div.loading-panel"));
        new WebDriverWait(driver, ofSeconds(10)).until(stalenessOf(loader));
        //TODO: найти, как отлавливать закрытие элемента
//        new WebDriverWait(driver, ofSeconds(10)).until(ExpectedConditions.not(stalenessOf(loader)));
    }

    public SearchResultPage closeChips(Chips chipsToClose){
        List<WebElement> chips = driver.findElements(cssSelector(".filter-reset"));
        for (WebElement c : chips) {
            if (c.getText().equalsIgnoreCase(chipsToClose.getTitle())){
                c.findElement(cssSelector(".filter-reset__icon")).click();
                waitLoader();
                break;
            }
        }
        return this;
    }

}
