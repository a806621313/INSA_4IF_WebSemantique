/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import com.google.gson.JsonObject;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author YANG ZHENYU
 */
@WebServlet(name = "ServiceWebSemanServlet", urlPatterns = {"/ServiceWebSeman"})
public class ServiceWebSemanServlet extends HttpServlet {
    static int errorInternal = 0;
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {

            String sujet = null;
            String queryType = null;
            String objet = null;
            String sParameter = request.getParameter("Sujet");
            String tParameter = request.getParameter("Type");
            String oParameter = request.getParameter("Objet");
            if (oParameter != null) {
                objet = oParameter;
            }
            if (sParameter != null) {
                sujet = sParameter;
            }
            if (tParameter != null) {
                queryType = tParameter;
            }

            //JsonObject container = new JsonObject();

            ServiceWebSeman service = new ServiceWebSeman();

            boolean serviceCalled = true;

            if ("Select".equals(queryType)) {
                service.ExecuteSelectJson(sujet,objet,response.getOutputStream());
            }  else if ("Ask".equals(queryType)){

            }else {

                serviceCalled = false;
            }

            if (serviceCalled) {
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Subject Error");
            }
            if(errorInternal == 1){
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Service Exception: InternalError");
                this.getServletContext().log("Service Exception in " + this.getClass().getName());
            }
        }  catch (Exception ex) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Service Exception: " + ex.getMessage());
            this.getServletContext().log("Service Exception in " + this.getClass().getName(), ex);
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Service Web Semantique Servlet";
    }

}

