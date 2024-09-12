package com.openmonet.corecommonstep.gherkintables;

public class CompareModel extends MathOperationModel {
    public String comparisonAlias;
    public String operator;
    public String secondValue;

    public CompareModel(String comparisonAlias, String valuesType, String firstValue, String operation, String operator, String secondValue) {
        super(valuesType, firstValue, operation, null, null);
        this.comparisonAlias = comparisonAlias;
        this.operator = operator;
        this.secondValue = secondValue;
    }
}
