package com.openmonet.corecommonstep.gherkintables;

public class MathOperationModel {
    public String valuesType;
    public String firstValue;
    public String operation;
    public String variableName;

    public String format;

    public MathOperationModel(String valuesType, String firstValue, String operation, String variableName, String format) {
        this.valuesType = valuesType;
        this.firstValue = firstValue;
        this.operation = operation;
        this.variableName = variableName;
        this.format = format;
    }
}
