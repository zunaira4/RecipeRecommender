package recipeRecommender;

import tech.tablesaw.api.Row;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.io.csv.CsvReadOptions;

public class View {
    public static RecipesList history = new RecipesList(10);
    public static RecipesList saved = new RecipesList(20);
    public static Reviews reviews = new Reviews();
    public static Ratings ratings = new Ratings();


    public static Table datasetReader(String filepath) {
        CsvReadOptions options = CsvReadOptions.builder(filepath)
                .maxCharsPerColumn(13000)
                .build();
        try {
            return Table.read().csv(options);
        } catch (tech.tablesaw.io.AddCellToColumnException e) {
            System.err.println("Error reading dataset: " + e.getMessage());
            return null; // error handling for odd entries in the dataset
        }
    }

    public static String centerAlign(String text, int width) {
        int padding = (width - text.length()) / 2;
        return " ".repeat(Math.max(padding, 0)) + text + " ".repeat(Math.max(padding, 0));
    }

    public static void printHeader(String title, String subtitle, int WIDTH) {
        System.out.println(centerAlign(title, WIDTH));
        System.out.println(centerAlign(subtitle, WIDTH));
    }

    public static void viewRecipe(Row recipe) {

        int id = recipe.getInt("id");

        String name = recipe.getText("name").trim();
        String desc = recipe.getText("description").trim();

        int servings = recipe.getInt("servings");

        String servingSize = recipe.getText("serving_size").trim();

        String ingredients = recipe.getText("ingredients_raw").trim();

        String steps = recipe.getText("steps").trim();

        System.out.printf(
                centerAlign(String.valueOf(id), 100) + "\n" +
                centerAlign(name, 100) + "\n\n" +
                "Description:\n" +
                        "%s\n\n" +
                        "Servings: %s\n" +
                        "Serving Size: %s\n\n" +
                        "Ingredients:\n" +
                        "%s\n\n" +
                        "Steps:\n" +
                        "%s\n\n",
                desc, servings, servingSize, ingredients, steps
        );
        history.add(recipe);
        if (Input.stringInput("Add to saved recipes? (y/n)").equalsIgnoreCase("y")) {
            saved.add(recipe);
            System.out.println("Recipe saved.");
        } else {
            System.out.println("Recipe not saved.");
        }
        if (Input.stringInput("Do you want to rate this recipe? (y/n)").equalsIgnoreCase("y")) {
            int rating = Input.intInput("Rate this recipe (1-5):");
            ratings.addRating(name, rating);
        } else {
            System.out.println("Rating skipped.");
        }

        if (Input.stringInput("Do you want to review this recipe? (y/n)").equalsIgnoreCase("y")) {
            String review = Input.stringInput("Write a review for this recipe:");
            reviews.addReview(name, review);
        } else {
            System.out.println("Review skipped.");
        }
    }

    public static void viewRecipes(Table dataset) {
        Column<String> names = dataset.textColumn("name");
        int numberOfRecipes = names.size();
        System.out.println(centerAlign("\nFound " + numberOfRecipes + " recipes\n", 100));

        int idx = 0;
        int count = 1;
        for (String name : names) {
            System.out.printf("%3d %s %n", idx, name.trim());
            idx++;
            count++;
            if (count > 20) {
                count = 1;
                if (Input.stringInput("View more recipes? (y/n)").equalsIgnoreCase("n")) {
                    break;
                }
            }
        }
    }
}