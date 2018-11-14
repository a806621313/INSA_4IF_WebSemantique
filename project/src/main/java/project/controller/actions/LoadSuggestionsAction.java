package project.controller.actions;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import project.controller.services.ResourceServices;

public class LoadSuggestionsAction implements Action {
  
  private static Gson gson = new GsonBuilder().setPrettyPrinting().create();

  @Override
  public void execute(HttpServletRequest request, HttpServletResponse response) {
    Map<String, String> companies = null;//ResourceServices.getRandomCompanies();
    Map<String, String> films = null;//ResourceServices.getRandomFilms();
    Map<String, String> persons = null;//ResourceServices.getRandomPersons();
    
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    
    try {
      JsonObject container = new JsonObject();
      container.addProperty("responseType", "suggestions");
      JsonArray suggestions = new JsonArray();
      for (Map.Entry<String, String> resource : companies.entrySet()) {
        JsonObject suggestion = new JsonObject();
        suggestion.addProperty("", "");
        suggestion.addProperty("", "");
        suggestion.addProperty("", "");
        suggestions.add(suggestion);
      }
      container.add("responseContent", suggestions);
      response.getWriter().println(gson.toJson(container));
    } catch (IOException ex) {
      Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
    }
  }

}
