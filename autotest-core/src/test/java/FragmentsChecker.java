import io.cucumber.core.gherkin.Feature;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import com.openmonet.utils.CustomAssert;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;

import static com.openmonet.aspects.FragmentsAspect.replaceSteps;
import static com.openmonet.utils.ErrorMessage.*;
import static com.openmonet.utils.FeatureUtil.loadFeature;
import static com.openmonet.utils.FileUtil.getParentPath;
import static com.openmonet.utils.RegexUtil.getMatch;

public class FragmentsChecker {

    @DataProvider
    public Object[] modules() {
        return new Object[]{"rest-plugin"};
    }

    @DataProvider
    public Object[][] negativeData() {
        return new Object[][]{
                {"feature-test", "negative"}
        };
    }

    @DataProvider
    public Object[][] positiveData() {
        return new Object[][]{
                {"feature-test", "positive"}
        };
    }

    /**
     * тут используется SoftAssert чтобы проверить все фичи по модулям
     * проверка фич на корректность
     *
     * @throws IOException
     * @throws IllegalAccessException
     */
    @Test(groups = {"fragments"}, dataProvider = "modules")
    public void checkFeatures(String module) throws IOException, IllegalAccessException {
        CustomAssert softAssert = new CustomAssert();
        List<Feature> features = loadFeature(getParentPath(module).toString(), softAssert, true);
        replaceSteps(features, softAssert);
        softAssert.assertAll("Найдены следующие ошибки: \n");
    }

    /**
     * проверка негативного кейса что все ошибки будут выброшены
     *
     * @param module      наименование директории
     * @param packageName наименование директории
     * @throws IOException
     * @throws IllegalAccessException
     */
    @Test(groups = {"fragments"}, dataProvider = "negativeData")
    public void checkNegativeFeatures(String module, String packageName) throws IOException, IllegalAccessException {
        CustomAssert softAssert = new CustomAssert();
        List<Feature> features = loadFeature(getParentPath(module, packageName).toString(), softAssert, true);
        replaceSteps(features, softAssert);
        HashSet<String> actualAssert = softAssert.getErrorMessages("");
        // далее идет сравнение на матчинг сообщения двух коллекций (одна коллекция с ошибками, вторая коллекция с ожидаемыми ошибками)
        String separator = System.getProperty("file.separator");
        HashSet<String> expectedAsserts = new HashSet<>();
        expectedAsserts.add("По пути 'test.test' не найдено значение в файле 'existFile'.");
        expectedAsserts.add(String.format("Количество значений в таблице не равно двум. Актуальное значение: \\d+. Путь '%s.+%sfeature-test%snegative%stestFeature.feature'",
                separator, separator, separator, separator, separator));
        expectedAsserts.add("Нет такого фрагмента с названием - 'не существующий фрагмент'.");
        expectedAsserts.add("Следующие переменные не были переданы в динамический фрагмент из сценария 'негативный сценарий' для шага 'ввести в поле \"тест\" значение \"<test_field>\"': test_field");
        expectedAsserts.add(String.format("Не найден файл 'notExistFile.json' в директории '%s.+%sdata'",
                separator, separator, separator));
        StringBuilder errorsNotFound = new StringBuilder();
        StringBuilder expectedErrors = new StringBuilder();
        StringBuilder actualErrors = new StringBuilder();
        expectedAsserts.stream().forEach(p -> expectedErrors.append(p).append(","));
        actualAssert.stream().forEach(p -> actualErrors.append(p).append(","));
        expectedAsserts.forEach(expected -> {
            boolean found = actualAssert.stream().anyMatch(actual -> getMatch(actual, expected));
            if (!found) {
                errorsNotFound.append(expected).append(",");
            }
        });
        if (errorsNotFound.length() != 0) {
            Assert.fail(getErrorMessage(ERRORS_NOT_FOUND, errorsNotFound.toString(), expectedErrors.toString(), actualErrors.toString()));
        }
        if (expectedAsserts.size() != actualAssert.size()) {
            Assert.fail(getErrorMessage(SOMETHING_WRONG, expectedErrors.toString(), actualErrors.toString()));
        }
    }

    /**
     * проверка позитивного кейса
     *
     * @param module      наименование директории
     * @param packageName наименование директории
     * @throws IOException
     * @throws IllegalAccessException
     */
    @Test(groups = {"fragments"}, dataProvider = "positiveData")
    public void checkPositiveFeatures(String module, String packageName) throws IOException, IllegalAccessException {
        CustomAssert softAssert = new CustomAssert();
        List<Feature> features = loadFeature(getParentPath(module, packageName).toString(), softAssert, true);
        replaceSteps(features, softAssert);
        HashSet<String> asserts = softAssert.getErrorMessages("");
        Assert.assertNull(asserts);
    }
}
