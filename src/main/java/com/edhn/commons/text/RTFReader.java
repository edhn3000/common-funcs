package com.edhn.commons.text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.rtf.RTFEditorKit;

public class RTFReader {

    String text;

    DefaultStyledDocument dsd;

    RTFEditorKit rtf;

    public void readRtf(File in) {
            rtf=new RTFEditorKit();
            dsd=new DefaultStyledDocument();
            try {
                rtf.read(new InputStreamReader(new FileInputStream(in), "GBK"), dsd, 0);
                text = dsd.getText(0, dsd.getLength());
//                text = new String();
                System.out.println(text);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

    public void writeRtf(File out) {
        try {
            rtf.write(new FileOutputStream(out), dsd, 0, dsd.getLength());
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (BadLocationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

        RTFReader readRTF = new RTFReader();
        String fileName = "f:\\文书校对资料\\法律文书校对系统－各版本安装包\\法院3.7.1\\template.rtf";
        readRTF.readRtf(new File(fileName));
//        readRTF.writeRtf(new File("e:\\out.rtf"));
    }
}
