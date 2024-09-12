package com.openmonet.pagecore.utils;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import com.openmonet.JsCommand;
import com.openmonet.utils.WaitUtil;

import java.util.ArrayList;
import java.util.List;

import static com.openmonet.utils.ErrorMessage.BROWSER_TAB_NUMBER_MORE_THAN_TABS;
import static com.openmonet.utils.ErrorMessage.getErrorMessage;
import static com.openmonet.utils.UrlUtil.PATTERN_WEB;
import static com.openmonet.utils.UrlUtil.formatUrl;

public class CoreActions {

    private static final Logger LOGGER = LoggerFactory.getLogger(CoreActions.class);

    private static WebDriver getDriver() {
        return WebDriverRunner.getWebDriver();
    }

    /**
     * Открывает ссылку по переданному url и переводит контекст драйвера на новое окно
     */
    public static void openUrlOnNewTab(String url) {
        String command = String.format("window.open('%s')", formatUrl(PATTERN_WEB, url, null, null));
        Selenide.executeJavaScript(command);
        List<String> handles = new ArrayList<>(getDriver().getWindowHandles());
        Selenide.switchTo().window(handles.get(handles.size() - 1));
        LOGGER.info("Отрытие новой вкладки с урл {}", url);
    }

    /**
     * Переключается на следующую вкладку или вкладку по порядковому номеру (1, 2, ...)
     */
    public static void switchToNextTab(Integer tabNumber) {
        List<String> handles = new ArrayList<>(getDriver().getWindowHandles());
        if (tabNumber != null) {
            Assert.assertTrue(handles.size() >= tabNumber, getErrorMessage(BROWSER_TAB_NUMBER_MORE_THAN_TABS, tabNumber));
        }
        tabNumber = tabNumber == null ? handles.size() - 1 : tabNumber - 1;
        Selenide.switchTo().window(handles.get(tabNumber));
        LOGGER.info("Переключение на вкладку {}", handles.size());
    }

    /**
     * Закрывает текущую вкладку и переключается на предыдущую
     */
    public static void closeCurrentTabAndSwitchToPrevious() {
        Selenide.closeWindow();
        List<String> handles = new ArrayList<>(getDriver().getWindowHandles());
        Selenide.switchTo().window(handles.get(handles.size() - 1));
    }

    /**
     * Посимвольное заполнение поля
     *
     * @param element - элемент
     * @param text    - значение
     */
    public static void fillInputByCharacter(SelenideElement element, String text) {
        fillInputByCharacter(element, text, 0.2);
    }

    public static void fillInputByCharacter(SelenideElement element, String text, double pause) {
        element.sendKeys(Keys.HOME);
        for (char character : text.toCharArray()) {
            element.sendKeys(String.valueOf(character));
            WaitUtil.waitSeconds(pause);
        }
    }

    /**
     * Очищает поле посимвольно
     * @param input -   поле для очистки
     */
    public static void clearFieldByChar(SelenideElement input) {
        String value = input.val();
        if (value != null) {
            for (char character : value.toCharArray()) {
                input.sendKeys(Keys.BACK_SPACE);
            }
        }
    }

    /**
     * Удаляет первую букву в поле
     * @param input -   поле для очистки
     */
    public static void clearFieldByFirstChar(SelenideElement input) {
        String value = input.val();
        if (value != null) {
            for (char character : value.toCharArray()) {
                input.sendKeys(Keys.ARROW_LEFT);
            }
            input.sendKeys(Keys.DELETE);
        }
    }

    public static void clearField(SelenideElement input) {
        input.click();
        input.clear();
        input.sendKeys(Keys.chord(Keys.LEFT_CONTROL, "a"));
        input.sendKeys(Keys.BACK_SPACE);
    }

    /**
     * Снимает фокус с поля
     *
     * @param element SelenideElement
     */
    public static void removeFocus(SelenideElement element) {
        Selenide.executeJavaScript(JsCommand.REMOVE_FOCUS.getJS(), element);
    }
}
