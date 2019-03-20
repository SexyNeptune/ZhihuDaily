package gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class News {

    public int date;

    public List<Stories> stories;

    @SerializedName("top_stories")
    public List<TStories> tStories;

}
