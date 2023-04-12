package org.example;


public class Equation extends Thread {
    public
    int n;
    int progress = 0;
    double[][] A;
    double[][] A1;
    double[][] A2;
    double[][] B2;
    double[][] Y3;
    double[][] C2;

    double[][] b;
    double[][] b1;
    double[][] c1;
    double[][] y1;
    double[][] y2;
    boolean OpenCL;
    CLmatrice cl;

    Lab2 lab2;
    public Equation(int n, Lab2 Lab2, boolean OpenCL, CLmatrice cl){
        this.n = n;
        this.lab2 = Lab2;
        this.OpenCL = OpenCL;
        this.cl = cl;
    }

    public void fillC2(){
        for (int i=0; i<this.n; i++){
            for (int j=0; j<this.n; j++){
                this.C2[i][j]=25.0/( Math.pow(i+j+2,3));
            }
        }
    }

    public void fillb(){
        for (int i=0; i<this.n; i++){
            this.b[i][0]=25.0/( Math.pow(i+1,3)*((i+1)%2) + 1*((i+2)%2) );
        }
    }

    public void random(double[][] Matrix) {
        for ( int i=0; i<Matrix.length; i++){
            for (int j=0; j<Matrix[0].length; j++){
                Matrix[i][j] =  Math.random()*40;
            }
        }
    }
    public void init(){
        this.A = new double[n][n];
        this.A1 = new double[n][n];
        this.A2 = new double[n][n];
        this.B2 = new double[n][n];
        this.Y3 = new double[n][n];
        this.C2 = new double[n][n];

        this.b = new double[n][1];
        this.b1 = new double[n][1];
        this.c1 = new double[n][1];
        this.y1 = new double[n][1];
        this.y2 = new double[n][1];

        random(A);
        random(A1);
        random(A2);
        random(B2);
        random(Y3);
        fillC2();


        fillb();
        random(b1);
        random(c1);
        random(y1);
        random(y2);

        lab2.y1.setText("");
        lab2.y2.setText("");
        lab2.y3.setText("");

        lab2.r1.setText("");
        lab2.r2.setText("");
        lab2.leq.setText("");
        lab2.req.setText("");
        lab2.x.setText("");
    }



    public class y1part extends Thread {
        public double[][] A;
        public double[][] b;
        public double[][] Y1;
        public y1part(double[][] A, double[][] b){
            this.A = A;
            this.b = b;
        }

        public void run(){
            if (OpenCL){
                this.Y1 = cl.multiplyMatrices(this.A,this.b);
            }
            else {
                this.Y1 = matrixoper.multiplyMatrices(this.A,this.b);
            }

        }
    }

    public class y2part extends Thread {
        public double[][] b1;
        public double[][] c1;
        public double[][] A1;
        public double[][] y2;
        public y2part(double[][] b1, double[][] c1, double[][] A1){
            this.b1 = b1;
            this.c1 = c1;
            this.A1 = A1;
        }
        public void run(){
            if (OpenCL){
                this.y2 = cl.multiplyMatrices(A1, matrixoper.addMatrices(this.b1,this.c1));
            }
            else {
                this.y2 = matrixoper.multiplyMatrices(A1, matrixoper.addMatrices(this.b1,this.c1));
            }

        }
    }

    public class y3part extends Thread {
        public double[][] B2;
        public double[][] C2;
        public double[][] A2;
        public double[][] y3;
        public y3part(double[][] B2, double[][] C2, double[][] A2){
            this.B2 = C2;
            this.C2 = C2;
            this.A2 = A2;
        }
        public void run(){
            if (OpenCL){
                this.y3 = cl.multiplyMatrices(A2, matrixoper.addMatrices(this.B2,this.C2));
            }
            else {
                this.y3 = matrixoper.multiplyMatrices(A2, matrixoper.addMatrices(this.B2,this.C2));
            }

        }
    }

    public class R1solve extends Thread {
        public double[][] y3;
        public double[][] y1;
        public double[][] r1;
        public R1solve(double[][] y3, double[][] y1){
            this.y3 = y3;
            this.y1 = y1;
        }
        public void run(){
            if (OpenCL){
                this.r1 = cl.multiplyMatrices(y3, cl.multiplyMatrices(y3, y3));
                this.r1 = cl.multiplyMatrices(this.r1, y1);
            }
            else {
                this.r1 = matrixoper.multiplyMatrices(y3, matrixoper.multiplyMatrices(y3, y3));
                this.r1 = matrixoper.multiplyMatrices(this.r1, y1);
            }
            this.r1 = matrixoper.addMatrices(this.r1, y1);
        }
    }

    public void printusedmemory(){
        System.out.println("Used memory "+Long.toString(( Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576) +" Mb" );
    }
    public class R2solve extends Thread {
        public double[][] y3;
        public double[][] y1;
        public double[][] y2;
        public double[][] r2;
        public R2solve(double[][] y3, double[][] y1, double[][] y2){
            this.y3 = y3;
            this.y1 = y1;
            this.y2 = y2;
        }
        public void run(){
            if (OpenCL){
                this.r2 = cl.multiplyMatrices(y1, matrixoper.transposeMatrix(y2));
                this.r2 = cl.multiplyMatrices(this.r2, y3);
                this.r2 = cl.multiplyMatrices(this.r2, y1);
            }
            else {
                this.r2 = matrixoper.multiplyMatrices(y1, matrixoper.transposeMatrix(y2));
                this.r2 = matrixoper.multiplyMatrices(this.r2, y3);
                this.r2 = matrixoper.multiplyMatrices(this.r2, y1);
            }

        }
    }

    public class Leq extends Thread {
        public double[][] y3;
        public double[][] y1;
        public double[][] y2;
        public double[][] l;
        public Leq(double[][] y3, double[][] y1, double[][] y2){
            this.y3 = y3;
            this.y1 = y1;
            this.y2 = y2;
        }
        public void run(){
            if (OpenCL){
                this.l = cl.multiplyMatrices(matrixoper.transposeMatrix(y1), y3);
                this.l = cl.multiplyMatrices(this.l, y2);
                this.l = matrixoper.addMatrices(this.l, matrixoper.transposeMatrix(y2));
            }
            else {
                this.l = matrixoper.multiplyMatrices(matrixoper.transposeMatrix(y1), y3);
                this.l = matrixoper.multiplyMatrices(this.l, y2);
                this.l = matrixoper.addMatrices(this.l, matrixoper.transposeMatrix(y2));
            }

        }
    }

    public class Req extends Thread {
        public double[][] R1;
        public double[][] R2;
        public double[][] Req;
        public Req(double[][] R1, double[][] R2){
            this.R1 = R1;
            this.R2 = R2;
        }
        public void run(){
            this.Req = matrixoper.addMatrices(R1,R2);
        }
    }

    public class x extends Thread {
        public double[][] Req;
        public double[][] Leq;
        public double[][] xeq;

        public x(double[][] Req, double[][] Leq){
            this.Req = Req;
            this.Leq = Leq;
        }
        public void run(){
            this.xeq = matrixoper.multiplyMatrices(Leq,Req);
        }
    }


    public void run(){
        this.init();
        lab2.y1.setText("");
        lab2.y2.setText("");
        lab2.y3.setText("");

        y1part y1 = new y1part(A,b);
        y1.start();

        y2part y2 = new y2part(b1,c1,A1);
        y2.start();

        y3part y3 = new y3part(B2,C2,A2);
        y3.start();


        try {
            y1.join();
            lab2.y1.setText(Integer.toString(y1.Y1.length) + " " + Integer.toString(y1.Y1[0].length));
            lab2.progress(15);
            y2.join();
            lab2.y2.setText(Integer.toString(y2.y2.length) + " " + Integer.toString(y2.y2[0].length));
            lab2.progress(30);
            y3.join();
            lab2.y3.setText(Integer.toString(y3.y3.length) + " " + Integer.toString(y3.y3[0].length));
            lab2.progress(40);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        R1solve r1 = new R1solve(y3.y3,y1.Y1);
        r1.start();

        R2solve r2 = new R2solve(y3.y3,y1.Y1,y2.y2);
        r2.start();
        Leq l = new Leq(y3.y3,y1.Y1,y2.y2);
        l.start();

        try {
            r1.join();
            lab2.r1.setText(Integer.toString(r1.r1.length) + " " + Integer.toString(r1.r1[0].length));
            lab2.progress(70);
            r2.join();
            lab2.r2.setText(Integer.toString(r2.r2.length) + " " + Integer.toString(r2.r2[0].length));
            lab2.progress(80);
            l.join();
            lab2.leq.setText(Integer.toString(l.l.length) + " " + Integer.toString(l.l[0].length));
            lab2.progress(90);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Req req = new Req(r1.r1,r2.r2);
        req.start();
        try {
            req.join();
            lab2.req.setText(Integer.toString(req.Req.length) + " " + Integer.toString(req.Req[0].length));
            lab2.progress(95);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        x xeq = new x(req.Req,l.l);
        xeq.start();
        try {
            xeq.join();
            lab2.x.setText(Integer.toString(xeq.xeq.length) + " " + Integer.toString(xeq.xeq[0].length));
            lab2.x.setText(lab2.x.getText() + " " + xeq.xeq[0][0]);
            lab2.progress(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        printusedmemory();
        lab2.button1.setEnabled(true);
    }
}
