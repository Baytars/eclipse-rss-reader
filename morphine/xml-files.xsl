<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="/">
		<xml-files>
			<xsl:apply-templates select="directory"/>
		</xml-files>
	</xsl:template>
	
	<xsl:template match="directory">
		<xsl:apply-templates select="directory"/>
		<xsl:apply-templates select="file[substring(@name, string-length(@name) - 3)='.xml']"/>
	</xsl:template>
	
	<xsl:template match="file">
		<xsl:copy-of select="."/>
	</xsl:template>
</xsl:stylesheet>