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
  var length = information.length;
  var i = 0;
  var j = 0;
  var string = "";
  var stringAppend = "";
  var suggestionClass = "";
  for(i = 0; i < length;i++){
      string = "";
      var object = information[i];
      for(var key in object){
          var info = object[key];
          if(key === "Subject"){
              if(info.type === "Person"){
                  string += "<h5>About "+ info["value"] + " : <br/>";
                  string += "Profession : ";
                  var role;
                  for(j = 0; j < info.roles.length-1;j++){
                      role = info.roles[j];
                      string += role["role"] + ",";
                  }
                  role = info.roles[info.roles.length-1];
                  string += role["role"] + ".<h5/>";
              }
              if(info.type === "Film"){
                  string += "<h5>About "+ info["value"] + " : <h5/>";
              }
              if(info.type === "Company"){
                  string += "<h5>About "+ info["value"] + " : <h5/>";
              }
          }else{
              var termLength = info.length;
              var tempString = "";
              var term;
              for(j = 0; j < termLength;j++){
                  term = info[j];
                  if(term["type"] === "Litteral"){
                      if(j === 0){
                          tempString += term["value"] + " ";
                      }else{
                          tempString += term["value"];
                      }
                  }else{
                      var onClickAction = "queryByUri('" + term["uri"] + "')";
                      if(term["type"] === "Film"){
                          suggestionClass = "film-suggestion";
                      }else if(term["type"] === "Company"){
                          suggestionClass = "company-suggestion";
                      }else{
                          suggestionClass = "person-suggestion";
                      }
                      tempString += "<div class='row-sm-2 search-suggestion " + suggestionClass + "' onclick=\"" + onClickAction + "\">" + term["value"] + "</div>";
                  }
              }
              if(term["type"] === "Litteral"){
                  string += "<h5>" + key + " : " + tempString + ". <h5/>"; 
              }else{
                  string += "<h5>" + key + " : <h5/><br/>" + tempString;
              }
          }
      }
      stringAppend += string + "<br/>";
  }
  component.append(stringAppend);
}

function appendInformation(component, title, content) {
  component.append("<div class='row-sm-2'>" + title + " : " + content + "</div>");
}
