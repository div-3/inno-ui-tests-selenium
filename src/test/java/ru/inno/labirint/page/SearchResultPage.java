package ru.inno.labirint.page;

import org.openqa.selenium.WebDriver;
import ru.inno.labirint.block.SortOption;

import static org.openqa.selenium.By.cssSelector;

public class SearchResultPage extends Page{
    public SearchResultPage(WebDriver driver) {
        super(driver);
    }

    public void changeSort(SortOption option){
        driver.findElement(cssSelector("[data-event-content='" + option.getTitle() + "']")).submit();
    }


}
