#!/bin/sh

if [ $# -lt 2 ] ; then \
	echo usage: `basename $0` SITE_XML FEATURE_MAP
	exit 1
fi

if [ ! -f mksfsite.xsl ] ; then \
	echo File mksfsite.xsl is missing.
	exit 1
fi

if [ ! -f getMirrorIDs.xsl ] ; then \
	echo File getMirrorIDs.xsl is missing.
	exit 1
fi

if [ ! -f mirrors.xml ] ; then \
	echo File mirrors.xml is missing.
	exit 1
fi

if [ ! -f site.xsl ] ; then \
	echo File site.xsl is missing.
	exit 1
fi

echo -n Creating SourceForge-compatible site.xml... 
SITE=$1
FEATURE_MAP=$2
xsltproc --stringparam featureMap $FEATURE_MAP mksfsite.xsl $SITE > sfsite.xml
echo OK

echo Creating mirror sites...
MIRRORS=`xsltproc --nonet getMirrorIDs.xsl mirrors.xml`
for foo in $MIRRORS; do \
	echo $foo
	test -d $foo || mkdir $foo
	xsltproc --nonet --stringparam mirror $foo site.xsl sfsite.xml > $foo/site.xml 
done
echo OK
