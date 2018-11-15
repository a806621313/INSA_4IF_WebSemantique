function appendErrorMessage(component, message) {
  component.append("<div class='row-sm-2'>" + message + "</div>");
}

function appendTitle(component, message) {
  component.append("<div class='row-sm-2'><h3>" + message + "</h3></div>");
}

function appendQuerySuggestion(component, suggestion) {
  component.append("<div class='row-sm-2 query-suggestion'>" + suggestion.resourceName + " " + suggestion.resourceType + "</div>");
}

function appendResourceInformation(component, information) {
  
}

function appendInformation(component, title, content) {
  component.append("<div class='row-sm-2'>" + title + " : " + content + "</div>");
}