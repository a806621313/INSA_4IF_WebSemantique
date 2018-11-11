## TODO

- [x] Film Production Companies
- [x] Movies

- [x] Movies produced by a Company
- [x] Movies distributed by a Company
- [x] Actors starred in a Movie
- [x] Movie Director
- [ ] Movie Producer (staff) ?
- [x] Movie Music Composer
- [ ] Company Subsidiaries / Parent Company

- [x] Number of Movies produced by a Company
- [x] Movie duration
- [ ] Company Logo
- [ ] Company Assets
- [x] Movie Budget
- [ ] Movie Box Office
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

### Movies

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

### Movies and the Company that produced each Movie

The Movie and the Company that made the movie (`dbp:studio`).

```
SELECT ?f ?c WHERE {
  ?c rdf:type dbo:Company ;
     rdf:type ?o .
  ?f rdf:type dbo:Film ;
     dbp:studio ?c .
  FILTER regex(str(?o), "WikicatFilmProductionCompaniesOf")
}
```

### Movies and the Company that distributed each Movie

The Movie and the Company that made each Movie accessible to the public (`dbo:distributor`).

```
SELECT ?f ?c WHERE {
  ?c rdf:type dbo:Company ;
     rdf:type ?o .
  ?f rdf:type dbo:Film ;
     dbo:distributor ?c .
  FILTER regex(str(?o), "WikicatFilmProductionCompaniesOf")
}
```

### Actors of a Movie

```
SELECT ?f ?a WHERE {
  ?f rdf:type dbo:Film ;
     dbo:starring ?a
}
```

Example : Actors of the Movie "Cars"

```
SELECT ?a WHERE {
  <http://dbpedia.org/resource/Cars_(film)> dbo:starring ?a
}
```

### Movie Director

```
SELECT ?f ?d WHERE {
  ?f rdf:type dbo:Film ;
     dbo:director ?d .
}
```

### Movie Music Composer

```
SELECT ?f ?c WHERE {
  ?f rdf:type dbo:Film ;
     dbo:musicComposer ?c .
}
```

## Informations about Resources

### Number of Movies made by a Company

```
SELECT ?c (COUNT(?m) AS ?numberOfMovies) WHERE {
  ?c rdf:type dbo:Company ;
     rdf:type ?o .
  ?m rdf:type dbo:Film ;
     dbp:studio ?c .
  FILTER regex(str(?o), "WikicatFilmProductionCompaniesOf")
}
ORDER BY DESC(?numberOfMovies)
```

```
SELECT (COUNT(?m) AS ?numberOfMovies) WHERE {
  <http://dbpedia.org/resource/Universal_Television> rdf:type dbo:Company .
  ?m rdf:type dbo:Film ;
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

### Parent Company

```
SELECT ?c ?p WHERE {
  ?c rdf:type dbo:Company ;
     rdf:type ?o ;
     dbo:parentCompany ?p .
  FILTER regex(str(?o), "WikicatFilmProductionCompaniesOf")
}
```

### Movie Budget

```
SELECT ?b WHERE {
  <http://dbpedia.org/resource/Cars_(film)> dbo:budget ?b .
}
```

### Movie Duration (seconds)

```
SELECT ?d WHERE {
  <http://dbpedia.org/resource/Cars_(film)> dbo:runtime ?d .
}
```
