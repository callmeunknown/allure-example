package com.openmonet.utils.attachments;

import org.openqa.selenium.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.openmonet.pagecore.Environment;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import static com.openmonet.utils.attachments.AllureUtil.addAttachmentToAllure;

/**
 * Утиль для снятия скриншотов экрана/страницы, отдельных элементов
 */
public class ScreenShooterUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScreenShooterUtil.class);

    /**
     * Выполняет скриншот экрана/страницы и сохраняет как вложение
     */
    public static void takeScreenShoot() {
        takeScreenShoot("Скриншот экрана");
    }

    /**
     * Выполняет скриншот экрана/страницы и сохраняет как вложение с переданным названием
     * @param attachName - название скриншота в отчете
     */
    public static void takeScreenShoot(String attachName) {
        BufferedImage fullImage = takeScreenShootBufferedImage();
        attachByteArray(attachName, fullImage);
    }

    /**
     * Выполняет скриншот элемента и сохраняет как вложение
     */
    public static void takeScreenShootOfElement(WebElement webElement) {
        takeScreenShootOfElement("Скриншот элемента", webElement);
    }

    /**
     * Выполняет скриншот элемента и сохраняет как вложение с переданным названием
     * @param attachName - название скриншота в отчете
     */
    public static void takeScreenShootOfElement(String attachName, WebElement webElement) {
        BufferedImage elementImage = cutElementFromScreen(webElement);
        attachByteArray(attachName, elementImage);
    }

    /**
     * Прикрепляет изображение к allure-отчету
     * @param attachName - название скриншота в отчете
     */
    private static void attachByteArray(String attachName, BufferedImage image) {
        addAttachmentToAllure(attachName, "image/png", ".png", bufferedImageToByteArray(image));
    }

    /**
     * Выполняет скриншот элемента
     * @param webElement    -   веб-элемент/мобильный элемент
     */
    private static BufferedImage cutElementFromScreen(WebElement webElement) {
        try {
            BufferedImage fullImage = takeScreenShootBufferedImage();
            if (fullImage != null) {
                Point point = webElement.getLocation();
                int eleWidth = webElement.getSize().getWidth();
                int eleHeight = webElement.getSize().getHeight();
                return fullImage.getSubimage(point.getX(), point.getY(), eleWidth, eleHeight);
            } else {
                return null;
            }
        } catch (Exception e) {
            LOGGER.error("Не удалось получить скриншот элемента");
            return null;
        }
    }

    /**
     * Преобразует изображение из BufferedImage в массив байтов для аллюра
     * @param image     -   скриншот
     * @return  -   скриншот в виде байтов
     */
    private static byte[] bufferedImageToByteArray(BufferedImage image) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            baos.flush();
            byte[] imageInByte = baos.toByteArray();
            baos.close();
            return imageInByte;
        } catch (IOException ioException) {
            LOGGER.error("Не удалось сохранить скриншот");
            return null;
        }
    }

    /**
     * Получает скриншот в виде BufferedImage
     * @return  -   скиншот
     */
    private static BufferedImage takeScreenShootBufferedImage() {
        try {
            File screenshot = ((TakesScreenshot) Environment.getDriver()).getScreenshotAs(OutputType.FILE);
            return ImageIO.read(screenshot);
        } catch (IOException ioException){
            LOGGER.error("Не удалось получить скриншот экрана");
            return null;
        }
    }

    /**
     * Прикрепляет видео к allure-отчету
     * @param video -   виде в виде массива байт
     * @param videoName -   название аттача
     */
    public static void attachVideoShotToAllure(byte[] video, String videoName) {
        AllureUtil.addAttachmentToAllure(videoName, "video/mp4", ".mp4", video);
    }
}
