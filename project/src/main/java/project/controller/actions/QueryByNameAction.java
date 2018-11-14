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

public class QueryByNameAction implements Action {
  
  private final static Gson gson = new GsonBuilder().setPrettyPrinting().create();

  @Override
  public void execute(HttpServletRequest request, HttpServletResponse response) {
    String query = request.getParameter("query");
    
    Map<String, String> companies = ResourceServices.matchCompaniesByName(query);
    Map<String, String> films = ResourceServices.matchFilmsByName(query);
    Map<String, String> persons = ResourceServices.matchPersonsByName(query);
    
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    
    int numberOfResults = companies.size() + films.size() + persons.size();
    
    try {
      JsonObject container = new JsonObject();
      if(numberOfResults > 0) {
        container.addProperty("responseType", "queryResults");
      } else {
        container.addProperty("responseType", "noResult");
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
