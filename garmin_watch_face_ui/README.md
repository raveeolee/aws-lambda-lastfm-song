# Garmin Lambda Last.FM current song

### Install the App
- Get Last.FM account. 
- Have something to scrobble on it. Like connect your Spotify or install local scrobbler App.


### Building with via local eclipse 
- Install Eclipse with Garmin SDK & Connect IQ plugin.
https://developer.garmin.com/connect-iq/sdk/
- Go to 
```
cd garmin_watch_face_ui/
```
- Import this folder in Eclipse.
- Edit Resources > strings.xml with your AWS endpoint & Last.FM user.
```
<resources>
    <string id="AppName">Last.Fm Song</string>
    <string id="backend_url">https://YOUR_ENDPOINT.execute-api.eu-west-2.amazonaws.com/test</string>
    <string id="last_fm_user">YOUR_USER</string>
</resources>
```
- Go to Connect IQ > Build for Device wizard - Specify location on your watch.
- Tested on Fenix 3 HR.

### Building with Docker
- Edit resources/strings.xml with your settings
- Start:

```
$ sh build.sh 
Developer certificate keys generated here: ~/eclipse-workspace
Folder: ~/eclipse-workspace/aws-lambda-lastfm-song/garmin_watch_face_ui/
Certificate keys already created

```
- Connect watch, wait 30 seconds. 
- Place aws_song.prg in GARMIN/APPS
