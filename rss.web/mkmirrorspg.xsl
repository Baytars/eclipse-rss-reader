<?xml version="1.0" encoding="UTF-8"?>
<!--
	Generates a table of SourceForge mirrors.
	Author: Peter Nehrer <pnehrer@freeshell.org>
	Version: $Id$
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="html"/>
	<xsl:template match="mirrors">
		<xsl:comment>BEGIN mkmirrorspg.xsl</xsl:comment>
		<TABLE border="0" cellpadding="2" cellspacing="2" width="90%" align="center" class="tableList">
			<CAPTION class="tableList">SourceForge Mirrors</CAPTION>
			<TR>
				<TH class="tableList">Mirror</TH>
				<TH class="tableList">Location</TH>
			</TR>
			<xsl:apply-templates select="mirror"/>
			<TR>
				<TD colspan="2" align="right"><HR noshade="true" size="1"/><INPUT type="submit" value="Select"/></TD>
			</TR>
		</TABLE>
		<xsl:comment>END mkmirrorspg.xsl</xsl:comment>
	</xsl:template>
	
	<xsl:template match="mirror">
		<TR>
			<xsl:element name="TD">
				<xsl:if test="count(preceding-sibling::mirror) mod 2 = 0">
					<xsl:attribute name="class">oddRowOddColumn</xsl:attribute>
				</xsl:if>
				<xsl:if test="count(preceding-sibling::mirror) mod 2 = 1">
					<xsl:attribute name="class">evenRowOddColumn</xsl:attribute>
				</xsl:if>
				<xsl:element name="INPUT">
					<xsl:attribute name="id">mirror_<xsl:value-of select="@id"/></xsl:attribute>
					<xsl:attribute name="type">radio</xsl:attribute>
					<xsl:attribute name="name">mirror</xsl:attribute>
					<xsl:attribute name="value"><xsl:value-of select="@id"/></xsl:attribute>
					<xsl:attribute name="onclick">setMirror(this.value)</xsl:attribute>
				</xsl:element>
				<xsl:element name="A">
					<xsl:attribute name="class">tableListLink</xsl:attribute>
					<xsl:attribute name="href"><xsl:value-of select="@link"/></xsl:attribute>
					<xsl:if test="@image">
						<xsl:element name="IMG">
							<xsl:attribute name="src"><xsl:value-of select="@image"/></xsl:attribute>
							<xsl:attribute name="alt"><xsl:value-of select="@id"/></xsl:attribute>
							<xsl:attribute name="border">0</xsl:attribute>
						</xsl:element>
					</xsl:if>
				</xsl:element>
			</xsl:element>
			<xsl:element name="TD">
				<xsl:if test="count(preceding-sibling::mirror) mod 2 = 0">
					<xsl:attribute name="class">oddRowEvenColumn</xsl:attribute>
				</xsl:if>
				<xsl:if test="count(preceding-sibling::mirror) mod 2 = 1">
					<xsl:attribute name="class">evenRowEvenColumn</xsl:attribute>
				</xsl:if>
				<xsl:value-of select="@location"/>
			</xsl:element>
		</TR>
	</xsl:template>
</xsl:stylesheet>
