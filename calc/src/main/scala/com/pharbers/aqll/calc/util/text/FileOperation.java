package com.pharbers.aqll.calc.util.text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.RandomAccessFile;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileOperation {

    /**
     * create a file
     * @param fileName
     * @return
     */
    public static boolean createFile(File fileName)throws Exception{
        boolean flag=false;
        try{
            if(!fileName.exists()){
                fileName.createNewFile();
                flag=true;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return true;
    }

    /**
     * Read TXT file content
     * @param fileName
     * @return
     */
    public static List<String> readTxtFile(File fileName)throws Exception{
        String result=null;
        List<String> list = new ArrayList<String>();
        FileReader fileReader=null;
        BufferedReader bufferedReader=null;
        try{
            fileReader=new FileReader(fileName);
            bufferedReader=new BufferedReader(fileReader);
            try{
                String read=null;
                while((read=bufferedReader.readLine())!=null){
                    list.add(read);
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            if(bufferedReader!=null){
                bufferedReader.close();
            }
            if(fileReader!=null){
                fileReader.close();
            }
        }
        return list;
    }


    public static boolean writeTxtFile(String content,File  fileName)throws Exception{
        RandomAccessFile mm=null;
        boolean flag=false;
        FileOutputStream o=null;
        try {
            o = new FileOutputStream(fileName);
            o.write(content.getBytes("UTF-8"));
            o.flush();
            o.close();
            flag=true;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }finally{
            if(mm!=null){
                mm.close();
            }
        }
        return flag;
    }



    public static void contentToTxt(String filePath, String content) {
        String str = new String(); //The original content for txt
        String s1 = new String();//Content updates
        try {
            File f = new File(filePath);
            if (f.exists()) {
                System.out.print("File exists");
            } else {
                System.out.print("File does not exist");
                f.createNewFile();// There is no is created
            }
            BufferedReader input = new BufferedReader(new FileReader(f));

            while ((str = input.readLine()) != null) {
                s1 += str + "\n";
            }
            System.out.println(s1);
            input.close();
            s1 += content;

            BufferedWriter output = new BufferedWriter(new FileWriter(f));
            output.write(s1);
            output.close();
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

}