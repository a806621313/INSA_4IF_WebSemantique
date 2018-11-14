var sys;

$(document).ready(function() {
  // Initialize the particle system
  sys = arbor.ParticleSystem(200, 800, 0.5);
  sys.parameters({gravity:true});
  sys.renderer = renderer("#viewport");
  
  // Load search suggestions
  loadSuggestions();
  
  $("#searchButton").on("click", queryByName);
})
