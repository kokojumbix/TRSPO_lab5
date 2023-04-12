package org.example;

import org.jocl.*;
import org.jocl.Pointer;

public class CLmatrice {

    public static double[] matrice_to_array(double[][] Matrix){
        double[] result_matrice = new double[Matrix.length * Matrix[0].length];
        for( int i = 0; i < Matrix.length; i++){
            for( int j = 0; j < Matrix[0].length; j++) {
                result_matrice[i*Matrix.length + j] = Matrix[i][j];
            }
        }
        return result_matrice;
    }
    String programMult =
            "__kernel void " +
                    "matrixMultiplication(__global const double *A, " +
                    "__global const double *B, " +
                    "__global double *C, " +
                    "const int m, const int n, const int k) " +
                    "{" +
                    "    int i = get_global_id(0);" +
                    "    int j = get_global_id(1);" +
                    "    double sum = 0;" +
                    "    for (int h = 0; h < n; ++h) " +
                    "        sum += A[i*n+h] * B[h*k+j];" +
                    "    C[i*k+j] = sum;" +
                    "}";

    public static double[][] multiplyMatrices(double[][] firstMatrix, double[][] secondMatrix) {
        int rowFirstMatrix = firstMatrix.length;
        int columnFirstMatrix = firstMatrix[0].length;
        int rowSecondMatrix = secondMatrix.length;
        int columnSecondMatrix = secondMatrix[0].length;

        if (columnFirstMatrix != rowSecondMatrix) {
            throw new IllegalArgumentException("Розмірність матриць не співпадає.");
        }

        final int m = rowFirstMatrix;
        final int n = rowSecondMatrix;
        final int k = columnSecondMatrix;

        double[] srcArrayA = new double[rowFirstMatrix * columnFirstMatrix];


        double[] srcArrayB = new double[rowSecondMatrix * columnSecondMatrix];
        double[] dstArray = new double[rowFirstMatrix * columnSecondMatrix];

        double[][] resultMatrix = new double[rowFirstMatrix][columnSecondMatrix];

        Pointer srcA = Pointer.to(srcArrayA);
        Pointer srcB = Pointer.to(srcArrayB);
        Pointer dst = Pointer.to(dstArray);

        final int platformIndex = 0;
        final long deviceType = CL.CL_DEVICE_TYPE_GPU;
        final int deviceIndex = 0;

        CL.setExceptionsEnabled(true);

        int numPlatformsArray[] = new int[1];
        CL.clGetPlatformIDs(0, null, numPlatformsArray);
        int numPlatforms = numPlatformsArray[0];

        cl_platform_id platforms[] = new cl_platform_id[numPlatforms];
        CL.clGetPlatformIDs(platforms.length, platforms, null);
        cl_platform_id platform = platforms[platformIndex];

        cl_context_properties contextProperties = new cl_context_properties();
        contextProperties.addProperty(CL.CL_CONTEXT_PLATFORM, platform);

        int numDevicesArray[] = new int[1];
        CL.clGetDeviceIDs(platform, deviceType, 0, null, numDevicesArray);
        int numDevices = numDevicesArray[0];

        cl_device_id devices[] = new cl_device_id[numDevices];
        CL.clGetDeviceIDs(platform, deviceType, numDevices, devices, null);
        cl_device_id device = devices[deviceIndex];

        cl_context context = CL.clCreateContext(
                contextProperties, 1, new cl_device_id[]{device},
                null, null, null);

        cl_command_queue commandQueue =
                CL.clCreateCommandQueue(context, device, 0, null);

        cl_mem memObjects[] = new cl_mem[3];
        memObjects[0] = CL.clCreateBuffer(context,
                CL.CL_MEM_READ_ONLY | CL.CL_MEM_COPY_HOST_PTR,
                Sizeof.cl_double * rowFirstMatrix * columnFirstMatrix, srcA, null);
        memObjects[1] = CL.clCreateBuffer(context,
                CL.CL_MEM_READ_ONLY | CL.CL_MEM_COPY_HOST_PTR,
                Sizeof.cl_double * rowSecondMatrix * columnSecondMatrix, srcB, null);
        memObjects[2] = CL.clCreateBuffer(context,
                CL.CL_MEM_READ_WRITE,
                Sizeof.cl_double * rowFirstMatrix * columnSecondMatrix, null, null);








        return resultMatrix;

    }
}
