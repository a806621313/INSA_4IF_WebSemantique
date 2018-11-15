$(document).ready(function() {
  // Initialization
  initGraph("#viewport");
  loadSuggestions();
  
  // Bind events to actions
  $("#search-button").on("click", function() {
    queryByName($("#search-bar").val());
  });
  
  $("#search-bar").on("keyup", function(e) {
    e.preventDefault();
    if (e.keyCode === 13) {
      queryByName($("#search-bar").val());
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
      for (var i=0; i<data.responseContent.length; i++) {
        appendQuerySuggestion($("#query-results"), data.responseContent[i]);
      }
    } else if (data.responseType === "noResult") {
      appendErrorMessage($("#query-results"), "Sorry, no results found :(");
    } else {
      appendErrorMessage($("#query-results"), "Service unavailable");
    }
  });
}

function queryByUri(uri) {
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
