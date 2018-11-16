$(document).ready(function() {
  // Initialization
  initGraph("#viewport");
  loadSuggestions();
  
  // Bind events to actions
  $("#search-button").on("click", function() {
    var query = $("#search-bar").val();
      if (query) {
        queryByName(query);
      } else {
        loadSuggestions();
      }
  });
  
  $("#search-bar").on("keyup", function(e) {
    e.preventDefault();
    if (e.keyCode === 13) {
      var query = $("#search-bar").val();
      if (query) {
        queryByName(query);
      } else {
        loadSuggestions();
      }
    }
  });
});

function loadSuggestions() {
  clearGraph();
  $.ajax({
    url: "./ActionServlet",
    method: "GET",
    data: {
      action: "loadSuggestions"
    },
    dataType: "json"
  }).done(function(data) {
    $("#query-results").html("");
    if (data.responseType === "suggestions") {
      appendTitle($("#query-results"), "Search suggestions");
      for (var i=0; i<data.responseContent.length; i++) {
        appendQuerySuggestion($("#query-results"), data.responseContent[i]);
      }
    } else {
      appendErrorMessage($("#query-results"), "Service unavailable");
    }
  });
}

function queryByName(name) {
  $.ajax({
    url: "./ActionServlet",
    method: "GET",
    data: {
      action: "queryByName",
      query: name
    },
    dataType: "json"
  }).done(function (data) {
    $("#query-results").html("");
    clearGraph();
    if (data.responseType === "queryResults") {
      appendTitle($("#query-results"), "Search results");
      var numberOfFilms = 0;
      var numberOfCompanies = 0;
      var numberOfPersons = 0;
      var numberOfResults = data.responseContent.length;
      for (var i=0; i<data.responseContent.length; i++) {
        var result = data.responseContent[i];
        appendQuerySuggestion($("#query-results"), result);
        if (result.resourceType === "film") {
          numberOfFilms++;
        }
        if (result.resourceType === "company") {
          numberOfCompanies++;
        }
        if (result.resourceType === "person") {
          numberOfPersons++;
        }
      }
      var resultAnalysis = {
        nodes: {
          nresults: {name: "Results (" + numberOfResults + ")", color: "#171a1d", uri: "", radius: 0},
          nfilms: {name: "Films (" + numberOfFilms + ")", color: "#004085", uri: "", radius: 0},
          ncompanies: {name: "Companies (" + numberOfCompanies +")", color: "#155724", uri: "", radius: 0},
          npersons: {name: "Persons (" + numberOfPersons + ")", color: "#822224", uri: "", radius: 0}
        },
        edges: {
          nresults: {
            nfilms: {type: ""},
            ncompanies: {type: ""},
            npersons: {type: ""}
          }
        }
      };
      updateGraph(resultAnalysis);
    } else if (data.responseType === "noResult") {
      appendErrorMessage($("#query-results"), "Sorry, no results found :(");
    } else {
      appendErrorMessage($("#query-results"), "Service unavailable");
    }
  });
}

function queryByUri(uri) {
  
  alert("URI request : " + uri);
  
  $.ajax({
    url: "./ActionServlet",
    method: "GET",
    data: {
      action: "queryByUri",
      query: uri
    },
    dataType: "json"
  }).done(function (data) {
    $("#query-results").html("");
    clearGraph();
    if (data.responseType === "resourceInfoGraph") {
      updateGraph(data.responseContent.resourceGraph);
      appendResourceInformation($("#query-results"), data.responseContent.resourceInfo);
    } else {
      appendErrorMessage($("#query-results"), "Service unavailable :(");
    }
  });
}
