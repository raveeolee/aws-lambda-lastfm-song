//
// Copyright 2015-2016 by Garmin Ltd. or its subsidiaries.
// Subject to Garmin SDK License Agreement and Wearables
// Application Developer Agreement.
//
using Toybox.WatchUi as Ui;
using Toybox.Graphics;

class ResultsView extends Ui.View {
	var mMessage;
    var mModel;
    var mDc;
    var mTimer;

    function initialize(results) {
        Ui.View.initialize();
        mMessage = results;
    }

    // Load your resources here
    function onLayout(dc) {
        mDc = dc;
    }

    // Restore the state of the app and prepare the view to be shown
    function onShow() {}

    // Update the view
    function onUpdate(dc) {
        dc.setColor(Graphics.COLOR_GREEN, Graphics.COLOR_BLACK);
        dc.clear();
        displayLargeContent(dc, mMessage);
    }
    
    function displayLargeContent(dc, content) {
    	var max = content.size(); // TODO figure out pagination
    	var result = "";
    	
    	for (var i = 0; i < max; i++) {
    		result += content[i];
    	}
    	
    	var fontSize = Graphics.FONT_SYSTEM_XTINY;
    	
    	if (content.size() < 3) {
    		fontSize = Graphics.FONT_SYSTEM_SMALL;    		
    	}
    	
    	dc.drawText(dc.getWidth() / 2, dc.getHeight() / 2, fontSize, result, Graphics.TEXT_JUSTIFY_CENTER | Graphics.TEXT_JUSTIFY_VCENTER);
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