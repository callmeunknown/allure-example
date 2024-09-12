package com.openmonet.pagecore;


import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.openmonet.annotations.Name;
import com.openmonet.annotations.Page;
import com.openmonet.utils.ReflectionUtil;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

/**
 * Класс для реализации паттерна PageObject
 */
public abstract class PageCore {

    /**
     * Возвращает объект SelenideElement по его имени (аннотированного "Annotations.Name")
     */
    public SelenideElement getElement(String name) {
        Object instance = namedElements.get(name);
        if (instance instanceof ElementsCollection) {
            throw new ClassCastException(String.format("Элемент '%s' имеет тип 'ElementsCollection'", name));
        }
        return (SelenideElement) Optional.ofNullable(namedElements.get(name))
                .orElseThrow(() -> new IllegalArgumentException("Элемент '" + name + "' не описан на странице '" + this.getClass().getName() + "'"));
    }

    /**
     * @param name Annotations.Name
     * @return Возвращает объект ElementsCollection по его имени (аннотированного "Annotations.Name")
     */
    public ElementsCollection getElementsCollection(String name) {
        Object instance = namedElements.get(name);
        if (instance instanceof SelenideElement) {
            throw new ClassCastException(String.format("Элемент '%s' имеет тип 'SelenideElement'", name));
        }
        return (ElementsCollection) Optional.ofNullable(namedElements.get(name))
                .orElseThrow(() -> new IllegalArgumentException("Элемент '" + name + "' не описан на странице '" + this.getClass().getName() + "'"));
    }

    /**
     * @return Возвращает значение аннотации Page::title
     */
    public String title() {
        return this
                .getClass()
                .getAnnotation(Page.class)
                .title();
    }

    /**
     * Список всех элементов страницы
     */
    private Map<String, Object> namedElements;

    public PageCore initialize() {
        namedElements = readNamedElements();
        return this;
    }

    /**
     * Поиск и инициализации элементов страницы и ее супер-классов
     */
    private Map<String, Object> readNamedElements() {
        checkNamedAnnotations();
        ArrayList<Field> fields = new ArrayList<>(Arrays.asList(getClass().getDeclaredFields()));
        Class superClass = getClass().getSuperclass();
        while (!superClass.getName().equals(PageCore.class.getName())) {
            List<Field> SuperClassFields = Arrays.asList(superClass.getDeclaredFields());
            fields.addAll(SuperClassFields);
            superClass = superClass.getSuperclass();
        }
        return fields
                .stream()
                .filter(field -> field.getDeclaredAnnotation(Name.class) != null)
                .peek(this::checkFieldType)
                .collect(toMap(f -> f.getDeclaredAnnotation(Name.class).value(), this::extractFieldValueViaReflection));
    }

    private void checkFieldType(Field f) {
        if (!SelenideElement.class.isAssignableFrom(f.getType())
                && !PageCore.class.isAssignableFrom(f.getType())
        ) {
            this.checkCollectionFieldType(f);
        }
    }

    private void checkCollectionFieldType(Field f) {
        if (ElementsCollection.class.isAssignableFrom(f.getType())) {
            return;
        } else if (List.class.isAssignableFrom(f.getType())) {
            ParameterizedType listType = (ParameterizedType) f.getGenericType();
            Class<?> listClass = (Class<?>) listType.getActualTypeArguments()[0];
            if (SelenideElement.class.isAssignableFrom(listClass) || PageCore.class.isAssignableFrom(listClass)) {
                return;
            }
        }
        throw new IllegalStateException(
                format("Поле с аннотацией '@Name' должно иметь тип SelenideElement, List<SelenideElement> или ElementsCollection.\n" +
                        "Найдено поле с типом %s", f.getType()));
    }

    /**
     * Поиск по аннотации "Annotations.Name"
     */
    private void checkNamedAnnotations() {
        List<String> list = Arrays.stream(getClass().getDeclaredFields())
                .filter(f -> f.getDeclaredAnnotation(Name.class) != null)
                .map(f -> f.getDeclaredAnnotation(Name.class).value())
                .collect(toList());
        if (list.size() != new HashSet<>(list).size()) {
            throw new IllegalStateException("Найдено несколько аннотаций '@Name' с одинаковым значением '" + duplicatesElement(list) + "' в классе " + this.getClass().getName());
        }
    }

    private Object extractFieldValueViaReflection(Field field) {
        return ReflectionUtil.extractFieldValue(field, this);
    }

    private List<String> duplicatesElement(List<String> nameCollection) {
        return nameCollection.stream()
                .collect(Collectors.groupingBy(String::valueOf))
                .entrySet().stream()
                .filter(e -> e.getValue().size() > 1)
                .flatMap(e -> e.getValue().stream())
                .collect(Collectors.toList());
    }
}
