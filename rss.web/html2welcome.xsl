<?xml version="1.0" encoding="UTF-8"?>
<!--
	Converts an HTML page to an Eclipse welcome page. Use xsltproc \-\-html.
	Author: Peter Nehrer <pnehrer@freeshell.org>
	Version: $Id$
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output indent="yes"/>
	<xsl:template match="html">
		<xsl:apply-templates select="body"/>
	</xsl:template>
	<xsl:template match="body">
		<xsl:element name="welcomePage">
			<xsl:attribute name="title"><xsl:value-of select="../head/title"/></xsl:attribute>
			<xsl:attribute name="format">wrap</xsl:attribute>
			<xsl:apply-templates select="descendant::h1 | descendant::h2"/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="h1">
		<intro>
			<xsl:apply-templates select="following-sibling::node()[not(preceding-sibling::h2) and not(self::h2)]"/>
		</intro>
	</xsl:template>
	<xsl:template match="h2">
		<xsl:variable name="i" select="count(preceding-sibling::h2) + 1"/>
		<item>
			<b><xsl:value-of select="."/></b>
			<xsl:apply-templates select="following-sibling::node()[count(preceding-sibling::h2)=$i]"/>
		</item>
	</xsl:template>
	<xsl:template match="ul | ol | dl">
		<xsl:apply-templates select="li | dt"/>
	</xsl:template>
	<xsl:template match="dt">
		<p><b><xsl:value-of select="."/></b></p>
		<xsl:apply-templates select="following-sibling::dd[1]"/>
	</xsl:template>
	<xsl:template match="h3">
		<xsl:variable name="i" select="count(preceding-sibling::h3) + 1"/>
		<p><b><xsl:value-of select="."/></b></p>
		<xsl:apply-templates select="following-sibling::node()[count(preceding-sibling::h3)=$i]"/>
	</xsl:template>
	<xsl:template match="p | dd">
		<p><xsl:apply-templates select="node()"/></p>
	</xsl:template>
	<xsl:template match="li[parent::ol]">
		<p><xsl:value-of select="count(preceding-sibling::li) + 1"/>. <xsl:apply-templates select="node()"/></p>
	</xsl:template>
	<xsl:template match="li[parent::ul]">
		<p>- <xsl:apply-templates select="node()"/></p>
	</xsl:template>
</xsl:stylesheet>
