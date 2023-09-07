package ru.inno.pageFactory.block;

public enum Chips {
    AVAILABLE("в наличии"),
    PREORDER("предзаказ"),
    AWAITING("ожидаются"),
    NOT_AVAILABLE("нет в продаже");

    private final String title;

    Chips(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
