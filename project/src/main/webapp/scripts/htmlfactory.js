function appendErrorMessage(component, message) {
  component.append("<div class='row-sm-2 side-title'>" + message + "</div>");
}

function appendTitle(component, message) {
  component.append("<div class='row-sm-2 side-title'>" + message + "</div>");
}

function appendQuerySuggestion(component, suggestion) {
  var suggestionClass;
  if (suggestion.resourceType === "company") {
    suggestionClass = "company-suggestion";
  }
  if (suggestion.resourceType === "film") {
    suggestionClass = "film-suggestion";
  }
  if (suggestion.resourceType === "person") {
    suggestionClass = "person-suggestion";
  }
  var onClickAction = "queryByUri('" + suggestion.resourceUri + "')";
  component.append("<div class='row-sm-2 search-suggestion " + suggestionClass + "' onclick=\"" + onClickAction + "\">" + suggestion.resourceName + "</div>");
}

function appendResourceInformation(component, information) {
  
}

function appendInformation(component, title, content) {
  component.append("<div class='row-sm-2'>" + title + " : " + content + "</div>");
}
