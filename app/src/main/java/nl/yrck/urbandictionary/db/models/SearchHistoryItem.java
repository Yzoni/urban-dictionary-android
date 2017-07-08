/*
 * Yorick de Boer - 10786015
 */

package nl.yrck.urbandictionary.db.models;

public class SearchHistoryItem {

    private int id;
    private String term;
    private String timestamp;

    public SearchHistoryItem() {
        // Required empty constructor for Firebase
    }

    public SearchHistoryItem(int id, String term, String timestamp) {
        this.id = id;
        this.term = term;
        this.timestamp = timestamp;
    }

    public String getTerm() {
        return term;
    }
}
