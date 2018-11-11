function renderer(canvasId) {
  var canvas = $(canvasId).get(0)
  var context = canvas.getContext("2d");
  var particleSystem
  
  var renderer = {
    init:function(system) {
      particleSystem = system
      particleSystem.screenSize(canvas.width, canvas.height) 
      particleSystem.screenPadding(80)
      
      $(canvas).on('mousedown', function(e) {
        var pos = $(canvas).offset();
        mousePoint = arbor.Point(e.pageX-pos.left, e.pageY-pos.top)
        nearest = particleSystem.nearest(mousePoint);
        if(nearest.node.data.uri && nearest.distance < nearest.node.data.radius) {
          queryResourceByUri(nearest.node.data.uri)
        }
      })
    },
    
    redraw:function() {
      context.fillStyle = "white"
      context.fillRect(0, 0, canvas.width, canvas.height)
      
      // edge: {source:Node, target:Node, length:#, data:{}}
      // pt1:  {x:#, y:#}  source position in screen coords
      // pt2:  {x:#, y:#}  target position in screen coords
      particleSystem.eachEdge(function(edge, pt1, pt2) {
        context.strokeStyle = "#990000"
        context.lineWidth = 2
        context.beginPath()
        context.moveTo(pt1.x, pt1.y)
        context.lineTo(pt2.x, pt2.y)
        context.stroke()
      })

      // node: {mass:#, p:{x,y}, name:"", data:{}}
      // pt:   {x:#, y:#}  node position in screen coords
      particleSystem.eachNode(function(node, pt) {
        var w = node.data.radius
        context.fillStyle = "#000011"
        context.fillRect(pt.x-w, pt.y-w, 2*w, 2*w, {fill:context.fillStyle})
      })    			
    },
  }
  return renderer
}
