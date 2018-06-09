package edu.JNU.SDM.model;

import edu.JNU.SDM.coordinate.point;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class genPDBFrame extends JFrame {

    final private JButton inputButton, outputButton, confirmGenerate;
    private JTextField inputTextField, outputTextField, fileNameField;
    private JFileChooser inputChooser, outputChooser;
    private String selectedFile;
    private boolean flag = false;
    private JProgressBar progressBar;
    private Container container;
    private Thread barThread;
    private modifyPDB[] modifyPDBS;
    private int pdbCount = 300;


    public String getInputText() {
        return this.inputTextField.getText();
    }

    public String getOutputText() {
        return this.outputTextField.getText();
    }

    public genPDBFrame() {
        super("修改并生成新的pdb文件");
        try {
            String lookAndFeel = UIManager.getSystemLookAndFeelClassName();
            UIManager.setLookAndFeel(lookAndFeel);
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        Font font1 = new Font("TimesRoman", Font.BOLD, 20);
        Font font2 = new Font("宋体", Font.ROMAN_BASELINE, 20);
        container = getContentPane();
        container.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        /**
         * 选择导入文件夹
         */
        JLabel inputFolderLabel = new JLabel("导入pdb的文件夹");
        inputFolderLabel.setFont(font1);
        container.add(inputFolderLabel);

        inputButton = new JButton("选择文件夹");
        container.add(inputButton);
        /**
         * 显示输入路径
         */
        inputTextField = new JTextField("pdb输入路径:", 60);
        inputTextField.setFont(font2);
        inputTextField.setEditable(false);
        container.add(inputTextField);

        fileNameField = new JTextField("文件名:", 60);
        fileNameField.setFont(font2);
        fileNameField.setEditable(false);
        container.add(fileNameField);
        /**
         * 选择输出文件夹
         */
        JLabel outputFolderLabel = new JLabel("输出pdb的文件夹");
        outputFolderLabel.setFont(font1);
        container.add(outputFolderLabel);

        outputButton = new JButton("选择输出文件夹");
        container.add(outputButton);

        /**
         * 显示输出路径
         */
        outputTextField = new JTextField("pdb输出路径:", 60);
        outputTextField.setFont(font2);
        outputTextField.setEditable(false);
        container.add(outputTextField);

        /**
         * 按钮事件
         */
        inputButton.addActionListener(
                new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        inputChooser = new JFileChooser();
                        //inputChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                        inputChooser.setApproveButtonText("确定");
                        inputChooser.showOpenDialog(new JPanel());
                        selectedFile = new String(inputChooser.getSelectedFile().getName());
                        String selectedParent = new String(inputChooser.getSelectedFile().getParent());
                        fileNameField.setText(selectedFile);
                        inputTextField.setText(selectedParent + "/");

                    }
                }
        );
        outputButton.addActionListener(

                new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        outputChooser = new JFileChooser();
                        outputChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                        outputChooser.setApproveButtonText("确定");
                        outputChooser.showOpenDialog(new JPanel());
                        String selectedFolder = new String(outputChooser.getSelectedFile().getPath());

                        outputTextField.setText(selectedFolder + "/");

                    }
                }
        );
        confirmGenerate = new JButton("确定");
        container.add(confirmGenerate);

        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setBackground(Color.CYAN);

        container.add(progressBar);

        confirmHandler handler = new confirmHandler();
        confirmGenerate.addActionListener(handler);

        setSize(640, 400);
        point screenSize = new point(640, 400);
        setLocation(screenSize.getX(), screenSize.getY());
        setVisible(true);
    }

    private class confirmHandler implements ActionListener{
        public class Buffer {
            private int buffer = -1;
            private int occupiedBufferCount = 0;

            public synchronized void set(int value) {
                while (occupiedBufferCount == 1) {
                    try {
                        wait();
                    } catch (InterruptedException ieSet) {
                        ieSet.printStackTrace();
                    }
                }
                this.buffer = value;
                ++occupiedBufferCount;
                notify();
            }

            public synchronized int get() {
                while (occupiedBufferCount == 0) {
                    try {
                        wait();
                    } catch (InterruptedException ieGet) {
                        ieGet.printStackTrace();
                    }
                }
                --occupiedBufferCount;
                notify();
                return buffer;
            }
        }

        public class setBarValue implements Runnable {
            private Buffer sharedLocation;

            public setBarValue(Buffer shared) {
                this.sharedLocation = shared;
            }

            public void run() {

                for (int i = 0; i < pdbCount; i++) {
                    int serials = sharedLocation.get();
                    int process = serials / (pdbCount / 100 + pdbCount % 100);
                    progressBar.setValue(process);

                }


            }
        }

        public class modifyFile implements Runnable {
            private Buffer sharedLocation;

            public modifyFile(Buffer shared) {
                this.sharedLocation = shared;
            }

            public void run() {
                modifyPDBS = new modifyPDB[pdbCount];
                String pdb = fileNameField.getText().toString().split("\\.")[0];


                flag = false;
                try {
                    System.out.println(pdb);
                    System.out.println(getInputText());
                    System.out.println(getOutputText());


                    for (int i = 0, j = 0; i < pdbCount; i++, j += 10) {
                        modifyPDBS[i] = new modifyPDB(j + 1, pdb);
                        int serialNumber = modifyPDBS[i].getPdbNumber();
                        String pdbName = modifyPDBS[i].getPdb();
                        modifyPDBS[i].modify(serialNumber, pdbName, getInputText(), getOutputText());
                        sharedLocation.set(i);
                    }
                    flag = true;

                } catch (Exception err1) {
                    err1.printStackTrace();
                }
                if (flag == true) {
                    System.out.println("All Clear");
                    JOptionPane.showMessageDialog(null, "全部完成");
                    System.exit(0);
                }

            }
        }
/*
        public void run() {
            modifyPDB[] modifyPDBS = new modifyPDB[300];
            String pdb = fileNameField.getText().toString().split("\\.")[0];


            flag = false;
            try {
                System.out.println(pdb);
                System.out.println(getInputText());
                System.out.println(getOutputText());


                for (int i = 0, j = 0; i < modifyPDBS.length; i++, j += 10) {
                    modifyPDBS[i] = new modifyPDB(j + 1, pdb);
                    int serialNumber = modifyPDBS[i].getPdbNumber();
                    String pdbName = modifyPDBS[i].getPdb();
                    modifyPDBS[i].modify(serialNumber, pdbName, getInputText(), getOutputText());
                    int process=i/(modifyPDBS.length/100+modifyPDBS.length%100);
                    progressBar.setValue(process);
                }
                flag = true;

            } catch (Exception err1) {
                err1.printStackTrace();
            }
            if (flag == true) {
                System.out.println("All Clear");
                JOptionPane.showMessageDialog(null, "全部完成");
                System.exit(0);
            }

        }
*/

        public void actionPerformed (ActionEvent e) {
            if (fileNameField.getText() == "文件名"
                    || fileNameField.getText().indexOf("pdb") == -1
                    || getInputText() == "pdb输入路径:"
                    || getOutputText() == "pdb输出路径:") {
                JOptionPane.showMessageDialog(null, "请选择正确的导入目录或者导出目录");
                return;
            }
            try{
                Buffer sharedLocation = new Buffer();
                Runnable modifyPdbFile = new modifyFile(sharedLocation);
                Thread modifyThread = new Thread(modifyPdbFile);
                Runnable setBarValues = new setBarValue(sharedLocation);
                Thread setBarThread = new Thread(setBarValues);

                modifyThread.start();
                setBarThread.start();

            }catch (Exception lastException){
                lastException.printStackTrace();
                JOptionPane.showMessageDialog(null,lastException.toString());
            }


/*
            barThread=new Thread(this,"bar-thread");
            barThread.start();
*/

        }
    }


}
