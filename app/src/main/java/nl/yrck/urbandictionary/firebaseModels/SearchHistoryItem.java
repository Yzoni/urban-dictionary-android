package nl.yrck.urbandictionary.firebaseModels;

public class SearchHistoryItem {

    public String term;
    public Long timestamp;

    public SearchHistoryItem() {
        // Empty constructor needed for Firebase
    }

    public SearchHistoryItem(String term, Long timestamp) {
        this.term = term;
        this.timestamp = timestamp;
    }
}
