package com.openmonet.utils.attachments;

import io.qameta.allure.Allure;
import io.qameta.allure.AllureLifecycle;

public class AllureUtil {

    /**
     * Добавляет вложение в отчет allure
     * @param name  -   название
     * @param type  -   тип
     * @param extension -   расширение
     * @param body  -   массив байтов (само вложение)
     */
    public static void addAttachmentToAllure(String name, String type, String extension, byte[] body) {
        if (body != null) {
            AllureLifecycle lifecycle = Allure.getLifecycle();
            lifecycle.addAttachment(name, type, extension, body);
        }
    }

    /**
     * Добавляет простое текстовое вложения в отчет
     * @param name  -   название вложения
     * @param content   -   содержание
     */
    public static void addAttachmentToAllure(String name, String content) {
        Allure.addAttachment(name, content);
    }

    /**
     * Добавляет в отчет вложение со скриншотом с заданным названием
     * @param attachName    -   название вложение
     */
    public static void attachScreenShoot(String attachName) {
        ScreenShooterUtil.takeScreenShoot(attachName);
    }

    /**
     * Добавляет в отчет вложение со скриншотом (с дефолтным названием)
     */
    public static void attachScreenShoot() {
        ScreenShooterUtil.takeScreenShoot();
    }
}

