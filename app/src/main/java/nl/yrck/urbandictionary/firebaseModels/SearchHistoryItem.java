/*
 * Yorick de Boer - 10786015
 */

package nl.yrck.urbandictionary.firebaseModels;

public class SearchHistoryItem {

    public String term;
    public Long timestamp;

    public SearchHistoryItem() {
        // Required empty constructor for Firebase
    }

    public SearchHistoryItem(String term, Long timestamp) {
        this.term = term;
        this.timestamp = timestamp;
    }
}
