package nl.yrck.urbandictionary.models;

public class SearchHistoryItem {

    public String term;

    public SearchHistoryItem() {
        // Empty constructor needed for Firebase
    }

    public SearchHistoryItem(String term) {
        this.term = term;
    }
}
