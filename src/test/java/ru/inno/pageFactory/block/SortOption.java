package ru.inno.pageFactory.block;

public enum SortOption {
    RELEVANT("релевантные"),
    NEW("новинки"),
    TOP_SALE("лидеры продаж"),
    REVIEWED("рецензируемые"),
    CHEAP("дешевые"),
    EXPENSIVE("дорогие"),
    MAX_DISCOUNT("с макс. скидкой"),
    HIGH_RATE("высокий рейтинг");

    private String title;

    SortOption(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
