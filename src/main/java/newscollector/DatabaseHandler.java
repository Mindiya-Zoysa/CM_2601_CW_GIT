package newscollector;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// The DatabaseHandler class manages all database-related operations
// For the News Collector application, including user, article, and rating management.

public class DatabaseHandler {
    // Database connection details
    private static final String URL = "jdbc:mysql://localhost:3306/newscollector";
    private static final String USER = "agampodi.20231603@iit.ac.lk";
    private static final String PASSWORD = "Mindiya@019";
    private static Connection connection;

    // Establishes a connection to the database.
    // To ensure only one connection is active throughout the application's lifecycle.
    public static Connection getConnection() {
        if (connection == null) {
            try {
                System.out.println("Connecting to database...");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Database connection established.");
            } catch (SQLException e) {
                System.out.println("Failed to connect to the database.");
                e.printStackTrace();
                return null;
            }
        }
        return connection;
    }

    // Saves a new user to the database.
    public boolean saveUser(User user) {
        String insertUserSQL = "INSERT INTO user_detail (userName, userPassword, registrationDate) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = getConnection().prepareStatement(insertUserSQL, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setDate(3, new java.sql.Date(user.getRegDate().getTime()));
            pstmt.executeUpdate();

            ResultSet keys = pstmt.getGeneratedKeys();
            if (keys.next()) {
                user.setUserId(String.valueOf(keys.getInt(1)));
            }

            System.out.println("User saved successfully with userID: " + user.getUserId());
            return true;
        } catch (SQLIntegrityConstraintViolationException e) {
            // This exception is thrown when a duplicate entry is attempted
            System.out.println("Username already exists. Please try a different username.");
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Checks if a username already exists in the database.
    public boolean isUsernameExists(String username) {
        String checkUserSQL = "SELECT 1 FROM user_detail WHERE userName = ?";
        try (PreparedStatement pstmt = getConnection().prepareStatement(checkUserSQL)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            return rs.next(); // Returns true if username exists, false otherwise
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Saves user preferences (categories) to the database.
    public void saveUserPreferences(String userId, List<String> preferences) {
        String insertPrefSQL = "INSERT INTO user_preference (userId, categoryName) VALUES (?, ?)";
        try (PreparedStatement pstmt = getConnection().prepareStatement(insertPrefSQL)) {
            for (String preference : preferences) {
                pstmt.setString(1, userId);
                pstmt.setString(2, preference);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            System.out.println("User preferences saved successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Saves an article to the database. If the article already exists, it is replaced.
    public void saveArticle(Article article) {
        String insertSQL = "REPLACE INTO article (categoryName, title, author, content, PublishedDate, sourceName) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = getConnection().prepareStatement(insertSQL)) {
            pstmt.setString(1, article.getCategoryName());
            pstmt.setString(2, article.getTitle());
            pstmt.setString(3, article.getAuthor());
            pstmt.setString(4, article.getContent());
            pstmt.setDate(5, new java.sql.Date(article.getPublishedDate().getTime()));
            pstmt.setString(6, article.getSourceName());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Saves a rating for an article by a user.
    public void saveRating(String userId, String articleId, int rating) {
        String insertRatingSQL = "INSERT INTO article_rating (userId, articleId, rating, timeincident) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = getConnection().prepareStatement(insertRatingSQL)) {
            pstmt.setString(1, userId);
            pstmt.setString(2, articleId);
            pstmt.setInt(3, rating);
            pstmt.setTimestamp(4, new Timestamp(System.currentTimeMillis())); // Set current timestamp
            pstmt.executeUpdate();
            System.out.println("Rating saved successfully for article ID: " + articleId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Retrieves all ratings given by a user.
    public Map<String, Integer> getUserRatings(String userId) {
        Map<String, Integer> userRatings = new HashMap<>();
        String selectSQL = "SELECT articleId, rating FROM article_rating WHERE userId = ?";
        try (PreparedStatement pstmt = getConnection().prepareStatement(selectSQL)) {
            pstmt.setString(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                userRatings.put(rs.getString("articleId"), rs.getInt("rating"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userRatings;
    }

    // Retrieves user details and preferences based on the provided credentials.
    public User getUser(String userName, String userPassword) {
        // Add BINARY to enforce case sensitivity on the username comparison
        String selectSQL = "SELECT userId, userName, userPassword FROM user_detail WHERE BINARY userName = ? AND userPassword = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(selectSQL)) {
            pstmt.setString(1, userName);
            pstmt.setString(2, userPassword);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String userId = rs.getString("userId");
                User user = new User(userId, userName, userPassword);
                List<String> preferences = getUserPreferences(userId);
                user.updatePreferences(preferences);
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Retrieves a list of articles by User Preferences.
    private List<String> getUserPreferences(String userId) {
        List<String> preferences = new ArrayList<>();
        String selectSQL = "SELECT categoryName FROM user_preference WHERE userId = ?";
        try (PreparedStatement pstmt = getConnection().prepareStatement(selectSQL)) {
            pstmt.setString(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                preferences.add(rs.getString("categoryName"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return preferences;
    }

    // Retrieves a list of articles by category.
    public List<Article> getArticlesBycategoryName(String categoryName) {
        List<Article> articlesList = new ArrayList<>();
        String selectSQL = "SELECT * FROM article WHERE categoryName = ?";
        try (PreparedStatement pstmt = getConnection().prepareStatement(selectSQL)) {
            pstmt.setString(1, categoryName);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Article article = createArticleFromResultSet(rs);
                articlesList.add(article);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return articlesList;
    }

    // Creates an Article object from a ResultSet.
    private Article createArticleFromResultSet(ResultSet rs) throws SQLException {
        String articleId = rs.getString("articleId");
        String categoryName = rs.getString("categoryName");
        String title = rs.getString("title");
        String author = rs.getString("author");
        String content = rs.getString("content");
        Date PublishedDate = rs.getDate("publishedDate");
        String source = rs.getString("sourceName");
        return new Article(articleId, categoryName, title, author, content,  PublishedDate, source);
    }
}
