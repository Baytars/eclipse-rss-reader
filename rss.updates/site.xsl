<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:msxsl="urn:schemas-microsoft-com:xslt">
<xsl:output method="html" encoding="ISO-8859-1"/>
<xsl:key name="cat" match="category" use="@name"/>
<xsl:template match="/">
<xsl:for-each select="site">
	<span>
	<h1>Update Site</h1>
	<p><xsl:value-of select="description"/></p>
	<table border="0"><tr><td>
	<xsl:for-each select="category-def">
		<xsl:sort select="@label" order="ascending" case-order="upper-first"/>
		<xsl:sort select="@name" order="ascending" case-order="upper-first"/>
		<table width="100%" border="0" cellpadding="2" cellspacing="2" class="tableList">
	<xsl:if test="count(key('cat',@name)) != 0">
			<caption>
				<xsl:value-of select="@label"/>
			</caption>
			<tr><th width="40%">Feature</th><th width="20%">Version</th><th width="40%">Environment</th></tr>
			<xsl:for-each select="key('cat',@name)">
			<xsl:sort select="ancestor::feature//@version" order="ascending"/>
			<xsl:sort select="ancestor::feature//@id" order="ascending" case-order="upper-first"/>
			<tr>
				<td id="indent">
					<xsl:choose>
					<xsl:when test="(position() mod 2 = 1)">
						<xsl:attribute name="class">oddRowOddColumn</xsl:attribute>
					</xsl:when>
					<xsl:otherwise>
						<xsl:attribute name="class">evenRowOddColumn</xsl:attribute>
					</xsl:otherwise>
					</xsl:choose>
					<xsl:choose>
					<xsl:when test="ancestor::feature//@label">
						<a href="{ancestor::feature//@url}"><xsl:value-of select="ancestor::feature//@label"/></a>
						<br/>
						<div id="indent">
						(<xsl:value-of select="ancestor::feature//@id"/>)
						</div>
					</xsl:when>
					<xsl:otherwise>
					<a href="{ancestor::feature//@url}"><xsl:value-of select="ancestor::feature//@id"/></a>
					</xsl:otherwise>
					</xsl:choose>
					<br />
				</td>
				<td>
					<xsl:choose>
					<xsl:when test="(position() mod 2 = 1)">
						<xsl:attribute name="class">oddRowEvenColumn</xsl:attribute>
					</xsl:when>
					<xsl:otherwise>
						<xsl:attribute name="class">evenRowEvenColumn</xsl:attribute>
					</xsl:otherwise>
					</xsl:choose>
					<a href="{ancestor::feature//@url}"><xsl:value-of select="ancestor::feature//@version"/></a>
					<br />
				</td>
				<td>
					<xsl:choose>
					<xsl:when test="(position() mod 2 = 1)">
						<xsl:attribute name="class">oddRowOddColumn</xsl:attribute>
					</xsl:when>
					<xsl:otherwise>
						<xsl:attribute name="class">evenRowOddColumn</xsl:attribute>
					</xsl:otherwise>
					</xsl:choose>
					<table>
						<xsl:if test="ancestor::feature//@os">
							<tr><td id="indent">Operating Systems:</td>
							<td id="indent"><xsl:value-of select="ancestor::feature//@os"/></td>
							</tr>
						</xsl:if>
						<xsl:if test="ancestor::feature//@ws">
							<tr><td id="indent">Windows Systems:</td>
							<td id="indent"><xsl:value-of select="ancestor::feature//@ws"/></td>
							</tr>
						</xsl:if>
						<xsl:if test="ancestor::feature//@nl">
							<tr><td id="indent">Languages:</td>
							<td id="indent"><xsl:value-of select="ancestor::feature//@nl"/></td>
							</tr>
						</xsl:if>
						<xsl:if test="ancestor::feature//@arch">
							<tr><td id="indent">Architecture:</td>
							<td id="indent"><xsl:value-of select="ancestor::feature//@arch"/></td>
							</tr>
						</xsl:if>
					</table>
				</td>
			</tr>
			</xsl:for-each>
			<tr><td class="spacer"><br/></td><td class="spacer"><br/></td></tr>
		</xsl:if>
		</table>
	</xsl:for-each>
	</td></tr></table>
	</span>
</xsl:for-each>
</xsl:template>
</xsl:stylesheet>
