package com.openmonet.pagecore.utils;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.ex.ElementShould;
import org.apache.commons.lang3.time.StopWatch;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import com.openmonet.configurations.WebConfigurations;
import com.openmonet.utils.ErrorMessage;
import com.openmonet.utils.WaitUtil;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.$;
import static com.openmonet.utils.UrlUtil.PATTERN_WEB;
import static com.openmonet.utils.UrlUtil.formatUrl;

public class CoreChecks {

    private static final Logger LOGGER = LoggerFactory.getLogger(CoreChecks.class);

    private static int getTimeoutSeconds(Integer timeoutSeconds) {
        return timeoutSeconds == null
                ? WebConfigurations.config().webDriverTimeoutSeconds()
                : timeoutSeconds;
    }

    private static WebDriver getDriver() {
        return WebDriverRunner.getWebDriver();
    }

    /**
     * Проверяет что текущий url равен переданному
     *
     * @param url ожидаемый url
     */
    public static void urlEquals(String url) {
        String expectedUrl = formatUrl(PATTERN_WEB, url, null, null);
        Assert.assertEquals(getDriver().getCurrentUrl(), expectedUrl,
                ErrorMessage.getErrorMessage(ErrorMessage.URL_NOT_EQUAL_ACTUAL, expectedUrl, getDriver().getCurrentUrl()));
        LOGGER.info("url '{}' равен текущему '{}'", expectedUrl, getDriver().getCurrentUrl());
    }

    /**
     * Проверяет что текущий url содержит текст
     *
     * @param text ожидаемый текст
     */
    public static void urlContains(String text) {
        Assert.assertTrue(getDriver().getCurrentUrl().contains(text),
                ErrorMessage.getErrorMessage(ErrorMessage.URL_NOT_CONTAINS_TEXT, getDriver().getCurrentUrl(), text));
        LOGGER.info("Текущий url '{}' содержит текст '{}'", getDriver().getCurrentUrl(), text);
    }

    /**
     * Проверяет, что атрибут элемента равен ожидаемому
     *
     * @param element     Элемент
     * @param attrName    Атрибут
     * @param expectValue Ожидаемое значение атрибута
     */
    public static void checkAttribute(SelenideElement element, String attrName, String expectValue, Integer timeoutSeconds) {
        int timeout = getTimeoutSeconds(timeoutSeconds);
        element
                .shouldBe(Condition.exist, Duration.ofSeconds(timeout))
                .shouldHave(Condition.attribute(attrName, expectValue), Duration.ofSeconds(timeout));
    }

    /**
     * Проверяет, что атрибут элемента содержит ожидаемое значение
     *
     * @param element     Элемент
     * @param attrName    Атрибут
     * @param expectValue Часть текста ожидаемая в атрибуте
     */
    public static void checkThatAttrValueContainsText(SelenideElement element, String attrName, String expectValue, Integer timeoutSeconds) {
        int timeout = getTimeoutSeconds(timeoutSeconds);
        element
                .shouldBe(Condition.exist, Duration.ofSeconds(timeout))
                .shouldHave(Condition.attributeMatching(attrName, ".*" + expectValue + ".*"));
    }

    /**
     * Проверяет, что на странице имеется элемент
     */
    public static void elementVisibleOnPage(SelenideElement element, Integer timeoutSeconds) {
        int timeout = getTimeoutSeconds(timeoutSeconds);
        element
                .shouldBe(Condition.exist, Duration.ofSeconds(timeout))
                .shouldBe(Condition.visible, Duration.ofSeconds(timeout));
    }

    /**
     * Проверяет, что на странице имеется текст
     */
    public static void textVisibleOnPage(String text, Integer timeoutSeconds) {
        int timeout = getTimeoutSeconds(timeoutSeconds);
        $(Selectors.byText(text))
                .shouldBe(Condition.exist, Duration.ofSeconds(timeout))
                .shouldBe(Condition.visible, Duration.ofSeconds(timeout));
    }

    /**
     * Проверяет, что на странице отсутствует текст
     */
    public static void textAbsentOnPage(String text, Integer timeoutSeconds) {
        int timeout = getTimeoutSeconds(timeoutSeconds);
        $(Selectors.byText(text))
                .shouldBe(Condition.not(Condition.exist), Duration.ofSeconds(timeout))
                .shouldBe(Condition.not(Condition.visible), Duration.ofSeconds(timeout));
    }

    /**
     * Проверяет, что на странице отсутствует элемент
     */
    public static void elementAbsentOnPage(SelenideElement element, Integer timeoutSeconds) {
        int timeout = getTimeoutSeconds(timeoutSeconds);
        element
                .shouldBe(Condition.not(Condition.exist), Duration.ofSeconds(timeout))
                .shouldBe(Condition.not(Condition.visible), Duration.ofSeconds(timeout));
    }

    /**
     * Проверяет, что элемент доступен
     */
    public static void elementEnabled(SelenideElement element, Integer timeoutSeconds) {
        int timeout = getTimeoutSeconds(timeoutSeconds);
        SelenideElement webElementNew;
        Point start;
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        do {
            WaitUtil.waitSeconds(1);
            webElementNew = element;
            start = webElementNew.getLocation();
        } while (!element.getLocation().equals(start) && stopWatch.getTime() > timeout * 1000);
        stopWatch.stop();
        element
                .shouldBe(Condition.exist, Duration.ofSeconds(timeout))
                .shouldBe(Condition.enabled, Duration.ofSeconds(timeout));
    }

    /**
     * Проверяет, что элемент недоступен
     */
    public static void elementDisabled(SelenideElement element, Integer timeoutSeconds) {
        int timeout = getTimeoutSeconds(timeoutSeconds);
        element
                .shouldBe(Condition.exist, Duration.ofSeconds(timeout))
                .shouldBe(Condition.disabled, Duration.ofSeconds(timeout));
    }

    /**
     * Проверяет, что элемент невидим
     */
    public static void elementNotVisible(SelenideElement element, Integer timeoutSeconds) {
        int timeout = getTimeoutSeconds(timeoutSeconds);
        element
                .shouldBe(Condition.exist, Duration.ofSeconds(timeout))
                .shouldNotBe(Condition.visible, Duration.ofSeconds(timeout));
    }

    /**
     * Проверяет, что текст элемента соответствует ожидаемому тексту
     *
     * @param element        элемент
     * @param expectedText   ожидаемый текст
     * @param timeoutSeconds количество секунд, в течении этого времени ожидается текст
     */
    public static void elementTextEqualsExpectedText(SelenideElement element, String expectedText, Integer timeoutSeconds) {
        int timeout = getTimeoutSeconds(timeoutSeconds);
        element.shouldBe(Condition.exactTextCaseSensitive(expectedText), Duration.ofSeconds(timeout));
    }

    /**
     * проверяет что текст элемента не соответствует ожидаемому тексту
     *
     * @param element        элемент
     * @param expectedText   текст
     * @param timeoutSeconds количество секунд
     */
    public static void elementTextNotEqualsExpectedText(SelenideElement element, String expectedText, Integer timeoutSeconds) {
        int timeout = getTimeoutSeconds(timeoutSeconds);
        element.shouldNotBe(Condition.exactTextCaseSensitive(expectedText), Duration.ofSeconds(timeout));
    }

    /**
     * Проверяет, что текст элемента содержит ожидаемый текст
     *
     * @param element        элемент
     * @param expectedText   ожидаемый текст или регулярное выражение
     * @param timeoutSeconds количество секунд, в течении этого времени ожидается текст
     */
    public static void elementTextContainsExpectedText(SelenideElement element, String expectedText, Integer timeoutSeconds) {
        int timeout = getTimeoutSeconds(timeoutSeconds);
        element.shouldBe(Condition.matchText(expectedText), Duration.ofSeconds(timeout));
    }

    /**
     * Проверяет, появился ли элемент за переданное время
     *
     * @param element        - веб-элемент
     * @param timeoutSeconds -   таймаут ожидания (может быть null)
     * @return -   появился ли элемент
     */
    public static boolean isElementWillAppear(SelenideElement element, Integer timeoutSeconds) {
        try {
            elementVisibleOnPage(element, timeoutSeconds);
            return true;
        } catch (ElementNotFound elementNotFound) {
            return false;
        }
    }

    /**
     * Проверяет, появился ли элемент за переданное время
     *
     * @param xpath          - xpath веб-элемента
     * @param timeoutSeconds -   таймаут ожидания(может быть null)
     * @return -   появился ли элемент
     */
    public static boolean isElementWillAppear(String xpath, Integer timeoutSeconds) {
        try {
            SelenideElement element = $(Selectors.byXpath(xpath));
            elementVisibleOnPage(element, timeoutSeconds);
            return true;
        } catch (ElementNotFound elementNotFound) {
            return false;
        }
    }

    /**
     * Проверяет, исчез ли элемент за переданное время
     *
     * @param element        веб-элемент
     * @param timeoutSeconds -   таймаут ожидания (может быть null)
     * @return -   исчез ли элемент
     */
    public static boolean isElementWillDisappear(SelenideElement element, Integer timeoutSeconds) {
        try {
            elementAbsentOnPage(element, timeoutSeconds);
            return true;
        } catch (ElementShould elementShould) {
            return false;
        }
    }

    /**
     * Проверяет, исчез ли элемент за переданное время
     *
     * @param xpath          - xpath веб-элемента
     * @param timeoutSeconds -   таймаут ожидания (может быть null)
     * @return -   исчез ли элемент
     */
    public static boolean isElementWillDisappear(String xpath, Integer timeoutSeconds) {
        try {
            SelenideElement element = $(Selectors.byXpath(xpath));
            elementAbsentOnPage(element, timeoutSeconds);
            return true;
        } catch (ElementShould elementShould) {
            return false;
        }
    }

    /**
     * Проверяет, выбран ли чекбокс
     * @param element   -   чекбокс
     * @return  -   выбран ли чекбокс
     */
    public static boolean isCheckboxSelected(SelenideElement element) {
        return element.isSelected();
    }
}
