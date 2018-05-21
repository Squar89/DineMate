package FoodRecipeGenerator;

import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main
{
    private static final String searchUrl = "http://food2fork.com/api/search?key=8962e0ea976016c7e0a60cc9fa7afa1e";
    private static final String getUrl = "http://food2fork.com/api/get?key=8962e0ea976016c7e0a60cc9fa7afa1e";
    private static Statement databaseStatement;

    public static void main(String[] args)
    {
        int startingPage = 1;
        int pageCount = 1; /** Change pageCount to 1 when debugging! **/
        int pageNumber = 0;
        String recipeID = "";
        
        try (Connection databaseConnection = getDatabaseConnection())
        {
            databaseStatement = databaseConnection.createStatement();

            /* Iterating over "search" API calls. Each API call returns a list of 30 (or less) recipes */
            for (pageNumber = startingPage; pageNumber <= startingPage + pageCount - 1; pageNumber++)
            {
                String searchResponse = RestAPICaller.call(searchUrl + "&page=" + pageNumber);
                JSONObject searchJSONResponse = new JSONObject(searchResponse);
                int count = searchJSONResponse.getInt("count");
                JSONArray searchJSONArray = searchJSONResponse.getJSONArray("recipes");

                /* Iterating over recipes from a single "search" API call */
                for (int i = 0; i < count; i++) /** Change count to 1 when debugging! **/
                {
                    JSONObject searchJSONRecipe = searchJSONArray.getJSONObject(i);
                    recipeID = searchJSONRecipe.getString("recipe_id");
                    addRecipe(recipeID);
                    recipeID = "";
                }
            }
            pageNumber--;
        }
        catch (SQLException | URISyntaxException e)
        {
            e.printStackTrace();
        }
        finally
        {
            System.out.println("\n[SCRIPT INFO]\n");
            System.out.println("Started at the page: " + startingPage);
            System.out.println("Stopped at the page: " + pageNumber);
            if (recipeID.isEmpty())
            {
                System.out.println("Current recipe: none");
            }
            else
            {
                System.out.println("Current recipe: " + recipeID);
            }
        }
    }
    
    private static void addRecipe(String recipeID) throws SQLException
    {
        System.out.println("\n=====================================================================================");
        /* Checking if the recipe is already in the database */
        String checkRecipeSQL = String.format("SELECT Count(*) AS Count FROM dishes WHERE dish_id = '%s'", recipeID);
        ResultSet selectResult = databaseStatement.executeQuery(checkRecipeSQL);
        if (selectResult.next() && selectResult.getInt("Count") == 0)
        {
            String getResponse = RestAPICaller.call(getUrl + "&rId=" + recipeID);
            JSONObject getJSONRecipe = new JSONObject(getResponse).getJSONObject("recipe");

            /* Preparing the fields needed for the database. */
            String name = getJSONRecipe.getString("title");
            name = name.substring(0, 1).toUpperCase() + name.substring(1);
            JSONArray ingredientsJSONArray = getJSONRecipe.getJSONArray("ingredients");
            StringBuilder ingredientsBuilder = new StringBuilder();
            for (int j = 0; j < ingredientsJSONArray.length(); j++)
            {
                ingredientsBuilder.append(ingredientsJSONArray.getString(j).replaceAll("\n", "")).append("\n");
            }
            String ingredients = ingredientsBuilder.toString();
            String imageUrl = getJSONRecipe.getString("image_url");

            /* To get the recipe's "directions" field we need to scrap the source website ourselves due to API limitations */
            String publisherUrl = getJSONRecipe.getString("publisher_url");
            if (Publishers.dictionary().containsKey(publisherUrl))
            {
                String sourceUrl = getJSONRecipe.getString("source_url");
                String sourceHTML = getHTMLFromUrl(sourceUrl);
                Pattern pattern = Pattern.compile(Publishers.dictionary().get(publisherUrl));
                Matcher matcher = pattern.matcher(sourceHTML);
                if (matcher.find())
                {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(matcher.group(1));
                    while (matcher.find())
                    {
                        stringBuilder.append(" ").append(matcher.group(1));
                    }
                    String directions = stringBuilder.toString();
                    String insertRecipeSQL = String.format("INSERT INTO dishes VALUES ('%s', '%s', '%s', '%s', '%s', '%s')",
                                                           recipeID, name, ingredients, directions, imageUrl, publisherUrl);
                    databaseStatement.executeUpdate(insertRecipeSQL);
                    
                    System.out.println("Successfully added a recipe " + recipeID);
                    System.out.println("Name: " + name);
                    System.out.println("Ingredients: " + ingredients);
                    System.out.println("Directions: " + directions);
                    System.out.println("Image url: " + imageUrl);
                    System.out.println("Publisher url: " + publisherUrl);
                }
                else
                {
                    System.out.println("Regexp failed on recipe " + recipeID);
                }
            }
            else
            {
                System.out.println("Skipping a recipe " + recipeID + " because of its publisher " + publisherUrl);
            }
        }
        else
        {
            System.out.println("Skipping a recipe " + recipeID + " since it already exists in the database");
        }
    }
    
    private static String getHTMLFromUrl(String url)
    {
        String HTML = "";
        try
        {
            HttpClient client = HttpClientBuilder.create().build();
            HttpGet request = new HttpGet(url);
            HttpResponse response = client.execute(request);
            
            InputStream in = response.getEntity().getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while((line = reader.readLine()) != null)
            {
                stringBuilder.append(line);
            }
            in.close();
            HTML = stringBuilder.toString();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return HTML;
    }
    
    private static Connection getDatabaseConnection() throws URISyntaxException, SQLException {
        URI dbUri = new URI("postgres://pikbtrtfcvbary:1714f6eb4cbc70cb56a2be007106435db3de2f91a3d5b5346b37a7b434637c71@ec2-54-247-81-88.eu-west-1.compute.amazonaws.com:5432/de3q258qts38nm");

        String username = dbUri.getUserInfo().split(":")[0];
        String password = dbUri.getUserInfo().split(":")[1];
        String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath() + "?sslmode=require";

        return DriverManager.getConnection(dbUrl, username, password);
    }
}

