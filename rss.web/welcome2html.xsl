<?xml version="1.0" encoding="UTF-8"?>
<!--
	Converts an Eclipse welcome page to HTML.
	Author: Peter Nehrer <pnehrer@freeshell.org>
	Version: $Id$
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="html"/>
	<xsl:template match="welcomePage">
		<xsl:comment> tpl:insert page="/theme/home.htpl" </xsl:comment>
			<HTML><HEAD>
		<xsl:comment> tpl:put name="head" </xsl:comment>
		<xsl:apply-templates select="@title" mode="head"/>
		<xsl:comment> /tpl:put </xsl:comment>
			</HEAD><BODY>
		<xsl:comment> tpl:put name="body" </xsl:comment>
		<xsl:apply-templates select="@title"/>
		<xsl:apply-templates select="intro"/>
		<xsl:apply-templates select="item"/>
		<xsl:comment> /tpl:put </xsl:comment>
			</BODY></HTML>
		<xsl:comment> /tpl:insert </xsl:comment>
	</xsl:template>
	<xsl:template match="@title" mode="head">
		<TITLE><xsl:value-of select="."/></TITLE>
	</xsl:template>
	<xsl:template match="@title">
		<H1><xsl:value-of select="."/></H1>
	</xsl:template>
	<xsl:template match="intro | item | action">
		<xsl:apply-templates select="node()"/>
	</xsl:template>
	<xsl:template match="topic[@href]">
		<xsl:element name="A">
			<xsl:attribute name="href"><xsl:value-of select="@href"/></xsl:attribute>
			<xsl:apply-templates select="node()"/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="b[parent::item][not(preceding-sibling::b)]">
		<H2><xsl:apply-templates select="node()"/></H2>
	</xsl:template>
	<xsl:template match="@* | node()">
		<xsl:copy>
			<xsl:apply-templates select="@* | node()"/>
		</xsl:copy>
	</xsl:template>
</xsl:stylesheet>
