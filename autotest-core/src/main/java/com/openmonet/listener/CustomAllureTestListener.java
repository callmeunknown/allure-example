package com.openmonet.listener;

import io.qameta.allure.listener.TestLifecycleListener;
import io.qameta.allure.model.Status;
import io.qameta.allure.model.StepResult;
import io.qameta.allure.model.TestResult;

import java.util.ArrayList;
import java.util.List;

/**
 * "Слушатель" результатов теста
 * Нужен для переопределения шагов в отчете, конкретно для группировки шагов фрагментов в спойлер
 */
public class CustomAllureTestListener implements TestLifecycleListener {

    /**
     * Ищет шаги-заглушки ФРАГМЕНТ "название фрагмента" и прячет шаги фрагмента под спойлер
     * @param testResult    -   результаты тестов
     */
    @Override
    public void beforeTestWrite(TestResult testResult) {
        testResult.getSteps().clear();
//        List<StepResult> originSteps = testResult.getSteps();
//        List<StepResult> newSteps = wrapFragment(originSteps);
//        testResult.setSteps(newSteps);
    }

    /**
     * Метод прячет все шаги фрагмента под спойлер
     * @param originSteps   -   оригинальный набор шагов для отчета
     * @return  -   переопределенные шаги отчета
     */
    private List<StepResult> wrapFragment(List<StepResult> originSteps) {
        List<StepResult> newSteps = new ArrayList<>();
        for (int i=0; i < originSteps.size(); i++) {
            StepResult step = originSteps.get(i);
            if (step.getName().contains("FRAGMENT") && i+1 < originSteps.size()) {
                i++;
                long timeStop = step.getStop();
                List<StepResult> subSteps = new ArrayList<>();
                List<Status> statusList = new ArrayList<>();
                //идем дальше по тесту и собираем все шаги фрагмента в отдельный список
                do {
                    StepResult subStep = originSteps.get(i);
                    if (subStep.getName().contains(step.getName())) {
                        timeStop = subStep.getStop();
                        break;
                    } else {
                        subSteps.add(subStep);
                        statusList.add(subStep.getStatus());
                        i++;
                    }
                } while (i < originSteps.size());
                //если есть фрагменты внутри фрагмента
                subSteps = wrapFragment(subSteps);
                //таки все шаги фрагмента уйдут под спойлер (как саб-шаги)
                step.setSteps(subSteps);
                //ставим изменяем время конца шага, чтобы была сумма всех саб-шагов
                step.setStop(timeStop);
                //меняем статус шагу-фрагменту, если статус у одного из шагов не passed или не skipped
                if (statusList.contains(Status.FAILED)) {
                    step.setStatus(Status.FAILED);
                } else if (statusList.contains(Status.BROKEN)) {
                    step.setStatus(Status.BROKEN);
                }
                newSteps.add(step);
            } else {
                newSteps.add(step);
            }
        }
        return newSteps;
    }

}
