package com.openmonet.pagecore;

import com.codeborne.selenide.Selenide;
import com.google.common.collect.Maps;
import com.openmonet.annotations.Page;

import java.util.Map;

/**
 * Предназначен для хранения страниц, используемых при прогоне тестов
 */
public final class PageCache {

    /**
     * Страницы, на которых будет производится тестирование < Имя, Страница >
     */
    private Map<String, Class<? extends PageCore>> pages;

    public PageCache() {
        pages = Maps.newHashMap();
    }

    /**
     * Получение страницы из "pages" по имени
     */
    public PageCore get(String pageName) {
        return Selenide.page(getPageFromPagesByName(pageName)).initialize();
    }

    /**
     * @param pageTitle title
     * @return Возвращает Page::url класса по его title
     */
    public String urlByPageTitle(String pageTitle) {
        return getPageFromPagesByName(pageTitle)
                .getAnnotation(Page.class)
                .url();
    }

    public String getPrefixByPageTitle(String pageTitle) {
        return getPageFromPagesByName(pageTitle)
                .getAnnotation(Page.class)
                .prefix();
    }

    /**
     * Получение страницы по классу
     */
    @SuppressWarnings("unchecked")
    public <T extends PageCore> T get(Class<T> clazz, String name) {
        PageCore page = Selenide.page(getPageFromPagesByName(name)).initialize();
        if (!clazz.isInstance(page)) {
            throw new IllegalStateException(name + " page is not a instance of " + clazz + ". Named page is a " + page);
        }
        return (T) page;
    }

    private Map<String, Class<? extends PageCore>> getPageMapInstanceInternal() {
        return pages;
    }

    private Class<? extends PageCore> getPageFromPagesByName(String pageName) throws IllegalArgumentException {
        Class<? extends PageCore> page = getPageMapInstanceInternal().get(pageName);
        if (page == null) {
            throw new IllegalArgumentException("Страница с именем '" + pageName + "' не задекларирована");
        }
        return page;
    }

    public void put(String pageName, Class<? extends PageCore> page) {
        if (page == null) {
            throw new IllegalArgumentException("Была передана пустая страница");
        }
        pages.put(pageName, page);
    }
}