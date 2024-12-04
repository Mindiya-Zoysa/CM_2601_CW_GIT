package newscollector;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// The ArticleFetcher class is responsible for fetching articles from an external API.
// It supports fetching articles by category or fetching a mix of articles across multiple categories.

public class ArticleFetcher {

    // API key for accessing the News API
    private String apiKey = "cfa5e5c1cedf4689aa59172795c3cf32";
    // Base URL for the News API
    private String sourceName = "https://newsapi.org/v2/top-headlines";
    // Supported categories for articles
    private static final String[] CATEGORIESName = {"Sports", "Technology", "Politics", "Health", "Entertainment", "Business"};
    // Country for filtering articles
    private static final String COUNTRY = "us";

    public List<Article> fetchArticles(String categoryName, int limit) {
        List<Article> articlesList = new ArrayList<>();
        try {
            // Construct the API URL with query parameters
            String urlString = sourceName + "?country=" + COUNTRY + "&categoryName=" + categoryName + "&pageSize=" + limit + "&apiKey=" + apiKey;
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Check the API response code
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Read the API response
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();

                // Parse the JSON response
                JSONObject jsonResponse = new JSONObject(response.toString());
                String status = jsonResponse.optString("status");
                if (!"ok".equals(status)) {
                    System.out.println("API returned error status: " + status);
                    return articlesList;
                }

                // Extract articles from the JSON response
                JSONArray articlesArray = jsonResponse.getJSONArray("articles");
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                for (int i = 0; i < articlesArray.length(); i++) {
                    JSONObject articleJSON = articlesArray.getJSONObject(i);

                    // Extract article details
                    String articleId = articleJSON.getString("url");
                    String title = articleJSON.getString("title");
                    String author = articleJSON.optString("author", "Unknown Author");
                    String content = articleJSON.optString("content", "No content available");
                    Date PublishedDate = dateFormat.parse(articleJSON.getString("publishedAt"));
                    String sourceName = articleJSON.getJSONObject("source").getString("name");

                    // Create an Article object and add it to the list
                    Article article = new Article(articleId, categoryName, title, author, content, PublishedDate, sourceName);
                    articlesList.add(article);
                }
            } else {
                System.out.println("GET request failed for category " + categoryName + " with response code: " + responseCode);
            }
        } catch (Exception e) {
            System.out.println("Exception while fetching articles for category " + categoryName + ": " + e.getMessage());
            e.printStackTrace();
        }
        return articlesList;
    }

    //Fetches a mix of articles across multiple categories.
    public List<Article> fetchMultipleCategories(int totalArticles) {
        List<Article> mixedArticles = new ArrayList<>();
        // Ensure at least 1 article per category
        int articlesPercategoryName = Math.max(totalArticles / CATEGORIESName.length, 1);

        // Fetch articles for each category
        for (String categoryName : CATEGORIESName) {
            List<Article> categoryNameArticles = fetchArticles(categoryName, articlesPercategoryName);
            mixedArticles.addAll(categoryNameArticles);
        }

        // If we didn't fetch enough articles, fetch more in a round-robin fashion
        int currentIndex = 0;
        while (mixedArticles.size() < totalArticles) {
            String categoryName = CATEGORIESName[currentIndex % CATEGORIESName.length];
            // Fetch one more article per category
            List<Article> additionalArticles = fetchArticles(categoryName, 1);
            mixedArticles.addAll(additionalArticles);
            currentIndex++;
        }

        System.out.println("Total mixed articles fetched: " + mixedArticles.size());
        return mixedArticles;
    }
}
