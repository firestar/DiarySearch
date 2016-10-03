package com.synload.diary.partialSearch;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Nathaniel on 7/13/2016.
 */
public class WordChain {
    private Link[] links = null;
    private int size;
    private String word;
    private Object[] data = null;
    public WordChain(String word){
        links = new Link[word.length()];
        this.word = word;
        this.size = word.length();
        int i =0;
        for(char x : word.toCharArray()){
            links[i] = new Link(x, i);
            if(i>0){
                links[i-1].setNext(links[i]);
            }
            i++;
        }
    }
    public WordChain(String word, Object[] data){
        this.data = data;
        links = new Link[word.length()];
        this.word = word;
        this.size = word.length();
        int i =0;
        for(char x : word.toCharArray()){
            links[i] = new Link(x, i);
            if(i>0){
                links[i-1].setNext(links[i]);
            }
            i++;
        }
    }
    public Object[] getData() {
        return data;
    }
    public void setData(Object[] data) {
        this.data = data;
    }
    public Link[] getLinks() {
        return links;
    }
    public void setLinks(Link[] links) {
        this.links = links;
    }
    public int getSize() {
        return size;
    }
    public void setSize(int size) {
        this.size = size;
    }
    public String getWord() {
        return word;
    }
    public void setWord(String word) {
        this.word = word;
    }
    public boolean startAtPosition(char[] searchTerm, List<WordChain> words, int position){
        if(position<links.length){
            return match(searchTerm, links[position], words);
        }
        return false;
    }
    public boolean match(char[] searchTerm, Link currentLink, List<WordChain> words){
        if(currentLink==null){
            return false;
        }
        if((searchTerm.length-1)+currentLink.getPosition() > this.size) {
            return false;
        }
        if(searchTerm.length==1) {
            if (searchTerm[0] == currentLink.getValue()) {
                words.add(this);
                return true;
            } else {
                return false;
            }
        } else {
            if (searchTerm[0] != currentLink.getValue()) {
                return false;
            }
        }
        if (match(Arrays.copyOfRange(searchTerm, 1, searchTerm.length), currentLink.getNext(), words)) {
            return true;
        }
        return false;
    }
}
