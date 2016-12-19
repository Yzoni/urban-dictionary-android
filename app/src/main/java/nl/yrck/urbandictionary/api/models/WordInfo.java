/*
 * Yorick de Boer - 10786015
 */

package nl.yrck.urbandictionary.api.models;

import java.io.Serializable;

/*
 * Single matching found word
 */
public class WordInfo implements Serializable {
    public int defid;
    public String word;
    public String permalink;
    public String definition;
    public String example;
    public String thumbs_up;
    public String thumbs_down;
    public String current_vote;
}
