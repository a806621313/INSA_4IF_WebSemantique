Warning : DBpedia restrict the number of results per query to 10000, and the maximum offset is 40000.

## Core Resources

### Film Production Companies

These are the only companies that we will handle.

```
SELECT ?c WHERE {
  ?c rdf:type dbo:Company ;
     rdf:type ?o .
  FILTER regex(str(?o), "WikicatFilmProductionCompaniesOf")
}
```

### Films

These are the only films that we will handle. They must have a known duration.

A workaround will be necessary to get all the movie names, because there are more than 100.000 movies in the database !

```
SELECT DISTINCT ?f WHERE {
  ?f rdf:type dbo:Film ;
     dbo:runtime ?r .
}
ORDER BY ?f
```

## Relationships

### Films and the Company that produced each Film

The Film and the Company that made the Film (`dbp:studio`).

```
SELECT ?f ?c WHERE {
  ?c rdf:type dbo:Company ;
     rdf:type ?o .
  ?f rdf:type dbo:Film ;
     dbp:studio ?c .
  FILTER regex(str(?o), "WikicatFilmProductionCompaniesOf")
}
```

### Films and the Company that distributed each Film

The Film and the Company that made each Film accessible to the public (`dbo:distributor`).

```
SELECT ?f ?c WHERE {
  ?c rdf:type dbo:Company ;
     rdf:type ?o .
  ?f rdf:type dbo:Film ;
     dbo:distributor ?c .
  FILTER regex(str(?o), "WikicatFilmProductionCompaniesOf")
}
```

### Actors

```
SELECT DISTINCT count(?a) WHERE {
  ?f rdf:type dbo:Film ;
     dbo:starring ?a .
  ?a rdfs:label ?l .
  FILTER(lang(?l)='en') .
  FILTER(strlen(str(?l)) < 6)
}

```

### Actors of a Film

```
SELECT ?f ?a WHERE {
  ?f rdf:type dbo:Film ;
     dbo:starring ?a
}
```

Example : Actors of the Film "Cars"

```
SELECT ?a WHERE {
  <http://dbpedia.org/resource/Cars_(film)> dbo:starring ?a
}
```

### Film Director

```
SELECT ?f ?d WHERE {
  ?f rdf:type dbo:Film ;
     dbo:director ?d .
}
```

### Film Producer

```
SELECT ?f ?p WHERE {
  ?f rdf:type dbo:Film ;
     dbo:producer ?p .
}
```

### Film Music Composer

```
SELECT ?f ?c WHERE {
  ?f rdf:type dbo:Film ;
     dbo:musicComposer ?c .
}
```

### Parent Company

```
SELECT ?c ?p WHERE {
  ?c rdf:type dbo:Company ;
     rdf:type ?o ;
     dbo:subsidiary ?p .
  FILTER regex(str(?o), "WikicatFilmProductionCompaniesOf")
}
```

### Parent Company

```
SELECT ?c ?p WHERE {
  ?c rdf:type dbo:Company ;
     rdf:type ?o ;
     dbo:parentCompany ?p .
  FILTER regex(str(?o), "WikicatFilmProductionCompaniesOf")
}
```

## Informations about Resources

### Number of Films made by a Company / Film Director / Music Composer / Actor ...

```
SELECT ?c (COUNT(?f) AS ?numberOfMovies) WHERE {
  ?c rdf:type dbo:Company ;
     rdf:type ?o .
  ?f rdf:type dbo:Film ;
     dbp:studio ?c .
  FILTER regex(str(?o), "WikicatFilmProductionCompaniesOf")
}
ORDER BY DESC(?numberOfMovies)
```

```
SELECT (COUNT(?f) AS ?numberOfMovies) WHERE {
  <http://dbpedia.org/resource/Universal_Television> rdf:type dbo:Company .
  ?f rdf:type dbo:Film ;
     dbp:studio <http://dbpedia.org/resource/Universal_Television> .
}
```

### Company Number of Employees

```
SELECT ?c ?noe WHERE {
  ?c rdf:type dbo:Company ;
     rdf:type ?o ;
     dbo:numberOfEmployees ?noe.
  FILTER regex(str(?o), "WikicatFilmProductionCompaniesOf")
}
```

### Company Logo

```
SELECT ?c ?l WHERE {
  ?c rdf:type dbo:Company ;
     rdf:type ?o ;
     dbo:netIncome ?l .
  FILTER regex(str(?o), "WikicatFilmProductionCompaniesOf")
}
```

### Film Budget

```
SELECT ?b WHERE {
  <http://dbpedia.org/resource/Cars_(film)> dbo:budget ?b .
}
```

### Film Box Office

```
SELECT ?b WHERE {
  <http://dbpedia.org/resource/Cars_(film)> dbo:gross ?b .
}
```

### Film Duration (seconds)

```
SELECT ?d WHERE {
  <http://dbpedia.org/resource/Cars_(film)> dbo:runtime ?d .
}
```


## Suggestions

### Actors who frequently starred with a given Actor

```
SELECT ?a (COUNT(?a) AS ?n) WHERE {
  ?f rdf:type dbo:Film ;
     dbo:starring <http://dbpedia.org/resource/Gérard_Depardieu> ;
     dbo:starring ?a .
  FILTER(?a != <http://dbpedia.org/resource/Gérard_Depardieu>).
}
GROUP BY ?a
ORDER BY DESC(?n)
LIMIT 5
```

### Actors who frequently starred with a given Producer

```
SELECT ?a (COUNT(?a) AS ?n) WHERE {
  ?f rdf:type dbo:Film ;
     dbo:producer <http://dbpedia.org/resource/Jerry_Bruckheimer> ;
     dbo:starring ?a .
  FILTER(?a != <http://dbpedia.org/resource/Jerry_Bruckheimer>).
}
GROUP BY ?a
ORDER BY DESC(?n)
LIMIT 5
```

### Actors who frequently starred for a given studio

```
SELECT ?a (COUNT(?a) AS ?n) WHERE {
  ?f rdf:type dbo:Film ;
     dbp:studio <http://dbpedia.org/resource/Metro-Goldwyn-Mayer> ;
     dbo:starring ?a .
}
GROUP BY ?a
ORDER BY DESC(?n)
LIMIT 5
```

### Film Directors who frequently worked with a given Music Composer

```
SELECT ?d (COUNT(?d) AS ?n) WHERE {
  ?f rdf:type dbo:Film ;
     dbo:musicComposer <http://dbpedia.org/resource/Hans_Zimmer> ;
     dbo:director ?d .
  FILTER(?d != <http://dbpedia.org/resource/Hans_Zimmer>).
}
GROUP BY ?d
ORDER BY DESC(?n)
LIMIT 5
```

### Film Directors who frequently starred with a given Producer

```
SELECT ?d (COUNT(?d) AS ?n) WHERE {
  ?f rdf:type dbo:Film ;
     dbo:producer <http://dbpedia.org/resource/Jerry_Bruckheimer> ;
     dbo:director ?d .
  FILTER(?d != <http://dbpedia.org/resource/Jerry_Bruckheimer>).
}
GROUP BY ?d
ORDER BY DESC(?n)
LIMIT 5
```

### Film Directors who frequently worked for a given Studio

```
SELECT ?d (COUNT(?d) AS ?n) WHERE {
  ?f rdf:type dbo:Film ;
     dbo:director ?d ;
     dbp:studio <http://dbpedia.org/resource/Metro-Goldwyn-Mayer> .
}
GROUP BY ?d
ORDER BY DESC(?n)
LIMIT 5
```

### Music Composers who frequently worked with a given Film Director

```
SELECT ?c (COUNT(?c) AS ?n) WHERE {
  ?f rdf:type dbo:Film ;
     dbo:director <http://dbpedia.org/resource/Christopher_Nolan> ;
     dbo:musicComposer ?c .
  FILTER(?c != <http://dbpedia.org/resource/Christopher_Nolan>).
}
GROUP BY ?c
ORDER BY DESC(?n)
LIMIT 5
```

### Music Composers who frequently starred with a given Producer

```
SELECT ?c (COUNT(?c) AS ?n) WHERE {
  ?f rdf:type dbo:Film ;
     dbo:producer <http://dbpedia.org/resource/Jerry_Bruckheimer> ;
     dbo:musicComposer ?c .
  FILTER(?c != <http://dbpedia.org/resource/Jerry_Bruckheimer>).
}
GROUP BY ?c
ORDER BY DESC(?n)
LIMIT 5
```

### Music Composers who frequently worked for a given Studio

```
SELECT ?c (COUNT(?c) AS ?n) WHERE {
  ?f rdf:type dbo:Film ;
     dbo:musicComposer ?c ;
     dbp:studio <http://dbpedia.org/resource/Metro-Goldwyn-Mayer> .
}
GROUP BY ?c
ORDER BY DESC(?n)
LIMIT 5
```

### Producer who frequently worked for a given Studio

```
SELECT ?p (COUNT(?p) AS ?n) WHERE {
  ?f rdf:type dbo:Film ;
     dbo:producer ?p ;
     dbp:studio <http://dbpedia.org/resource/Metro-Goldwyn-Mayer> .
}
GROUP BY ?p
ORDER BY DESC(?n)
LIMIT 5
```

### Producer who frequently worked with a given Film Director

```
SELECT ?p (COUNT(?p) AS ?n) WHERE {
  ?f rdf:type dbo:Film ;
     dbo:director <http://dbpedia.org/resource/Christopher_Nolan> ;
     dbo:producer ?p .
  FILTER(?p != <http://dbpedia.org/resource/Christopher_Nolan>).
}
GROUP BY ?p
ORDER BY DESC(?n)
LIMIT 5
```


### Producer who frequently starred with a given Actor

```
SELECT ?p (COUNT(?p) AS ?n) WHERE {
  ?f rdf:type dbo:Film ;
     dbo:starring <http://dbpedia.org/resource/Gérard_Depardieu> ;
     dbo:producer ?p .
  FILTER(?p != <http://dbpedia.org/resource/Gérard_Depardieu>).
}
GROUP BY ?p
ORDER BY DESC(?n)
LIMIT 5
```


### Producer who frequently worked with a given Music Composer

```
SELECT ?p (COUNT(?p) AS ?n) WHERE {
  ?f rdf:type dbo:Film ;
     dbo:musicComposer <http://dbpedia.org/resource/Hans_Zimmer> ;
     dbo:producer ?p .
  FILTER(?p != <http://dbpedia.org/resource/Hans_Zimmer>).
}
GROUP BY ?p
ORDER BY DESC(?n)
LIMIT 5
```

### Famous Films of a given Actor

```
SELECT ?f ?g WHERE {
  ?f rdf:type dbo:Film ;
     dbo:starring <http://dbpedia.org/resource/Gérard_Depardieu> ;
     dbo:gross ?g .
}
ORDER BY DESC(xsd:integer(?g))
LIMIT 5
```

### Famous Films of a given Director

```
SELECT ?f ?g WHERE {
  ?f rdf:type dbo:Film ;
     dbo:director <http://dbpedia.org/resource/Christopher_Nolan> ;
     dbo:gross ?g .
}
ORDER BY DESC(xsd:integer(?g))
LIMIT 5
```

### Famous Films of a given Producer

```
SELECT ?f ?g WHERE {
  ?f rdf:type dbo:Film ;
     dbo:producer <http://dbpedia.org/resource/Jerry_Bruckheimer> ;
     dbo:gross ?g .
}
ORDER BY DESC(xsd:integer(?g))
LIMIT 5
```

### Famous Films of a given Music Composer

```
SELECT ?f ?g WHERE {
  ?f rdf:type dbo:Film ;
     dbo:musicComposer <http://dbpedia.org/resource/Hans_Zimmer> ;
     dbo:gross ?g .
}
ORDER BY DESC(xsd:integer(?g))
LIMIT 5
```

### Famous Films of a given studio

```
SELECT ?f ?g WHERE {
  ?f rdf:type dbo:Film ;
     dbp:studio <http://dbpedia.org/resource/Metro-Goldwyn-Mayer> ;
     dbo:gross ?g .
}
ORDER BY DESC(xsd:integer(?g))
LIMIT 5
```
