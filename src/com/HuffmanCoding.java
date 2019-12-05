package com;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class HuffmanCoding {

    private static final String LEFT_VALUE = "0";
    private static final String RIGHT_VALUE = "1";
    private static final int bit = 8;

    /*허프만 인코딩*/
    public static void encoding(File targetFile) throws Exception {

        String orgStr = "";
        ArrayList<Node> charCntChkList = null;
        String binaryStr = "";
        Node binaryTree = null;
        ArrayList<Integer> intList = null;
        File encodingFile;
        File treeFile;

        //파일 내부 문자열을 읽어들임
        orgStr = fileToStr(targetFile);

        encodingFile = new File(targetFile.getParent() + File.separator + targetFile.getName().substring(0, targetFile.getName().lastIndexOf(".")) + ".bin");

        if (orgStr.length() > 1) {

            /*문자당 빈도수 체크*/
            charCntChkList = charCntChk(orgStr);

            /*문자빈도수 별 역정렬*/
            charCntDescSort(charCntChkList);

            /*이진트리 생성*/
            binaryTree = createBinaryTree(charCntChkList);

            /*2진 문자열 생성*/
            binaryStr = createBinaryStr(orgStr, binaryTree);

            /*2진문자열을 8bit씩 나눠서 10진수로 변환*/
            intList = binaryStrToInt(binaryStr);

            /*1byte int로 압축된 문자열 파일로 생성*/
            intToFile(intList, encodingFile);

            /*트리객체 출력(디코딩을 위해 필요함..)*/
            treeFile = new File(targetFile.getParent() + File.separator + targetFile.getName().substring(0, targetFile.getName().lastIndexOf(".")) + ".tree");

            //원본파일 확장자 저장
            binaryTree.setOrgFileSuffix(targetFile.getName().substring(targetFile.getName().lastIndexOf(".")));

            objectOutput(treeFile, binaryTree);

        } else {
            /*문자열가 1개 이하라면 그대로 파일 생성*/
            strToFile(encodingFile, orgStr);
        }

    }


    /*허프만 디코딩*/
    public static void decoding(File encodingFile) throws Exception {

        ArrayList<Integer> intList = null;
        String binaryStr = "";
        File treeFile = null;
        Node tree = null;
        String decodingStr = "";
        File decodingFile;

        //압축파일을 읽어서 1byte 단위 int 리스트로 변환
        intList = fileToInt(encodingFile);

        /*int 리스트를 이진문자열로 변환*/
        binaryStr = intToBinaryStr(intList);

        /*디코딩을 위한 트리객체 읽어들임*/
        treeFile = new File(encodingFile.getParent() + File.separator + encodingFile.getName().substring(0, encodingFile.getName().lastIndexOf(".")) + ".tree");
        tree = objectInput(treeFile);

        /*2진 문자열로 트리경로를 탐색하여 디코딩 문자열 추출*/
        decodingStr = searchLeaf(binaryStr, tree);

        /*디코딩 파일 생성*/
        decodingFile = new File(encodingFile.getParent() + File.separator + encodingFile.getName().substring(0, encodingFile.getName().lastIndexOf(".")) + tree.getOrgFileSuffix());
        strToFile(decodingFile, decodingStr);

    }


    //파일 내부 문자열 읽어들임
    private static String fileToStr(File targetFile) throws Exception {

        BufferedReader br = null;
        String str = "";
        StringBuffer strLine = new StringBuffer();

        try {

            //파일 내부 문자열을 읽어들임
            br = new BufferedReader(new InputStreamReader(new FileInputStream(targetFile), "EUC-KR"));

            while ((str = br.readLine()) != null) {

                if (strLine.length() > 0) {
                    strLine.append("\n");
                }
                strLine.append(str);
            }
            str = strLine.toString();

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (br != null) br.close();
        }
        return str;

    }


    /*문자별 빈도수 체크*/
    private static ArrayList<Node> charCntChk(String str) {

        HashMap<String, Long> charCntChkMap = new HashMap<String, Long>();
        ArrayList<Node> chrCntList = new ArrayList<Node>();
        Iterator<String> keys;
        String word;
        int strLenght = str.length();

        //문자별 빈도수 체크
        for (int i = 0; i < strLenght; i++) {
            word = str.substring(i, i + 1);
            word = "\n".equals(word) ? "\\n" : word;
            charCntChkMap.put(word, charCntChkMap.containsKey(word) ? charCntChkMap.get(word) + 1 : 1);
        }

        System.out.println("문자별 빈도수 체크");
        System.out.println("*******************");

        //문자와 빈도수가 포함된 Node를 정렬을 위해 삽입,삭제가 유리한 list 객체 담음

        keys = charCntChkMap.keySet().iterator();

        while (keys.hasNext()) {
            word = keys.next();
            chrCntList.add(new Node(word, charCntChkMap.get(word)));
            System.out.println(word + " : " + charCntChkMap.get(word));
        }

        System.out.println("*******************\n");

        return chrCntList;

    }


    /*문자빈도수 별 역정렬*/
    private static void charCntDescSort(ArrayList<Node> charCntChkList) {

        int listSize = charCntChkList.size();
        Node tempNode;

        for (int i = 0; i < listSize; i++) {
            for (int j = 0; j < i; j++) {
                if (charCntChkList.get(j).getCnt() < charCntChkList.get(i).getCnt()) {
                    // if(charCntChkList.get(j).getCnt() > charCntChkList.get(i).getCnt()){

                    tempNode = charCntChkList.get(j);
                    charCntChkList.set(j, charCntChkList.get(i));
                    charCntChkList.set(i, tempNode);

                }
            }
        }

        System.out.println("문자빈도수 별 역정렬");
        System.out.println("*******************");

        for (int i = 0; i < listSize; i++) {
            System.out.println(charCntChkList.get(i).getWord() + " : " + charCntChkList.get(i).getCnt());
        }
        System.out.println("*******************\n");

    }


    /*이진트리생성*/
    private static Node createBinaryTree(ArrayList<Node> charCntDescSortList) {

        //원본 list에 영향이 없도록 주소값이 다른 복사객체 생성
        ArrayList<Node> copyList = (ArrayList<Node>) charCntDescSortList.clone();
        Node parentNode = null;

        while (copyList.size() > 1) {

            parentNode = new Node();
            parentNode.setRight(copyList.remove(copyList.size() - 1));
            parentNode.setLeft(copyList.remove(copyList.size() - 1));
            parentNode.setCnt(parentNode.getLeft().getCnt() + parentNode.getRight().getCnt());
            parentNode.getLeft().setValue(LEFT_VALUE);
            parentNode.getRight().setValue(RIGHT_VALUE);

            copyList.add(parentNode);
            charCntDescSort(copyList);

        }

        return parentNode;

    }


    /*이진트리 탐색 : 전위순회를 통해 root에서 특정 leaf노드까지의 경로 추출*/
    private static String searchLeafPath(String word, Node tree) {

        StringBuffer binary = new StringBuffer();
        Node tempNode = tree.clone();
        HashSet<Node> visitNode = new HashSet<Node>(); //방문했던 노드 기록(중복방문 방지)
        ArrayList<Node> parentNode = new ArrayList<Node>(); //right node 탐색후 되돌아갈 부모노드를 찾기위해 탐색했던 부모노드 기록

        while (true) {

            visitNode.add(tempNode);

            if ("".equals(tempNode.getWord())) {

                parentNode.add(tempNode);

                if (!visitNode.contains(tempNode.getLeft())) {
                    tempNode = tempNode.getLeft();
                } else {
                    tempNode = tempNode.getRight();
                }

                binary.append(tempNode.getValue());

            } else {
                //리프노드체크
                if (word.equals(tempNode.getWord())) {
                    break;
                } else {
                    if (LEFT_VALUE.equals(tempNode.getValue())) {
                        tempNode = parentNode.remove(parentNode.size() - 1);
                        binary.deleteCharAt(binary.length() - 1);
                    } else {
                        while (true) {
                            tempNode = parentNode.remove(parentNode.size() - 1);
                            binary.deleteCharAt(binary.length() - 1);

                            if (!visitNode.contains(tempNode.getRight())) {
                                break;
                            }
                        }
                    }
                }
            }
        }

        return binary.toString();

    }


    /*2진 문자열 생성*/
    private static String createBinaryStr(String orgStr, Node tree) {

        int strLength = orgStr.length();
        StringBuffer encodingStr = new StringBuffer();
        String word = "";
        String binaryStr = "";

        System.out.println("문자별 2진 문자열");
        System.out.println("*******************");

        for (int i = 0; i < strLength; i++) {
            word = orgStr.substring(i, i + 1);
            word = "\n".equals(word) ? "\\n" : word;
            binaryStr = searchLeafPath(word, tree);
            encodingStr.append(binaryStr);
            System.out.println(word + " : " + searchLeafPath(word, tree));
        }

        System.out.println("변환된 2진 문자열 생성 : " + encodingStr);
        System.out.println("*******************");

        return encodingStr.toString();

    }

    /*2진문자열을 8bit비트씩 읽어서 10진수로 변환*/
    private static ArrayList<Integer> binaryStrToInt(String binaryStr) {

        ArrayList<Integer> intList = new ArrayList<Integer>();
        int index = 0, strLength = binaryStr.length();

        System.out.println("8bit 2진 문자열 10진수로 변환");
        System.out.println("*******************");

        while ((index += bit) <= strLength) {
            intList.add(Integer.parseInt(binaryStr.substring(index - bit, index), 2));
            System.out.println(binaryStr.substring(index - bit, index) + " : " + Integer.parseInt(binaryStr.substring(index - bit, index), 2));
        }

        /*8개씩 변환하고 남은 2진문자열은 추가로 10진수 변환*/
        if (strLength > bit && strLength % bit > 0) {
            intList.add(Integer.parseInt(binaryStr.substring(index - bit), 2));
            intList.add(0);    //마지막 2진문자열 8비트 여부
            System.out.println(binaryStr.substring(index - bit) + " : " + Integer.parseInt(binaryStr.substring(index - bit), 2));
        } else {
            intList.add(1);    //마지막 2진문자열 8비트 여부
        }

        System.out.println("*******************\n");

        return intList;
    }

    /*int값을 파일로 생성*/
    private static void intToFile(ArrayList<Integer> intList, File outpuFile) throws Exception {

        DataOutputStream dos = null;
        int listSize = -1;
        try {

            listSize = intList.size();
            dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(outpuFile)));
            for (int i = 0; i < listSize; i++) {
                dos.write(intList.get(i));
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (dos != null) dos.close();
        }

    }

    /*트리 객체 직렬화*/
    private static void objectOutput(File outputFile, Node binaryTree) throws Exception {

        ObjectOutputStream oos = null;

        try {
            oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(outputFile)));
            oos.writeObject(binaryTree);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (oos != null) oos.close();
        }

    }

    /*인코딩 파일을 1byte 씩 읽어서 int 변환*/
    private static ArrayList<Integer> fileToInt(File targetFile) throws Exception {

        DataInputStream dis = null;
        ArrayList<Integer> intList = new ArrayList<Integer>();
        int intValue = -1;

        try {

            dis = new DataInputStream(new BufferedInputStream(new FileInputStream(targetFile)));

            while ((intValue = dis.read()) > -1) {
                intList.add(intValue);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (dis != null) dis.close();
        }

        return intList;
    }

    //int 리스트를 2진 문자열로 변환
    private static String intToBinaryStr(ArrayList<Integer> intList) throws Exception {

        int listSize = 0;
        String binaryStr = "";
        StringBuffer bitBuf = null;
        int bitChk = -1;
        StringBuffer binaryBuf = new StringBuffer();

		/*인코딩시 2진 문자열을 8bit씩 10진수로 변환하는데
		가장 마지막에 변환된 2진 문자열은 원본이 8bit 일수도 있고, 아닐 수도 있다.
		이를 구분하여 원본그대로 복원해야 한다.
		가장 마지막에 입력된 0 또는 1의 값이 이에 대한 구분값이다.*/

        listSize = intList.size();
        bitChk = intList.get(listSize - 1);    //마지막 2진문자열 8bit여부

        for (int i = 0; i < listSize - 1; i++) {
            binaryStr = Integer.toBinaryString(intList.get(i));

            bitBuf = new StringBuffer();
            bitBuf.append(binaryStr);

            if (i == listSize - 1) {
                if (bitChk == 1) {
                    while (bitBuf.length() < bit) {
                        bitBuf.insert(0, "0");
                    }
                }
            } else {
                while (bitBuf.length() < bit) {
                    bitBuf.insert(0, "0");
                }
            }

            binaryBuf.append(bitBuf.toString());

        }


        return binaryBuf.toString();
    }

    /*트리 객체 역직렬화*/
    private static Node objectInput(File inputFile) throws Exception {

        ObjectInputStream ois = null;
        Node inputObject = null;

        try {
            ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(inputFile)));
            inputObject = (Node) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (ois != null) ois.close();
        }

        return inputObject;
    }

    /*이진트리 탐색 : 지정된 경로의 leaf노드 값 추출*/
    private static String searchLeaf(String binaryStr, Node tree) {

        int strLength = binaryStr.length();
        Node tempNode = tree.clone();
        String chkWord = "";
        StringBuffer decodingStr = new StringBuffer();

		/*이진문자열을 하나씩 읽으면서 트리 순회
		리프노드에 도달해서 문자가 발견되면
		해당 문자를 꺼내고 다시 root부터 트리 순회 반복*/
        for (int i = 0; i < strLength; i++) {
            String word = binaryStr.substring(i, i + 1);

            if (LEFT_VALUE.equals(word)) {
                chkWord = tempNode.getLeft().getWord();
                if (!"".equals(chkWord)) {
                    decodingStr.append("\\n".equals(chkWord) ? "\r\n" : chkWord);
                    tempNode = tree.clone();
                } else {
                    tempNode = tempNode.getLeft();
                }
            } else if (RIGHT_VALUE.equals(word)) {
                chkWord = tempNode.getRight().getWord();
                if (!"".equals(chkWord)) {
                    decodingStr.append("\\n".equals(chkWord) ? "\r\n" : chkWord);
                    tempNode = tree.clone();
                } else {
                    tempNode = tempNode.getRight();
                }
            }

        }

        return decodingStr.toString();

    }

    //문자열을 파일로 생성
    private static void strToFile(File outputFile, String str) throws Exception {

        BufferedWriter bw = null;

        try {
            bw = new BufferedWriter(new FileWriter(outputFile));
            bw.write(str);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (bw != null) bw.close();
        }
    }
}