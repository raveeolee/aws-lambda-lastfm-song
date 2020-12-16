//
// Copyright 2015-2016 by Garmin Ltd. or its subsidiaries.
// Subject to Garmin SDK License Agreement and Wearables
// Application Developer Agreement.
//

using Toybox.Application as App;

class SongRequestApp extends App.AppBase {
    hidden var mView;

    function initialize() {
        App.AppBase.initialize();
    }

    // onStart() is called on application start up
    function onStart(state) {
    }

    // onStop() is called when your application is exiting
    function onStop(state) {
    	System.println("Stop called");
    }

    // Return the initial view of your application here
    function getInitialView() {
        mView = new WelcomeView();
        return [mView, new WelcomePageDelegate(mView.method(:onReceive))];
    }
}