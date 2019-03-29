package db;

public class NewsContent {
    private String body;
    private String title;
    private String image;
    private int id;
    private String imageResouce;

    public String getImageResouce() {
        return imageResouce;
    }

    public void setImageResouce(String imageResouce) {
        this.imageResouce = imageResouce;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "NewsContent{" +
                "body='" + body + '\'' +
                ", title='" + title + '\'' +
                ", image='" + image + '\'' +
                ", id=" + id +
                '}';
    }
}
