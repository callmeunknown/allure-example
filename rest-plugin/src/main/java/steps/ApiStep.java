package steps;

import com.bettercloud.vault.json.Json;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.qameta.allure.Allure;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import com.openmonet.api.ApiRequest;
import com.openmonet.api.BodyEditor;
import com.openmonet.api.dataParsers.CompareParser;
import com.openmonet.api.dataParsers.RemoveJsonKeyParser;
import com.openmonet.api.dataParsers.RequestParser;
import com.openmonet.context.ContextHolder;
import com.openmonet.utils.DataUtils;
import com.openmonet.utils.WaitUtil;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.openmonet.context.ContextHolder.replaceVarsIfPresent;
import static com.openmonet.utils.CompareUtil.compare;
import static com.openmonet.utils.CompareUtil.compareType;
import static com.openmonet.utils.DataUtils.generateValueByMask;
import static com.openmonet.utils.ErrorMessage.*;
import static com.openmonet.utils.JsonUtil.getFieldFromJson;
import static com.openmonet.utils.RegexUtil.getMatchValueByGroupNumber;

public class ApiStep {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiStep.class);
    private static final String SOUT_TEMPLATE_SAVED_VAR = "сохраненная переменная: '%s' -> '%s';\n";
    private ApiRequest apiRequest;

    private static String getSavedVarSout(String key, String value) {
        return String.format(SOUT_TEMPLATE_SAVED_VAR, key, value);
    }

    /**
     * создаем контекстные переменные в {@link ContextHolder}
     *
     * @param dataTable формат: первый столбец наименование переменной,
     *                  второй столбец значение
     */
    @When("^create context variables$")
    public void setContextVariable(Map<String, String> dataTable) {
        StringBuilder builder = new StringBuilder();
        builder.append("\n");
        dataTable.forEach((key, value) -> {
            String replacedValue = replaceVarsIfPresent(value);
            builder.append(getSavedVarSout(key, replacedValue));
            Allure.addAttachment(key, replacedValue);
            ContextHolder.put(key, replacedValue);
        });
        LOGGER.info(builder.toString());
    }

    private void extractVariablesFromJson(String body, Map<String, String> dataTable) {
        String json = Json.parse(body).toString();
        StringBuilder builder = new StringBuilder();
        builder.append("\n");
        dataTable
                .forEach((key, value) -> {
                    builder.append(getSavedVarSout(key, extractVariablesFromJson(key, value, json)));
                });
        LOGGER.info(builder.toString());
    }

    private String extractVariablesFromJson(String key, String value, String json) {
        String jsonPath = replaceVarsIfPresent(value);
        String extractedValue = getFieldFromJson(json, jsonPath);
        ContextHolder.put(key, extractedValue);
        Allure.addAttachment(key, extractedValue);
        return extractedValue;
    }

    /**
     * извлекаем необходимые данные из тела ответа (json) по json path
     *
     * @param dataTable формат: первый столбец наименование переменной,
     *                  второй столбец json path по которому будет найдено значение в теле запроса
     */
    @And("^extract data from the response body into the context variable$")
    public void extractVariablesFromResponseBody(Map<String, String> dataTable) {
        extractVariablesFromJson(apiRequest.getResponse().getBody().asString(), dataTable);
    }

    @And("^check that there are no values in the response body")
    public void checkThatFieldAbsent(Map<String, String> dataTable) {
        String json = Json.parse(apiRequest.getResponse().getBody().asString()).toString();
        dataTable
                .forEach((key, value) -> {
                    String result = null;
                    try {
                        result = extractVariablesFromJson(key, value, json);
                    } catch (AssertionError error) {
                        if (!error.getMessage().startsWith("\nНе найдено значение по jsonpath.")) {
                            throw error;
                        }
                    }
                    if (result != null) {
                        throw new AssertionError(String.format("Значение в теле ответа присутствует!\nТело: %s\nJsonpath: %s\n Найденное значение: %s", json, replaceVarsIfPresent(value), result));
                    }
                });
    }

    /**
     * извлекаем необходимые данные из тела запроса (json) по json path
     *
     * @param dataTable формат: первый столбец наименование переменной,
     *                  второй столбец json path по которому будет найдено значение в теле запроса
     */
    @And("^extract data from the request body into a context variable$")
    public void extractVariablesFromRequestBody(Map<String, String> dataTable) {
        if (apiRequest.getRequestBody() != null) {
            extractVariablesFromJson(apiRequest.getRequestBody(), dataTable);
        } else {
            throw new AssertionError("Нет тела запроса для извлечения переменных.\nВозможно, последним был вызван GET запрос");
        }
    }

    @And("extract data from the variable {string}")
    public void extractVariables(String var, Map<String, String> dataTable) {
        String value = ContextHolder.getValue(var);
        if (value == null) {
            throw new AssertionError(String.format("Не объявлена переменная \"%s\"!", var));
        }
        extractVariablesFromJson(value, dataTable);
    }

    /**
     * извлекаем данные из тела ответа (не json тип) по регулярному выражению
     *
     * @param regex        регулярное выражение
     * @param groupNumber  номер группы в регулярке
     * @param variableName наименование переменной куда сохранится значение
     */
    @And("^extract data from the response body using a regular expression \"(.+)\" group number \"(\\d+)\" into the context variable \"(.+)\"$")
    public void extractVariableByRegex(String regex, int groupNumber, String variableName) {
        String response = apiRequest.getResponse().getBody().asString();
        regex = replaceVarsIfPresent(regex);
        String findValue = getMatchValueByGroupNumber(response, regex, groupNumber).trim();
        Assert.assertNotNull(findValue, getErrorMessage(NOT_FOUND_VALUE_BY_REGEX, regex, groupNumber));
        Allure.addAttachment("response body", "application/text", response, ".txt");
        ContextHolder.put(variableName, findValue);
    }

    /**
     * извлекаем данные из переменной по регулярному выражению
     *
     * @param varName      название контекстной переменной
     * @param regex        регулярное выражение
     * @param groupNumber  номер группы в регулярке
     * @param variableName наименование переменной куда сохранится значение
     */
    @And("^extract data from \"(.+)\" by regular expression \"(.+)\" group number \"(\\d+)\" into the context variable \"(.+)\"$")
    public void extractVariableByRegexFromVar(String varName, String regex, int groupNumber, String variableName) {
        String text = replaceVarsIfPresent(varName);
        regex = replaceVarsIfPresent(regex);
        String findValue = getMatchValueByGroupNumber(text, regex, groupNumber);
        Assert.assertNotNull(findValue, getErrorMessage(NOT_FOUND_VALUE_BY_REGEX, regex, groupNumber));
        ContextHolder.put(variableName, findValue.trim());
    }

    /**
     * создание значения по маске
     *
     * @param dataTable формат: первый столбец наименование переменной,
     *                  второй столбец это значение в виде маски {@link DataUtils#generateValueByMask(String)}
     */
    @When("^generate variable by mask$")
    public void generateVariable(Map<String, String> dataTable) {
        StringBuilder builder = new StringBuilder();
        builder.append("\n");
        dataTable.forEach((key, value) -> {
            value = generateValueByMask(replaceVarsIfPresent(value));
            builder.append(getSavedVarSout(key, value));
            Allure.addAttachment(key, value);
            ContextHolder.put(key, value);
        });
        LOGGER.info(builder.toString());
    }

    /**
     * проверка ожидаемого статус кода от выполненного запроса
     *
     * @param expectedCode ожидаемый статус код
     */
    @Then("^expected status code \"(\\d+)\"$")
    public void expectCode(int expectedCode) {
        int actualCode = apiRequest.getResponse().statusCode();
        Assert.assertEquals(actualCode, expectedCode, getErrorMessage(WRONG_STATUS, expectedCode, actualCode));
    }

    /**
     * сравнение контекстных и не контекстных переменных
     *
     * @param comparedValues формат: первый столбец может быть как значение, так и контекстная переменная указанная в формате %{var}%;
     *                       <br/>второй столбец оператор сравнения - для строк (равно, не равно, содержит, не содержит),
     *                       для чисел (равно, не равно, больше, меньше);
     *                       <br/>третий столбец может быть как значение, так и контекстная переменная указанная в формате %{var}%
     */
    @Then("^compare values$")
    public void checkVariables(List<CompareParser> comparedValues) {
        comparedValues.forEach(line -> {
            String variableName = replaceVarsIfPresent(line.getVariableName());
            String actualValue = replaceVarsIfPresent(line.getActualValue());
            String expectedValue = replaceVarsIfPresent(line.getExpectedValue());
            String comparisonMark = line.getComparisonMark();
            boolean compare = compare(actualValue, expectedValue, comparisonMark);
            Assert.assertTrue(compare, getErrorMessage(QUITE_NOT_EQUALS, variableName, expectedValue, actualValue, line.getComparisonMark()));
            LOGGER.info("The current value - '{}', Expected value - '{}', Comparison sign - '{}'", actualValue, expectedValue, comparisonMark);
        });
    }

    @Then("^compare the variable type")
    public void checkType(List<CompareParser> comparedValues) {
        comparedValues.forEach(line -> {
            String variableName = replaceVarsIfPresent(line.getVariableName());
            String actualValue = replaceVarsIfPresent(line.getActualValue());
            String expectedType = replaceVarsIfPresent(line.getExpectedValue());
            String operator = line.getComparisonMark();
            boolean compare = compareType(actualValue, expectedType, operator);
            Assert.assertTrue(compare, getErrorMessage(QUITE_NOT_EQUALS, variableName, expectedType, actualValue, line.getComparisonMark()));
            LOGGER.info("The current value - '{}', Expected variable type - '{}', Comparison sign - '{}'", actualValue, expectedType, operator);
        });
    }

    /**
     * проверка значения конкретного хедера ответа
     *
     * @param headerName          наименование хедера
     * @param expectedHeaderValue ожидаемое значение
     */
    @Then("^check header \"(.+)\" has a value \"(.+)\"$")
    public void checkHeader(String headerName, String expectedHeaderValue) {
        Headers headers = apiRequest.getResponse().headers();
        headerName = replaceVarsIfPresent(headerName);
        try {
            String actualHeaderValue = headers.get(headerName).getValue();
            Assert.assertEquals(
                    actualHeaderValue,
                    expectedHeaderValue,
                    getErrorMessage(ACTUAL_VALUE_NOT_EQUAL_EXPECTED, actualHeaderValue, expectedHeaderValue));
        } catch (NullPointerException e) {
            Assert.fail(getErrorMessage(HEADER_NOT_FOUND, headerName, headers.asList().toString()));
        }
    }

    //далее шаги с помощью которых строим апи запрос


    @When("create the request")
    public void createRequest(RequestParser requestData) {
        apiRequest = new ApiRequest(requestData);
    }


    @When("create and send the request")
    public void createAndSendRequest(RequestParser requestData) {
        createRequest(requestData);
        send();
    }


    /**
     * добавление хедеров к запросу
     *
     * @param table формат: первый столбец наименование хедера,
     *              второй столбец значение хедера или контекстная переменная
     */
    @And("^add a header to the request$")
    public void setHeadersToRequest(DataTable table) {
        table.asLists().forEach(it -> apiRequest.setHeader(it.get(0), it.get(1)));
    }

    @When("add query parameters")
    public void addQueryParams(DataTable table) {
        Map<String, String> query = new HashMap<>();
        table.asLists().forEach(it -> query.put(it.get(0), it.get(1)));
        apiRequest.setQuery(query);
    }

    /**
     * выполнение запроса
     */
    @And("^send the request$")
    public void send() {
        apiRequest.sendRequest();
    }

    @When("send a request every {int} seconds until the status is {int}")
    public void sendRequestUntilResponseStatus(int time, int expectStatus) {
        sendRequestUntilResponseStatusWithTimeout(time, expectStatus, null);
    }

    @When("send a request every {int} seconds until the status is {int} until the timeout expires {int} seconds")
    public void sendRequestUntilResponseStatusWithTimeout(int time, int expectStatus, Integer timeout) {
        final int maxSeconds = timeout != null? timeout: 180;
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Response response;
        int actualStatus;
        do {
            response = apiRequest.sendRequest().getResponse();
            actualStatus = response.statusCode();

            if (actualStatus == expectStatus) {
                break;
            } else {
                WaitUtil.waitSeconds(time);
            }
        } while (stopWatch.getTime(TimeUnit.SECONDS) <= maxSeconds);
        Assert.assertEquals(actualStatus, expectStatus,
                String.format("По прошествии %s секунд, статус-код %s не вернулся от сервера",
                        stopWatch.getTime(TimeUnit.SECONDS), expectStatus));
        stopWatch.stop();
    }


    /**
     * Получение тела запроса без заданного ключа или его значения 'value'
     *
     * @param removerJsonKeyDatas - данные из таблицы fileWithBody, keyToRemove или valueOfKeyToModify, variableWithBodyWithoutKey
     * @throws IllegalArgumentException - выбрасываемый тип ошибки
     */
    @And("modify the request body {string} and write it to {string}")
    public void createBodyWithoutKeys(String fileWithBody, String variableWithBodyWithoutKeys, List<RemoveJsonKeyParser> removerJsonKeyDatas) {
        BodyEditor bodyEditor = new BodyEditor(fileWithBody, removerJsonKeyDatas);
        String bodyWithoutKey = bodyEditor.removeJsonKeyFromData();
        ContextHolder.put(variableWithBodyWithoutKeys, bodyWithoutKey);
        LOGGER.info("Modified request body: '{}'", bodyWithoutKey);
    }


    @When("get the redirect link")
    public void takeTheLinkFromRedirectUrl(DataTable dataTable) {
        dataTable.asLists().forEach(line -> {
            String originalUrl = replaceVarsIfPresent(line.get(1));
            String redirectUrl = "";
            try {
                HttpURLConnection con = (HttpURLConnection) (new URL(originalUrl).openConnection());
                con.setInstanceFollowRedirects(false);
                con.connect();
                int responseCode = con.getResponseCode();
                if (responseCode == 302 || responseCode == 301) {
                    redirectUrl = con.getHeaderField("Location");
                    ContextHolder.put(line.get(0), redirectUrl);
                    LOGGER.info("redirect-ссылка: {}", redirectUrl);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (redirectUrl.isEmpty()) {
                throw new AssertionError("Failed to retrieve the redirect link for the URL: " + originalUrl);
            }
        });
    }
}