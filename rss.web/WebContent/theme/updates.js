// updates.js

function getArgs() {
   var args = new Object();
   var query = location.search.substring(1);
   var pairs = query.split("&");
   for(var i = 0; i < pairs.length; ++i) {
      var pos = pairs[i].indexOf('=');
      if(pos == -1) 
      	continue;

      var argname = pairs[i].substring(0, pos);
      var value = pairs[i].substring(pos + 1);
      args[argname] = unescape(value);
   }

   return args;
}

function setCookie(name, value, expire) {
	document.cookie = name + "=" + escape(value)
		+ ((expire == null) ? "" : ("; expires=" + expire.toGMTString()));
}

function getCookie(Name) {
	var search = Name + "=";
	if(document.cookie.length > 0) { // if there are any cookies
		offset = document.cookie.indexOf(search);
		if(offset != -1) { // if cookie exists 
			offset += search.length;
		
			// set index of beginning of value
			end = document.cookie.indexOf(";", offset);
			
			// set index of end of cookie value
			if(end == -1) 
				end = document.cookie.length;
			
			return unescape(document.cookie.substring(offset, end));
		}
	}
}

function persistMirror() {
	var mirror = null;
	var args = getArgs();
	if(args.mirror) {
		mirror = args.mirror;
		setCookie("mirror", mirror);
	}
	else
		mirror = getCookie("mirror");
	
	return mirror;
}

function persistUpdateURL() {
	var updateURL = null;
	var args = getArgs();
	if(args.updateURL) {
		updateURL = args.updateURL;
		setCookie("updateURL", updateURL);
	}
	else
		updateURL = getCookie("updateURL");

	return updateURL;
}
