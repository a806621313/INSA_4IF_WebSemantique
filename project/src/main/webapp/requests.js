
function loadSuggestions() {
  $.ajax({
    url: './ActionServlet',
    method: 'GET',
    data: {
      action: 'loadSuggestions'
    },
    dataType: 'json'
  }).done(function(data) {
    if (data.responseType === 'suggestions') {
      // Todo: display results in the side panel
      for (var i=0; i<data.responseContent.length; i++) {
        alert(data.responseContent[i].resourceName);
      }
    }
  });
}

function queryByName() {

}

function queryByUri(uri) {

}
