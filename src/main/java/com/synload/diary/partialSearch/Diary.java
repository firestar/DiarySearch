package com.synload.diary.partialSearch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Created by Nathaniel on 7/13/2016.
 */
public class Diary {
    private String name = "none";
    private int size=0;
    private Map<Character, Map<Integer, List<WordChain>>> index = null;
    public Diary(String name){
        this.name = name;
        this.index = new HashMap<Character, Map<Integer, List<WordChain>>>();
    }
    public Diary(){
        this.index = new HashMap<Character, Map<Integer, List<WordChain>>>();
        this.size=0;
    }
    public void addEntry(String word, Object[] data) {
        this.size++;
        if (word.length() > 0){
            WordChain wc = new WordChain(word, data);
            if (wc.getLinks().length > 0) {
                for(Link link: wc.getLinks()){
                    if(!index.containsKey(link.getValue())){
                        index.put(link.getValue(), new HashMap<Integer, List<WordChain>>());
                    }
                    Map<Integer, List<WordChain>> diaryIndex = index.get(link.getValue());
                    if(!diaryIndex.containsKey(link.getPosition())) {
                        diaryIndex.put(link.getPosition(), new ArrayList<WordChain>());
                    }
                    boolean exists = false;
                    for(WordChain wChain : diaryIndex.get(link.getPosition())){
                        if(wChain.getWord().equalsIgnoreCase(word)){
                            exists = true;
                        }
                    }
                    if(!exists) {
                        diaryIndex.get(link.getPosition()).add(wc);
                    }
                }
            }
        }
    }
    public List<WordChain> search(String searchTerm){
        char[] terms = searchTerm.toCharArray();
        List<WordChain> words = new ArrayList<WordChain>();
        if(terms.length>2){
            if(index.containsKey(terms[0])){
                SearchStatus ss = new SearchStatus(index.get(terms[0]).size());
                for(Entry<Integer, List<WordChain>> entry: index.get(terms[0]).entrySet()){
                    new Thread(new PartialSearch(entry.getKey(), entry.getValue(), searchTerm, words, ss)).start();
                }
                while(!ss.isComplete()){
                    try {
                        Thread.sleep(1);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                // search completed
                return words;
            }
        }
        return words;
    }
    public class SearchStatus{
        private int completed=0;
        private int total=0;
        public SearchStatus(int total){
            this.total = total;
        }
        public void addCompleted(){
            completed++;
        }
        public boolean isComplete(){
            return (total==completed);
        }
    }
    public class PartialSearch implements Runnable{
        private List<WordChain> words;
        private String searchTerm;
        private List<WordChain> chains;
        private SearchStatus ss;
        private int position;
        public PartialSearch(int position, List<WordChain> chains, String searchTerm, List<WordChain> words, SearchStatus ss){
            this.words = words;
            this.chains = chains;
            this.position = position;
            this.searchTerm = searchTerm;
            this.ss = ss;
        }
        public void run(){
            List<WordChain> wcs = new ArrayList<WordChain>(chains); // new instance (new entries or removed entries would cause errors otherwise)
            for(WordChain wc: wcs){
                wc.startAtPosition(searchTerm.toCharArray(), words, position);
            }
            ss.addCompleted();
        }
    }

    public int getSize() {
        return size;
    }
}