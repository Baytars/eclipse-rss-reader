<?xml version="1.0" encoding="UTF-8"?>
<!--
	Translates simple RSS formats, such as RSS 0.91, 0.92, and 2.0, into 
	the internal RSS format.
	Author: <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
	Version $Id$
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

    <xsl:template match="rss">
    	<xsl:apply-templates select="channel"/>
    </xsl:template>
    
    <xsl:template match="channel">
    	<xsl:element name="channel">
    		<xsl:apply-templates select="title | link | description | pubDate"/>
    		<xsl:apply-templates select="@*"/>
    		<xsl:apply-templates select="node()[not(self::title | self::link | self::description | self::pubDate)]"/>
    	</xsl:element>
    </xsl:template>
    
    <xsl:template match="image">
    	<xsl:element name="image">
    		<xsl:apply-templates select="title | link | url"/>
    		<xsl:apply-templates select="@*"/>
    		<xsl:apply-templates select="node()[not(self::title | self::link | self::url)]"/>
    	</xsl:element>
    </xsl:template>

    <xsl:template match="item">
    	<xsl:element name="item">
    		<xsl:apply-templates select="title | link | description | pubDate"/>
    		<xsl:apply-templates select="@*"/>
    		<xsl:apply-templates select="node()[not(self::title | self::link | self::description | self::pubDate)]"/>
    	</xsl:element>
    </xsl:template>

    <xsl:template match="textinput">
    	<xsl:element name="textInput">
    		<xsl:apply-templates select="title | link | description | name"/>
    		<xsl:apply-templates select="@*"/>
    		<xsl:apply-templates select="node()[not(self::title | self::link | self::description | self::name)]"/>
    	</xsl:element>
    </xsl:template>

    <xsl:template match="title | link | description | url">
    	<xsl:attribute name="{local-name()}"><xsl:value-of select="."/></xsl:attribute>
    </xsl:template>

    <xsl:template match="pubDate">
    	<xsl:attribute name="date"><xsl:value-of select="."/></xsl:attribute>
    </xsl:template>
    
    <xsl:template match="text()[parent::channel | parent::image | parent::item]"/>
    
    <xsl:template match="node() | @*">
    	<xsl:copy>
    		<xsl:apply-templates select="@* | node()"/>
    	</xsl:copy>
    </xsl:template>
</xsl:stylesheet>
