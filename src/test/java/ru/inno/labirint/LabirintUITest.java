package ru.inno.labirint;

import io.github.bonigarcia.seljup.SeleniumJupiter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import ru.inno.labirint.block.BookCard;
import ru.inno.labirint.block.Chips;
import ru.inno.labirint.block.SortOption;
import ru.inno.labirint.page.MainPage;
import ru.inno.labirint.page.SearchResultPage;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.openqa.selenium.By.cssSelector;

@ExtendWith(SeleniumJupiter.class)
public class LabirintUITest {


    @Test
    public void buyJavaBooksManual(ChromeDriver browser) throws InterruptedException {

        //Установка неявного ожидания для всех команд 4 секунды
        browser.manage().timeouts().implicitlyWait(Duration.ofSeconds(4));

        //1. Открытие страницы
        browser.get("https://www.labirint.ru/");

        //2. Скрыть плашку с cookies
        // Установка cookie для отключения показа плашки "Принять Cookie"
        Cookie cookiePolicy = new Cookie("cookie_policy", "1");
        browser.manage().addCookie(cookiePolicy);
        browser.navigate().refresh();

        //3. В поисковую строку написать `Java`
        //4. Выполнить поиск
        browser.findElement(cssSelector("#search-field")).sendKeys("Java", Keys.RETURN);

        //5. Изменить сортировку с `Сначала релевантные` на `Сначала высокий рейтинг`
        browser.findElement(cssSelector("span.sorting-items")).click();
        browser.findElement(cssSelector("[data-event-content='высокий рейтинг']")).click();

        //6. Добавить все товары на странице в корзину (кнопка Купить)
//        Thread.sleep(5000);   //Просто подождать 5 секунд до появления кнопок "В корзину"

        //Или
        //Отключение неявного ожидания
        Duration d = browser.manage().timeouts().getImplicitWaitTimeout();
        browser.manage().timeouts().implicitlyWait(Duration.ZERO);

        //Включение явного ожидания появления кнопки "В корзину". Считаем, что все кнопки появляются одновременно
        WebDriverWait wait = new WebDriverWait(browser, Duration.ofSeconds(15));
        wait.withMessage("Не дождались инициализации кнопок \"В корзину\"!")
                .until(ExpectedConditions.stalenessOf(browser.findElement(cssSelector(".btn-tocart.buy-link"))));

        //Включение неявного ожидания
        browser.manage().timeouts().implicitlyWait(d);


        //Получение списка кнопок "В корзину"
        List<WebElement> buyButtons = browser.findElements(cssSelector(".btn-tocart.buy-link"));

        //Нажатие всех кнопок "В корзину"
        for (WebElement element : buyButtons) {
            element.click();
        }

        //7. Счетчик товаров в корзине равен количеству добавленных товаров на шаге 6

        //Просто подождать пока корзина обновится
        Thread.sleep(5000);

        //Или явное ожидание.
        //Но не подойдёт, т.к. мы принудительно дожидаемся когда значение в корзине будет равно требуемому. А если оно через секунду будет больше?
//        wait.withMessage("Счётчик товаров в пиктограмме корзины не достиг нужного значения.")
//                .until(ExpectedConditions.textToBe(cssSelector(".b-header-b-personal-e-icon-count-m-cart.basket-in-cart-a"), String.valueOf(buyButtons.size())));

        //Получение счётчика товаров в корзине
        int cartCounter = Integer.parseInt(browser.findElement(cssSelector(".b-header-b-personal-e-icon-count-m-cart.basket-in-cart-a")).getText());

        //Проверка счётчика
        assertEquals(buyButtons.size(), cartCounter);
    }

    @Test
    public void buyJavaBooksPageObject(ChromeDriver browser) throws InterruptedException {
        //Установка неявного ожидания для всех команд 4 секунды
        browser.manage().timeouts().implicitlyWait(Duration.ofSeconds(4));

        MainPage mainPage = new MainPage(browser);

        //1. Открытие страницы
        //2. Скрыть плашку с cookies
        mainPage.open();

        //3. В поисковую строку написать `Java`
        //4. Выполнить поиск
        SearchResultPage searchResultPage = mainPage.getHeader().search("Java");

        //5. Изменить сортировку с `Сначала релевантные` на `Сначала высокий рейтинг`
        searchResultPage.changeSort(SortOption.HIGH_RATE);

        //6. Добавить все товары на странице в корзину (кнопка Купить)
        //Получение списка кнопок "В корзину"
        List<BookCard> books = searchResultPage
                .closeChips(Chips.PREORDER)
                .closeChips(Chips.AWAITING)
                .closeChips(Chips.NOT_AVAILABLE)
                .getAllBooks();

        for (BookCard book : books) {
            book.addToCart();
        }

        //7. Счетчик товаров в корзине равен количеству добавленных товаров на шаге 6
        //Получение счётчика товаров в корзине
        int cartCounter =searchResultPage
                .getHeader()
                .awaitCartCounterToBe(books.size())
                .getCartCounter();

        //Проверка счётчика
        assertEquals(books.size(), cartCounter);
    }
}
