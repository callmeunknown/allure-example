package com.openmonet.pagecore;

/**
 * Класс, который хранит текущую страницу теста
 */
public class PageManager {

    public PageManager() {
    }

    private PageCore currentPage;

    /**
     * Возвращает текущую страницу, на которой в текущий момент производится тестирование
     */
    public PageCore getCurrentPage() {
        if (currentPage == null) {
            throw new IllegalStateException("Текущая страница не задана");
        }
        return currentPage.initialize();
    }

    /**
     * Задает текущую страницу по ее имени
     */
    public void setCurrentPage(PageCore page) {
        this.currentPage = page;
    }

}