
function loadSuggestions() {

}

function queryByName() {
  var action = "queryByName";
  var query = $('#research-bar').val();
  
  $("search-button").on("click", function(){
    $.ajax({
    url: './ActionServlet',
    method: 'GET',
    data: {
      action: action,
      query: query
    },
    dataType: 'json'
  }).done(function (data) {
    if (data.responseType === "queryResult") {
      for (content in data.responseContent) {
        $('#query-result').append("<li>" + content.ressourceName + " " + content.ressourceType + "</li>");
      }
    }
  });
  });
}

function queryByUri(uri) {

}
