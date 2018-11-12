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
      context.fillStyle = 'white'
      context.fillRect(0, 0, canvas.width, canvas.height)
      
      // edge: {source:Node, target:Node, length:#, data:{type:""}}
      // pt1:  {x:#, y:#}  source position in screen coords
      // pt2:  {x:#, y:#}  target position in screen coords
      particleSystem.eachEdge(function(edge, pt1, pt2) {
        // metrics
        var text = edge.data.type
        context.font = 'bold 14pt Calibri';
        context.textAlign = 'center';
        var textMetrics = context.measureText(text);
        
        // drawing
        context.strokeStyle = 'black'
        context.lineWidth = 2
        context.beginPath()
        context.moveTo(pt1.x, pt1.y)
        context.lineTo(pt2.x, pt2.y)
        context.stroke()
        context.fillStyle = 'black';
        context.fillText(text, (pt1.x + pt2.x) / 2, (pt1.y + pt2.y) / 2 + 6)
      })

      // node: {mass:#, p:{x,y}, name:"", data:{name:"", uri:"", radius:#, color:#}}
      // pt:   {x:#, y:#}  node position in screen coords
      particleSystem.eachNode(function(node, pt) {
        // metrics
        var text = node.data.name
        context.font = '14pt Calibri';
        context.textAlign = 'center';
        var textMetrics = context.measureText(text);
        var radius = (textMetrics.width) / 2 + 8 + node.data.radius;
        
        // drawing
        context.beginPath();
        context.arc(pt.x, pt.y, radius, 0, 2 * Math.PI, false);
        context.fillStyle = node.data.color;
        context.fill();
        context.lineWidth = 4;
        context.strokeStyle = 'black';
        context.stroke();
        context.fillStyle = 'black';
        context.fillText(text, pt.x, pt.y + 6)
      })    			
    },
  }
  return renderer
}
