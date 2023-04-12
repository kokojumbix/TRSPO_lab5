package org.example;

import org.jocl.*;
import org.jocl.Pointer;

public class CLmatrice {

    public static double[] matrice_to_array(double[][] Matrix){
        double[] result_matrice = new double[Matrix.length * Matrix[0].length];
        for( int i = 0; i < Matrix.length; i++){
            for( int j = 0; j < Matrix[0].length; j++) {
                result_matrice[i*Matrix[0].length + j] = Matrix[i][j];
            }
        }
        return result_matrice;
    }

    public static double[][] array_to_matrice(double[] Array, int rows, int cols){
        double[][] result_matrice = new double[rows][cols];
        for( int i = 0; i < rows; i++){
            for( int j = 0; j < cols; j++) {
                result_matrice[i][j] = Array[j+i*cols];
            }
        }
        return result_matrice;
    }

    public static String programMult =
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

    public int numPlatformsArray[] = new int[1];
    CLmatrice(){
        CL.clGetPlatformIDs(0, null, numPlatformsArray);
    }

    public double[][] multiplyMatrices(double[][] firstMatrix, double[][] secondMatrix) {
        long startTime = System.currentTimeMillis();

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

        double[] srcArrayA = matrice_to_array(firstMatrix);
        double[] srcArrayB = matrice_to_array(secondMatrix);
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
        int numPlatforms = this.numPlatformsArray[0];

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

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

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

        cl_program program = CL.clCreateProgramWithSource(context,
                1, new String[]{ programMult }, null, null);

        CL.clBuildProgram(program, 0, null, null, null, null);

        // Create the kernel
        cl_kernel kernel = CL.clCreateKernel(program, "matrixMultiplication", null);

        CL.clSetKernelArg(kernel, 0,
                Sizeof.cl_mem, Pointer.to(memObjects[0]));
        CL.clSetKernelArg(kernel, 1,
                Sizeof.cl_mem, Pointer.to(memObjects[1]));
        CL.clSetKernelArg(kernel, 2,
                Sizeof.cl_mem, Pointer.to(memObjects[2]));
        CL.clSetKernelArg(kernel, 3, Sizeof.cl_int, Pointer.to(new int[]{m}));
        CL.clSetKernelArg(kernel, 4, Sizeof.cl_int, Pointer.to(new int[]{n}));
        CL.clSetKernelArg(kernel, 5, Sizeof.cl_int, Pointer.to(new int[]{k}));

        long global_work_size[] = new long[]{m,k};
        long local_work_size[];
        if (m==k && m % 10 == 0)
            local_work_size = new long[]{10,10};
        else if (m!=k && k == 1 && m%10==0)
            local_work_size = new long[]{10,1};
        else if (m!=k && m == 1 && k%10==0)
            local_work_size = new long[]{1,10};
        else
            local_work_size = new long[]{1,1};


        // Execute the kernel
        CL.clEnqueueNDRangeKernel(commandQueue, kernel, 2, null,
                global_work_size, local_work_size, 0, null, null);


        // Read the output data
        CL.clEnqueueReadBuffer(commandQueue, memObjects[2], CL.CL_TRUE, 0,
                rowFirstMatrix * columnSecondMatrix * Sizeof.cl_double, dst, 0, null, null);

        // Release kernel, program, and memory objects
        CL.clReleaseMemObject(memObjects[0]);
        CL.clReleaseMemObject(memObjects[1]);
        CL.clReleaseMemObject(memObjects[2]);
        CL.clReleaseKernel(kernel);
        CL.clReleaseProgram(program);
        CL.clReleaseCommandQueue(commandQueue);
        CL.clReleaseContext(context);


        return array_to_matrice(dstArray,rowFirstMatrix, columnSecondMatrix );

    }
}
