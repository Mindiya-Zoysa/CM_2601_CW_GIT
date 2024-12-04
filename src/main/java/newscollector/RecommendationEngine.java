package newscollector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// The RecommendationEngine class generates personalized recommendations for users
// Based on their preferences, previous ratings and similarity with other articles.

public class RecommendationEngine {
    private DatabaseHandler dbHandler; // Handles database-related operations
    private ArticleFetcher articleFetcher; // Fetches articles from external sources

    // Constructor for RecommendationEngine.
    public RecommendationEngine(DatabaseHandler dbHandler, ArticleFetcher articleFetcher) {
        this.dbHandler = dbHandler;
        this.articleFetcher = articleFetcher;
    }

    // Generates a list of recommended articles for a user.
    public List<Article> getRecommendations(User user) {
        // Fetch all articles in the user's primary preferred category
        List<Article> allArticles = dbHandler.getArticlesBycategoryName(user.getPrimarycategoryName());
        // Retrieve user's previous article ratings
        Map<String, Integer> userRatings = dbHandler.getUserRatings(user.getUserId());
        // Map to store articles and their calculated recommendation scores
        Map<Article, Double> scoredArticles = new HashMap<>();

        // Calculate scores for each article
        for (Article article : allArticles) {
            // Base score based on similarity to user preferences
            double score = calculateSimilarityScore(article, user.getPreferences());

            // Boost score if the user has rated the article previously
            if (userRatings.containsKey(article.getArticleId())) {
                int rating = userRatings.get(article.getArticleId());
                score += rating; // Increase score based on rating value
            }

            // Add the article and its score to the map
            scoredArticles.put(article, score);
        }

        // Sort articles by their scores in descending order and return the list
        return scoredArticles.entrySet().stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue())) // Sort by score (descending)
                .map(Map.Entry::getKey) // Extract articles from the sorted entries
                .toList(); // Convert to a list
    }

    // Calculates a similarity score for an article based on user preferences.
    private double calculateSimilarityScore(Article article, List<String> preferences) {
        double score = 0.0;
        for (String preference : preferences) {
            // Increment the score if the article's category matches the user's preference
            if (article.getCategoryName().equalsIgnoreCase(preference)) {
                score += 1.0;
            }
        }
        return score;
    }
}
