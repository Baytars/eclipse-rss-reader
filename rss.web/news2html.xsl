<?xml version="1.0" encoding="UTF-8"?>
<!--
	Converts an RSS 2.0 feed into a PHP template insert.
	Author: <a href="pnehrer@users.sourceforge.net">Peter Nehrer</a>
	Version $Id$
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="html"/>
<xsl:template match="/">
	<xsl:apply-templates select="rss/channel"/>
</xsl:template>
<xsl:template match="channel">
	<DL>
		<xsl:apply-templates select="item"/>
	</DL>
</xsl:template>
<xsl:template match="item">
	<DT><xsl:apply-templates select="pubDate"/>: <xsl:value-of select="title"/></DT>
	<DD><xsl:value-of select="description" disable-output-escaping="yes"/></DD>
</xsl:template>
<!-- EEE, dd MMM yyyy HH:mm:ss z -->
<xsl:template match="pubDate">
	<xsl:variable name="s0" select="substring-after(., ', ')"/>
	<xsl:variable name="s1" select="substring-after($s0, ' ')"/>
	<xsl:variable name="s2" select="substring-after($s1, ' ')"/>
	<xsl:value-of select="concat(substring-before($s0, ' '), ' ', substring-before($s1, ' '), ' ', substring-before($s2, ' '))"/>
</xsl:template>
</xsl:stylesheet>
