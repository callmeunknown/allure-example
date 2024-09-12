package com.openmonet.corecommonstep;

import com.openmonet.corecommonstep.gherkintables.CompareModel;
import com.openmonet.corecommonstep.gherkintables.MathOperationModel;
import com.openmonet.utils.CompareUtil;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.openmonet.context.ContextHolder;
import com.openmonet.corecommonstep.fragment.FragmentReplacer;
import com.openmonet.utils.DateUtils;
import com.openmonet.utils.WaitUtil;

import java.util.List;

import static com.openmonet.context.ContextHolder.replaceVarsIfPresent;
import static com.openmonet.utils.ErrorMessage.FRAGMENT_ERROR;
import static com.openmonet.utils.ErrorMessage.getErrorMessage;

public class CommonSteps {
    private final Logger LOGGER = LoggerFactory.getLogger(CommonSteps.class);

    @And(FragmentReplacer.REGEX_FRAGMENT)
    public void userInsertsFragment(String fragmentName) {
        LOGGER.info("insert fragment '{}'", fragmentName);
        throw new IllegalStateException(getErrorMessage(FRAGMENT_ERROR, fragmentName));
    }

    @And("^wait (\\d+) seс$")
    public void waitSeconds(int timeout) {
        LOGGER.info("пауза '{}' секунд", timeout);
        WaitUtil.waitSeconds(timeout);
    }

    @And("^DEBUG STEP № (\\d+)$")
    public void debugStep(int stepNumber) {
        //ниже поставить брейкпоинт
        System.out.println("step № " + stepNumber);
    }

    /**
     * Шаг-заглушка для отчета аллюра и группировки шагов фрагмента под спойлер
     * Если будет использоваться в тесте - вылетит ошибка что надо использовать другой шаг
     * FragmentReplacer.addGraphNode() - строка if (getMatch(step.getText(), REGEX_FRAGMENT_SPOILER))
     * @param fragmentName
     */
    @And("^FRAGMENT \"(.+)\"$")
    public void fragment(String fragmentName) {
    }

    @And("^PRINT TO CONSOLE \"(.+)\"$")
    public void debugPrint(String text) {
        System.out.println(replaceVarsIfPresent(text));
    }

    /**
     * Генерирует дату по заданному паттерну
     *
     * @param table <b>| ключ | значение | паттерн |</b>
     *              <br> | дата | сегодня |
     *              <br> | дата | плюс 3 дня |
     *              <br> | дата | плюс 1 месяц |
     *              <br> | дата | плюс 1 год |
     *              <br> | дата | минус 1 месяц |
     *              <br> Если нужен специфичный формат то можно указать третий столбец
     *              <br> | дата | минус 1 месяц | dd/MM/yyyy |
     *              <br> Получить сгенерированную дату можно из контектс <b>%{date}%</b>
     */
    @And("generate date")
    public void dateGenerator(DataTable table) {
        table.asLists().forEach(e -> {
            String date = DateUtils.getDateByRussianDateFormat(e.get(1), table.row(0).size() == 2 ? null : e.get(2));
            ContextHolder.put(e.get(0), date);
            LOGGER.info("Date generated - '{}'", date);
        });
    }

    @And("perform a math operation and save result to the variable")
    public void mathOperationAndSaveVariable(List<MathOperationModel> mathOperationModels) {
        for (MathOperationModel mathOperation : mathOperationModels) {
            CompareUtil.performMathOperationsAndSaveVariable(mathOperation);
        }
    }
}
