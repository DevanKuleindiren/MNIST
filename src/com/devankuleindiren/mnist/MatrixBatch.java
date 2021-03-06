package com.devankuleindiren.mnist;

/**
 * Created by Devan Kuleindiren on 29/08/15.
 */
public class MatrixBatch {

    private Matrix inputs;
    private Matrix targets;

    public MatrixBatch (Matrix inputs, Matrix targets) {
        this.inputs = inputs;
        this.targets = targets;
    }

    public Matrix getInputs() {
        return inputs;
    }

    public Matrix getTargets() { return targets; }
}
