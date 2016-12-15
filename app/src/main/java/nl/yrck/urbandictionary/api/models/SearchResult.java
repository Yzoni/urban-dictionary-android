package nl.yrck.urbandictionary.api.models;


import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchResult {
    public List<String> tags;

    @SerializedName("result_type")
    public String resultType;

    @SerializedName("list")
    public List<WordInfo> wordInfos;

    public List<String> sounds;
}
