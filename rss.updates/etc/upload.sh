#!/bin/sh
# Uploads update site archives to SourceForge.
# Author: Peter Nehrer <pnehrer@freeshell.org>
# Version $Id$

test -d plugins || (echo Directory plugins not found!; exit 1)
test -d features || (echo Directory features not found!; exit 1)

SERVER=upload.sourceforge.net
USERID=anonymous
PASSWORD=pnehrer@users.sourceforge.net
TMPDIR=/tmp/updates.tmp
test -d $TMPDIR && rm -fR $TMPDIR
mkdir -p $TMPDIR
for foo in plugins/*.jar; do cp $foo $TMPDIR/p_`basename $foo`; done
for foo in features/*.jar; do cp $foo $TMPDIR/f_`basename $foo`; done
ncftpput -u $USERID -p $PASSWORD -R $SERVER incoming $TMPDIR/*.jar
rm -fR $TMPDIR
