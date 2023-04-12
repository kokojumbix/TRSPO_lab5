package org.example;

public class Main {
    /**
     * The source code of the OpenCL program
     */


    private static String programSource =
            "__kernel void " +
                    "matrixMultiplication(__global const float *A, " +
                    "__global const float *B, " +
                    "__global float *C, " +
                    "const int m, const int n, const int k) " +
                    "{" +
                    "    int i = get_global_id(0);" +
                    "    int j = get_global_id(1);" +
                    "    float sum = 0;" +
                    "    for (int h = 0; h < n; ++h) " +
                    "        sum += A[i*n+h] * B[h*k+j];" +
                    "    C[i*k+j] = sum;" +
                    "}";

    public static void main(String args[])
    {
        int n = 1000;
        double srcArrayA[][] = new double[n][n];
        double srcArrayB[][] = new double[n][n];
        double dstArray[][] = new double[n][n];

        for (int i=0; i<n; i++)
        {
            for (int j=0; j<n; j++) {
                srcArrayA[i][j] = i;
                srcArrayB[j][i] = j;
            }
        }

        double test[];
        CLmatrice cl = new CLmatrice();
        long startTime = System.currentTimeMillis();
        dstArray = cl.multiplyMatrices(srcArrayA,srcArrayB);
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        System.out.println("Time taken: " + duration + " milliseconds");
        dstArray = cl.multiplyMatrices(srcArrayA,srcArrayB);
        endTime = System.currentTimeMillis();
        duration = endTime - startTime;
        System.out.println("Time taken: " + duration + " milliseconds");

        startTime = System.currentTimeMillis();
        matrixoper.multiplyMatrices(srcArrayB,srcArrayA);
        endTime = System.currentTimeMillis();
        duration = endTime - startTime;
        System.out.println("Time taken: " + duration + " milliseconds");
        /*
        for( int i = 0; i < dstArray.length; i++){
            for( int j = 0; j < dstArray[0].length; j++) {
                System.out.print( Double.toString(dstArray[i][j]) + " " );
            }
            System.out.println();
        }
        */
        // OPENCL



    }


}