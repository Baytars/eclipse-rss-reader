#!/bin/sh

if [ $# -lt 3 ] ; then \
	echo usage: `basename $0` SITE_XML FEATURE_MAP MIRRORS
	exit 1
fi

BASEDIR=`dirname $0`

if [ ! -f $BASEDIR/mksfsite.xsl ] ; then \
	echo File mksfsite.xsl is missing.
	exit 1
fi

if [ ! -f $BASEDIR/getMirrorIDs.xsl ] ; then \
	echo File getMirrorIDs.xsl is missing.
	exit 1
fi

if [ ! -f $BASEDIR/site.xsl ] ; then \
	echo File site.xsl is missing.
	exit 1
fi

echo -n Creating SourceForge-compatible site.xml... 
SITE=$1
FEATURE_MAP=$2
xsltproc --stringparam featureMap $FEATURE_MAP $BASEDIR/mksfsite.xsl $SITE > sfsite.xml
echo OK

echo Creating mirror sites...
MIRRORS_XML=$3
MIRRORS=`xsltproc --nonet $BASEDIR/getMirrorIDs.xsl $MIRRORS_XML`
for foo in $MIRRORS; do \
	echo $foo
	test -d $foo || mkdir $foo
	xsltproc --nonet --stringparam mirror $foo $BASEDIR/site.xsl sfsite.xml > $foo/site.xml
done
echo OK
