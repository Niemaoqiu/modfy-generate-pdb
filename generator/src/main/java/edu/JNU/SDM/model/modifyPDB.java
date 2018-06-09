package edu.JNU.SDM.model;

import javax.swing.*;
import java.io.*;

public class modifyPDB {
    private int pdbNumber=0;
    private String pdb;
    public modifyPDB(){}
    public modifyPDB(int pdbNumber,String pdb){
        setPdb(pdb);
        setPdbNumber(pdbNumber);
    }

    public String getPdb() {
        return pdb;
    }

    public void setPdb(String pdb) {
        this.pdb = pdb;
    }

    public void setPdbNumber(int pdbNumber) {
        this.pdbNumber = pdbNumber;
    }

    public int getPdbNumber() {
        return pdbNumber;
    }

    public void modify(int pdbNumber,String pdb,String inputFolder,String outputFolder){
        try {
            String pdbFile=pdb+".pdb."+pdbNumber;
            FileInputStream fileInputStream=new FileInputStream(inputFolder+pdbFile);
            InputStreamReader inputStreamReader=new InputStreamReader(fileInputStream);
            BufferedReader reader=new BufferedReader(inputStreamReader);

            String output=pdb+"_"+pdbNumber+".pdb";
            FileOutputStream fileOutputStream=new FileOutputStream(outputFolder+output);
            OutputStreamWriter outputStreamWriter=new OutputStreamWriter(fileOutputStream);
            BufferedWriter writer=new BufferedWriter(outputStreamWriter);

            String sLine;
            String content="";
            int count=0;
            while ((sLine=reader.readLine())!=null){
                String tmp="";

                if(sLine.indexOf("ATOM")!=-1){
                    char[] tmpChar=sLine.toCharArray();
                    tmpChar[21]='A';
                    tmp=String.valueOf(tmpChar);
                    if(tmp.indexOf("HIE")!=-1){
                        tmp=tmp.replace("HIE","HIS");

                    }
                }

                content=content.concat(tmp+"\n");
                count++;
            }
/*
            System.out.println(count);
            System.out.println(content);
*/
            inputStreamReader.close();
            writer.write(content);
            writer.close();

            System.out.println(pdbNumber+"  is succeeded");
        }catch (IOException e){
            e.printStackTrace();
        }
    }

}
