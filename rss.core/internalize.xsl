<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
	xmlns:rss09="http://my.netscape.com/rdf/simple/0.9/"
	xmlns:rss10="http://purl.org/rss/1.0/"
	exclude-result-prefixes="rdf rss09 rss10"
    version="1.0">
    
    <xsl:template match="rss">
    	<xsl:apply-templates select="channel"/>
    </xsl:template>
    
    <xsl:template match="rdf:RDF">
    	<xsl:apply-templates select="rss09:channel"/>
    	<xsl:apply-templates select="rss10:channel"/>
    </xsl:template>
    
    <xsl:template match="channel | rss09:channel | rss10:channel">
    	<xsl:element name="channel">
    		<xsl:apply-templates select="title | rss09:title | rss10:title | link | rss09:link | rss10:link | description | rss09:description | rss10:description | pubDate"/>
    		<xsl:apply-templates select="node()[not(self::title | self::rss09:title | self::rss10:title | self::link | self::rss09:link | self::rss10:link | self::description | self::rss09:description | self::rss10:description | self::pubDate)]"/>
			<xsl:apply-templates select="following-sibling::rss09:image | following-sibling::rss09:item | following-sibling::rss09:textinput"/>
    	</xsl:element>
    </xsl:template>
    
    <xsl:template match="rss10:image[@rdf:resource]">
    	<xsl:apply-templates select="/rdf:RDF/rss10:image[@rdf:about=current()/@rdf:resource]"/>
    </xsl:template>
    
    <xsl:template match="image | rss09:image | rss10:image[@rdf:about]">
    	<xsl:element name="image">
    		<xsl:apply-templates select="title | rss09:title | rss10:title | link | rss09:link | rss10:link | url | rss09:url | rss10:url"/>
    		<xsl:apply-templates select="node()[not(self::title | self::rss09:title | self::rss10:title | self::link | self::rss09:link | self::rss10:link | self::url | self::rss09:url | self::rss10:url)]"/>
    	</xsl:element>
    </xsl:template>

	<xsl:template match="rss10:items">
		<xsl:apply-templates select="rdf:Seq/rdf:li"/>
	</xsl:template>
    
    <xsl:template match="rdf:li">
    	<xsl:apply-templates select="/rdf:RDF/rss10:item[@rdf:about=current()/@rdf:resource]"/>
    </xsl:template>
    
    <xsl:template match="item | rss09:item | rss10:item">
    	<xsl:element name="item">
    		<xsl:apply-templates select="title | rss09:title | rss10:title | link | rss09:link | rss10:link | description | rss10:description | pubDate"/>
    		<xsl:apply-templates select="node()[not(self::title | self::rss09:title | self::rss10:title | self::link | self::rss09:link | self::rss10:link | self::description | self::rss10:description | self::pubDate)]"/>
    	</xsl:element>
    </xsl:template>

    <xsl:template match="rss10:textinput[@rdf:resource]">
    	<xsl:apply-templates select="/rdf:RDF/rss10:textinput[@rdf:about=current()/@rdf:resource]"/>
    </xsl:template>
    
    <xsl:template match="textinput | rss09:textinput | rss10:textinput[@rdf:about]">
    	<xsl:element name="textInput">
    		<xsl:apply-templates select="rss09:title | rss10:title | rss09:link | rss10:link | rss09:description | rss10:description | rss09:name | rss10:name"/>
    		<xsl:apply-templates select="node()[not(self::rss09:title | self::rss10:title | self::rss09:link | self::rss10:link | self::rss09:description | self::rss10:description | self::rss09:name | self::rss10:name)]"/>
    	</xsl:element>
    </xsl:template>

    <xsl:template match="title | link | description | url | rss09:title | rss09:link | rss09:description | rss09:url | rss09:name | rss10:title | rss10:link | rss10:description | rss10:url | rss10:name">
    	<xsl:attribute name="{local-name()}"><xsl:value-of select="."/></xsl:attribute>
    </xsl:template>

    <xsl:template match="pubDate">
    	<xsl:attribute name="date"><xsl:value-of select="."/></xsl:attribute>
    </xsl:template>
    
    <xsl:template match="text()[parent::channel | parent::image | parent::item | parent::rss09:channel | parent::rss09:image | parent::rss09:item | parent::rss09:textinput | parent::rss10:channel | parent::rss10:image | parent::rss10:item | parent::rss10:textinput]"/>
    
    <xsl:template match="node() | @*">
    	<xsl:copy>
    		<xsl:apply-templates select="node() | @*"/>
    	</xsl:copy>
    </xsl:template>
</xsl:stylesheet>
