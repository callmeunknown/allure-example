package com.openmonet.api;

import com.google.gson.Gson;
import io.qameta.allure.Allure;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.http.Method;
import io.restassured.response.Response;
import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;
import com.openmonet.api.dataParsers.RequestParser;
import com.openmonet.api.interceptors.SwaggerCoverageRestAssuredInterceptor;
import com.openmonet.listener.RestAssuredCustomLogger;
import com.openmonet.utils.ErrorMessage;
import com.openmonet.utils.FileUtil;
import com.openmonet.utils.JsonUtil;
import com.openmonet.utils.UrlUtil;

import java.io.File;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static com.openmonet.context.ContextHolder.replaceVarsIfPresent;
import static com.openmonet.utils.FileUtil.getParentPath;
import static com.openmonet.utils.UrlUtil.PATTERN_API;
import static com.openmonet.utils.UrlUtil.formatUrl;

public class ApiRequest {

    private Method method;
    private String multipart;
    private String formUrlEncoded;
    private String requestBody;
    private String path;
    private String fullUrl;
    private String prefix;
    private String headers;
    private Response response;

    private RequestSpecBuilder requestBuilder;

    public Response getResponse() {
        return response;
    }

    public ApiRequest(RequestParser requestParser) {
        this.requestBuilder = new RequestSpecBuilder();
        this.requestBuilder.setUrlEncodingEnabled(false);
        this.path = requestParser.getPath();
        this.fullUrl = requestParser.getFullUrl();
        this.method = Method.valueOf(requestParser.getMethod());
        this.prefix = requestParser.getPrefix();
        this.requestBody = requestParser.getRequestBody();
        this.multipart = requestParser.getMultipart();
        this.formUrlEncoded = requestParser.getFormUrlEncoded();
        this.headers = requestParser.getHeaders();
        setUrlAndPath();
        SwaggerCoverageRestAssuredInterceptor.
                generateSwaggerFiles(this.requestBuilder,
                        FileUtil.getParentPath("rest-plugin", "target", "swagger").toString());
        setBody();
        setDefaultHeaders();
        setJsonHeaders();
        addLoggingListener();
    }

    public String getRequestBody() {
        return requestBody;
    }

    /**
     * Сеттит тело запроса: json-тело, мультипарт-форма или форма с параметрами
     */
    private void setBody() {
        if (multipart != null && requestBody != null || multipart != null && formUrlEncoded != null || formUrlEncoded != null && requestBody != null) {
            throw new IllegalArgumentException("Нельзя одновременно использовать 'body','multipart' и 'form_url_encoded' как тело запроса");
        }
        setJsonBody();
        setMultipart();
        setFormUrlEncoded();
    }

    /**
     * Сеттит для запроса url и путь
     */
    private void setUrlAndPath() {
        if (path != null && fullUrl != null) {
            throw new IllegalArgumentException("Нельзя одновременно использовать 'path' и 'full_url' как url для запроса");
        }
        URI baseUrl;
        if (fullUrl != null) {
            baseUrl = URI.create(replaceVarsIfPresent(fullUrl));
        } else {
            if (path != null && path.startsWith("http")) {
                throw new AssertionError("Полный путь для запроса надо указывать через поле \"full_url\", не \"path\"");
            }
            baseUrl = URI.create(formatUrl(PATTERN_API, replaceVarsIfPresent(path), prefix, null));
        }
        this.requestBuilder.setBaseUri(baseUrl.toString());
        fullUrl = baseUrl.toString();
    }

    /**
     * Конструктор для запросов с телом в контроллерах
     *
     * @param method  тип метода
     * @param fullUrl путь
     * @param requestBody    тело
     */
    public ApiRequest(Method method, String fullUrl, String requestBody) {
        this(new RequestParser(method.name(), fullUrl, requestBody));
    }

    public ApiRequest(Method method, String fullUrl) {
        this(new RequestParser(method.name(), fullUrl));
    }

    /**
     * Сеттит хидер по Ключу и Значению
     */
    public ApiRequest setHeader(String name, String value) {
        requestBuilder.addHeader(name, replaceVarsIfPresent(value));
        return this;
    }

    /**
     * Сеттит query-параметры в запрос
     */
    public void setQuery(Map<String, String> query) {
        query.forEach((k, v) -> {
            requestBuilder.addQueryParam(replaceVarsIfPresent(k), UrlUtil.encode(v));
        });
    }

    /**
     * Отправляет подготовленный запрос
     *
     * @return возвращает инстанс объекта this
     */
    public ApiRequest sendRequest() {
        // потому что некоторые гет запросы работают только через get
        if (method.equals(Method.GET)) {
            response = RestAssured
                    .given()
                    .spec(requestBuilder.build())
                    .get(fullUrl);
        } else {
            response = RestAssured
                    .given()
                    .spec(requestBuilder.build())
                    .request(method).andReturn();
        }

        String responseBody = response.getBody().asString();

        if (requestBody != null) {
            Allure.addAttachment(
                    "Request",
                    "application/json",
                    IOUtils.toInputStream(requestBody, StandardCharsets.UTF_8),
                    ".txt");
        }

        responseBody = JsonUtil.jsonToUtf(responseBody);
        Allure.addAttachment("Response", "application/json", responseBody, ".txt");
        return this;
    }

    /**
     * Проверяет код ответа и возвращает тело ответа как JSONObject по ключам json
     *
     * @param expectedStatusCode -   ожидамеый код ответа
     * @param jsonKeys           -   ключи json (как jsonpath)
     * @return -   JSONObject тела ответа
     */
    public JSONObject getResponseAsJsonObject(int expectedStatusCode, String... jsonKeys) {
        int actualStatusCode = response.statusCode();
        Assert.assertEquals(actualStatusCode, expectedStatusCode, String.format(ErrorMessage.WRONG_STATUS, expectedStatusCode, actualStatusCode));
        JSONObject object;
        try {
            if (jsonKeys != null && jsonKeys.length != 0) {
                object = new JSONObject(response.body().asString());
                for (String jsonKey : jsonKeys) {
                    object = object.getJSONObject(jsonKey);
                }
            } else {
                object = new JSONObject(response.body().asString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
            throw new AssertionError(String.format(ErrorMessage.ERROR_CONVERTING_STRING_TO_JSON, response.asString()));
        }
        return object;
    }

    /**
     * Сеттит тело запроса переданное через столбец body
     */
    public void setJsonBody() {
        if (requestBody != null) {
            String jsonBody = replaceVarsIfPresent(requestBody);
            if (jsonBody.endsWith(".json")) {
                jsonBody = replaceVarsIfPresent(FileUtil.readBodyFromJsonDir(jsonBody));
            }
            this.requestBody = jsonBody;
            requestBuilder.setBody(jsonBody);
        }
    }

    /**
     * Сетит мультипарт тело переданное через имя файла
     */
    private void setMultipart() {
        if (multipart != null) {
            createMultipartBody();
        }
    }

    /**
     * Сеттит хидеры переданное через столбец headers
     */
    private void setJsonHeaders() {
        if (headers != null) {
            headers = headers.endsWith(".json")
                    ? replaceVarsIfPresent(FileUtil.readBodyFromJsonDir(headers))
                    : replaceVarsIfPresent(headers);
            Map<String, String> map = new Gson().fromJson(headers, HashMap.class);
            map.forEach(this::setHeader);
        }
    }


    /**
     * Сеттит дефолтные хидеры во все запросы
     */
    private void setDefaultHeaders() {
        requestBuilder
                .setAccept(ContentType.JSON)
                .setContentType(ContentType.JSON);
    }

    /**
     * Создает и сеттит мультипарт тело<br>
     * В качестве <b>multipart</b> можно передать название файла или путь, например:<br>
     * 1. empty.txt<br>
     * 2. /images/damages/1.jpg
     */
    private void createMultipartBody() {
        String parentPath = getParentPath("resources", "files").toString();
        File file = FileUtil.getFileByNameOrPath(parentPath, replaceVarsIfPresent(multipart));
        requestBuilder
                    .addMultiPart(file);
    }

    /**
     * Создает x-www-form-urlencoded форму
     */
    private void setFormUrlEncoded() {
        if (formUrlEncoded != null) {
            String json = replaceVarsIfPresent(formUrlEncoded);
            if (json.endsWith(".json")) {
                json = replaceVarsIfPresent(FileUtil.readBodyFromJsonDir(json));
            }
            requestBuilder
                    .setConfig(RestAssured.config)
                    .setContentType(ContentType.URLENC.withCharset("UTF-8"));
            try {
                JSONObject form = new JSONObject(json);
                form.toMap().forEach((param, value) -> {
                    requestBuilder.addFormParam(param, value);
                });
            } catch (JSONException jsonException) {
                throw new AssertionError("Неправильно указан json для параметров формы. Json:\n" + json);
            }
        }
    }

    private void addLoggingListener() {
        requestBuilder.addFilter(new RestAssuredCustomLogger());
    }
}