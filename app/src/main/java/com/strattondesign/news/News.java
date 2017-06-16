package com.strattondesign.news;

import java.util.Date;

public class News {
    private String title;
    private String description;
    private String category;
    private String author;
    private String link;
    private Date publishedDate;

    /**
     * Constructor with all data
     * @param title
     * @param description
     * @param category
     * @param author
     * @param publishedDate
     */
    public News(String title, String description, String category, String author, String link, Date publishedDate) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.author = author;
        this.link = link;
        this.publishedDate = publishedDate;
    }

    // BEGIN Getters and setters
    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public String getAuthor() {
        return author;
    }

    public String getLink() {
        return link;
    }

    public Date getPublishedDate() {
        return publishedDate;
    }

    // END Getters and setters
}
