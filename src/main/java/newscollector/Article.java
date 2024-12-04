package newscollector;

import java.util.Date;

// The Article class represents an article in the News Collector application.
// It contains information such as the article ID, category, title, author, content, publication date, and source.

public class Article {

    // Attributes representing the article's properties
    private String articleId;       // Unique identifier for the article
    private String categoryName;    // Category of the article (e.g., Sports, Technology)
    private String title;           // Title of the article
    private String author;          // Author of the article
    private String content;         // Main content of the article
    private Date PublishedDate;     // Publication date of the article
    private String sourceName;      // Source of the article (e.g., website or publisher)

    // Constructor to initialize an Article object with all attributes.
    public Article(String articleId, String categoryName, String title, String author, String content, Date PublishedDate, String sourceName) {
        this.articleId = articleId;
        this.categoryName = categoryName;
        this.title = title;
        this.author = author;
        this.content = content;
        this.PublishedDate = PublishedDate;
        this.sourceName = sourceName;
    }

    // Getter and Setter methods for each attribute

    public String getArticleId() {
        return articleId;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Date getPublishedDate() {
        return PublishedDate;
    }

    public void setPublishedDate(Date PublishedDate) {
        this.PublishedDate = PublishedDate;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }
}
