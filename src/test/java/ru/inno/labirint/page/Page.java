package ru.inno.labirint.page;

import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import ru.inno.labirint.block.Header;

public abstract class Page {
    protected WebDriver driver;
    protected Header header;


    public Page(WebDriver driver) {
        this.driver = driver;
        this.header = new Header(driver);
    }

    public Header getHeader(){
        return this.header;
    }
}
