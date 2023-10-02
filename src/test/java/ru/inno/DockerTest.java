package ru.inno;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import ru.inno.pageFactory.other.NotChangeTextForXSecond;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.openqa.selenium.By.cssSelector;

public class DockerTest {
    private static List<WebDriver> openedBrowsers = new ArrayList<>();
    private WebDriver browser;


    @BeforeEach
    public void setUp() throws MalformedURLException {
        browser = new RemoteWebDriver(new URL("http://localhost:4444"), new FirefoxOptions());
        openedBrowsers.add(browser);
    }

    @AfterEach
    public void clear() {
        for (WebDriver b : openedBrowsers) {
            b.quit();
        }
    }

    @Test
    public void storeToDB() throws SQLException {
        String connectionString = "jdbc:postgresql://localhost:5432/my-db";
        String user = "myuser";
        String password = "mypass";
        Connection connection = DriverManager.getConnection(connectionString, user, password);

        String createTableQuery = "CREATE TABLE books (" +
                "book_id int," +
                "book_name varchar(255)," +
                "book_price int" +
                ");";
//        connection.createStatement().executeQuery(createTableQuery);
//        connection.createStatement().execute(createTableQuery);

//        String insertQuery = "insert into books (\"book_id\", \"book_name\", \"book_price\") values (?,?,?);";
//        PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
//        preparedStatement.setInt(1, 1);
//        preparedStatement.setString(2, "Java");
//        preparedStatement.setInt(3, 1234);
//        preparedStatement.executeUpdate();


        String getAllQuery = "select * from books;";
        ResultSet resultSet = connection.createStatement().executeQuery(getAllQuery);

        String s = "";
        while (resultSet.next()) {

            s = resultSet.getInt("book_id") + " " +
                    resultSet.getString("book_name") + " " +
                    resultSet.getInt("book_price");
        }

        System.out.println(s);
    }

    @Test
    @DisplayName("Добавление в корзину всех книг по Java (исходный)")
    public void buyJavaBooksManual() throws SQLException {

        String connectionString = "jdbc:postgresql://localhost:5432/my-db";
        String user = "myuser";
        String password = "mypass";
        Connection connection = DriverManager.getConnection(connectionString, user, password);

        String createTableQuery = "CREATE TABLE books (" +
                "book_id int," +
                "book_name varchar(255)," +
                "book_price int" +
                ");";
//        connection.createStatement().executeQuery(createTableQuery);
//        connection.createStatement().execute(createTableQuery);

//        String insertQuery = "insert into books (\"book_id\", \"book_name\", \"book_price\") values (?,?,?);";
//        PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
//        preparedStatement.setInt(1, 1);
//        preparedStatement.setString(2, "Java");
//        preparedStatement.setInt(3, 1234);
//        preparedStatement.executeUpdate();


        String getAllQuery = "select * from books;";
        ResultSet resultSet = connection.createStatement().executeQuery(getAllQuery);

        String s = "";
        while (resultSet.next()) {

            s = resultSet.getInt("book_id") + " " +
                    resultSet.getString("book_name") + " " +
                    resultSet.getInt("book_price");
        }

        System.out.println(s);

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
        WebDriverWait wait = new WebDriverWait(browser, Duration.ofSeconds(15), Duration.ofMillis(100));
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
//        Thread.sleep(5000);

        //Или явное ожидание.
        //Но не подойдёт, т.к. мы принудительно дожидаемся когда значение в корзине будет равно требуемому.
        // А если оно через секунду будет больше?
//        wait.withMessage("Счётчик товаров в пиктограмме корзины не достиг нужного значения.")
//                .until(ExpectedConditions.textToBe(cssSelector(".basket-in-cart-a"), String.valueOf(buyButtons.size())));


        //Кастомный ExpectedCondition, который ждёт пока значение текста будет неизменно в течении указанного количества
        // секунд. При использовании задаётся: локатор, период неизменности текста в секундах, sleepDuration, который
        // указан в wait в миллисекундах.
        By cartIcon = cssSelector(".basket-in-cart-a");
        WebElement cart = (WebElement) wait.withMessage("Счётчик товаров в пиктограмме корзины не достиг нужного значения.")
                .until(new NotChangeTextForXSecond(cartIcon, 3, 100));

        //Получение счётчика товаров в корзине
        int cartCounter = Integer.parseInt((cart).getText());

        //Проверка счётчика
        assertEquals(buyButtons.size(), cartCounter);
    }
}
