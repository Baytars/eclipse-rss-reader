<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="text"/>
	
	<xsl:template match="mirrors">
		<xsl:apply-templates select="mirror"/>
	</xsl:template>
	
	<xsl:template match="mirror">
		<xsl:if test="count(preceding-sibling::mirror)&gt;0">
			<xsl:text>
</xsl:text>
		</xsl:if>
		<xsl:value-of select="@id"/>
	</xsl:template>
</xsl:stylesheet>
