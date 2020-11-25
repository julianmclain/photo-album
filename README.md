# Photo Album 

## Architecture Notes 

The frontend is a React App seeded with `create-react-app`. The backend is a JSON based REST API implemented with Play
. To
 simplify development and eliminate CORS headaches, the Play application also serves the frontend via static routes.
 Create React App doesn't really support that out of the box, so I had to make a few tweeks:
 - I modified the build script to publish the fronent distribution artifacts to the `/public` directory. The npm
  packages `rimraf` and `ncp` helped out here since that's not configurable with create-react-app.
 - I added a proxy config for the React app to proxy the REST endpoints while the app is running in dev mode. 

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
- Setup auth with silhouette 

## Future TODO
- Add bootstrap during build process instead of using it from their CDN: https://ics-software-engineering.github.io/play-example-bootstrap/
- finish setting up React https://blog.usejournal.com/react-with-play-framework-2-6-x-a6e15c0b7bd