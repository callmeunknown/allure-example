package com.openmonet.corewebdriversteps;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.And;
import io.cucumber.java.en.When;
import org.openqa.selenium.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.openmonet.context.ContextHolder;
import com.openmonet.pagecore.PageManager;
import com.openmonet.pagecore.utils.CoreActions;
import com.openmonet.utils.ReflectionUtil;
import com.openmonet.utils.WaitUtil;

import static com.openmonet.context.ContextHolder.replaceVarsIfPresent;
import static com.openmonet.pagecore.utils.CoreActions.clearFieldByChar;
import static com.openmonet.pagecore.utils.CoreActions.clearFieldByFirstChar;

public class ActionCoreSteps {

    private PageManager pageManager;
    private static final Logger LOGGER = LoggerFactory.getLogger(ActionCoreSteps.class);

    public ActionCoreSteps(PageManager pageManager) {
        this.pageManager = pageManager;
    }

    /**
     * Нажимает на элемент
     *
     * @param elementName - название элемента
     */
    @Given("press on the element {string}")
    public void clickOnElement(String elementName) {
        elementName = replaceVarsIfPresent(elementName);
        SelenideElement element = pageManager
                .getCurrentPage()
                .getElement(elementName);
        element
                .shouldBe(Condition.visible)
                .shouldBe(Condition.enabled)
                .click();
        LOGGER.info("клик на элемент '{}'", elementName);
    }

    @Given("hover to the element {string}")
    public void moveCursorToElement(String elementName) {
        elementName = replaceVarsIfPresent(elementName);
        SelenideElement element = pageManager
                .getCurrentPage()
                .getElement(elementName);
        element
                .shouldBe(Condition.visible)
                .shouldBe(Condition.enabled)
                .hover();
        LOGGER.info("курсор наведен на элемент '{}'", elementName);
    }

    /**
     * нажимает на элемент двойным кликом
     *
     * @param elementName наименование элемента
     */
    @Given("press on the element {string} double click")
    public void doubleClick(String elementName) {
        SelenideElement element = pageManager
                .getCurrentPage()
                .getElement(elementName);
        element
                .shouldBe(Condition.visible).doubleClick();
        LOGGER.info("двойной клик на элемент '{}'", elementName);
    }

    /**
     * Ввод значения в элемент
     *
     * @param field - наименование элемента
     * @param value - значение
     */
    @When("enter the value {string} into the field {string}")
    public void fillTheField(String value, String field) {
        fillField(field, value, false);
    }

    @When("selected the value {string} from the dropdown list {string}")
    public void selectValue(String value, String field) {
        pageManager.getCurrentPage()
                .getElement(field)
                .should(Condition.visible)
                .selectOption(value);
    }

    /**
     * Ввод значения в элемент с маской. Перед вводом кликает на поле
     *
     * @param field - наименование элемента
     * @param value - значение
     */
    @When("enter the value {string} into the masked field {string}")
    public void fillTheMaskedField(String value, String field) {
        fillField(field, value, true);
    }

    /**
     * Снимает фокус с поля посредством нажатия на TAB
     *
     * @param field имя элемента
     */
    @When("remove focus from the field {string}")
    public void removeFocusFromField(String field) {
        field = replaceVarsIfPresent(field);
        SelenideElement element = pageManager
                .getCurrentPage()
                .getElement(field)
                .shouldBe(Condition.visible);
        CoreActions.removeFocus(element);
    }

    /**
     * Вводит значение в поле. если поле с маской - кликает перед вводом
     * @param field -   название поля
     * @param value -   значение для поля
     * @param isMasked  -   есть ли у поля маска
     */
    private void fillField(String field, String value, boolean isMasked) {
        field = replaceVarsIfPresent(field);
        value = replaceVarsIfPresent(value);
        SelenideElement fieldElement = pageManager
                .getCurrentPage()
                .getElement(field)
                .should(Condition.visible);
        if (isMasked) {
            fieldElement.click();
            CoreActions.fillInputByCharacter(fieldElement, value, 0.05d);
            return;
        }
        fieldElement.setValue(value);
        LOGGER.info("в поле '{}' введено значение '{}'", field, value);
    }

    /**
     * Посимвольный ввод значения в элемент
     *
     * @param field - наименование элемента
     * @param value - значение
     */
    @When("enter the value {string} character by character into the field {string}")
    public void fillFieldByCharacter(String value, String field) {
        field = replaceVarsIfPresent(field);
        value = replaceVarsIfPresent(value);
        SelenideElement fieldElement = pageManager
                .getCurrentPage()
                .getElement(field)
                .shouldBe(Condition.visible);
        CoreActions.fillInputByCharacter(fieldElement, value);
        LOGGER.info("в поле '{}' введено значение '{}'", field, value);
    }

    /**
     * Очистка поля
     *
     * @param elementName наименование элемента
     */
    @When("^clear the value of the element \"([^\"]*)\"$")
    public void clearField(String elementName) {
        SelenideElement element = pageManager
                .getCurrentPage()
                .getElement(replaceVarsIfPresent(elementName))
                .shouldBe(Condition.visible);
        clearFieldByChar(element);
        LOGGER.info("очищено поле {}", elementName);
    }

    @When("^delete the first letter of the element \"([^\"]*)\"$")
    public void deleteFirstChar(String elementName) {
        SelenideElement element = pageManager
                .getCurrentPage()
                .getElement(replaceVarsIfPresent(elementName))
                .shouldBe(Condition.visible);
        clearFieldByFirstChar(element);
        LOGGER.info("удалена первая буква в поле {}", elementName);
    }

    @When("^clear the value of the field \"([^\"]*)\" and enter a new value \"([^\"]*)\"$")
    public void clearAndFillField(String elementName, String value) {
        clearField(elementName);
        fillField(elementName, value, false);
    }

    @When("^clear the value of the masked field \"([^\"]*)\" and enter a new value \"([^\"]*)\"$")
    public void clearAndFillMaskedField(String elementName, String value) {
        clearField(elementName);
        fillField(elementName, value, true);
    }

    @When("^clear the value of the field \"([^\"]*)\" and enter the value character by charcter \"([^\"]*)\"$")
    public void clearAndFillByCharField(String elementName, String value) {
        clearField(elementName);
        fillFieldByCharacter(elementName, value);
    }

    /**
     * Шаг выделяет все значение поля и посимвольно вводит новое значение
     * @param elementName
     * @param value
     */
    @When("^replace the value of the field \"([^\"]*)\" with a new one \"([^\"]*)\"$")
    public void replaceValueInField(String elementName, String value) {
        SelenideElement element = pageManager
                .getCurrentPage()
                .getElement(replaceVarsIfPresent(elementName))
                .shouldBe(Condition.visible);
        element.click();
        element.sendKeys(Keys.LEFT_CONTROL, "A");
        value = replaceVarsIfPresent(value);
        for (char character : value.toCharArray()) {
            element.sendKeys(String.valueOf(character));
            WaitUtil.waitSeconds(0.2);
        }
    }

    @When("save the value of the element {string} into variable {string}")
    public void saveElementValue(String elementName, String varName) {
        elementName = replaceVarsIfPresent(elementName);
        SelenideElement element = pageManager
                .getCurrentPage()
                .getElement(elementName);
        String value = element.getValue() != null? element.getValue(): element.getText();
        ContextHolder.put(varName, value);
        LOGGER.info("в переменную '{}' сохранено значение '{}'", varName, value);
    }

    /**
     * проверка количества элементов
     *
     * @param elementsNames наименование элементов
     * @param size          ожидаемое количество
     */
    @When("the page contains a collection of {int} elements with the name {string}")
    public void elementsSize(int size, String elementsNames) {
        ElementsCollection elementsCollection = pageManager
                .getCurrentPage()
                .getElementsCollection(elementsNames);
        elementsCollection
                .forEach(selenideElement -> selenideElement.shouldBe(Condition.visible));
        elementsCollection
                .shouldHave(CollectionCondition.size(size));
    }

    //region шаги для выполнения действий с аннотацией @Action
    @And("^\\((.*)\\)$")
    public void act(String action) {
        this.action(action);
        LOGGER.info("Выполнен шаг '{}' на странице '{}'", action, pageManager.getCurrentPage().title());
    }

    @And("perform the action {string}")
    public void action(String action) {
        doAction(action);
    }

    @And("perform the action {string} with parameter {string}")
    public void action(String action, String param) {
        doAction(action, param);
    }

    @And("perform the action{string} with parameters {string} {string}")
    public void action(String action, String param1, String param2) {
        doAction(action, param1, param2);
    }

    @And("perform the action {string} with parameters {string} {string} {string}")
    public void action(String action, String param1, String param2, String param3) {
        doAction(action, param1, param2, param3);
    }

    @And("perform the action {string} with data")
    public void action(String action, DataTable table) {
        doAction(action, table);
    }

    private void doAction(String action, Object... param) {
        ReflectionUtil.executeMethodByTitle(pageManager.getCurrentPage(), action, param);
    }
    //endregion
}
