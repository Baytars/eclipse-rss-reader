<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="html"/>
	<xsl:template match="mirrors">
		<TABLE border="0" cellspacing="2" cellpadding="2">
			<CAPTION>SourceForge mirrors</CAPTION>
			<TR>
				<TH style="background-color: #c0c0c0"></TH>
				<TH style="background-color: #c0c0c0">Mirror</TH>
				<TH style="background-color: #c0c0c0">Location</TH>
				<TH style="background-color: #c0c0c0">Continent</TH>
			</TR>
			<xsl:apply-templates select="mirror"/>
		</TABLE>
	</xsl:template>
	
	<xsl:template match="mirror">
		<TR>
			<xsl:element name="TD">
				<xsl:if test="count(preceding-sibling::mirror) mod 2 = 0">
					<xsl:attribute name="style">background-color: #e0e0e0</xsl:attribute>
				</xsl:if>
				<xsl:if test="count(preceding-sibling::mirror) mod 2 = 1">
					<xsl:attribute name="style">background-color: white</xsl:attribute>
				</xsl:if>
				<xsl:element name="INPUT">
					<xsl:attribute name="id">mirror_<xsl:value-of select="@id"/></xsl:attribute>
					<xsl:attribute name="type">radio</xsl:attribute>
					<xsl:attribute name="name">mirror</xsl:attribute>
					<xsl:attribute name="value"><xsl:value-of select="@id"/></xsl:attribute>
				</xsl:element>
			</xsl:element>
			<xsl:element name="TD">
				<xsl:if test="count(preceding-sibling::mirror) mod 2 = 0">
					<xsl:attribute name="style">background-color: #e0e0e0</xsl:attribute>
				</xsl:if>
				<xsl:if test="count(preceding-sibling::mirror) mod 2 = 1">
					<xsl:attribute name="style">background-color: white</xsl:attribute>
				</xsl:if>
				<xsl:element name="A">
					<xsl:attribute name="href"><xsl:value-of select="@link"/></xsl:attribute>
					<xsl:element name="IMG">
						<xsl:attribute name="src"><xsl:value-of select="@image"/></xsl:attribute>
						<xsl:attribute name="alt"><xsl:value-of select="@id"/></xsl:attribute>
						<xsl:attribute name="border">0</xsl:attribute>
					</xsl:element>
				</xsl:element>
			</xsl:element>
			<xsl:element name="TD">
				<xsl:if test="count(preceding-sibling::mirror) mod 2 = 0">
					<xsl:attribute name="style">background-color: white</xsl:attribute>
				</xsl:if>
				<xsl:if test="count(preceding-sibling::mirror) mod 2 = 1">
					<xsl:attribute name="style">background-color: #e0e0e0</xsl:attribute>
				</xsl:if>
				<xsl:value-of select="@location"/>
			</xsl:element>
			<xsl:element name="TD">
				<xsl:if test="count(preceding-sibling::mirror) mod 2 = 0">
					<xsl:attribute name="style">background-color: #e0e0e0</xsl:attribute>
				</xsl:if>
				<xsl:if test="count(preceding-sibling::mirror) mod 2 = 1">
					<xsl:attribute name="style">background-color: white</xsl:attribute>
				</xsl:if>
				<xsl:value-of select="@continent"/>
			</xsl:element>
		</TR>
	</xsl:template>
</xsl:stylesheet>
