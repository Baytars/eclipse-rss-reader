<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:runtime="xalan://org.eclipse.core.runtime" 
	xmlns:resources="xalan://org.eclipse.core.resources" 
	exclude-result-prefixes="runtime resources" 
	version="1.0">
	
<xsl:output method="xml" indent="yes"/>

<xsl:param name="project"/>
<xsl:param name="label" select="document('plugin.xml')/plugin/@name"/>
<xsl:param name="link_to"/>
<xsl:variable name="_workspace" select="resources:ResourcesPlugin.getWorkspace()"/>
<xsl:variable name="_workspaceRoot" select="resources:getRoot($_workspace)"/>
<xsl:variable name="_project" select="resources:getProject($_workspaceRoot, $project)"/>

<xsl:template match="website[@version='510']">
	<xsl:element name="toc">
		<xsl:attribute name="label"><xsl:value-of select="$label"/></xsl:attribute>
		<xsl:if test="string-length($link_to) &gt; 0">
			<xsl:attribute name="link_to"><xsl:value-of select="$link_to"/></xsl:attribute>
		</xsl:if>
		<xsl:apply-templates select="structure/page"/>
	</xsl:element>
</xsl:template>

<xsl:template match="page">
	<xsl:element name="topic">
		<xsl:attribute name="label"><xsl:value-of select="title"/></xsl:attribute>
		<xsl:variable name="path" select="runtime:Path.new(@src)"/>
		<xsl:variable name="file" select="resources:getFile($_project, $path)"/>
		<xsl:if test="resources:exists($file)">
			<xsl:attribute name="href"><xsl:value-of select="runtime:makeRelative($path)"/></xsl:attribute>
		</xsl:if>
		<xsl:apply-templates select="page"/>
	</xsl:element>
</xsl:template>

</xsl:stylesheet>
