package com.devankuleindiren.mnist;

/**
 * Created by Devan Kuleindiren on 29/08/15.
 */
public class Matrix implements Cloneable {

    private int height;
    private int width;
    protected double[][] values;
    private double biasWeight;

    public Matrix (int height, int width) {
        this.height = height;
        this.width = width;
        values = new double[height][width];
    }

    public int getHeight () { return height; }
    public int getWidth () { return width; }
    public double[][] getValues () { return values; }
    public double getBiasWeight () { return biasWeight; }
    public void setBiasWeight (double newWeight) { biasWeight = newWeight; }
    public void incBiasWeight (double diff) { biasWeight += diff; }

    public double get (int row, int col) {
        if (row >= 0 && row < height && col >= 0 && col < width) {
            return values[row][col];
        }
        return 0;
    }

    public void set (int row, int col, double newVal) {
        if (row >= 0 && row < height && col >= 0 && col < width) {
            values[row][col] = newVal;
        }
    }

    public void inc (int row, int col, double diff) {
        if (row >= 0 && row < height && col >= 0 && col < width) {
            values[row][col] += diff;
        }
    }

    public Matrix add (Matrix toAdd) throws MatrixDimensionMismatchException {
        if (height == toAdd.height && width == toAdd.width) {
            Matrix result = new Matrix(height, width);
            for (int row = 0; row < height; row++) {
                for (int col = 0; col < width; col++) {
                    result.values[row][col] = values[row][col] + toAdd.values[row][col];
                }
            }
            result.biasWeight = this.biasWeight + toAdd.biasWeight;
            return result;
        } else {
            throw new MatrixDimensionMismatchException("addition");
        }
    }

    public Matrix subtract (Matrix toSubtract) throws MatrixDimensionMismatchException {
        return add(toSubtract.scalarMultiply(-1.0));
    }

    public Matrix multiply (Matrix toMultiply) throws MatrixDimensionMismatchException {
        if (width == toMultiply.height) {
            Matrix result = new Matrix(height, toMultiply.width);
            for (int row = 0; row < result.height; row++) {
                for (int col = 0; col < result.width; col++) {
                    double value = 0;
                    for (int count = 0; count < width; count++) {
                        value += values[row][count] * toMultiply.values[count][col];
                    }
                    result.values[row][col] = value;
                }
            }
            return result;
        } else {
            throw new MatrixDimensionMismatchException("multiplication");
        }
    }

    public Matrix multiplyEach (Matrix toMultiply) throws MatrixDimensionMismatchException {
        if (height == toMultiply.height && width == toMultiply.width) {
            Matrix result = new Matrix(height, width);
            for (int row = 0; row < height; row++) {
                for (int col = 0; col < width; col++) {
                    result.values[row][col] = values[row][col] * toMultiply.values[row][col];
                }
            }
            result.biasWeight = this.biasWeight * toMultiply.biasWeight;
            return result;
        } else {
            throw new MatrixDimensionMismatchException("multiplication");
        }
    }

    public Matrix transpose () {
        Matrix result = new Matrix(width, height);
            for (int row = 0; row < result.height; row++) {
                for (int col = 0; col < result.width; col++) {
                    result.values[row][col] = values[col][row];
                }
            }
        return result;
    }

    public Matrix scalarMultiply (double scalar) {
        Matrix result = new Matrix(height, width);
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                result.values[row][col] = values[row][col] * scalar;
            }
        }
        result.biasWeight = this.biasWeight * scalar;
        return result;
    }

    @Override
    public Object clone () {
        Matrix clone = new Matrix (height, width);
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                clone.values[row][col] = values[row][col];
            }
        }
        return clone;
    }

    // USEFUL FOR NEURAL NETWORKS:

    // MULTIPLY THE MATRIX BY THE WEIGHTS, APPLY THE ACTIVATION FUNCTION AND ADD A BIAS COLUMN
    public Matrix feedForwardAndAddBias (Matrix weights) throws MatrixDimensionMismatchException {

        if (width == weights.height) {
            Matrix result = new Matrix (height, weights.width + 1);

            // PERFORM MULTIPLICATION AGAINST WEIGHTS MATRIX
            for (int row = 0; row < result.height; row++) {
                for (int col = 0; col < result.width - 1; col++) {
                    double value = 0;
                    for (int count = 0; count < width; count++) {
                        value += values[row][count] * weights.values[count][col];
                    }
                    result.values[row][col] = value;
                }
            }

            // APPLY THE ACTIVATION FUNCTION
            result.applyLogisticActivation();

            // ADD THE BIAS INPUTS FOR THE NEXT LAYER
            for (int row = 0; row < result.height; row++) {
                result.values[row][result.width - 1] = -1;
            }

            return result;
        } else {
            throw new MatrixDimensionMismatchException("multiplication");
        }
    }

    // SET THE GREATEST ACTIVATION TO 1 AND ALL OTHERS TO 0
    public void rectifyActivations () {
        for (int row = 0; row < height; row++) {
            double tempMaxAct = values[row][0];
            int tempMaxPos = 0;

            for (int col = 0; col < width; col++) {
                if (values[row][col] > tempMaxAct) {
                    tempMaxAct = values[row][col];
                    tempMaxPos = col;
                }
                values[row][col] = 0;
            }
            values[row][tempMaxPos] = 1;
        }
    }

    // APPLY LOGISTIC ACTIVATION FUNCTION
    public void applyLogisticActivation () {
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                values[row][col] = 1 / (1 + Math.exp(-values[row][col]));
            }
        }
    }

    // GENERATE A MATRIX FULL OF 1s
    public static Matrix onesMatrix (int height, int width) {
        Matrix result = new Matrix(height, width);
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                result.set(row, col, 1);
            }
        }
        return result;
    }

    // REMOVE THE COLUMN OF BIAS WEIGHTS
    public Matrix removeBiasCol () {
        Matrix result = new Matrix(height, width - 1);
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width - 1; col++) {
                result.values[row][col] = values[row][col];
            }
        }
        return result;
    }

    // REMOVE THE ROW OF BIAS WEIGHTS
    public Matrix removeBiasRow () {
        Matrix result = new Matrix(height - 1, width);
        for (int row = 0; row < height - 1; row++) {
            for (int col = 0; col < width; col++) {
                result.values[row][col] = values[row][col];
            }
        }
        return result;
    }

    // USE THIS MATRIX AS A KERNEL
    public Matrix convolute (Matrix input) throws MatrixDimensionMismatchException {

        if (height <= input.getHeight() && width <= input.getWidth()) {
            Matrix result = new Matrix(input.getHeight() - height + 1, input.getWidth() - width + 1);

            for (int row = 0; row < result.getHeight(); row++) {
                for (int col = 0; col < result.getWidth(); col++) {
                    double sum = 0;

                    for (int kRow = 0; kRow < height; kRow++) {
                        for (int kCol = 0; kCol < width; kCol++) {
                            sum += input.get(row + kRow, col + kCol) * values[kRow][kCol];
                        }
                    }
                    sum += -1 * biasWeight;
                    result.set(row, col, sum);
                }
            }

            return result;
        } else {
            throw new MatrixDimensionMismatchException("image convolution");
        }
    }
}
