package project.controller;

import java.io.IOException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import project.controller.actions.Action;
import project.controller.actions.LoadSuggestionsAction;
import project.controller.actions.QueryByNameAction;
import project.controller.actions.QueryByUriAction;
import project.controller.services.ResourceServices;

@WebServlet(name = "ActionServlet", urlPatterns = {"/ActionServlet"})
public class ActionServlet extends HttpServlet {
  
  /**
   * Initializes the Servlet before it accepts requests.
   * 
   * @param config the servlet's configuration and initialization parameters
   * @throws ServletException if a servlet-specific error occurs
   */
  @Override
  public void init(ServletConfig config) throws ServletException {
    ResourceServices.init();
  }
  
  /**
   * Handles the HTTP GET method.
   *
   * @param request servlet request
   * @param response servlet response
   * @throws ServletException if a servlet-specific error occurs
   * @throws IOException if an I/O error occurs
   */
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
    try {
      String action = request.getParameter("action");
      if ("load-suggestions".equals(action)) {
        Action servletAction = new LoadSuggestionsAction();
        servletAction.execute(request, response);
      } else if ("query-by-name".equals(action)) {
        Action servletAction = new QueryByNameAction();
        servletAction.execute(request, response);
      } else if ("query-by-uri".equals(action)) {
        Action servletAction = new QueryByUriAction();
        servletAction.execute(request, response);
      } else {
        response.sendError(HttpServletResponse.SC_BAD_REQUEST);
      }
    } catch (Exception ex) {
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
      getServletContext().log("Exception in " + getClass().getName() + " :\n" + ex.getMessage());
    }
  }

  /**
   * Returns a short description of the servlet.
   * 
   * @return a String containing servlet description
   */
  @Override
  public String getServletInfo() {
    return "IF-4-WS-Project Servlet";
  }

}
