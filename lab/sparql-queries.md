## TODO

- [x] Film Production Companies
- [x] Films

- [x] Films produced by a Company
- [x] Films distributed by a Company
- [x] Actors starred in a Film
- [x] Film Director
- [ ] Film Producer (staff) ?
- [x] Film Music Composer
- [ ] Company Subsidiaries / Parent Company

<<<<<<< HEAD
- [x] Number of Movies produced by a Company
- [x] Movie duration
- [x] Company Logo
- [ ] Company Assets
- [x] Movie Budget
- [x] Movie Box Office
=======
- [x] Number of Films produced by a Company
- [x] Film duration
- [ ] Company Logo
- [ ] Company Assets
- [x] Film Budget
- [ ] Film Box Office
>>>>>>> 71c2f0f30e995d8c11ebd18975a16aecec42d955
- [ ] Company CEO


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

These are the only movies that we will handle. They must have a known duration.

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

The Film and the Company that made the movie (`dbp:studio`).

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

### Film Music Composer

```
SELECT ?f ?c WHERE {
  ?f rdf:type dbo:Film ;
     dbo:musicComposer ?c .
}
```

### Movie Producer

```
SELECT ?f ?d WHERE {
  ?f rdf:type dbo:Film ;
     dbo:director ?d .
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
SELECT ?c (COUNT(?f) AS ?numberOfFilms) WHERE {
  ?c rdf:type dbo:Company ;
     rdf:type ?o .
  ?f rdf:type dbo:Film ;
     dbp:studio ?c .
  FILTER regex(str(?o), "WikicatFilmProductionCompaniesOf")
}
ORDER BY DESC(?numberOfFilms)
```

```
SELECT (COUNT(?f) AS ?numberOfFilms) WHERE {
  <http://dbpedia.org/resource/Universal_Television> rdf:type dbo:Company .
  ?f rdf:type dbo:Film ;
     dbp:studio <http://dbpedia.org/resource/Universal_Television> .
}
```

### Company Logo

```
SELECT ?c ?l WHERE {
  ?c rdf:type dbo:Company ;
     rdf:type ?o ;
     dbp:logo ?l .
  FILTER regex(str(?o), "WikicatFilmProductionCompaniesOf")
}
```

### Film Budget

```
SELECT ?b WHERE {
  <http://dbpedia.org/resource/Cars_(film)> dbo:budget ?b .
}
```

<<<<<<< HEAD
### Movie Box Office

```
SELECT ?b WHERE {
  <http://dbpedia.org/resource/Cars_(film)> dbo:gross ?b .
}
```

### Movie Duration (seconds)
=======
### Film Duration (seconds)
>>>>>>> 71c2f0f30e995d8c11ebd18975a16aecec42d955

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
```
