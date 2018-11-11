$(document).ready(function() {
  var sys = arbor.ParticleSystem(1000, 600, 0.5)
  sys.parameters({gravity:true})
  sys.renderer = renderer("#viewport")
  
  sys.addNode('node0', {name: 'Node0', radius: 80})
  sys.addNode('node1', {name: 'GitHub/AlexisLeConte', radius: 32, uri:'https://github.com/AlexisLeConte/IF-4-WS-Project'})
  sys.addNode('node2', {name: 'Zimbra INSA Lyon', radius: 42, uri:'https://login.insa-lyon.fr/zimbra/'})
  sys.addNode('node3', {name: 'Planète INSA Lyon', radius: 64, uri:'https://planete.insa-lyon.fr/uPortal/f/welcome/normal/render.uP'})
  sys.addEdge('node0','node1')
  sys.addEdge('node0','node2')
  sys.addEdge('node0','node3')
})

function queryResourceByUri(uri) {
  alert(uri)
}
