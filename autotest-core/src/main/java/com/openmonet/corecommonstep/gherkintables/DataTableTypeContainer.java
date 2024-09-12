package com.openmonet.corecommonstep.gherkintables;

import io.cucumber.java.DataTableType;
import com.openmonet.api.dataParsers.*;

import java.util.Map;

import static com.openmonet.context.ContextHolder.replaceVarsIfPresent;

public class DataTableTypeContainer {
    @DataTableType
    public CompareParser compareParser(Map<String, String> entry) {
        return new CompareParser(
                entry.get("variableName"),
                entry.get("actualValue"),
                entry.get("expectedValue"),
                entry.get("comparisonMark"));
    }

    @DataTableType
    public RequestParser requestParser(Map<String, String> entry) {
        return new RequestParser(
                entry.get("method"),
                entry.get("prefix"),
                entry.get("path"),
                entry.get("body"),
                entry.get("multipart"),
                entry.get("headers"),
                entry.get("form_url_encoded"),
                entry.get("full_url"));
    }

    @DataTableType
    public MapParser mapParser(Map<String, String> entry) {
        return new MapParser(
                entry.get("key"),
                entry.get("value"));
    }

    @DataTableType(replaceWithEmptyString = "[emptyValue]")
    public RemoveJsonKeyParser removerJsonKeyParser(Map<String, String> entry) {
        return new RemoveJsonKeyParser(
                entry.get("keyToRemove") != null? entry.get("keyToRemove"): entry.get("ключ для удаления"),
                entry.get("valueOfKeyToModify") != null? entry.get("valueOfKeyToModify"): entry.get("ключ для изменения"),
                entry.get("newValue") != null? entry.get("newValue"): entry.get("новое значение"),
                entry.get("positionInJson") != null? entry.get("positionInJson"): entry.get("позиция в json"),
                entry.get("номер в массиве")
                );
    }

    @DataTableType
    public MathOperationModel mathOperationModel(Map<String, String> entry) {
        return new MathOperationModel(
                entry.get("values_type").toLowerCase(),
                entry.get("first_value"),
                entry.get("operation"),
                entry.get("variable_name"),
                entry.get("format"));
    }

    @DataTableType
    public CompareModel compareModel(Map<String, String> entry) {
        return new CompareModel(
                entry.get("comparison_alias"),
                entry.get("values_type").toLowerCase(),
                entry.get("first_value"),
                entry.get("operation"),
                entry.get("operator").toLowerCase(),
                entry.get("second_value"));
    }
}
