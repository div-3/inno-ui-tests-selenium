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

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.openqa.selenium.By.cssSelector;

@ExtendWith(SeleniumJupiter.class)
public class LabirintUITest {

    @Test
    public void buyJavaBooks(ChromeDriver browser) throws InterruptedException {

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
        browser.findElement(cssSelector("[data-event-content='высокий рейтинг']")).submit();

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
}
