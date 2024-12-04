package newscollector;

// Main class for the News Collector application.
// This class initializes the necessary components and launches the Command Line Interface (CLI) for user interaction.

public class NewsCollectorApp {
    public static void main(String[] args) {
        // This component is responsible for fetching articles from external APIs or other sources.
        ArticleFetcher articleFetcher = new ArticleFetcher();

        // This component handles all interactions with the database, including saving and retrieving data.
        DatabaseHandler dbHandler = new DatabaseHandler();

        // This component generates personalized article recommendations based on user preferences and reading history.
        RecommendationEngine recommendationEngine = new RecommendationEngine(dbHandler, articleFetcher);

        // The Interface class manages user interactions and integrates with the backend components.
        Interface cliInterface = new Interface(dbHandler, recommendationEngine, articleFetcher);

        // The start method begins the application's user interface loop, allowing users to create accounts, log in, view articles, and receive recommendations.
        cliInterface.start();
    }
}
