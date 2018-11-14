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
    
    Map<String, String> resources = ResourceServices.matchResourcesByName(query);
    
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    
    try {
      JsonObject container = new JsonObject();
      if(resources.size() > 0) {
        container.addProperty("responseType", "queryResults");
      } else {
        container.addProperty("responseType", "noResult");
      }
      JsonArray suggestions = new JsonArray();
      for (Map.Entry<String, String> res : resources.entrySet()) {
        JsonObject suggestion = new JsonObject();
        suggestion.addProperty("resourceName", res.getKey());
        suggestion.addProperty("resourceUri", res.getValue());
        suggestion.addProperty("resourceType", ResourceServices.getCategory(res.getValue()));
        suggestions.add(suggestion);
      }
      container.add("responseContent", suggestions);
      response.getWriter().println(gson.toJson(container));
    } catch (IOException ex) {
      Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
    }
  }
  
}
