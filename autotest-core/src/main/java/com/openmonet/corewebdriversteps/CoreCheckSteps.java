package com.openmonet.corewebdriversteps;

import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import com.openmonet.pagecore.Environment;
import com.openmonet.pagecore.PageManager;
import com.openmonet.pagecore.utils.CoreChecks;
import com.openmonet.utils.ErrorMessage;
import com.openmonet.utils.RegexUtil;

import static com.codeborne.selenide.Selenide.$;
import static com.openmonet.context.ContextHolder.replaceVarsIfPresent;
import static com.openmonet.utils.ErrorMessage.URL_NO_MATCHES;

public class CoreCheckSteps {

    private PageManager pageManager;
    private static final Logger LOGGER = LoggerFactory.getLogger(CoreCheckSteps.class);

    public CoreCheckSteps(PageManager pageManager) {
        this.pageManager = pageManager;
    }

    /**
     * Возвращает элемент с текущей страницы
     * @param elementName   -   название элемента
     * @return  -   веб-элемент
     */
    private SelenideElement getElement(String elementName) {
        return pageManager
                .getCurrentPage()
                .getElement(elementName);
    }

    /**
     * проверка что на странице отображен элемент
     *
     * @param elementName наименование элемента
     */
    @When("page contain the element {string}")
    public void elementAppearOnThePage(String elementName) {
        SelenideElement element = getElement(elementName);
        CoreChecks.elementVisibleOnPage(element, null);
        LOGGER.info("на странице '{}' имеется элемент '{}'", pageManager.getCurrentPage().title(), elementName);
    }

    /**
     * Проверка наличия элемента по тексту
     * @param text
     */
    @When("page contain the element with text {string}")
    public void checkElementWithText(String text) {
        checkItElementWithText(text);
    }


    private void checkItElementWithText(String text) {
        SelenideElement element = $(Selectors.byText(text));
        CoreChecks.elementVisibleOnPage(element, null);
        LOGGER.info("элемент с текстом '{}' существует", text);
    }

    /**
     * ожидание элемента на странице в течении некоторого времени
     *
     * @param elementName    наименование элемента
     * @param timeoutSeconds количество секунд
     */
    @When("wait for the appearance of the element {string} for {int} sec")
    public void waitUntilElementIsVisibleOnPage(String elementName, int timeoutSeconds) {
        SelenideElement element = getElement(elementName);
        CoreChecks.elementVisibleOnPage(element, timeoutSeconds);
        LOGGER.info("на странице '{}' имеется элемент '{}'", pageManager.getCurrentPage().title(), elementName);
    }

    /**
     * проверка что на странице отсуствует элемент
     *
     * @param elementName наименование элемента
     */
    @When("element {string} is not present on the page")
    public void elementAbsentOnPage(String elementName) {
        SelenideElement element = getElement(elementName);
        CoreChecks.elementAbsentOnPage(element, null);
        LOGGER.info("на странице '{}' отсутствует элемент '{}'", pageManager.getCurrentPage().title(), elementName);
    }

    /**
     * ожидание исчезновения элемента со страницы в течении некоторого времени
     *
     * @param elementName    наименование элемента
     * @param timeoutSeconds количество секунд
     */
    @When("wait for the disappearance of the element {string} for {int} sec")
    public void waitUntilElementAbsentOnPage(String elementName, int timeoutSeconds) {
        SelenideElement element = getElement(elementName);
        CoreChecks.elementAbsentOnPage(element, timeoutSeconds);
        LOGGER.info("на странице '{}' отсутствует элемент '{}'", pageManager.getCurrentPage().title(), elementName);
    }

    /**
     * проверка значения атрибута у элемента
     *
     * @param attrName    наименование атрибута
     * @param elementName наименование элемента
     * @param expectValue ожидаемое значение
     */
    @When("^attribute \"(.*)\" of element \"(.+)\" equals the value \"(.*)\"()$")
    @When("^attribute \"(.*)\" of element \"(.+)\" equals the value \"(.*)\"( without spaces)$")
    public void attributeElementEqual(String attrName, String elementName, String expectValue, String format) {
        attributeEqualsWithTimeout(attrName, elementName, expectValue, format, null);
    }

    @When("^expecting that the attribute \"(.*)\" of element \"(.+)\" will become equal to the value \"(.*)\"() for \"(.*)\" sec$")
    @When("^expecting that the attribute \"(.*)\" of element \"(.+)\" will become equal to the value \"(.*)\"( бwithout spaces) for \"(.*)\" sec$")
    public void attributeEqualsWithTimeout(String attrName, String elementName, String expectValue, String format, String timeout) {
        expectValue = replaceVarsIfPresent(expectValue);
        SelenideElement element = getElement(elementName);
        Integer timeoutInt = timeout != null? Integer.parseInt(timeout): null;
        CoreChecks.elementVisibleOnPage(element, timeoutInt);
        if (element.getAttribute(attrName) != null) {
            if (format.isEmpty()) {
                CoreChecks.checkAttribute(element, attrName, expectValue, timeoutInt);
            } else {
                String actualValue = element.getAttribute(attrName).replaceAll("\\W", "");
                Assert.assertEquals(actualValue, expectValue, ErrorMessage.getErrorMessage(ErrorMessage.ACTUAL_VALUE_NOT_EQUAL_EXPECTED, actualValue, expectValue));
            }
            LOGGER.info("атрибут '{}' элемента '{}' равен '{}'", attrName, elementName, expectValue);
        } else {
            throw new AssertionError(String.format("У элемента '%s' нет атрибута '%s'", elementName, attrName));
        }
    }

    /**
     * проверка значения атрибута у элемента
     *
     * @param attrName    наименование атрибута
     * @param elementName наименование элемента
     * @param expectedValue текст или часть ожидаемого значения
     */
    @When("attribute {string} of element {string} contains the value {string}")
    public void checkThatAttrValueContainsText(String attrName, String elementName, String expectedValue) {
        expectedValue = replaceVarsIfPresent(expectedValue);
        SelenideElement element = getElement(elementName);
        CoreChecks.checkThatAttrValueContainsText(element, attrName, expectedValue, null);
        LOGGER.info("атрибут '{}' элемента '{}' содержит '{}'", attrName, elementName, expectedValue);
    }

    /**
     * проверяет текст именно в html тегах (не в атрибутах) соотвествует ожидаемому тексту
     *
     * @param elementName  наименование элемента на странице
     * @param expectedText ожидаемый текст
     */
    @Then("check that the element {string} has text with a value {string}")
    public void elementTextEqualsExpected(String elementName, String expectedText) {
        SelenideElement element = getElement(elementName);
        CoreChecks.elementTextEqualsExpectedText(element, replaceVarsIfPresent(expectedText), null);
    }

    /**
     * проверяет, что текст именно в html тегах (не в атрибутах) не соотвествует ожидаемому тексту
     *
     * @param elementName  наименование элемента на странице
     * @param expectedText ожидаемый текст
     */
    @Then("check that the text of element {string} does not match the value {string}")
    public void elementTextNotEqualsExpected(String elementName, String expectedText) {
        SelenideElement element = getElement(elementName);
        CoreChecks.elementTextNotEqualsExpectedText(element, replaceVarsIfPresent(expectedText), null);
    }

    /**
     * Проверяет, что элемент доступен
     *
     * @param elementName   -   название элемента
     */
    @Then("check that the element {string} is available")
    public void elementEnable(String elementName) {
        SelenideElement element = getElement(elementName);
        CoreChecks.elementEnabled(element, null);
    }

    @Then("check that element {string} became available for {int} sec")
    public void elementEnable(String elementName, int timeout) {
        SelenideElement element = getElement(elementName);
        CoreChecks.elementEnabled(element, timeout);
    }

    /**
     * Проверяет, что элемент недоступен
     *
     * @param elementName   -   название элемента
     */
    @Then("check that the element {string} is unavailable")
    public void elementDisable(String elementName) {
        SelenideElement element = getElement(elementName);
        CoreChecks.elementDisabled(element, null);
    }

    /**
     * Проверяет, что элемент невидим
     *
     * @param elementName   -   название элемента
     */
    @Then("check that the element {string} is invisible")
    public void elementNotVisible(String elementName) {
        SelenideElement element = getElement(elementName);
        CoreChecks.elementNotVisible(element, null);
    }

    @Then("check that the checkbox {string} is selected")
    public void checkboxIsSelected(String elementName) {
        SelenideElement checkbox = getElement(elementName);
        elementEnable(elementName);
        Assert.assertTrue(CoreChecks.isCheckboxSelected(checkbox), "Чекбокс не выбран!");
    }

    @Then("check that the checkbox {string} not selected")
    public void checkboxIsNotSelected(String elementName) {
        SelenideElement checkbox = getElement(elementName);
        elementEnable(elementName);
        Assert.assertFalse(CoreChecks.isCheckboxSelected(checkbox), "Чекбокс выбран!");
    }

    /**
     * проверка текста по регулярному выражению или тексту
     *
     * @param elementName  наименование элемента на странице
     * @param expectedText регулярное выражение или текст
     */
    @Then("check that the element {string} text contains {string}")
    public void elementTextContainsExpected(String elementName, String expectedText) {
        SelenideElement element = getElement(elementName);
        CoreChecks.elementTextContainsExpectedText(element, replaceVarsIfPresent(expectedText), null);
    }

    /**
     * проверка текущего url
     * <br/>можно начать написание url с переменной %{apiUrl}% или %{webUrl}%
     *
     * @param url часть или полный url (также может содержать переменные)
     */
    @Then("check that the current URL matches {string}")
    public void currentUrlEqualsExpected(String url) {
        CoreChecks.urlEquals(url);
    }

    /**
     * проверка текущего url
     * <br/>можно начать написание url с переменной %{apiUrl}% или %{webUrl}%
     *
     * @param url часть url (также может содержать переменные)
     */
    @Then("check that the current URL contains text {string}")
    public void currentUrlContainsExpected(String url) {
        CoreChecks.urlContains(replaceVarsIfPresent(url));
    }

    @Then("check that the current URL matches a regular expression{string}")
    public void checkUrlByRegex(String regex) {
        String currentUrl = Environment.getDriver().getCurrentUrl();
        Assert.assertTrue(RegexUtil.getMatch(currentUrl, regex), ErrorMessage.getErrorMessage(URL_NO_MATCHES, currentUrl, regex));
    }
}
