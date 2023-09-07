package ru.inno.pageFactory.other;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;

//Кастомный ExpectedCondition, который ждёт пока значение текста будет неизменно в течении указанного количества
// секунд. При использовании задаётся: локатор, период неизменности текста в секундах, sleepDuration, который
// указан в wait в миллисекундах.
//https://seleniumjava.com/2017/01/06/how-to-create-custom-expected-conditions-in-selenium/
public class NotChangeTextForXSecond implements ExpectedCondition {
    private final By elementLocator;
    private final int secondInMs = 1000;    //1 секунда в мс
    private final int limit;      //Количество отсчётов waiter, которое значение не должно меняться
    private String textToCompare = "";
    private int counter = 0;

    public NotChangeTextForXSecond(By elementLocator, int seconds, int sleepDurationInMS) {
        this.elementLocator = elementLocator;
        this.limit = seconds * secondInMs / sleepDurationInMS;
    }

    @Override
    public WebElement apply(Object input) {
        WebElement element = ((WebDriver) input).findElement(elementLocator);

        //Если текст в элементе при новом запросе совпал с сохранённым значением, то увеличиваем счётчик
        if (element.getText().equals(textToCompare)) {
            counter++;
        } else {
            //Иначе считаем что значение изменилось и считаем заново
            textToCompare = element.getText();
            counter = 0;
        }
//        System.out.println(elementLocator + " = " + textToCompare + ", counter = " + counter); //Отладка

        //Если достигли лимита, то возвращаем элемент
        if (counter >= limit) {
            return element;
        }
        return null;
    }
}
