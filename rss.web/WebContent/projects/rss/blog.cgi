#!/bin/sh
echo Content-type: text/html
echo
if [ -f blog.html ] ; then 
	TS_CACHE=`stat -f "%m" blog.html`
else
	TS_CACHE=0
fi
TS_NOW=`date +"%s"`
TS_YESTERDAY=`expr $TS_NOW - 86400`
if [ ! -f blog.html -o $TS_CACHE -lt $TS_YESTERDAY ] ; then \
	wget -q -O blog.tmp 'http://jroller.com/page/pnehrer/rssblog?catname=Eclipse%20RSS%20Reader' > /dev/null && mv -f blog.tmp blog.html
fi
cat blog.html