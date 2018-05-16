package FoodRecipeGenerator;

import org.json.JSONArray;
import org.json.JSONObject;

public class Main
{
    public static void main(String[] args)
    {
        // String url = "https://jsonplaceholder.typicode.com/posts/1";
        String searchUrl = "http://food2fork.com/api/search?key=8962e0ea976016c7e0a60cc9fa7afa1e";
        String getUrl = "http://food2fork.com/api/get?key=8962e0ea976016c7e0a60cc9fa7afa1e";
        
        int startingPage = 1;
        int pageCount = 1;
        
        RestAPICaller restAPICaller = new RestAPICaller();
        
        for (int i = startingPage; i <= startingPage + pageCount - 1; i++)
        {
            String searchResponse = restAPICaller.call(searchUrl + "&page=" + i);
            JSONObject jsonResponse = new JSONObject(searchResponse);
            int count = jsonResponse.getInt("count");
            JSONArray jsonArray = jsonResponse.getJSONArray("recipes");
            
            for (int j = 0; j < 1; j++)
            {
                JSONObject jsonRecipe = jsonArray.getJSONObject(j);
                String recipeId = jsonRecipe.getString("recipe_id");
                
                // Checking if the recipe is already in the database
                if (true)
                {
                    String getResponse = restAPICaller.call(getUrl + "&rId=" + recipeId);
                    System.out.println(getResponse);
                }
            }
        }
    }
}

