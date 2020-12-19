//
// Copyright 2016 by Garmin Ltd. or its subsidiaries.
// Subject to Garmin SDK License Agreement and Wearables
// Application Developer Agreement.
//

using Toybox.Communications as Comm;
using Toybox.WatchUi as Ui;
using Toybox.Timer;

class WelcomePageDelegate extends Ui.BehaviorDelegate {
    hidden var _notify;
    hidden var _shift = 0;
    hidden var _step = 8;
    hidden var _max = _step;
    hidden var _progressBar;
    hidden var _allItems = [];
    hidden var _mainTimer;

    // Handle menu button press
    function onMenu() {
    	System.println("Menu");
    	startLoopCheckingCurrentSong();
        //showCurrentSong();
        return true;
    }

    function onSelect() {
    	System.println("Select");
        startLoopCheckingCurrentSong();
        return true;
    }
    
    function startLoopCheckingCurrentSong() {
    	if (_allItems.size() == 0) {
    		
    		showProgress();
    		
    		var timer = new Timer.Timer();
			timer.start(method(:showCurrentSong), 9000, true);
			_mainTimer = timer;
		}
        
    }
    
    function showProgress() {
    	_progressBar = new Ui.ProgressBar(
	            "Loading ...",
	            0
	        );
	        
	    Ui.pushView(_progressBar, self, Ui.SLIDE_DOWN);
    	_progressBar.setProgress(30);
    }
     
    // Set up the callback to the view
    function initialize(handler) {
        Ui.BehaviorDelegate.initialize();
        _notify = handler;
    }

    function makeRequestTo(toDo) {
        //_notify.invoke("...");
        resetState();
        
        var headers = {
        	"Content-Type" => Comm.REQUEST_CONTENT_TYPE_URL_ENCODED
        };
        
        var url  = WatchUi.loadResource(Rez.Strings.backend_url);
        var user = WatchUi.loadResource(Rez.Strings.last_fm_user);
		
        Comm.makeWebRequest(//url, parameters, options, responseCallback) (
            url,
            {
            	"user" => user
            },
            {
                //"Content-Type" => Comm.REQUEST_CONTENT_TYPE_URL_ENCODED,
                //"Content-Type" => Comm.REQUEST_CONTENT_TYPE_JSON,
                :method => Comm.HTTP_REQUEST_METHOD_GET,
                :headers => headers,
                :responseType  => Comm.HTTP_RESPONSE_CONTENT_TYPE_JSON
            },
            toDo
        );        
    }
       
    function showCurrentSong() {
    	if (_allItems.size() == 0) {   
	    	_progressBar.setProgress(60);   	
    	}
    
    	makeRequestTo(method(:onReceive));
    }
    
    function resetState() {
    	_shift = 0;
    	_max = 8;
    }
    
    function switchToWelcome() {
    	Ui.switchToView(new WelcomeView(), self, Ui.SLIDE_IMMEDIATE);
    }
    
    // Receive the data from the web request
    function onReceive(responseCode, data) {
    	if (responseCode != 200) {
    		// TODO errors            
            var message = "Failed.\nError: " + responseCode.toString();            
            if (responseCode == -104) {
            	message += ".\nPlz, check connection";
            }  
			
			// Display error message			
			_progressBar.setDisplayString(message);
			
			// After 2 seconds switch back to welcome page, so user can retry
			var timer = new Timer.Timer();
			timer.start(method(:switchToWelcome), 2000, false);
            return;
    	}
        
        var song = parseSong(data);        
        addNewItems(song);        
        
        _progressBar.setProgress(100); 
        _notify.invoke("Press >");           
        showResults(_allItems);
    }
    
    function addNewItems(song) {    	
    	
    	for (var i = 0; i < _allItems.size(); i++) {    			
                var item = _allItems[i];              
            	if (item.equals(song)) {
            		return;
            	}
        }
		
		if (_allItems.size() > 4) {
			_allItems = _allItems.slice(2, _allItems.size());
		}
		
		if (_allItems.size() > 0) {
			_allItems.add("---------------------------\n");
		}
		
        _allItems.add(song);
    }
       
    function showResults(results) {    
    	Ui.popView(Ui.SLIDE_IMMEDIATE);	
    	Ui.switchToView(new ResultsView(results.slice(_shift, _step)), 
    				new ResultsPageDelegate(results, _shift, _step, self), 
    				Ui.SLIDE_IMMEDIATE
    	); 
    }
    
    function reset() {
    	_mainTimer.stop();
    	_allItems = [];
    }
    
    function showMessagePage(results, allLines) {
    	Ui.switchToView(new ResultsView(results.slice(_shift, _step)), 
    			new ResultsMessageDelegate(results, allLines), Ui.SLIDE_IMMEDIATE); 
    }
     
    
    function parseSong(json_string) { 
        return "" + json_string.get("artist") + "\n" + json_string.get("track") + "\n";
    }
      
    
    // When a next page behavior occurs, onNextPage() is called.
    // @return [Boolean] true if handled, false otherwise
    //function onNextPage() {
    //	onNextPageP();
        //return true;   
    //}

    // When a previous page behavior occurs, onPreviousPage() is called.
    // @return [Boolean] true if handled, false otherwise
    //function onPreviousPage() {}

    // When a menu behavior occurs, onMenu() is called.
    // @return [Boolean] true if handled, false otherwise
    //function onMenu() {}

    // When a back behavior occurs, onBack() is called.
    // @return [Boolean] true if handled, false otherwise
    function onBack() {
    	System.exit();
    	return true;
    }

    // When a next mode behavior occurs, onNextMode() is called.
    // @return [Boolean] true if handled, false otherwise
    //function onNextMode() {}

    // When a previous mode behavior occurs, onPreviousMode() is called.
    // @return [Boolean] true if handled, false otherwise
    //function onPreviousMode() {
    //	Ui.popView(Ui.SLIDE_IMMEDIATE);
    //}
}