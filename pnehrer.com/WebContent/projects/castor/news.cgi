#!/bin/sh
echo Content-type: text/html
echo
if [ -f news.html ] ; then 
	TS_CACHE=`stat -f "%m" news.html`
else
	TS_CACHE=0
fi
TS_NOW=`date +"%s"`
TS_YESTERDAY=`expr $TS_NOW - 86400`
if [ ! -f news.html -o $TS_CACHE -lt $TS_YESTERDAY ] ; then \
	wget -q -O news.tmp 'http://xdoclipse.sourceforge.net/presence/projects/castor/news.html' > /dev/null && mv -f news.tmp news.html
fi
cat news.html