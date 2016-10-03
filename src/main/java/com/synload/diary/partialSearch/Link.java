package com.synload.diary.partialSearch;

/**
 * Created by Nathaniel on 7/13/2016.
 */
public class Link {
    private char value;
    private int position;
    private Link next = null;
    public Link(char value, int position){
        this.value = value;
        this.position = position;
    }
    public char getValue() {
        return value;
    }
    public void setValue(char value) {
        this.value = value;
    }
    public int getPosition() {
        return position;
    }
    public void setPosition(int position) {
        this.position = position;
    }
    public Link getNext() {
        return next;
    }
    public void setNext(Link next) {
        this.next = next;
    }
}
