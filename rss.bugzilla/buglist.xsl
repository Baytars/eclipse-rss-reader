<?xml version="1.0" encoding="UTF-8"?>
<!--
	Translates a Bugzilla XML bug list into the internal RSS format.
	Author: <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
	Version $Id$
-->
<xsl:stylesheet 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
	xmlns:bz="http://www.bugzilla.org/rdf#"
	exclude-result-prefixes="rdf bz"
    version="1.0">
    
    <xsl:template match="rdf:RDF">
    	<xsl:apply-templates select="bz:result"/>
    </xsl:template>
    
    <xsl:template match="bz:result">
    	<channel title="{bz:installation/@rdf:resource}" link="{substring-before(@rdf:about, '&amp;ctype=rdf')}">
    		<xsl:apply-templates select="bz:bugs/rdf:Seq/rdf:li"/>
			<textInput title="Search Bugzilla" link="{bz:installation/@rdf:resource}buglist.cgi" description="Enter Bugzilla query:" name="content"/>
    	</channel>
    </xsl:template>
    
    <xsl:template match="rdf:li">
    	<item title="{bz:bug/bz:id}" link="{bz:bug/@rdf:about}" description="{bz:bug/bz:short_short_desc}"/>
    </xsl:template>
</xsl:stylesheet>
