package newscollector;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// The Interface class serves as the Command-Line Interface (CLI) for the News Collector application.
// It handles user interactions such as account creation, login, viewing articles, and providing recommendations.

public class Interface {
    private DatabaseHandler dbHandler; // Handles database operations
    private newscollector.RecommendationEngine recommendationEngine; // Generates article recommendations
    private ArticleFetcher articleFetcher; // Fetches articles from external sources
    private Scanner scanner; // Used for user input
    private List<Article> viewedArticles;  // Track fully viewed articles

    // Constructor for the Interface class.
    public Interface(DatabaseHandler dbHandler, RecommendationEngine recommendationEngine, ArticleFetcher articleFetcher) {
        this.dbHandler = dbHandler;
        this.recommendationEngine = recommendationEngine;
        this.articleFetcher = articleFetcher;
        this.scanner = new Scanner(System.in);
        this.viewedArticles = new ArrayList<>();
    }

    // Starts the CLI for user interaction. Provides options to create an account, login, or exit.
    public void start() {
        System.out.println("Welcome to NewsCollector!");
        while (true) {
            System.out.println("\nSelect an option:");
            System.out.println("1. Create an Account");
            System.out.println("2. Login to an Existing Account");
            System.out.println("3. Exit");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> createUser();
                case 2 -> login();
                case 3 -> {
                    System.out.println("Thank you for using News Collector. Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    // Allows the user to create a new account. Saves user details and preferences.
    public void createUser() {
        System.out.print("Enter username: ");
        String userName = scanner.nextLine().trim();
        System.out.print("Enter password: ");
        String userPassword = scanner.nextLine().trim();

        // Check if the username already exists
        if (dbHandler.isUsernameExists(userName)) {
            System.out.println("Username already exists. Please choose a different username.");
            return;
        }

        User newUser = User.createUser(userName, userPassword);
        boolean isSaved = dbHandler.saveUser(newUser);

        if (isSaved) {
            System.out.println("Account created successfully!");

            // Fetch and save a diverse set of 50 articles
            List<Article> articlesList = articleFetcher.fetchMultipleCategories(50);
            for (Article article : articlesList) {
                dbHandler.saveArticle(article);
            }

            selectPreferences(newUser);
            dashboard(newUser);
        } else {
            System.out.println("Account creation failed. Please try again.");
        }
    }

    // Allows the user to select their preferences for article categories.
    private void selectPreferences(User user) {
        System.out.println("\nSelect your preferences (choose categories): ");
        System.out.println("1. Sports");
        System.out.println("2. Technology");
        System.out.println("3. Politics");
        System.out.println("4. Health");
        System.out.println("5. Entertainment");
        System.out.println("6. Business");

        List<String> preferences = new ArrayList<>();
        System.out.print("Enter your choices separated by commas (e.g., 1,2,3): ");
        String[] choices = scanner.nextLine().split(",");

        for (String choice : choices) {
            switch (choice.trim()) {
                case "1" -> preferences.add("Sports");
                case "2" -> preferences.add("Technology");
                case "3" -> preferences.add("Politics");
                case "4" -> preferences.add("Health");
                case "5" -> preferences.add("Entertainment");
                case "6" -> preferences.add("Business");
                default -> System.out.println("Invalid choice: " + choice);
            }
        }

        user.updatePreferences(preferences);
        dbHandler.saveUserPreferences(user.getUserId(), preferences);
        System.out.println("Preferences updated successfully!");
    }

    // Allows an existing user to log in. Fetches their data and preferences.
    private void login() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        User user = dbHandler.getUser(username, password);
        if (user != null) {
            System.out.println("Login successful!");

            // Fetch and save a diverse set of 50 articles
            List<Article> articlesList = articleFetcher.fetchMultipleCategories(50);
            for (Article article : articlesList) {
                dbHandler.saveArticle(article);
            }
            dashboard(user);
        } else {
            System.out.println("Invalid username or password.");
        }
    }

    // Displays the dashboard for logged-in users, allowing them to view recommendations, select articles, rate articles or log out.
    private void dashboard(User user) {
        viewedArticles.clear(); // Clear viewed articles on each new login session
        while (true) {
            System.out.println("\nDashboard - Select an option:");
            System.out.println("1. View Recommendations");
            System.out.println("2. Select Category to Read Article");
            System.out.println("3. Rate an Article");
            System.out.println("4. Logout");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> viewRecommendations(user);
                case 2 -> selectcategoryNameToRead(user);
                case 3 -> rateArticle(user);
                case 4 -> {
                    System.out.println("Logging out...");
                    return;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    // Displays personalized article recommendations for the user.
    private void viewRecommendations(User user) {
        List<Article> recommendations = recommendationEngine.getRecommendations(user);

        if (recommendations.isEmpty()) {
            System.out.println("No recommendations available at this time.");
            return;
        }

        System.out.println("\nYour Recommendations (showing top 10):");
        for (int i = 0; i < Math.min(recommendations.size(), 10); i++) {
            Article article = recommendations.get(i);
            System.out.println((i + 1) + ". " + article.getTitle() + " (" + article.getCategoryName() + ") [ID: " + article.getArticleId() + "]");
        }

        System.out.print("Enter the number of the article you wish to read, or type 'Exit' to return to the dashboard: ");
        String input = scanner.nextLine().trim();

        if (input.equalsIgnoreCase("Exit")) {
            System.out.println("Returning to the dashboard...");
            return;
        }

        try {
            int choice = Integer.parseInt(input);
            if (choice > 0 && choice <= Math.min(recommendations.size(), 10)) {
                Article selectedArticle = recommendations.get(choice - 1);
                displayFullArticle(selectedArticle);
                viewedArticles.add(selectedArticle);  // Add to viewed list
            } else {
                System.out.println("Invalid choice. Please try again.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number between 1 and 10, or 'Exit' to return to the dashboard.");
        }
    }

    private void selectcategoryNameToRead(User user) {
        System.out.println("Select a category:");
        System.out.println("1. Sports\n2. Technology\n3. Politics\n4. Health\n5. Entertainment\n6. Business");

        // Read user choice for the category
        int choice = scanner.nextInt();
        scanner.nextLine();

        // Determine category name based on user input
        String categoryName = switch (choice) {
            case 1 -> "Sports";
            case 2 -> "Technology";
            case 3 -> "Politics";
            case 4 -> "Health";
            case 5 -> "Entertainment";
            case 6 -> "Business";
            default -> {
                System.out.println("Invalid choice.");
                yield null; // Invalid input handling
            }
        };

        if (categoryName != null) {
            // Fetch articles from the selected category
            List<Article> articles = dbHandler.getArticlesBycategoryName(categoryName);

            if (articles.isEmpty()) {
                System.out.println("No articles found in the selected category.");
                return; // Exit if no articles are found
            }

            // Display up to 10 articles from the selected category
            System.out.println("Article in " + categoryName + ":");
            for (int i = 0; i < Math.min(articles.size(), 10); i++) {
                Article article = articles.get(i);
                System.out.println((i + 1) + ". " + article.getTitle() + " [ID: " + article.getArticleId() + "]");
            }

            System.out.print("Enter the number of the article you wish to read, or type 'Exit' to return to the dashboard: ");
            String input = scanner.nextLine().trim();

            // Handle user choice to read an article or exit
            if (input.equalsIgnoreCase("Exit")) {
                System.out.println("Returning to the dashboard...");
                return;
            }

            try {
                int articleChoice = Integer.parseInt(input);
                if (articleChoice > 0 && articleChoice <= Math.min(articles.size(), 10)) {
                    // Display selected article content and add it to the viewed list
                    Article selectedArticle = articles.get(articleChoice - 1);
                    displayFullArticle(selectedArticle);
                    viewedArticles.add(selectedArticle);  // Add to viewed list
                } else {
                    System.out.println("Invalid choice. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number or 'Exit' to return to the dashboard.");
            }
        }
    }


    private void rateArticle(User user) {
        // Check if the user has viewed any articles
        if (viewedArticles.isEmpty()) {
            System.out.println("No articles have been viewed yet. Please view an article first.");
            return;
        }

        // Display the list of viewed articles
        System.out.println("\nArticle you have viewed:");
        for (int i = 0; i < viewedArticles.size(); i++) {
            Article article = viewedArticles.get(i);
            System.out.println((i + 1) + ". " + article.getTitle() + " [ID: " + article.getArticleId() + "]");
        }

        // Prompt the user to enter the ID of the article to rate
        System.out.print("Enter the article ID of the article you want to rate: ");
        String articleId = scanner.nextLine().trim();  // Read and trim input for comparison
        // Prompt the user to enter a rating
        System.out.print("Enter rating (1-5): ");
        int rating = readIntInput();

        // Validate the rating value
        if (rating < 1 || rating > 5) {
            System.out.println("Invalid rating. Please enter a value between 1 and 5.");
            return;
        }

        // Check if the article is in the viewedArticles list
        Article articleToRate = viewedArticles.stream()
                .filter(a -> a.getArticleId().equals(articleId))
                .findFirst()
                .orElse(null);

        if (articleToRate != null) {
            // Save the rating in the database
            dbHandler.saveRating(user.getUserId(), articleToRate.getArticleId(), rating);
            System.out.println("Thank you for rating the article!");
        } else {
            System.out.println("Article not found in viewed articles list. Please check the ID and try again.");
        }
    }

    // Utility method to safely read integer inputs
    private int readIntInput() {
        while (true) {
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a valid number: ");
            }
        }
    }

    // Displays the full content of an article.
    private void displayFullArticle(Article article) {
        System.out.println("\n--- Full Article ---");
        System.out.println("Category: " + article.getCategoryName());
        System.out.println("Title: " + article.getTitle());
        System.out.println("Author: " + article.getAuthor());
        System.out.println("Content: " + article.getContent());
        System.out.println("Published Date: " + article.getPublishedDate());
        System.out.println("Source: " + article.getSourceName());
        System.out.println("-------------------\n");
    }
}
