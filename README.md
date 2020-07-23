# Spotification

As a Spotify user, I always keep up with new releases through the "Release Radar" playlist.

The way I normally use Release Radar is clicking in "Go to Album" to listen to the whole release (I like to listen to full albums) but to my frustration, most new releases are Singles =(

But, hey... I'm a programmer and Spotify has an API! Let's solve the problem! It's also an opportunity to apply some cool stuff like FP Scala, ZIO with Http4s, Refined and Newtypes, etc.

The "Release Radar (no singles)" use case is just the beginning; the possibilities are unlimited.

## Project setup

To use the project, you need to register your application [here](https://developer.spotify.com/documentation/general/guides/app-settings/). This will give you the `CLIENT_ID` and `CLIENT_SECRET`.

Then, on the Dashboard, you must configure your authorization `REDIRECT_URI`.

You will need the id of your "Release Radar" playlist. You can get it from the Spotify app itself in "Share > Copy Spotify URI" on the playlist page. it is the 3rd part `(<1st>:<2nd>:<THE ID>)`

For the "Release Radar No Singles", you must create the empty playlist yourself on Spotify app and get its ID like you did above.

Now that you have `CLIENT_ID`, `CLIENT_SECRET` and `REDIRECT_URI`, create a file called ".env" in the root of the project containing this variables (their values will be injected in the application.conf file).

Save `Release Radar ID` and `Release Radar No Singles ID`. You'll need them!

## Running

With the environment variables in place, run the application with the command:
```
sbt "~reStart" 
```

You can see if everything is ok accessing: 
[http://localhost:8080/health](http://localhost:8080/health)

## Authorization

To be able to manipulate the playlists, the app must get authorization from Spotify. To do so, access [http://localhost:8080/authorization](http://localhost:8080/authorization) and agree with the asked permissions. This will give you an `access_token` and a `refresh_token`.

Save the `refresh_token` somewhere, you'll need it to operate the app!

## Filling the Release Radar No Singles playlist

Just execute the following command (of course you can use Postman or something like this):
```
curl -H "Authorization: Bearer <refresh_token>" -X PATCH "http://localhost:8080/playlists/release-radar-no-singles" -d '{"releaseRadarId": "???", "releaseRadarNoSinglesId": "???"}'
```

Then go to Spotify app and enjoy your brand new full albums! 🎶🎵

## Docker

It's possible to run locally inside docker too. For this:

1) Build the image
```
./build.sh
```
2) Run it
```
./run.sh
```

The logs can be watched with the command:
```
./logs.sh
```
