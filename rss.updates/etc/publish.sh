#!/bin/sh
# Publishes current directory to remote site.
# Author: Peter Nehrer <pnehrer@freeshell.org>
# Version $Id$

if [ $# -lt 2 ] ; then \
	echo usage: `basename $0` HOST DIR
	exit 1
fi

HOST=$1
DIR=$2
REMOTE_CMD="tar -xvf - -C $DIR && chmod -R a+r $DIR"
tar -cf - `find -type f -not -path '*/CVS/*'` | ssh $HOST "$REMOTE_CMD"
