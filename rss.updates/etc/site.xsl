<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:param name="project">morphine</xsl:param>
<xsl:param name="mirror">unc</xsl:param>

<xsl:template match="site">
	<xsl:copy>
		<xsl:attribute name="url">http://<xsl:value-of select="$mirror"/>.dl.sourceforge.net/sourceforge/<xsl:value-of select="$project"/>/</xsl:attribute>
		<xsl:apply-templates select="node() | @*[not(local-name()='url')]"/>
	</xsl:copy>
</xsl:template>

<xsl:template match="feature">
	<xsl:copy>
		<xsl:attribute name="url">f_<xsl:value-of select="substring-after(@url, 'features/')"/></xsl:attribute>
		<xsl:apply-templates select="node() | @*[not(local-name()='url')]"/>
	</xsl:copy>
</xsl:template>

<xsl:template match="archive">
	<xsl:copy>
		<xsl:attribute name="url">p_<xsl:value-of select="substring-after(@url, 'plugins/')"/></xsl:attribute>
		<xsl:apply-templates select="node() | @*[not(local-name()='url')]"/>
	</xsl:copy>
</xsl:template>

<xsl:template match="node() | @*">
	<xsl:copy>
		<xsl:apply-templates select="node() | @*"/>
	</xsl:copy>
</xsl:template>

</xsl:stylesheet>
