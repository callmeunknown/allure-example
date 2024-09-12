package com.openmonet.listener;

import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.logevents.LogEvent;
import com.codeborne.selenide.logevents.LogEventListener;
import io.qameta.allure.Allure;
import io.qameta.allure.AllureLifecycle;
import io.qameta.allure.model.Status;
import io.qameta.allure.model.StatusDetails;
import io.qameta.allure.model.StepResult;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriverException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.openmonet.utils.ErrorTranslator;

import java.util.Optional;
import java.util.UUID;

import static io.qameta.allure.util.ResultsUtils.getStatus;
import static io.qameta.allure.util.ResultsUtils.getStatusDetails;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Класс листенер Selenide
 * реализует фичу Allure, lifecycle, для замены сообщения об ошибке в отчете allure
 */
@SuppressWarnings("unused")
public class AllureLifecycleListener implements LogEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(AllureLifecycleListener.class);

    private boolean saveScreenshots = true;
    private boolean savePageHtml = false;
    private boolean includeSelenideLocatorsSteps = true;
    private final AllureLifecycle lifecycle;

    public AllureLifecycleListener() {
        this(Allure.getLifecycle());
    }

    public AllureLifecycleListener(final AllureLifecycle lifecycle) {
        this.lifecycle = lifecycle;
    }

    /**
     * конструктор скриншотов
     */
    public AllureLifecycleListener screenshots(final boolean saveScreenshots) {
        this.saveScreenshots = saveScreenshots;
        return this;
    }

    /**
     * конструктор пейджсурсы (по умолчанию выкл.)
     */
    public AllureLifecycleListener savePageSource(final boolean savePageHtml) {
        this.savePageHtml = savePageHtml;
        return this;
    }

    /**
     * получение скриншотов
     */
    private static Optional<byte[]> getScreenshotBytes() {
        try {
            return WebDriverRunner.hasWebDriverStarted()
                    ? Optional.of(((TakesScreenshot) WebDriverRunner.getWebDriver()).getScreenshotAs(OutputType.BYTES))
                    : Optional.empty();
        } catch (WebDriverException e) {
            LOGGER.warn("Could not get screen shot", e);
            return Optional.empty();
        }
    }

    /**
     * получение пейджи (по умолчанию выкл)
     */
    private static Optional<byte[]> getPageSourceBytes() {
        try {
            return WebDriverRunner.hasWebDriverStarted()
                    ? Optional.of(WebDriverRunner.getWebDriver().getPageSource().getBytes(UTF_8))
                    : Optional.empty();
        } catch (WebDriverException e) {
            LOGGER.warn("Could not get page source", e);
            return Optional.empty();
        }
    }

    /**
     * до события
     * создаем уникальный UUID для степов
     * стартуем степы
     */
    @Override
    public void beforeEvent(final LogEvent event) {
//        lifecycle.getCurrentTestCaseOrStep().ifPresent(parentUuid -> {
//            final String uuid = UUID.randomUUID().toString();
//            lifecycle.startStep(parentUuid, uuid, new StepResult().setName(event.toString()));
//        });

    }

    /**
     * после события
     * проверяем что тест FAIL
     * при необходимости сохраняем сккриншоты и html пейджу
     * создаем новую Throwable err через класс ErrorTranslator
     * назначаем сообщению об ошибке степа нашу ошибку
     */
    @Override
    public void afterEvent(final LogEvent event) {
        if (event.getStatus().equals(LogEvent.EventStatus.FAIL)) {
            lifecycle.getCurrentTestCaseOrStep().ifPresent(parentUuid -> {
                if (saveScreenshots) {
                    getScreenshotBytes()
                            .ifPresent(bytes -> lifecycle.addAttachment("Screenshot", "image/png", "png", bytes));
                }
                if (savePageHtml) {
                    getPageSourceBytes()
                            .ifPresent(bytes -> lifecycle.addAttachment("Page source", "text/html", "html", bytes));
                }
            });


        /*
        Throwable err = new ErrorTranslator(ErrorTranslator.errorMessageValue(event.getError()), event.getError());
        lifecycle.updateStep(stepResult -> {
            stepResult.setStatus(getStatus(event.getError()).orElse(Status.FAILED));
            stepResult.setStatusDetails(getStatusDetails(err).orElse(new StatusDetails()));
        });
        */
        }

    }
}

