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
        if(nearest.distance < nearest.node.data.radius){
          if(nearest.node.data.uri) {
            queryResourceByUri(nearest.node.data.uri)
          }
          else if (nearest.node.data.deploy)
          {
            queryDeployNode(nearest.node)
          }
          else if (nearest.node.data.focus)
          {
            queryFocusNode(nearest.node)
          }
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
        context.strokeStyle = '#888888'
        context.lineWidth = 2
        context.beginPath()
        context.moveTo(pt1.x, pt1.y)
        context.lineTo(pt2.x, pt2.y)
        context.stroke()
        context.fillStyle = 'black';
        var rotation = Math.acos((Math.abs(pt1.y-pt2.y))/(Math.sqrt(Math.pow((pt1.y-pt2.y),2)+Math.pow((pt1.x-pt2.x),2))))
        context.save();
        context.translate((pt1.x + pt2.x) / 2,(pt1.y + pt2.y) / 2 + 6);
        if(pt2.x>=pt1.x && pt2.y>=pt1.y){          //en bas à droite
          context.rotate(-rotation+Math.PI*0.5);
        }
        else if(pt2.x<=pt1.x && pt2.y>=pt1.y){     //en bas à gauche
          context.rotate(rotation-Math.PI*0.5);
        }
        else if(pt2.x<=pt1.x && pt2.y<=pt1.y){     //en haut à gauche
          context.rotate(-rotation+Math.PI*0.5);
        }
        else if(pt2.x>=pt1.x && pt2.y<=pt1.y){     //en haut à droite
          context.rotate(rotation-Math.PI*0.5);
        }
        
        context.fillText(text, 0, -8);
        context.restore();
      })

      // node: {mass:#, p:{x,y}, name:"", data:{name:"", uri:"", radius:#, color:#}}
      // pt:   {x:#, y:#}  node position in screen coords
      particleSystem.eachNode(function(node, pt) {
        // metrics
        var text = node.data.name;
        var words = text.split(" ");
        context.font = '14pt Calibri';
        context.textAlign = 'center';
        var textMetrics = node.data.radius;
        words.forEach(word => {
          var measure = context.measureText(word);
          if(textMetrics<measure.width)
          {
            textMetrics = measure.width;
          }
        });
        node.data.radius = (textMetrics) / 2 + 8;
        
        // drawing
        context.beginPath();
        context.arc(pt.x, pt.y, node.data.radius, 0, 2 * Math.PI, false);
        context.fillStyle = node.data.color;
        context.fill();
        context.lineWidth = 4;
        context.strokeStyle = 'black';
        context.stroke();
        context.fillStyle = 'black';
        var i = 0
        words.forEach(word => {
          if (words.length%2==0){
            context.fillText(word, pt.x, pt.y + 12*i)
          }
          else{
            context.fillText(word, pt.x, pt.y + 12*i + 6)
          }
          i++;
        });
      })    			
    },
  }
  return renderer
}
