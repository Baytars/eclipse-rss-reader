<?xml version="1.0" encoding="UTF-8"?>
<!--
	Translates a Bugzilla XML bug detail into the internal RSS format.
	Author: <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
	Version $Id$
-->
<xsl:stylesheet 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	version="1.0">
	
<xsl:template match="bugzilla">
	<xsl:apply-templates select="bug[1]"/>
</xsl:template>

<xsl:template match="bug">
	<channel title="Bugzilla detail: {bug_id}" link="{parent::bugzilla/@urlbase}show_bug.cgi?id={bug_id}" description="{short_desc}" date="{creation_ts}">
		<xsl:apply-templates select="long_desc"/>
		<textInput title="Search Bugzilla" link="{parent::bugzilla/@urlbase}buglist.cgi" description="Enter Bugzilla query:" name="content"/>
	</channel>
</xsl:template>

<xsl:template match="long_desc">
	<item title="{who}" link="#c{1 + count(preceding-sibling::long_desc)}" description="{thetext}" date="{bug_when}"/>
</xsl:template>

</xsl:stylesheet>