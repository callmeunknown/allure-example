package com.openmonet.data;

import io.cucumber.core.gherkin.Pickle;
import io.qameta.allure.Allure;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.bson.json.JsonParseException;
import org.testng.asserts.Assertion;
import ru.sbtqa.tag.datajack.TestDataObject;
import ru.sbtqa.tag.datajack.adaptors.json.JsonDataObjectAdaptor;
import ru.sbtqa.tag.datajack.exceptions.DataException;

import java.nio.file.FileSystemNotFoundException;

import static com.openmonet.corecommonstep.fragment.GherkinSerializer.DATA_TAG;
import static com.openmonet.utils.ErrorMessage.VALUE_NOT_FOUND_BY_PATH;
import static com.openmonet.utils.ErrorMessage.getErrorMessage;
import static com.openmonet.utils.FileUtil.getParentPath;
import static com.openmonet.utils.FileUtil.searchFileInDirectory;
import static com.openmonet.utils.RegexUtil.getMatch;
import static com.openmonet.utils.RegexUtil.getMatchValueByGroupNumber;

public class DataReplacer {

    private static final String REGEX_VAR = "\\$([\\w\\d]*)\\{([\\w\\d\\.]*)\\}";

    /**
     * замена переменных вида '$collectionName{json.path}' или '${json.path}'
     * на значения из json коллекции
     *
     * @param value   текст шага/таблицы/докстринг
     * @param dataTag тег коллекции @data
     * @return текст с замененнными значениями
     */
    public static String replaceDataInStep(String value, String dataTag, Assertion assertion, Pickle scenario) {
        while (getMatch(value, REGEX_VAR)) {
            String collection = getMatchValueByGroupNumber(value, REGEX_VAR, 1);
            boolean hasCollection = true;
            if ((collection == null || collection.isEmpty()) && dataTag != null && !dataTag.isEmpty()) {
                collection = dataTag.replace(DATA_TAG, "");
                hasCollection = false;
            }
            String path = getMatchValueByGroupNumber(value, REGEX_VAR, 2);
            StringBuilder pathBuilder = getParentPath("data");
            if (collection.isEmpty()) {
                assertion.fail(getErrorMessage("Укажите название коллекции для json файла через тег или для каждой переменной '%s'", scenario.getUri().getPath()));
                break;
            }
            String fileSource = collection.concat(".json");
            // сделан поиск файла в директории, без полного пути директории не находит файл
            String pathDir;
            try {
                pathDir = searchFileInDirectory(pathBuilder.toString(), fileSource)
                        .getAbsolutePath().replace(fileSource, "");
            } catch (FileSystemNotFoundException e) {
                assertion.fail(e.getMessage());
                break;
            }
            JsonDataObjectAdaptor adaptor;
            try {
                adaptor = new JsonDataObjectAdaptor(pathDir, collection);
            } catch (DataException e) {
                e.printStackTrace();
                Allure.addAttachment(e.getMessage(), ExceptionUtils.getStackTrace(e));
                assertion.fail(e.getMessage());
                break;
            } catch (JsonParseException jsonParseException) {
                assertion.fail("\nНевалидный json, проверьте файл:\n" + pathDir + collection + ".json\n");
                break;
            }
            try {
                TestDataObject testDataObject = adaptor.get(path);
                String jsonValue;
                String key = path.substring(path.lastIndexOf(".") + 1);
                if (testDataObject.getKeySet().contains(key)) {
                    jsonValue = testDataObject.getValue();
                } else {
                    jsonValue = testDataObject.toString();
                }
                if (hasCollection) {
                    value = value.replace("$" + collection + "{" + path + "}", jsonValue);
                } else {
                    value = value.replace("${" + path + "}", jsonValue);
                }
            } catch (DataException e) {
                System.out.println("Сценарий:" + scenario.getName());
                System.out.println("Шаг: " + value);
                assertion.fail(getErrorMessage(VALUE_NOT_FOUND_BY_PATH, path, collection));
                break;
            }

        }
        return value;
    }
}
