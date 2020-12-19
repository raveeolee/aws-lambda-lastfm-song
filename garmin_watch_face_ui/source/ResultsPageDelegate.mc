//
// Copyright 2016 by Garmin Ltd. or its subsidiaries.
// Subject to Garmin SDK License Agreement and Wearables
// Application Developer Agreement.
//
using Toybox.Communications as Comm;
using Toybox.WatchUi as Ui;

class ResultsPageDelegate extends Ui.BehaviorDelegate {
	hidden var _results;
	hidden var _notify;
    hidden var _shift = 0;
    hidden var _step = 8;
    hidden var _max = _step;
    hidden var _next;
    hidden var _previous;

	// Set up the callback to the view
    function initialize(results, shift, step, previous) {   
    	Ui.BehaviorDelegate.initialize();
    	
    	self._results = results;  	
    	self._shift = shift;
    	self._step = step;  
    	self._previous = previous;
    	
    	showNextPage(results);
    }
    
    function onKey(evt) {
        if (evt.getKey() == Ui.KEY_DOWN) {
        	System.println("KEY DOWN");
                      
           if (_results != null) {
           		showNextPage(_results);
           }  
                    	    
           return true;
        } 
        
        if (evt.getKey() == Ui.KEY_UP) {
        	System.println("KEY UP");
            showPreviousPage();
            return true;
        }
        
        if (evt.getKey() == Ui.KEY_MENU || evt.getKey() == Ui.KEY_ENTER) {
        	System.println("KEY_MENU");
            showPreviousPage();
            return true;
        }
        
        return true;
    }
    
    function showNextPage(results) {
    	if (results != null) {
    		var tMax = _max;
    		if (_max > results.size()) {
    			tMax = results.size();
    		}
    		
    		Ui.pushView(new ResultsView(results.slice(_shift, tMax)), self, Ui.SLIDE_IMMEDIATE);
    	
    		if (_max < results.size()) {
    			_shift += _step;
    			_max += _step;
    		}
    	}
    }   
    
    function onBack() {
    	System.println("Back");
    	_previous.reset();
    	System.exit();
    } 
    
    function showPreviousPage() {
    	_previous.reset();
    	Ui.popView(Ui.SLIDE_IMMEDIATE);
    }
}