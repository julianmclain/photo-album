# Photo Album 

## Architecture Notes 

The frontend is a React App seeded with `create-react-app`. The backend is a JSON based REST API implemented with Play
. To
 simplify development and eliminate CORS headaches, the Play application also serves the frontend via static routes.
 Create React App doesn't really support that out of the box, so I had to make a few tweeks:
 - I modified the build script to publish the fronent distribution artifacts to the `/public` directory. The npm
  packages `rimraf` and `ncp` helped out here since that's not configurable with create-react-app.
 - I added a proxy config for the React app to proxy the REST endpoints while the app is running in dev mode. 

## Bootstrap

I installed bootstrap as a WebJar instead of downloading the files and manually adding them to my assets directory
. Hypothetically, the WebJar approach should be easier to maintain since you can bump the version number (and easily
 see it) in the build.sbt.
 
There was a hiccup trying to get it setup. At the time of this writing, the setup steps in the webjar-play docs don't
 work if you're using a layout template and injecting another template into it. Thankfully someone already created an
  issue with this exact problem and posted their solution: https://github.com/webjars/webjars-play/issues/76.
  
## Notes

echo '{"username": "jjjj", "email": "j@gmail.com"}' | http POST localhost:9000/users

## Resources

- Play guide https://books.underscore.io/essential-play/essential-play.html
- Play docs on evolutions https://www.playframework.com/documentation/2.8.x/Evolutions
- Play Slick docs https://www.playframework.com/documentation/2.8.x/PlaySlick
- Slick guide https://books.underscore.io/essential-slick/essential-slick-3.html
- Slick guide https://scala-slick.org/doc/3.0.0/gettingstarted.html

https://deviq.com/repository-pattern/

## TODO
- v1 views
    - GET / redirects to /albums
    - GET /albums shows the albums you're a member of and ability to create a new album
    - POST /albums creates a new album
    - GET /albums/:id shows a single album
    - GET /albums/:id/photos/:id
    - POST /albums/:id/photos/:id
    
- v2 views
    - create the flow for inviting someone and seeing / accepting / declining pending invites
    
- Finish a basic DB layer
    - Nice to have: A Base Table trait with methods like findById, create, update, etc... Use the Iterable
     `TableWithIdQuery` for inspiration. You might need to read the Scala for the Impatient chapter on types in order
      to understand what's going on in that class. Also check out https://manuel.bernhardt.io/2013/07/08/crud-trait-for-slick-models-in-the-play-framework/

## Future TODO
- Setup auth with silhouette 
- Add bootstrap during build process instead of using it from their CDN: https://ics-software-engineering.github.io/play-example-bootstrap/
- finish setting up React https://blog.usejournal.com/react-with-play-framework-2-6-x-a6e15c0b7bd