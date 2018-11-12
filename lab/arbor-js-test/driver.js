var sys;

$(document).ready(function() {
  sys = arbor.ParticleSystem(200, 800, 0.5)
  sys.parameters({gravity:true})
  sys.renderer = renderer("#viewport")
  
  sys.addNode('node0', {name: 'Film', radius: 0, color: '#4444ff'})
  sys.addNode('node1', {name: 'Director', uri:'https://github.com/AlexisLeConte/IF-4-WS-Project', radius: 0, color: '#44ff44'})
  sys.addNode('node2', {name: 'Studio', uri:'https://login.insa-lyon.fr/zimbra/', radius: 0, color: '#ff4444'})
  sys.addNode('node3', {name: 'Actor', uri:'https://planete.insa-lyon.fr/uPortal/f/welcome/normal/render.uP', radius: 0, color: '#44cc44'})
  sys.addNode('node4', {name: 'Deploy', deploy:true, radius: 0, color: '#e8f442'})
  sys.addNode('node5', {name: 'New Focus', focus:true, radius: 0, color: '#e8092e'})
  sys.addEdge('node0','node1')
  sys.addEdge('node0','node2')
  sys.addEdge('node0','node3')
  sys.addEdge('node0','node4')
  sys.addEdge('node0','node5')
  

  /*
  setTimeout(function(){
    var postLoadData = {
      nodes:{
        node4:{name: 'Node4', radius: 20, color:'blue', uri: ''},
        node5:{name: 'Node5', radius: 30, color:'red', uri: ''},
      },
      edges:{
        node0:{node4:{type:'directed by'}, node5:{type:'produced by'}},
        node1:{node5:{type:'starred in'}},
      }
    };
    sys.graft(postLoadData);
  }, 4000);
  */

})

function queryDeployNode(source)
{
  if (sys.getEdgesFrom(source).length==0){
    sys.addNode('node6',{name: 'Info1', radius: 20, color:'blue', uri: ''})
    sys.addNode('node7',{name: 'Info2', radius: 30, color:'red', uri: ''})

    sys.addEdge(source.name,'node6',{type:'has', erase:false})
    sys.addEdge(source.name,'node7',{type:'owns', erase:false})
  }
  else{
    sys.getEdgesFrom(source).forEach(function(edge){
      sys.pruneNode(edge.target)
    });
  }
}


function queryFocusNode(source)
{
  var postLoadData = {
    nodes:{
      node0:source.data,
      node1:{name: 'Info1', radius: 20, color:'blue', uri: ''},
      node2:{name: 'Info2', radius: 30, color:'red', uri: ''},
    },
    edges:{
      node0:{node1:{type:'has'}, node2:{type:'owns'}},
    }
  };
  sys.merge(postLoadData);
}

function queryResourceByUri(uri) {
  alert(uri)
}
