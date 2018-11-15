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
  })
});

function loadSuggestions() {
  $.ajax({
    url: "./ActionServlet",
    method: "GET",
    data: {
      action: "loadSuggestions"
    },
    dataType: "json",
    success: function(data) {
      if (data.responseType === "suggestions") {
        // Todo: display results in the side panel
        $("#query-result").html("");
        for (var i=0; i<data.responseContent.length; i++) {
          $("#query-result").append("<li>" + data.responseContent[i].resourceName + " " + data.responseContent[i].resourceType + "</li>");
        }
      }
    },
    error: function() {
      $("#query-result").html("");
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
    if (data.responseType === "queryResults") {
      $("#query-result").html("");
      for (var i=0; i<data.responseContent.length; i++) {
        $("#query-result").append("<li>" + data.responseContent[i].resourceName + " " + data.responseContent[i].resourceType + "</li>");
      }
    }
  });
}

function queryByUri(uri) {

}
