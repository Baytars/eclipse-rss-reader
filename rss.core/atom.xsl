<?xml version="1.0" encoding="UTF-8"?>
<!--
	Translates the Atom 0.3 format into the internal RSS format.
	Author: <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
	Version $Id$
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:atom="http://purl.org/atom/ns#">

    <xsl:template match="atom:feed">
    	<xsl:element name="channel">
    		<xsl:apply-templates select="atom:title | atom:link[@rel='alternate'] | atom:modified | atom:tagline"/>
    		<xsl:apply-templates select="@*"/>
    		<xsl:apply-templates select="node()[not(self::atom:title | self::atom:link[@rel='alternate'] | self::atom:modified | self::atom:tagline)]"/>
    	</xsl:element>
    </xsl:template>
    
    <xsl:template match="atom:entry">
    	<xsl:element name="item">
    		<xsl:apply-templates select="atom:title | atom:link[@rel='alternate'] | atom:modified"/>
    		<xsl:choose>
    			<xsl:when test="atom:summary">
    				<xsl:apply-templates select="atom:summary"/>
    			</xsl:when>
    			<xsl:when test="atom:content">
    				<xsl:apply-templates select="atom:content[1]"/>
    			</xsl:when>
    		</xsl:choose>
    		<xsl:apply-templates select="@*"/>
    		<xsl:apply-templates select="node()[not(self::atom:title | self::atom:link[@rel='alternate'] | self::atom:summary | self::atom:content | self::atom:modified)]"/>
    	</xsl:element>
    </xsl:template>

    <xsl:template match="atom:title[parent::atom:feed | parent::atom:entry]">
    	<xsl:attribute name="title"><xsl:value-of select="."/></xsl:attribute>
    </xsl:template>

    <xsl:template match="atom:link[parent::atom:feed | parent::atom:entry][@rel='alternate']">
    	<xsl:attribute name="link"><xsl:value-of select="@href"/></xsl:attribute>
    </xsl:template>

    <xsl:template match="atom:tagline[parent::atom:feed]">
    	<xsl:attribute name="description"><xsl:value-of select="."/></xsl:attribute>
    </xsl:template>

    <xsl:template match="node()[parent::atom:entry][self::atom:summary | self::atom:content]">
    	<xsl:attribute name="description"><xsl:value-of select="."/></xsl:attribute>
    </xsl:template>

    <xsl:template match="atom:modified[parent::atom:feed | parent::atom:entry]">
    	<xsl:attribute name="date"><xsl:choose><xsl:when test="substring(., string-length(.)) = 'Z'"><xsl:value-of select="substring(., 1, string-length(.) - 1)"/>-0000</xsl:when><xsl:otherwise><xsl:value-of select="."/></xsl:otherwise></xsl:choose></xsl:attribute>
    </xsl:template>
    
    <xsl:template match="text()[parent::atom:feed | parent::atom:entry]"/>
    
    <xsl:template match="node() | @*">
    	<xsl:copy>
    		<xsl:apply-templates select="@* | node()"/>
    	</xsl:copy>
    </xsl:template>
</xsl:stylesheet>
