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
        int n = 10;
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
        test = CLmatrice.matrice_to_array(srcArrayA);

        for( int i = 0; i < srcArrayA.length; i++){
            for( int j = 0; j < srcArrayA[0].length; j++) {
                System.out.print( Double.toString(srcArrayA[i][j]) + " " );
            }
            System.out.println();
        }

        for( int i = 0; i < srcArrayA.length; i++){
            for( int j = 0; j < srcArrayA[0].length; j++) {
                System.out.print( Double.toString(test[i*10+j]) + " " );
            }
            System.out.println();
        }

        // OPENCL



    }


}