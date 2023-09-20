package ru.inno.pageFactory.page;

import io.qameta.allure.Step;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import ru.inno.pageFactory.block.BookCard;
import ru.inno.pageFactory.block.Chips;
import ru.inno.pageFactory.block.SortOption;

import java.util.ArrayList;
import java.util.List;

import static java.time.Duration.ofSeconds;
import static org.openqa.selenium.By.cssSelector;
import static org.openqa.selenium.support.ui.ExpectedConditions.invisibilityOf;

public class SearchResultPage extends Page {
    @FindBy(css = "span.sorting-items")
    private WebElement sortingSpan;
    @FindBy(css = ".product-card")
    private List<WebElement> cards;
    @FindBy(css = "div.loading-panel")
    private WebElement loader;
    @FindBy(css = ".filter-reset")
    private List<WebElement> chips;

    public SearchResultPage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
    }

    @Step("Установить сортировку товаров на '{option}'")
    public SearchResultPage changeSort(SortOption option) {
        sortingSpan.click();
        //TODO: разобраться, почему перестал работать чистый submit по элементу, без предварительного раскрытия списка
//        driver.findElement(cssSelector("[data-event-content='" + option.getTitle() + "']")).submit();
        driver.findElement(cssSelector("[data-event-content='" + option.getTitle() + "']")).click();
        waitLoader();
        return this;
    }

    @Step("Получить все книги")
    public List<BookCard> getAllBooks() {
        List<BookCard> books = new ArrayList<>();

        cards.forEach(c -> books.add(new BookCard(c)));     //Проверка Stream Api

        //Или
//        for (WebElement e : cards) {
//            books.add(new BookCard(e));
//        }
        return books;
    }

    @Step("Ждём окончания загрузки данных")
    private void waitLoader() {
        new WebDriverWait(driver, ofSeconds(10)).until(invisibilityOf(loader));
    }

    @Step("Закрыть фильтр '{chipsToClose.title}'")
    //Чтобы вытаскивалось нормальное название из Enum, надо переопределить метод toString
    public SearchResultPage closeChips(Chips chipsToClose) {
        for (WebElement c : chips) {
            if (c.getText().equalsIgnoreCase(chipsToClose.getTitle())) {
                c.findElement(cssSelector(".filter-reset__icon")).click();
                waitLoader();
                break;
            }
        }
        return this;
    }
}