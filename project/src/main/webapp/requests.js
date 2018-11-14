
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
            for (var suggestion in data.responseContent) {
                alert(suggestion);
            }
        }
    });
}

function queryByName() {
    
}

function queryByUri(uri) {
    
}
