package ru.inno.labirint.other;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;

//https://seleniumjava.com/2017/01/06/how-to-create-custom-expected-conditions-in-selenium/

public class MyExpectedConditions {
    private static String textToCompare = "";
    private static int counter = 0;
    private static int increment = 500;


    public static ExpectedCondition<WebElement> notChangeTextForXSecond(By locator, String propertyName, int seconds) {

        return new ExpectedCondition<>() {
            @Override
            public WebElement apply(WebDriver webDriver) {
                WebElement element = webDriver.findElement(locator);

                //Если текст в элементе совпал с сохранённым значением, то увеличиваем счётчик
                if (element.getText().equals(textToCompare)) {
                    counter++;
                } else {
                    textToCompare = element.getText();
                    counter = 0;
                }
//                String currentValue = element.getCssValue(propertyName);
                System.out.println(locator + " = " + textToCompare + ", counter = " + counter);
                if (counter >= seconds * increment * 2 / 2) {
                    return element;
                }
                return null;
            }
        };
    }
}
