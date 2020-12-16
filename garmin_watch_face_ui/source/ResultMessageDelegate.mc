//
// Copyright 2016 by Garmin Ltd. or its subsidiaries.
// Subject to Garmin SDK License Agreement and Wearables
// Application Developer Agreement.
//
using Toybox.Communications as Comm;
using Toybox.WatchUi as Ui;

class ResultsMessageDelegate extends Ui.BehaviorDelegate {	
	hidden var _results;
	hidden var _allLines;

	// Set up the callback to the view
    function initialize(results, allLines) {   
    	Ui.BehaviorDelegate.initialize();
    	
    	self._results = results;
    	self._allLines = allLines;
    }
    
    function onKey(evt) {
        if (evt.getKey() == Ui.KEY_DOWN) {       	
           showNextPage(_results);
           return true;
        } 
        
        if (evt.getKey() == Ui.KEY_UP) {
            showPreviousPage();
            return true;
        }   
        return true;
    }
    
    function onSelect() {
        showNextPage(_results);
        return true;
    }
    
    function showNextPage(results) { 	
    	new WelcomePageDelegate(null).showResults(_allLines);   	
    }   
    
    function onBack() {
    	System.exit();
    } 
    
    function showPreviousPage() {
    	Ui.popView(Ui.SLIDE_IMMEDIATE);   	
    }
}