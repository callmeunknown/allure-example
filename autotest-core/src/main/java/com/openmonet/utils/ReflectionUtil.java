package com.openmonet.utils;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.reflections.Reflections;
import com.openmonet.annotations.Action;
import com.openmonet.pagecore.PageCore;
import com.openmonet.utils.attachments.ScreenShooterUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class ReflectionUtil {

    /**
     * Получение списка классов по аннотации
     */
    public static Set<Class<?>> getPagesAnnotatedWith(String packageName,Class<? extends Annotation> annotation) {
        return new Reflections(packageName).getTypesAnnotatedWith(annotation);
    }

    /**
     * Получение поля класса с помощью механизма рефлексии
     */
    public static Object extractFieldValue(Field field, Object owner) {
        field.setAccessible(true);
        try {
            return field.get(owner);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } finally {
            field.setAccessible(false);
        }
    }

    /**
     * Выполняет метод по значение аннотации Action
     * @param context   -   класс страницы
     * @param title -   значение аннотации Action
     * @param param -   параметры действия (метода)
     */
    public static void executeMethodByTitle(Object context, String title, Object... param) {
        List<Method> methods = getDeclaredMethods(context.getClass());
        String pageName = context.getClass().isInstance(PageCore.class)? ((PageCore)context).title(): context.getClass().getSimpleName();
        for (Method method : methods) {
            if (isRequiredAction(method, title, param)) {
                try {
                    method.setAccessible(true);
                    method.invoke(context, param);
                    return;
                } catch (IllegalAccessException | InvocationTargetException | AssertionError e) {
                    ScreenShooterUtil.takeScreenShoot();
                    throw new AssertionError(String.format("Ошибка при выполнении действия \"%s\" для страницы \"%s\"\nСтектрейс: %s", title, pageName, ExceptionUtils.getRootCause(e)));
                }
            }
        }
        throw new AssertionError(String.format("Нет действия \"%s\" для страницы \"%s\" с параметрами: %s", title, pageName, Arrays.stream(param).map(s -> s.getClass().toString()).collect(Collectors.joining(", "))));
    }

    /**
     * Проверяет метод, является ли он необходимым действием
     * проверяет название действия и значение аннотации Action у метода,
     * затем проверяет количество параметров у метода и их типы
     * @param method        -   метод класса страницы
     * @param title         -   названия действия
     * @param actionParameters  -   параметры действия(метода)
     * @return      -   булевое значение
     */
    private static boolean isRequiredAction(Method method, String title, Object... actionParameters) {
        Parameter[] methodParameters = method.getParameters();
        String actionName = "";
        try {
            actionName = method
                    .getAnnotation(Action.class)
                    .value();
        } catch (NullPointerException npe) {
            //у метода нет аннотации @Action
        }
        if (title.equals(actionName)) {
            if (methodParameters.length != actionParameters.length) {
                return false;
            } else {
                for (int index = 0; index < methodParameters.length; index++) {
                    if (actionParameters[index] != null && !methodParameters[index].getType().isAssignableFrom(actionParameters[index].getClass())) {
                        return false;
                    }
                }
                return true;
            }
        } else {
            return false;
        }
    }

    public static List<Method> getDeclaredMethods(Class clazz) {
        List<Method> methods = new ArrayList<>(Arrays.asList(clazz.getDeclaredMethods()));

        Class supp = clazz.getSuperclass();

        while (supp != Object.class) {
            methods.addAll(Arrays.asList(supp.getDeclaredMethods()));
            supp = supp.getSuperclass();
        }

        return methods;
    }
}