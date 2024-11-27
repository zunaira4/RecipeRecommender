import tech.tablesaw.api.Row;
import tech.tablesaw.api.Table;
import tech.tablesaw.io.csv.CsvReadOptions;
import utility.Input;
public class Menu {

    private static boolean masterLoop;
    private static boolean loop;
    private static int selection;
    private static final Search search = new Search(datasetReader("src/main/resources/dataset_cleaned.csv"));

    public static Table datasetReader(String filepath) {
        CsvReadOptions options = CsvReadOptions.builder(filepath)
                .maxCharsPerColumn(13000)
                .build();
        return Table.read().csv(options);
    }


    public static void mainMenu() throws Exception {
        masterLoop = true;
        System.out.println(View.centerAlign("Recipe Recommender", 100));
        while (masterLoop) {
            System.out.printf(
                    View.centerAlign("Main Menu", 100) + "\n\n" +
                            "1. Search Recipes\n2. Surprise me\n3. Exit\n"
            );
            selection = Input.intInput("Select from the menu:");
            loop = true;
            switch (selection) {
                case 1:
                    searchMenu();
                    break;
                case 2:
                    Row surprise = search.surpriseMe();
                    View.fullRecipe(surprise);
                    break;
                case 3:
                    System.out.println(View.centerAlign("Goodbye! Exiting...", 100));
                    masterLoop = false;
                    break;
            }
        }
    }

    private static void searchSelector(int searchMethod) throws Exception {
        Table filteredDataset;
        switch (searchMethod) {
            case 1:
                filteredDataset = search.byIngredients();
                break;
            case 2:
                filteredDataset = search.byName();
                break;
            case 3:
                filteredDataset = search.byKeywords();
                break;
            default:
                throw new Exception();
        }
        int recipeNo;
        while (loop) {
            System.out.println(View.centerAlign("Found Results", 100));
            View.recipesList(filteredDataset);
            recipeNo = Input.intInput("Enter -1 to go back or select the recipe no. to view it.");
            if (recipeNo == -1) {
                break;
            }
            View.fullRecipe(filteredDataset.row(recipeNo));
            selection = Input.intInput("1. Back to the list\n2. Back to the search menu");
            switch (selection) {
                case 1:
                    System.out.println("Going back to the recipes list...");
                    break;
                case 2:
                    System.out.println("Going back to the Search Menu...");
                    loop = false;
                    break;
            }
        }
    }

    private static void searchMenu() throws Exception {
        masterLoop = true;
        while (masterLoop) {
            System.out.print("1. Search by Ingredients\n2. Search by Recipe Names\n3. Search by Keywords\n4. Back\n");
            selection = Input.intInput("Select from the menu:");
            Table filteredDataset;
            int recipeNo;
            loop = true;
            switch (selection) {
                case 1:
                    searchSelector(1);
                    break;
                case 2:
                    searchSelector(2);
                    break;
                case 3:
                    searchSelector(3);
                    break;
                case 4:
                    System.out.println("Going back to the main menu...");
                    return;
            }
        }
    }
}