package com;

import java.io.File;

public class Main {

    public static void main(String[] args) {

        try {
            String[] testArr = {"ENCODE", "C:\\Users\\조휘수\\Desktop\\huffman2\\test.txt"};
            //String[] testArr = {"DECODE", "C:\\Users\\조휘수\\Desktop\\huffman2\\test.bin"};
            args = testArr;

            String execFlag = args[0];
            File targetFile = new File(args[1]);

            if ("encode".equalsIgnoreCase(execFlag)) {
                HuffmanCoding.encoding(targetFile);
            } else if ("decode".equalsIgnoreCase(execFlag)) {
                HuffmanCoding.decoding(targetFile);
            } else {
                throw new Exception("잘못된 명령");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
