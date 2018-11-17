package project.controller.actions;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import project.controller.services.SparqlServices;

public class QueryByUriAction implements Action {

  @Override
  public void execute(HttpServletRequest request, HttpServletResponse response){
    //throw new UnsupportedOperationException("Not supported yet.");
        //Get Parameter
            String uri = null;
            String queryType = null;
            String sParameter = request.getParameter("query");
            String tParameter = request.getParameter("type");
            if (sParameter != null) {
                uri = sParameter;
            }
            if (tParameter != null) {
                queryType = tParameter;
            }
        
        //Determin the query type
        if ("Select".equals(queryType)) {
            PrintWriter out = null;
            try {
               out = response.getWriter();
            } catch (IOException ex) {
                Logger.getLogger(QueryByUriAction.class.getName()).log(Level.SEVERE, null, ex);
           }
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                JsonObject container = null;
                if(SparqlServices.isFilm(uri)){
                   container = createFilmResponse(uri);
                }else if(SparqlServices.isActor(uri)||SparqlServices.isFilmDirector(uri)||SparqlServices.isFilmProducer(uri)||SparqlServices.isFilmMusicComposer(uri)){
                   container = createPersonResponse(uri);
                }else if(SparqlServices.isCompany(uri)){
                   container = createCompanyResponse(uri);
                }else {
                    System.out.println("Type error");
                    container = new JsonObject();
                    container.addProperty("Error", "Subject type invalid.");
                }
               out.println(gson.toJson(container));
            if(out != null){
                out.close();
            }
        }  else if ("Ask".equals(queryType)){

        }
  }
  
  private JsonObject createCompanyResponse(String uri){
      JsonObject container = new JsonObject();
        container.addProperty("responseType","resourceInfoGraph");
        JsonArray infos = new JsonArray();
        JsonObject nodes = new JsonObject();
        JsonObject edges = new JsonObject();
        JsonObject edgeSubject = new JsonObject();
        JsonObject responseContent = new JsonObject();
        JsonObject resourceGraph = new JsonObject();
        int index = 0;
        index = SparqlServices.objectName(uri, infos, nodes, index);
        index = SparqlServices.numberOfFilmDistributedOrProducedCompany(uri, infos, index);
        index = SparqlServices.numberOfEmployeesCompany(uri, infos, index);
        index = SparqlServices.majorFilmsDistributedOrProducedCompany(uri, infos, nodes, edges, edgeSubject, index);
        index = SparqlServices.addDefaultNode(uri, nodes, edgeSubject, index);
        resourceGraph.add("nodes", nodes);
        edges.add("node0",edgeSubject);
        resourceGraph.add("edges", edges);
        responseContent.add("resourceInfo", infos);
        responseContent.add("resourceGraph", resourceGraph);
        container.add("responseContent", responseContent);
        return container;
  }
  
  private JsonObject createFilmResponse(String uri){
       JsonObject container = new JsonObject();
        container.addProperty("responseType","resourceInfoGraph");
        JsonArray infos = new JsonArray();
        JsonObject nodes = new JsonObject();
        JsonObject edges = new JsonObject();
        JsonObject edgeSubject = new JsonObject();
        JsonObject responseContent = new JsonObject();
        JsonObject resourceGraph = new JsonObject();
        int index = 0;
        index = SparqlServices.objectName(uri, infos, nodes, index);
        index = SparqlServices.filmBudget(uri, infos, index);
        index = SparqlServices.filmBoxOffice(uri, infos, index);
        index = SparqlServices.filmRunTime(uri, infos, index);
        index = SparqlServices.filmStarring(uri, infos, nodes, edges, edgeSubject, index);
        index = SparqlServices.addDefaultNode(uri, nodes, edgeSubject, index);
        resourceGraph.add("nodes", nodes);
        edges.add("node0",edgeSubject);
        resourceGraph.add("edges", edges);
        responseContent.add("resourceInfo", infos);
        responseContent.add("resourceGraph", resourceGraph);
        container.add("responseContent", responseContent);
        return container;
  }
  
  private JsonObject createPersonResponse(String uri){
      JsonObject container = new JsonObject();
      container.addProperty("responseType","resourceInfoGraph");
      JsonArray infos = new JsonArray();
      JsonObject nodes = new JsonObject();
      JsonObject edges = new JsonObject();
      JsonObject edgeSubject = new JsonObject();
      JsonObject responseContent = new JsonObject();
      JsonObject resourceGraph = new JsonObject();
      int index = 0;
      boolean isActor = SparqlServices.isActor(uri);
      boolean isProducer = SparqlServices.isFilmProducer(uri);
      boolean isDirector = SparqlServices.isFilmDirector(uri);
      boolean isMusicComposer = SparqlServices.isFilmMusicComposer(uri);
      index = SparqlServices.personName(uri, infos, nodes, index, isActor, isDirector, isMusicComposer, isProducer);
      if(isActor){
          index = addActorResponse(uri,infos,nodes,edges,edgeSubject,index);
      }
      if(isProducer){
          index = addProducerResponse(uri,infos,nodes,edges,edgeSubject,index);
      }
      if(isDirector){
          index = addDirectorResponse(uri,infos,nodes,edges,edgeSubject,index);
      }
      if(isMusicComposer){
          index = addMusicComposerResponse(uri,infos,nodes,edges,edgeSubject,index);
      }
      index = SparqlServices.addDefaultNode(uri, nodes, edgeSubject, index);
      resourceGraph.add("nodes", nodes);
        edges.add("node0",edgeSubject);
        resourceGraph.add("edges", edges);
        responseContent.add("resourceInfo", infos);
        responseContent.add("resourceGraph", resourceGraph);
        container.add("responseContent", responseContent);
        return container;
  }
  
  private int addActorResponse(String uri, JsonArray infos, JsonObject nodes, JsonObject edges, JsonObject edgeSubject, int index){
        index = SparqlServices.actorFrequentlyWorkWithActor(uri, infos, nodes, edges, edgeSubject, index);
        index = SparqlServices.famousFilmActor(uri, infos, nodes, edges, edgeSubject, index);
        index = SparqlServices.studioFrequentlyWorkWithActor(uri, infos, nodes, edges, edgeSubject, index);
        index = SparqlServices.producerFrequentlyWorkWithActor(uri, infos, nodes, edges, edgeSubject, index);
        index = SparqlServices.numberOfStarredFilmActor(uri, infos, index);
        return index;      
  }
  
  private int addProducerResponse(String uri, JsonArray infos, JsonObject nodes, JsonObject edges, JsonObject edgeSubject, int index){
        index = SparqlServices.directorFrequentlyWorkWithProducer(uri, infos, nodes, edges, edgeSubject, index);
        index = SparqlServices.musicComposerFrequentlyWorkWithProducer(uri, infos, nodes, edges, edgeSubject, index);
        index = SparqlServices.actorFrequentlyWorkWithProducer(uri, infos, nodes, edges, edgeSubject, index);
        index = SparqlServices.studioFrequentlyWorkWithProducer(uri, infos, nodes, edges, edgeSubject, index);
        index = SparqlServices.majorFilmProducer(uri, infos, nodes, edges, edgeSubject, index);
        index = SparqlServices.numberOfProducedFilmProducer(uri, infos, index);
        return index;      
  }
  
  private int addMusicComposerResponse(String uri, JsonArray infos, JsonObject nodes, JsonObject edges, JsonObject edgeSubject, int index){
        index = SparqlServices.directorFrequentlyWorkWithMusicComposer(uri, infos, nodes, edges, edgeSubject, index);
        index = SparqlServices.producerFrequentlyWorkWithMusicComposer(uri, infos, nodes, edges, edgeSubject, index);
        index = SparqlServices.studioFrequentlyWorkWithMusicComposer(uri, infos, nodes, edges, edgeSubject, index);
        index = SparqlServices.majorFilmMusicComposer(uri, infos, nodes, edges, edgeSubject, index);
        index = SparqlServices.numberOfComposedFilmComposer(uri, infos, index);
        return index;      
  }
  
  private int addDirectorResponse(String uri, JsonArray infos, JsonObject nodes, JsonObject edges, JsonObject edgeSubject, int index){
        index = SparqlServices.actorFrequentlyWorkWithDirector(uri, infos, nodes, edges, edgeSubject, index);
        index = SparqlServices.musicComposerFrequentlyWorkWithDirector(uri, infos, nodes, edges, edgeSubject, index);
        index = SparqlServices.producerFrequentlyWorkWithDirector(uri, infos, nodes, edges, edgeSubject, index);
        index = SparqlServices.producerFrequentlyWorkWithDirector(uri, infos, nodes, edges, edgeSubject, index);
        index = SparqlServices.majorFilmDirector(uri, infos, nodes, edges, edgeSubject, index);
        index = SparqlServices.numberOfDirectedFilmDirector(uri, infos, index);
        return index;      
  }
  
}

