# omdb
Spring boot Open Movie DB client

endpoints:
GET movies/ - retrieve movie information from the OMDB repo

  parameters:
    - apiKey: required
    - title: required
    - year: 1900 - 2023
    - type: movie, series, episode
    
GET movies/<imdb> - retrieve movie information based on its imdb key

  parameters:
    - apiKey: required
  
GET movies/favorites - retrieve the movies currently saved in the favorites list associated to the apiKey
  
  parameters:
    - apiKey: required
  
GET movies/favorites/<imdb> - retrieve movie information based on its imdb key
  
  parameters:
    - apiKey: required
  
POST movies/favorites/<imdb> - save a new movie in the favorites list associated to the apiKey
  
  parameters:
    - apiKey: required
  
DELETE movies/favorites/<imdb> - removes a movie from the favorites list associated to the apiKey
  
  parameters:
    - apiKey: required
