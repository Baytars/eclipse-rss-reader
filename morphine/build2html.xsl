<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="project">
		<html>
			<head>
				<title>Project <xsl:value-of select="@name"/></title>
			</head>
			<body>
				<h1>Project <xsl:value-of select="@name"/></h1>
				<p><b>Targets:</b></p>
				<ul><xsl:apply-templates select="target"/></ul>
			</body>
		</html>
	</xsl:template>
	
	<xsl:template match="target">
		<li><xsl:value-of select="@name"/></li>
	</xsl:template>
</xsl:stylesheet>