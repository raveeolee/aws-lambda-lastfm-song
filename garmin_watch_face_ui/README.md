# Garmin Lambda Last.FM current song

### Install the App
- Create Last.FM account. Connect your Spotify to it.
- Install Eclipse with Garmin SDK & Connect IQ plugin.

- Go to cd garmin_watch_face_ui/ Import this folder in Eclipse

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
