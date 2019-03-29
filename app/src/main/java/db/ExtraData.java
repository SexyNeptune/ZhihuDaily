package db;

public class ExtraData {
    private int popularity;
    private int comments;

    public int getPopularity() {
        return popularity;
    }

    public void setPopularity(int popularity) {
        this.popularity = popularity;
    }

    public int getComments() {
        return comments;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }

    @Override
    public String toString() {
        return "ExtraData{" +
                "popularity=" + popularity +
                ", comments=" + comments +
                '}';
    }
}
