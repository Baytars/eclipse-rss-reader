<?xml version="1.0" encoding="UTF-8"?>
<!--
	Transforms a site.xml into a SourceForge-compatible site.xml
	(i.e., explicitly resolves all archive URLs).
	Author: <a href="pnehrer@freeshell.org">Peter Nehrer</a>
	Version $Id$
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:param name="featureMap">featureMap.xml</xsl:param>

<xsl:template match="site">
	<xsl:copy>
		<xsl:apply-templates select="@*"/>
		<xsl:apply-templates select="description | feature | archive"/>
		<xsl:apply-templates select="feature" mode="archives"/>
		<xsl:apply-templates select="category-def"/>
	</xsl:copy>
</xsl:template>

<xsl:template match="feature" mode="archives">
	<xsl:variable name="path"><xsl:value-of select="document($featureMap)/features/feature[@id=current()/@id]/@path"/></xsl:variable>
	<xsl:apply-templates select="document($path)/feature/plugin" mode="archive"/>
</xsl:template>

<xsl:template match="plugin" mode="archive">
	<xsl:element name="archive">
		<xsl:attribute name="path">plugins/<xsl:value-of select="concat(@id, '_', @version)"/>.jar</xsl:attribute>
		<xsl:attribute name="url">plugins/<xsl:value-of select="concat(@id, '_', @version)"/>.jar</xsl:attribute>
	</xsl:element>
</xsl:template>

<xsl:template match="node() | @*">
	<xsl:copy>
		<xsl:apply-templates select="node() | @*"/>
	</xsl:copy>
</xsl:template>

</xsl:stylesheet>
