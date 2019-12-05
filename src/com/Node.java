package com;

import java.io.Serializable;

class Node implements Serializable{

    private static final long serialVersionUID = 1L;

    private String word;	//문자
    private long cnt;			//문자빈도수
    private String value;	//이진값
    private String orgFileSuffix;	//원본파일 확장자명

    private Node left;
    private Node right;

    public Node(){
        this.word = "";
        this.cnt = 0;
    }

    public Node(String word, long cnt){
        this.word = word;
        this.cnt = cnt;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public long getCnt() {
        return cnt;
    }

    public void setCnt(long cnt) {
        this.cnt = cnt;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Node getLeft() {
        return left;
    }

    public void setLeft(Node left) {
        this.left = left;
    }

    public Node getRight() {
        return right;
    }

    public void setRight(Node right) {
        this.right = right;
    }

    public String getOrgFileSuffix() {
        return orgFileSuffix;
    }

    public void setOrgFileSuffix(String orgFileSuffix) {
        this.orgFileSuffix = orgFileSuffix;
    }

    public Node clone(){

        Node newNode = new Node();

        newNode.setCnt(this.cnt);
        newNode.setWord(this.word);
        newNode.setValue(this.value);

        if(this.left != null){
            newNode.setLeft(this.left.clone());
        }

        if(this.right != null){
            newNode.setRight(this.right.clone());
        }

        return newNode;

    }

}