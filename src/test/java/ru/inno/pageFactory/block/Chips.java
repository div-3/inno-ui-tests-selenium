package ru.inno.pageFactory.block;

public enum Chips {
    AVAILABLE("В наличии"),
    PREORDER("Предзаказ"),
    AWAITING("Ожидаются"),
    NOT_AVAILABLE("Нет в продаже");

    private final String title;

    Chips(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
