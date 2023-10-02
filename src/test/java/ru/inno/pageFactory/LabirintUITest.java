package ru.inno.pageFactory;

import com.codeborne.selenide.*;
import io.github.bonigarcia.seljup.SeleniumJupiter;
import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junitpioneer.jupiter.cartesian.ArgumentSets;
import org.junitpioneer.jupiter.cartesian.CartesianTest;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import ru.inno.common.BaseUITest;
import ru.inno.common.MyTestWatcher;
import ru.inno.pageFactory.block.BookCard;
import ru.inno.pageFactory.block.Chips;
import ru.inno.pageFactory.block.SortOption;
import ru.inno.pageFactory.factory.DriverType;
import ru.inno.pageFactory.factory.WebDriverFactory;
import ru.inno.pageFactory.other.NotChangeTextForXSecond;
import ru.inno.pageFactory.page.MainPage;
import ru.inno.pageFactory.page.SearchResultPage;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Stream;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.openqa.selenium.By.cssSelector;

@DisplayName("UI-тесты labirint.ru:")
@ExtendWith({SeleniumJupiter.class, MyTestWatcher.class})
public class LabirintUITest extends BaseUITest {
    private static final List<WebDriver> openedBrowsers = new ArrayList<>();
    private static final String envPropsForAllureFilename = "allure-results/environment.properties";
    private final String BASE_URL = "https://www.labirint.ru";

    @BeforeAll
    public static void setUp() {
        try {
            Files.delete(Paths.get(envPropsForAllureFilename));
        } catch (IOException e) {
            System.out.println("Файл environment.properties отсутствует.");
        }
    }

    @AfterAll
    public static void tearDown() {
        for (WebDriver b : openedBrowsers) {
            b.quit();
        }
    }

    @Test
    @DisplayName("Добавление в корзину всех книг по Java (исходный)")
    public void buyJavaBooksManual(ChromeDriver browser) {
        //Установка неявного ожидания для всех команд 4 секунды
        browser.manage().timeouts().implicitlyWait(Duration.ofSeconds(4));

        //1. Открытие страницы
        browser.get(BASE_URL);

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

    @Test
    @Tag("Search")
    @DisplayName("Добавление в корзину всех книг по Java (Selenide)")
    public void buyJavaBooksManualSelenide() throws InterruptedException {

        //Настройка Selenide
        Configuration.baseUrl = BASE_URL;

        step("1. Открыть страницу " + BASE_URL, () -> {
            open("/");
        });

        webDriver = WebDriverRunner.getWebDriver();     //Для TestWatcher для создания скриншотов
        step("2. Скрыть плашку с cookies", () -> {
            Cookie cookie = new Cookie("cookie_policy", "1");
            WebDriverRunner.getWebDriver().manage().addCookie(cookie);
        });

        step("3. В поисковую строку написать `Java`, выполнить поиск", () -> {
            $("#search-field").val("Java").pressEnter();
        });

        step("4. Изменить сортировку с `Сначала релевантные` на `Сначала высокий рейтинг`", () -> {
            $("span.sorting-items").click();
            $("[data-event-content='высокий рейтинг']").click();
        });

        //Ожидаем закрытия loader'а
        waitLoaderOut();

        step("5. Закрыть 'чипсы'", () -> {
            closeChips(Chips.PREORDER);
            closeChips(Chips.AWAITING);
            closeChips(Chips.NOT_AVAILABLE);
        });

        step("6. Добавить все товары на странице в корзину (кнопка Купить)");
        //Ожидаем закрытия loader'а
        waitLoaderOut();

        //Получение списка кнопок "В корзину"
        ElementsCollection buyButtons = $$(".btn-tocart.buy-link");

        //Нажатие всех кнопок "В корзину"
        for (SelenideElement element : buyButtons) {
            element.click();
        }

        step("7. Счетчик товаров в корзине должен быть равен количеству добавленных товаров на шаге 6", () -> {
            //Получение счётчика товаров в корзине
            $(".basket-in-cart-a").shouldHave(ownText(String.valueOf(buyButtons.size())));  //Явно ждём появления в корзине нужного количества товара
            sleep(2000);    //Ещё немного ждём, чтобы проверить, что количество товаров не увеличилось

            int cartCounter = Integer.parseInt($(".basket-in-cart-a").getText());

            //Проверка счётчика
            assertEquals(buyButtons.size(), cartCounter);
        });
    }

    //Закрыть "чипсу"
    private static void closeChips(Chips chip) {
        ElementsCollection chips = $(".navisort__filters-reset").shouldBe(visible).$$(".filter-reset__content");
        for (SelenideElement c : chips) {
            if (c.getText().equalsIgnoreCase(chip.getTitle())) {
                c.shouldBe(visible, enabled).click();
                waitLoaderOut();
                return;
            }
        }
    }

    //Ожидаем закрытия loader'а
    private static void waitLoaderOut() {
        $("div.loading-panel").shouldNotBe(visible, Duration.ofSeconds(5));
    }

    @Test
    @DisplayName("Добавление в корзину всех книг по Java (на PageObject)")
    public void buyJavaBooksPageObject(ChromeDriver browser) {
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

        books.forEach(BookCard::addToCart);     //Проверка Stream Api

        //Или
//        books.forEach(b -> b.addToCart());     //Проверка Stream Api

        //Или
//        for (BookCard book : books) {
//            book.addToCart();
//        }

        //7. Счетчик товаров в корзине равен количеству добавленных товаров на шаге 6
        //Получение счётчика товаров в корзине
        int cartCounter = searchResultPage
                .getHeader()
                .awaitCartCounter()
                .getCartCounter();

        //Проверка счётчика
        assertEquals(books.size(), cartCounter);
    }

    /**
     * ------------------------------------------------------------------------------------
     * ДЗ2
     * ------------------------------------------------------------------------------------
     */

    /*Напишите фабрику WebDriver'ов
    Шаги:
    1. Изучите паттерн Фабрика (https://vertex-academy.com/tutorials/ru/pattern-factory-java/ или
    https://progery.ru/pattern-fabrika-java)
    2. Напишите класс WebDriverFactory:
    - Фабрика предоставляет два метода getDriver(String name) и getDriver(String name, String... args)
    - Первый метод создает новый веб-драйвер (какой именно - определяется из name)
    - Второй метод также создает драйвер, но запускает его с параметрами, которые были переданы в args
    3. Доработайте ваш тест на Лабиринт (или любой другой):
    - Тест должен стать параметризированным
    - Параметром является имя браузера ("chrome", "ff", "edge", ...)
    - Перед началом работ, тест получает драйвер по имени из фабрики

    Примеры вызовов:
    - Открыть Хром WebDriverFactory.getDriver(Drivers.CHROME)
    - Открыть Хром с опциями WebDriverFactory.getDriver(Drivers.CHROME, "--window-size=800,800", "--window-position=50,50")

    Список опций по браузерам:
    https://www.selenium.dev/documentation/webdriver/browsers/*/


    //Работа с:
    //1. Page Object - для ООП представления UI
    //2. Page Factory (Selenium) - для автоматического поиска WebElement для Page Object
    //3. Factory - для создания конкретного WebDriver
    //4. Allure - для создания отчётов
    //5. JUnit ParameterizedTest - для запуска тестов с параметрами

    //Вариант 1 с параметрами
    @DisplayName("Добавление в корзину всех книг по Java (PResolver 1):")
    @Epic("Каталог")
    @Story("Как пользователь, я могу искать товары по названию")
    @Feature("Поиск по каталогу")
    @Owner("Dudorov")
    @Tag("Positive")
    @Tags({@Tag("Search"), @Tag("AddToCart")})  //Теги для JUnit и Allure
    @Description("Тест поиска книг по Java и добавление в корзину со страницы результатов поиска.")
    //Описание теста для Allure
    @Severity(SeverityLevel.BLOCKER)    //Важность теста для Allure
    @ParameterizedTest(name = "в {0}: {1}")
    @ArgumentsSource(driverParameterProvider.class)
    public void buyJavaBooksFactoryParameterizedByJUnit(DriverType driverType, String... args) throws IOException {
        //Создание драйвера через фабрику
        WebDriverFactory factory = new WebDriverFactory();
        WebDriver browser;
        if (args.length == 0) {
            browser = factory.getDriver(driverType);
        } else {
            browser = factory.getDriver(driverType, args);
        }
        webDriver = browser;

        //Сохранение параметров среды в environment.properties
        addEnvParamsToAllure(browser, "Добавление в корзину всех книг по Java (PResolver 1): "
                + Arrays.toString(args)
                + driverType);

        //Сохранение драйвера для очистки после завершения тестов
        openedBrowsers.add(browser);

        MainPage mainPage = new MainPage(browser);

        //1. Открытие страницы
        //2. Скрыть плашку с cookies
        mainPage.open();
//        getScreenshot(browser); //Сделать скриншот главной страницы и добавить его в отчёт Allure

        //3. В поисковую строку написать `Java`
        //4. Выполнить поиск
        SearchResultPage searchResultPage = mainPage.getHeader().search("Java");

        //5. Изменить сортировку с `Сначала релевантные` на `Сначала высокий рейтинг`
        searchResultPage.changeSort(SortOption.HIGH_RATE);

        //6. Добавить все товары на странице в корзину (кнопка Купить)
        //Фильтрация выборки и получение списка карточек книг
        List<BookCard> books = searchResultPage
                .closeChips(Chips.PREORDER)
                .closeChips(Chips.AWAITING)
                .closeChips(Chips.NOT_AVAILABLE)
                .getAllBooks();

        //Добавление книг в корзины кликом по кнопке "В корзину"
        step("Добавить все товары в корзину.", () -> {

            generateJson(); //Добавление json-файла в Attachment к Allure

            for (BookCard book : books) {
                book.addToCart();
            }
        });

        //Пример step() в качестве логирования каких-то тестовых данных в Allure
        String getAllQuery = "SELECT * FROM CUSTOMER";
        //Что-то на JDBC
        step("Какой-то текст для логирования, например, SQL-запрос: " + getAllQuery);


        //7. Счетчик товаров в корзине равен количеству добавленных товаров на шаге 6
        //Получение счётчика товаров в корзине
        int cartCounter = searchResultPage
                .getHeader()
                .awaitCartCounter()
                .getCartCounter();

        //Проверка счётчика
        step("Проверить, что счётчик в корзине показывает " + books.size(),
                () -> {
                    assertEquals(books.size(), cartCounter);
                });
    }

    //-----------------------------------------------------------------------------------------------------------
    //Получение скриншота и добавление его в Allure как Attachment
    @Attachment(value = "screen.png", type = "image/png")
    private byte[] getScreenshot(WebDriver driver) {
        return driver.findElement(cssSelector("body")).getScreenshotAs(OutputType.BYTES);
    }

    //Получение Строки и добавление его в Allure как Attachment (например, как json-файл)
    @Attachment(value = "requestBody.json", type = "application/json")
    private String generateJson() {
        return "{\"tester\":\"Ivan\", \"age\":37}";
    }

    //Заполнение файла environment.properties параметрами окружения.
    //Вызывается в явном виде в тестах.
    public void addEnvParamsToAllure(WebDriver driver, String testId) throws IOException {
        Properties properties = new Properties();
        Capabilities caps = ((RemoteWebDriver) driver).getCapabilities();

        String testIdHash = String.valueOf(testId.hashCode());
        properties.put("test.id." + testIdHash, testId);
        properties.put("browser." + testIdHash, caps.getBrowserName());
        properties.put("browser.version." + testIdHash, caps.getBrowserVersion());
        properties.put("os." + testIdHash, System.getProperty("os.name"));

        //Создание каталога
        String path = "allure-results/";
        Files.createDirectories(Paths.get(path));

        //Сохранение параметров окружения в файл
        FileOutputStream outputStream = new FileOutputStream(envPropsForAllureFilename, true);
        properties.store(outputStream, "Tests Environment Properties");

    }

    //Провайдер данных для теста
    static class driverParameterProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {

            return Stream.of(
                    Arguments.of(DriverType.CHROME, new String[]{}),
                    Arguments.of(DriverType.CHROME, new String[]{"-headless"})
//                    Arguments.of(DriverType.CHROME, new String[]{"--window-size=800,800", "--window-position=50,50"}),
//                    Arguments.of(DriverType.CHROME, new String[]{"--window-size=100,1000", "--window-position=100,100"}),
//                    Arguments.of(DriverType.CHROME, new String[]{"--start-maximized"}),
//                    Arguments.of(DriverType.FIREFOX, new String[]{}),
//                    Arguments.of(DriverType.FIREFOX, new String[]{"-headless"}),
//                    Arguments.of(DriverType.FIREFOX, new String[]{"--width=800", "--height=800"}),
//                    Arguments.of(DriverType.FIREFOX, new String[]{"--width=100", "--height=1000"})
            );
        }
    }


    //Вариант 2 при наличии общих параметров для всех браузеров
    //--------------------------------------------------------------------------------------------------
    //Интересная библиотека Cartesian Product of Parameters (расширение для JUnit) https://junit-pioneer.org/docs/cartesian-product/
    //Позволяет просто выполнять полный перебор параметров для тестов (например, здесь для каждого браузера
    // из Enum DriverType будут выполнены тесты с разными параметрами т.е. будет выполнено Х*Y тестов)
    @DisplayName("Добавление в корзину всех книг по Java (PResolver 2):")
    @CartesianTest
    @CartesianTest.MethodFactory("browserParamsProvider")
    public void buyJavaBooksFactoryParameterizedByCartesian(DriverType driverType, String... args) {
        //Создание драйвера через фабрику
        WebDriverFactory factory = new WebDriverFactory();
        WebDriver browser;
        if (args.length == 0) {
            browser = factory.getDriver(driverType);
        } else {
            browser = factory.getDriver(driverType, args);
        }

        openedBrowsers.add(browser);    //Для очистки

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
        //Фильтрация выборки и получение списка карточек книг
        List<BookCard> books = searchResultPage
                .closeChips(Chips.PREORDER)
                .closeChips(Chips.AWAITING)
                .closeChips(Chips.NOT_AVAILABLE)
                .getAllBooks();

        //Добавление книг в корзины кликом по кнопке "В корзину"
        for (BookCard book : books) {
            book.addToCart();
        }

        //7. Счетчик товаров в корзине равен количеству добавленных товаров на шаге 6
        //Получение счётчика товаров в корзине
        int cartCounter = searchResultPage
                .getHeader()
                .awaitCartCounter()
                .getCartCounter();

        //Проверка счётчика
        assertEquals(books.size(), cartCounter);
    }

    //Провайдер данных для CartesianTest
    static ArgumentSets browserParamsProvider() {
        return ArgumentSets
                .argumentsForFirstParameter(
                        DriverType.values()
                )
                .argumentsForNextParameter(
                        new String[]{"-headless"},
                        new String[]{} //Базовые настройки
//                        new String[]{"--window-size=100,1000", "--window-position=100,100"},    //работает только для Chrome
//                        new String[]{"--start-maximized"}   //Работает только для Chrome
                );
    }


    @Test
    @DisplayName("Добавление в корзину всех книг по Java (на PageObject) + Docker (Firefox + PostgreSQL)")
    public void buyJavaBooksPageObjectDocker() throws MalformedURLException, SQLException {

        WebDriver browser = new RemoteWebDriver(new URL("http://localhost:4444"), new FirefoxOptions());
        openedBrowsers.add(browser);

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
//        searchResultPage.changeSort(SortOption.HIGH_RATE);

        //6. Добавить все товары на странице в корзину (кнопка Купить)
        //Получение списка кнопок "В корзину"
        List<BookCard> books = searchResultPage
                .closeChips(Chips.PREORDER)
                .closeChips(Chips.AWAITING)
                .closeChips(Chips.NOT_AVAILABLE)
                .getAllBooks();




        books.forEach(BookCard::addToCart);     //Проверка Stream Api

        //Сохранение книг в БД, развёрнутой в Docker
        String connectionString = "jdbc:postgresql://localhost:5432/my-db";
        String user = "myuser";
        String password = "mypass";
        Connection connection = DriverManager.getConnection(connectionString, user, password);

        int counter = 0;
        for (BookCard b: books) {
            String insertQuery = "insert into books (\"book_id\", \"book_name\", \"book_price\") values (?,?,?);";
            PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
            preparedStatement.setInt(1, counter);
            preparedStatement.setString(2, b.getTitle());
            preparedStatement.setInt(3, b.hashCode());
            preparedStatement.executeUpdate();
            counter++;
        }

        String getAllQuery = "select * from books;";
        ResultSet resultSet = connection.createStatement().executeQuery(getAllQuery);

        String s = "";
        while (resultSet.next()) {

            s = resultSet.getInt("book_id") + " " +
                    resultSet.getString("book_name") + " " +
                    resultSet.getInt("book_price");
            System.out.println(s);
        }

        //7. Счетчик товаров в корзине равен количеству добавленных товаров на шаге 6
        //Получение счётчика товаров в корзине
        int cartCounter = searchResultPage
                .getHeader()
                .awaitCartCounter()
                .getCartCounter();

        //Проверка счётчика
        assertEquals(books.size(), cartCounter);
    }
}
