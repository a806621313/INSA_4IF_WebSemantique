A FAIRE :
Rechercher entreprise film
Rechercher logo entreprise
Rechercher entreprise parente
Rechercher capital entreprise
Rechercher films entreprise
Rechercher nombre de film d’une entreprise
Rechercher filiales d’une entreprise
Rechercher acteurs d’un film
Rechercher durée d’un film
Rechercher budget d’un film
Rechercher nombre d’entrées d’un film
Rechercher  recette d’un film
Rechercher le PDG d’une entreprise
Rechercher producteurs d’un film
Rechercher distributeurs d’un film
Rechercher compositeur musique d’un film
Rechercher genre d’un film

Chercher tous les entreprises de film
select ?s ?o
where
{
?s rdf:type dbo:Company ;
rdf:type ?o .
FILTER regex(str(?o), "WikicatFilmProductionCompaniesOf")
}

Propriété qui détermine le propriétaire du film
dbp:studio nom de l'entreprise.

Tous les films de toutes les entreprises.
select ?film ?s
where
{
?s rdf:type dbo:Company ;
rdf:type ?o .
?film rdf:type dbo:Film ;
dbp:studio ?s .
FILTER regex(str(?o), "WikicatFilmProductionCompaniesOf")
}

Nombre des films d’une entreprise
%%Exemple de Pixar
select ?entreprise (COUNT(?film) as ?NbFilms)
where
{
?entreprise rdf:type dbo:Company .
?film rdf:type dbo:Film ;
dbp:studio ?enreprise .
FILTER (?entreprise = dbr:Pixar)
}

Trouver les acteurs et producteurs et music composers d’un film
%%Exemple Cars_(film)
select ?film ?actor
where
{
?film rdf:type dbo:Film .
?film dbo:starring ?actor .
FILTER (?film = <http://dbpedia.org/resource/Cars_(film)>)
}

Trouver logo entreprise
select ?entreprise ?logo
where
{
?entreprise a dbo:Company ;
a ?o ;
dbp:logo ?logo.
FILTER regex(str(?o), "WikicatFilmProductionCompaniesOf")
}

Trouver entreprise parente d’une entreprise
select ?entreprise ?parent
where
{
?entreprise a dbo:Company ;
a ?o ;
dbo:parentCompany ?parent.
FILTER regex(str(?o), "WikicatFilmProductionCompaniesOf")
}

Trouver le budget d’un film
%%Exemple Cars_(film)
select ?film ?budget
where
{
?film rdf:type dbo:Film .
?film dbo:budget ?budget .
FILTER (?film = <http://dbpedia.org/resource/Cars_(film)>)
}

Trouver la durée (en secondes) d’un film
%%Exemple Cars_(film)
select ?film ?duration
where
{
?film rdf:type dbo:Film .
?film dbo:runtime ?duration .
FILTER (?film = <http://dbpedia.org/resource/Cars_(film)>)
}
