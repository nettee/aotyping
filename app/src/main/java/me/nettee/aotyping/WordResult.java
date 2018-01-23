package me.nettee.aotyping;

public class WordResult {

    private String word;
    private String maxAccList;
    private String avgTouchSizeList;

    public WordResult(String word, String maxAccList, String avgTouchSizeList) {
        this.word = word;
        this.maxAccList = maxAccList;
        this.avgTouchSizeList = avgTouchSizeList;
    }
}
