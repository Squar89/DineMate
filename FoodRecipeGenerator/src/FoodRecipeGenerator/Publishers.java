package FoodRecipeGenerator;

import java.util.HashMap;

class Publishers
{
    // Match anything: "(.*)"
    
    private static final HashMap<String, String> dictionary;
    static
    {
        dictionary = new HashMap<String, String>();
        //dictionary.put("http://www.101cookbooks.com", "627");
        dictionary.put("http://allrecipes.com", "<span class=\"recipe-directions__list--item\">\\s*(.+?)\\s*</span>");
        //dictionary.put("http://www.aspicyperspective.com", "344");
        //dictionary.put("http://backtoherroots.com/", "287");
        //dictionary.put("http://www.bbc.co.uk/food", "594");
        dictionary.put("http://www.bbcgoodfood.com", "<li class=\"method__ite(?:m*?|m.+?)\" itemprop=\"recipeInstructions\"><span>(.+?)\\s*<\\/span><\\/li>");
        //dictionary.put("http://www.biggirlssmallkitchen.com/", "290");
        //dictionary.put("http://www.bonappetit.com", "5045");
        //dictionary.put("http://www.bunkycooks.com", "208");
        //dictionary.put("http://www.chow.com", "2702");
        dictionary.put("http://closetcooking.com", "<li itemprop=\"recipeInstructions\">(.+?)</li>");
        
        /* This one is quite problematic so just skip it */
        // dictionary.put("http://cookieandkate.com", "447");
        
        //dictionary.put("http://www.cookincanuck.com", "585");
        //dictionary.put("http://www.cookstr.com", "8568");
        //dictionary.put("http://delishhh.com", "138");
        //dictionary.put("http://www.joanne-eatswellwithothers.com", "121");
        //dictionary.put("http://www.elanaspantry.com", "218");
        //dictionary.put("http://www.epicurious.com", "4775");
        //dictionary.put("http://www.finedininglovers.com", "640");
        //dictionary.put("http://www.foodnetwork.com", "21");
        //dictionary.put("http://www.foodrepublic.com", "16");
        //dictionary.put("http://framedcooks.com", "904");
        //dictionary.put("http://www.healthy-delicious.com", "215");
        //dictionary.put("http://homesicktexan.blogspot.com", "224");
        //dictionary.put("http://www.jamieoliver.com", "1544");
        //dictionary.put("http://foodandspice.blogspot.com", "608");
        //dictionary.put("http://www.mybakingaddiction.com", "407");
        //dictionary.put("http://naturallyella.com", "358");
        //dictionary.put("http://paninihappy.com/", "46");

        /* This one is quite problematic so just skip it */
        //dictionary.put("http://www.pastryaffair.com", "294");
        
        //dictionary.put("http://www.pbs.org", "2071");
        //dictionary.put("http://picky-palate.com", "38");
        //dictionary.put("http://www.pillsburybaking.com", "1270");
        //dictionary.put("http://realsimple.com", "3052");
        //dictionary.put("http://www.seriouseats.com/", "185");
        //dictionary.put("http://simplyrecipes.com", "1348");
        //dictionary.put("http://www.smittenkitchen.com", "737");
        //dictionary.put("http://www.steamykitchen.com", "237");
        //dictionary.put("http://tastykitchen.com", "55229");
        dictionary.put("http://thepioneerwoman.com", "<span itemprop=\"recipeInstructions\">(.+?)</span>");
        //dictionary.put("http://www.twopeasandtheirpod.com", "811");
        //dictionary.put("http://www.thevintagemixer.com/", "9");
        
        /* This one is duplicated but currently it's not a problem */
        //dictionary.put("43", "");
    }

    static HashMap<String, String> dictionary()
    {
        return dictionary;
    }
}
