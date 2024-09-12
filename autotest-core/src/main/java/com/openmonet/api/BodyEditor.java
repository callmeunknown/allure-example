package com.openmonet.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.util.RawValue;
import com.openmonet.api.dataParsers.RemoveJsonKeyParser;
import com.openmonet.utils.FileUtil;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static com.openmonet.context.ContextHolder.replaceVarsIfPresent;


public class BodyEditor {
    private final List<RemoveJsonKeyParser> removerJsonKeyDatas;
    String bodyFromFile;
    ObjectMapper mapper;
    JsonNode tree;
    String keyToRemove;
    String valueOfKeyToModify;
    String newValue;
    Integer[] positionInJson;
    private Integer count = 0;


    public BodyEditor(String fileWithBody, List<RemoveJsonKeyParser> removerJsonKeyDatas) {
        this.removerJsonKeyDatas = removerJsonKeyDatas;
        bodyFromFile = FileUtil.readBodyFromJsonDir(fileWithBody);
        bodyFromFile = replaceVarsIfPresent(bodyFromFile);
        mapper = new ObjectMapper();

        try {
            tree = mapper.readTree(bodyFromFile);
        } catch (JsonProcessingException jpe) {
            throw new AssertionError(String.format("Ошибка при обработке json: \n%s, проверьте правильность передаваемого файла: \n%s ", bodyFromFile, fileWithBody));
        }
    }

    /**
     * Создает тело запроса без заданного ключа или его значения
     *
     * @return Модифицированное тело запроса
     */
    public String removeJsonKeyFromData() {

        for (RemoveJsonKeyParser removerJsonKeyData : removerJsonKeyDatas) {

            keyToRemove = removerJsonKeyData.getKeyToRemove();
            valueOfKeyToModify = removerJsonKeyData.getvalueOfKeyToModify();
            newValue = removerJsonKeyData.getNewValue();
            newValue = newValue != null ? replaceVarsIfPresent(newValue) : null;
            positionInJson = toIntArray(removerJsonKeyData.getPositionInJson());

            if ((valueOfKeyToModify != null) == (keyToRemove != null)) {
                throw new IllegalArgumentException("Необходимо использовать один параметр valueOfKeyToModify или keyToRemove");
            }

            if (valueOfKeyToModify != null) {
                change(tree);
            } else {
                if (removerJsonKeyData.getArrayPosition() == null) {
                    remove(tree);
                } else {
                    removeFromArray(tree, removerJsonKeyData.getArrayPosition());
                }
            }
            count = 0;
        }

        return tree.toString();
    }

    private void change(JsonNode parent) {

        boolean isRequiredToReplace = (Arrays.stream(positionInJson).anyMatch(s -> s.equals(count)));

        if (parent.has(valueOfKeyToModify)) {
            if (isRequiredToReplace) {
                ((ObjectNode) parent).putRawValue(valueOfKeyToModify, new RawValue(newValue));
            }
            count++;
        }

        for (JsonNode child : parent) {
            change(child);
        }
    }

    private void remove(JsonNode parent) {

        boolean isRequiredToReplace = (Arrays.stream(positionInJson).anyMatch(s -> s.equals(count)));

        if (parent.has(keyToRemove)) {
            if (isRequiredToReplace) {
                ((ObjectNode) parent).remove(keyToRemove);
            }
            count++;
        }

        for (JsonNode child : parent) {
            remove(child);
        }
    }

    /**
     * Удаляет из массива значение по порядковому номеру
     * @param parent    -   json-нода
     * @param arrayPositionToRemove -   порядковый номер для удаления
     */
    private void removeFromArray(JsonNode parent, int arrayPositionToRemove) {
        boolean isRequiredToReplace = (Arrays.stream(positionInJson).anyMatch(s -> s.equals(count)));
        if (parent.has(keyToRemove)) {
            if (isRequiredToReplace) {
                if (parent.get(keyToRemove).isArray()) {
                    Iterator<JsonNode> nodes = parent.get(keyToRemove).elements();
                    int nodeNumber = 0;
                    while (nodes.hasNext()) {
                        nodes.next();
                        if (nodeNumber == arrayPositionToRemove) {
                            nodes.remove();
                        }
                        nodeNumber++;
                    }
                } else {
                    throw new AssertionError(String.format("В json объект '%s' не является массивом!", keyToRemove));
                }
            }
            count++;
        }
        for (JsonNode child : parent) {
            removeFromArray(child, arrayPositionToRemove);
        }
    }

    private Integer[] toIntArray(String positionInJson) {
        String[] tempStringArrays = positionInJson.split(",");
        Integer[] tempIntArray = new Integer[tempStringArrays.length];

        for (int i = 0; i < tempStringArrays.length; i++) {
            try {
                int parseInt = Integer.parseInt(tempStringArrays[i]);
                tempIntArray[i] = parseInt;
            } catch (NumberFormatException nfe) {
                nfe.printStackTrace();
            }
        }
        return tempIntArray;
    }
}
