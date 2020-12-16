//
// Copyright 2015-2016 by Garmin Ltd. or its subsidiaries.
// Subject to Garmin SDK License Agreement and Wearables
// Application Developer Agreement.
//

using Toybox.WatchUi as Ui;
using Toybox.Graphics;

class WelcomeView extends Ui.View {
    var mMessage = "Press menu button";
    var mModel;
    var mDc;

    function initialize() {
        Ui.View.initialize();
    }

    // Load your resources here
    function onLayout(dc) {
        mMessage = "Press > ...";
        mDc = dc;
    }

    // Restore the state of the app and prepare the view to be shown
    function onShow() {
    }

    // Update the view
    function onUpdate(dc) {
        dc.setColor(Graphics.COLOR_GREEN, Graphics.COLOR_BLACK);
        dc.clear();
        
        if (mMessage instanceof Array) {
            displayLargeContent(dc, mMessage);
        } else {
            dc.drawText(dc.getWidth() / 2, dc.getHeight() / 2, Graphics.FONT_MEDIUM, mMessage, Graphics.TEXT_JUSTIFY_CENTER | Graphics.TEXT_JUSTIFY_VCENTER);
        }    
    }
    
    function displayLargeContent(dc, content) {
    	
    	// dc.pushView(view, delegate, transition)
    	// content.toString().s.split("")
    	var max = content.size(); // TODO figure out pagination
    	var result = "";
    	
    	for (var i = 0; i < max; i++) {
    		result += content[i];
    	}
    	
    	dc.drawText(dc.getWidth() / 2, 20, Graphics.FONT_SYSTEM_XTINY, result, Graphics.TEXT_JUSTIFY_CENTER);
    }

    // Called when this View is removed from the screen. Save the
    // state of your app here.
    function onHide() {}

    function onReceive(args) {
    	System.println("On Receive");
        mMessage = args;
        Ui.requestUpdate();
    }
}