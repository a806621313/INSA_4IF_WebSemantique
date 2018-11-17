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
  
  private final static Gson gson = new GsonBuilder().setPrettyPrinting().create();

  @Override
  public void execute(HttpServletRequest request, HttpServletResponse response) {
    Map<String, String> companies = ResourceServices.getRandomCompanies();
    Map<String, String> films = ResourceServices.getRandomFilms();
    Map<String, String> persons = ResourceServices.getRandomPersons();
    
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    
    try {
      JsonObject container = new JsonObject();
      if (companies.size() + films.size() + persons.size() > 0) {
        container.addProperty("responseType", "suggestions");
      } else {
        container.addProperty("responseType", "error");
      }
      JsonArray suggestions = new JsonArray();
      for (Map.Entry<String, String> resource : companies.entrySet()) {
        JsonObject suggestion = new JsonObject();
        suggestion.addProperty("resourceName", resource.getKey());
        suggestion.addProperty("resourceUri", resource.getValue());
        suggestion.addProperty("resourceType", "company");
        suggestions.add(suggestion);
      }
      for (Map.Entry<String, String> resource : films.entrySet()) {
        JsonObject suggestion = new JsonObject();
        suggestion.addProperty("resourceName", resource.getKey());
        suggestion.addProperty("resourceUri", resource.getValue());
        suggestion.addProperty("resourceType", "film");
        suggestions.add(suggestion);
      }
      for (Map.Entry<String, String> resource : persons.entrySet()) {
        JsonObject suggestion = new JsonObject();
        suggestion.addProperty("resourceName", resource.getKey());
        suggestion.addProperty("resourceUri", resource.getValue());
        suggestion.addProperty("resourceType", "person");
        suggestions.add(suggestion);
      }
      container.add("responseContent", suggestions);
      response.getWriter().println(gson.toJson(container));
    } catch (IOException ex) {
      Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
    }
  }

}
