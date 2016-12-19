package nl.yrck.urbandictionary.api.models;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/*
 * Wrapper for the search result
 */
public class SearchResult implements Serializable {
    public List<String> tags;

    @SerializedName("result_type")
    public String resultType;

    @SerializedName("list")
    public List<WordInfo> wordInfos;

    public List<String> sounds;
}
