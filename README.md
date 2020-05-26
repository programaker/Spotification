# Spotification

As a Spotify user, I always keep up with new releases through the "Release Radar" playlist.

The way I normally use Release Radar is clicking in "Go to Album" to listen to the whole release (I like to listen to full albums) but to my frustration, most new releases are Singles =(

But, hey... I'm a programmer and Spotify has an API! Let's solve the problem! It's also an opportunity to apply some cool stuff like FP Scala, ZIO with Http4s, Refined and Newtypes, etc.

The "Release Radar (no singles)" use case is just the beginning; the possibilities are unlimited.

## Project setup

To use the project, you need to register your application [here](https://developer.spotify.com/documentation/general/guides/app-settings/). This will give you the `CLIENT_ID` and `CLIENT_SECRET`.

Then, on the Dashboard, you must configure your authorization `REDIRECT_URI`.

You will need the id of your "Release Radar" playlist. you can get it from the Spotify app itself in "Share > Copy Spotify URI" on the playlist page. it is the 3rd part `(<1st>:<2nd>:<THE ID>)`

For the "Release Radar No Singles", you must create the empty playlist yourself on Spotify app and get its ID like you did above.

Now that you have `CLIENT_ID`, `CLIENT_SECRET`, `REDIRECT_URI`, `RELEASE_RADAR_ID` and `RELEASE_RADAR_NO_SINGLES_ID`, create a file called ".env" in the root of the project containing this variables (their values will be injected in the application.conf file).

## Running

With the environment variables in place, run the application with the command:
```
sbt ~reStart 
```

You can see if everything is ok accessing: 
[http://localhost:8080/health](http://localhost:8080/health)

## Authorization

To be able to manipulate the playlists, the app must get authorization from Spotify. To do so, access [http://localhost/authorization](http://localhost/authorization) and agree with the asked permissions. This will give you an `access_token` and a `refresh_token`.

Get the refresh_token and add it to the ".env" file as the variable `REFRESH_TOKEN`.With this, you won't need to run the authorization anymore!

## Filling the Release Radar No Singles playlist

Just execute the following command:
```
curl -v -X POST "http://localhost:8080/release-radar"
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
