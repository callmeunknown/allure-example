package com.openmonet.api.dataParsers;

public class CompareParser {

    private String variableName;
    private String actualValue;
    private String expectedValue;
    private String comparisonMark;

    public CompareParser(String variableName, String actualValue, String expectedValue, String comparisonMark){
        this.variableName = variableName;
        this.actualValue = actualValue;
        this.expectedValue = expectedValue;
        this.comparisonMark = comparisonMark;
    }

    public String getVariableName() {
        return variableName;
    }

    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }

    public String getActualValue() {
        return actualValue;
    }

    public void setActualValue(String actualValue) {
        this.actualValue = actualValue;
    }

    public String getExpectedValue() {
        return expectedValue;
    }

    public void setExpectedValue(String expectedValue) {
        this.expectedValue = expectedValue;
    }

    public String getComparisonMark() {
        return comparisonMark;
    }

    public void setComparisonMark(String comparisonMark) {
        this.comparisonMark = comparisonMark;
    }
}
