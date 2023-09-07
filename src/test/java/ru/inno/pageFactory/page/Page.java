package ru.inno.pageFactory.page;

import org.openqa.selenium.WebDriver;
import ru.inno.pageFactory.block.Header;

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
