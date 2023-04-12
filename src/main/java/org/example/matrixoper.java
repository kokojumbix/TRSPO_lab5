package org.example;

public class matrixoper {
    public static double[][] multiplyMatrices(double[][] firstMatrix, double[][] secondMatrix) {
        int rowFirstMatrix = firstMatrix.length;
        int columnFirstMatrix = firstMatrix[0].length;
        int rowSecondMatrix = secondMatrix.length;
        int columnSecondMatrix = secondMatrix[0].length;

        if (columnFirstMatrix != rowSecondMatrix) {
            throw new IllegalArgumentException("Розмірність матриць не співпадає.");
        }

        double[][] resultMatrix = new double[rowFirstMatrix][columnSecondMatrix];

        for (int i = 0; i < rowFirstMatrix; i++) {
            for (int j = 0; j < columnSecondMatrix; j++) {
                for (int k = 0; k < columnFirstMatrix; k++) {
                    resultMatrix[i][j] += firstMatrix[i][k] * secondMatrix[k][j];
                }
            }
        }

        return resultMatrix;
    }

    public static double[][] addMatrices(double[][] firstMatrix, double[][] secondMatrix){

        if ( firstMatrix.length == 1 && firstMatrix[0].length == 1 ){
            double[][] result = new double[secondMatrix.length][secondMatrix[0].length];
            for (int i = 0; i < secondMatrix.length; i++){
                for(int j = 0; j < secondMatrix[0].length; j++){
                    result[i][j] = secondMatrix[i][j] + firstMatrix[0][0];
                }
            }
            return result;
        }
        else{
            double[][] result = new double[secondMatrix.length][secondMatrix[0].length];
            for (int i = 0; i < secondMatrix.length; i++){
                for(int j = 0; j < secondMatrix[0].length; j++){
                    result[i][j] = secondMatrix[i][j] + firstMatrix[i][j];
                }
            }
            return result;
        }
    }

    public static double[][] transposeMatrix(double[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        double[][] resultMatrix = new double[cols][rows];

        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                resultMatrix[i][j] = matrix[j][i];
            }
        }

        return resultMatrix;
    }

}
