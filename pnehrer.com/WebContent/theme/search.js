// Support for site-wide search.
// Author: Peter Nehrer <pnehrer@freeshell.org>
// Version: $Id$

function search(what, event) {
	if(!event)
		event = window.event;
		
	if(event && (event.keyCode == 13 || event.keyCode == 10))
		window.location.replace("http://www.google.com/search?q=" + escape(what.value + " site:pnehrer.freeshell.org"));
}
