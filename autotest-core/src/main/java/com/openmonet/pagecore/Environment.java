package com.openmonet.pagecore;

import org.openqa.selenium.WebDriver;
import com.openmonet.annotations.Page;
import com.openmonet.utils.ReflectionUtil;

import java.util.Arrays;

/**
 * Используется для хранения кеша страниц и драйвера
 */
public class Environment {

    /**
     * Список веб-страниц, заданных пользователем, доступных для использования в сценарии
     */
    private static ThreadLocal<PageCache> pages = new ThreadLocal<>();

    /**
     * Текущий драйвер для теста
     */
    private static ThreadLocal<WebDriver> driver = new ThreadLocal<>();


    public static WebDriver getDriver() {
        return driver.get();
    }

    public static void setDriver(WebDriver driver) {
        Environment.driver.set(driver);
    }

    public static PageCache getPages() { return pages.get(); }

    public static PageCore getPage(String name) {
        return pages.get().get(name);
    }

    /**
     * Метод ищет классы, аннотированные "com.openmonet.annotations.Page",
     * добавляя ссылки на эти классы в поле "pages"
     * @param packageName наименование пакета где лежат файлы с описанием страниц
     */
    @SuppressWarnings("unchecked")
    public static void initPages(String packageName) {
        if (pages.get() == null) {
            pages.set(new PageCache());
            ReflectionUtil
                    .getPagesAnnotatedWith(packageName, Page.class)
                    .stream()
                    .map(it -> {
                        if (PageCore.class.isAssignableFrom(it)) {
                            return (Class<? extends PageCore>) it;
                        } else {
                            throw new IllegalStateException("Класс " + it.getName() + " должен наследоваться от PageCore");
                        }
                    })
                    .forEach(clazz -> pages.get().put(getClassAnnotationValue(clazz), clazz));
        }
    }

    /**
     * Вспомогательный метод, получает значение аннотации "com.openmonet.annotations.Name" для класса
     */
    private static String getClassAnnotationValue(Class<?> c) {
        return Arrays
                .stream(c.getAnnotationsByType(Page.class))
                .findFirst()
                .map(Page::title)
                .orElseThrow(() -> new AssertionError("Не найдены аннотации Page.Name в классe " + c.getName()));
    }

    public static String getPageUrlByPageTitle(String pageTitle) {
       return pages.get().urlByPageTitle(pageTitle);
    }

    public static String getPrefixByPageTitle(String pageTitle) {
        return pages.get().getPrefixByPageTitle(pageTitle);
    }

    /**
     * Закрывает веб-драйвер.
     * Вызывать в пост-хуке модуля
     */
    public static void demountDriver() {
        if (getDriver() != null) {
            getDriver().quit();
        }
        driver.remove();
    }
}