package com.openmonet.api.dataParsers;

public class RemoveJsonKeyParser {

    private String keyToRemove;
    private final String valueOfKeyToModify;
    private final String newValue;
    private final String positionInJson;
    private final Integer arrayPosition;

    public RemoveJsonKeyParser(String keyToRemove, String valueOfKeyToModify, String newValue, String positionInJson, String arrayPosition) {
        this.keyToRemove = keyToRemove;
        this.valueOfKeyToModify = valueOfKeyToModify;
        this.newValue = newValue;
        this.positionInJson = positionInJson;
        this.arrayPosition = arrayPosition != null? Integer.parseInt(arrayPosition): null;
    }

    public String getKeyToRemove() {
        return keyToRemove;
    }

    public void setKeyToRemove(String keyToRemove) {
        this.keyToRemove = keyToRemove;
    }

    public String getvalueOfKeyToModify() {
        return valueOfKeyToModify;
    }

    public String getPositionInJson() {
        return positionInJson;
    }

    public String getNewValue() {
        return newValue;
    }

    public Integer getArrayPosition() {
        return arrayPosition;
    }
}