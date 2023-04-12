package org.example;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Lab2 {
    public JButton button1;
    private JPanel panel1;
    private JTextField textField1;
    private JProgressBar progressBar1;
    public JTextField y1;
    public JTextField y2;
    public JTextField y3;
    public JTextField r1;
    public JTextField r2;
    public JTextField leq;
    public JTextField req;
    public JTextField x;
    private JTextField time;
    private JCheckBox useOpenCLCheckBox;
    public Lab2 lab2 = this;
    public CLmatrice cl = new CLmatrice();
    public void progress(int n){
        progressBar1.setValue(n);
    }
    public Lab2() {
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println(textField1.getText());
                button1.setEnabled(false);
                Equation eq = new Equation(Integer.parseInt(textField1.getText()), lab2, useOpenCLCheckBox.isSelected(), cl);
                eq.start();
            }
        });
        textField1.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
                char c = e.getKeyChar();

                if(!Character.isDigit(c)){
                    e.consume();
                }

            }
        });
    }

    public void show() {
        JFrame frame = new JFrame("Lab2");
        frame.setContentPane(new Lab2().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();

        frame.setSize(680,680);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        Lab2 lab = new Lab2();
        lab.show();
    }
}
