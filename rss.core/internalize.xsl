<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
	xmlns:rss="http://purl.org/rss/1.0/"
	exclude-result-prefixes="rdf rss"
    version="1.0">
    
    <xsl:template match="rss">
    	<xsl:apply-templates select="channel"/>
    </xsl:template>
    
    <xsl:template match="rdf:RDF">
    	<xsl:apply-templates select="rss:channel"/>
    </xsl:template>
    
    <xsl:template match="channel | rss:channel">
    	<xsl:element name="channel">
    		<xsl:apply-templates select="title | rss:title | link | rss:link | description | rss:description | pubDate"/>
    		<xsl:apply-templates select="node()[not(self::title | self::rss:title | self::link | self::rss:link | self::description | self::rss:description | self::pubDate)]"/>
    	</xsl:element>
    </xsl:template>
    
    <xsl:template match="rss:image[@rdf:resource]">
    	<xsl:apply-templates select="/rdf:RDF/rss:image[@rdf:about=current()/@rdf:resource]"/>
    </xsl:template>
    
    <xsl:template match="image | rss:image[@rdf:about]">
    	<xsl:element name="image">
    		<xsl:apply-templates select="title | rss:title | link | rss:link | url | rss:url"/>
    		<xsl:apply-templates select="node()[not(self::title | self::rss:title | self::link | self::rss:link | self::url | self::rss:url)]"/>
    	</xsl:element>
    </xsl:template>

	<xsl:template match="rss:items">
		<xsl:apply-templates select="rdf:Seq/rdf:li"/>
	</xsl:template>
    
    <xsl:template match="rdf:li">
    	<xsl:apply-templates select="/rdf:RDF/rss:item[@rdf:about=current()/@rdf:resource]"/>
    </xsl:template>
    
    <xsl:template match="item | rss:item">
    	<xsl:element name="item">
    		<xsl:apply-templates select="title | rss:title | link | rss:link | description | rss:description | pubDate"/>
    		<xsl:apply-templates select="node()[not(self::title | self::rss:title | self::link | self::rss:link | self::description | self::rss:description | self::pubDate)]"/>
    	</xsl:element>
    </xsl:template>

    <xsl:template match="rss:textinput[@rdf:resource]">
    	<xsl:apply-templates select="/rdf:RDF/rss:textinput[@rdf:about=current()/@rdf:resource]"/>
    </xsl:template>
    
    <xsl:template match="rss:textinput[@rdf:about]">
    	<xsl:element name="textInput">
    		<xsl:apply-templates select="rss:title | rss:link | rss:description | rss:name"/>
    		<xsl:apply-templates select="node()[not(self::rss:title | self::rss:link | self::rss:description | self::rss:name)]"/>
    	</xsl:element>
    </xsl:template>

    <xsl:template match="title | link | description | url | rss:title | rss:link | rss:description | rss:url | rss:name">
    	<xsl:attribute name="{local-name()}"><xsl:value-of select="."/></xsl:attribute>
    </xsl:template>

    <xsl:template match="pubDate">
    	<xsl:attribute name="date"><xsl:value-of select="."/></xsl:attribute>
    </xsl:template>
    
    <xsl:template match="text()[parent::channel | parent::image | parent::item | parent::rss:channel | parent::rss:image | parent::rss:item | parent::rss:textinput]"/>
    
    <xsl:template match="node() | @*">
    	<xsl:copy>
    		<xsl:apply-templates select="node() | @*"/>
    	</xsl:copy>
    </xsl:template>
</xsl:stylesheet>
