<!-- tpl:insert page="/theme/home.htpl" --><!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type"
	content="text/html; charset=ANSI_X3.4-1968">
<meta name="GENERATOR" content="IBM WebSphere Studio">
<SCRIPT language="JavaScript" src="/presence/theme/updates.js"></SCRIPT>
<link rel="stylesheet" href="/presence/theme/blue.css" type="text/css">
<SCRIPT language="JavaScript" src="/presence/theme/search.js"></SCRIPT>
<link rel="shortcut icon" href="/presence/theme/icons/favicon.ico" type="image/x-icon">
<!-- tpl:put name="head" -->
		<TITLE>RSS Reader News</TITLE><!-- /tpl:put -->
</head>
<body onload="javascript:return body_onload();"><div
	class="searchForm"><!-- siteedit:navbar target="topchildren" spec="/presence/theme/topnav.html" -->
<A class="extLink" href="/presence/home/index.html">Home</A> |
<A class="extLink" href="/presence/home/resume.html">Resume</A> |
<A class="extLink" href="/presence/projects/index.html">Projects</A> |
<!-- /siteedit:navbar -->Search: <input
	class="searchText" 
	type="text" 
	name="q"
	size="12"
	onkeypress="search(this, event)"></div><div 
	class="header"><a 
	href="/presence/home/index.html"><img 
	border="0"
	src="/presence/theme/banner.png" 
	width="320" 
	height="32"
	alt="pnehrer.com"></a></div><table
	width="100%" 
	border="0" 
	cellpadding="0" 
	cellspacing="0">
	<tr>
		<td valign="top" align="center" width="150" rowspan="2"><!-- siteedit:sitemap spec="/presence/theme/leftnav.html" onlychilden="true" targetlevel="1-4" -->
<IMG src="/presence/theme/blackdot.gif" width="2" height="16" alt="extends"><TABLE width="100%" cellpadding="0" cellspacing="0">
<TBODY><TR><TD>
<DIV class="menuBox">
<DIV class="menu"><A href="/presence/home/index.html"><IMG src="/presence/theme/icons/home.png" alt="Home" class="icon"> Home</A></DIV>
<DIV class="menu"><A href="/presence/home/resume.html"><IMG src="/presence/theme/icons/resume.png" alt="Resume" class="icon"> Resume</A></DIV>
<DIV class="menuAncestor"><A href="/presence/projects/index.html"><IMG src="/presence/theme/icons/projects.png" alt="Projects" class="icon"> Projects</A></DIV>
</DIV>
</TD><TD width="8">
</TD></TR>
</TBODY></TABLE>
<IMG src="/presence/theme/blackdot.gif" width="2" height="16" alt="extends"><TABLE width="100%" cellpadding="0" cellspacing="0">
<TBODY><TR><TD>
<DIV class="menuBox">
<DIV class="menu"><A href="/presence/home/about.html"><IMG src="/presence/theme/icons/about.png" alt="Presence" class="icon"> Presence</A></DIV>
<DIV class="menu"><A href="/presence/projects/cep/index.html"><IMG src="/presence/theme/icons/folder_blue_open.png" alt="Castor Plug-in" class="icon"> Castor Plug-in</A></DIV>
<DIV class="menuAncestor"><A href="/presence/projects/rss/index.html"><IMG src="/presence/theme/icons/xml_16.gif" alt="RSS Reader" class="icon"> RSS Reader</A></DIV>
<DIV class="menu"><A href="/presence/projects/jelly4eclipse/index.html"><IMG src="/presence/theme/icons/folder_blue_open.png" alt="Eclipse Scripting" class="icon"> Eclipse Scripting</A></DIV>
</DIV>
</TD><TD width="8">
</TD></TR>
</TBODY></TABLE>
<IMG src="/presence/theme/blackdot.gif" width="2" height="16" alt="extends"><TABLE width="100%" cellpadding="0" cellspacing="0">
<TBODY><TR><TD>
<DIV class="menuBox">
<DIV class="menuSelf"><IMG src="/presence/theme/icons/item_new_16.gif" alt="News" class="icon"> News</DIV>
<DIV class="menu"><A href="/presence/projects/rss/download.html"><IMG src="/presence/theme/icons/update_16.png" alt="Download" class="icon"> Download</A></DIV>
<DIV class="menu"><A href="/presence/projects/rss/features.html"><IMG src="/presence/theme/icons/navigator_16.png" alt="Features" class="icon"> Features</A></DIV>
<DIV class="menu"><A href="/presence/projects/rss/usage.html"><IMG src="/presence/theme/icons/browse_16.png" alt="Usage" class="icon"> Usage</A></DIV>
<DIV class="menu"><A href="/presence/projects/rss/screenshots.html"><IMG src="/presence/theme/icons/view_16.png" alt="Screenshots" class="icon"> Screenshots</A></DIV>
</DIV>
</TD><TD width="8">
<IMG src="/presence/theme/blackdot.gif" width="8" height="2" alt="aggregates">
</TD></TR>
</TBODY></TABLE>
<!-- /siteedit:sitemap --></td>
		<td height="8"></td>
	</tr>
	<tr>
		<td valign="top" class="content"><!-- tpl:put name="body" -->
<?php
	$fragment = "news.html";
	if($_SERVER["HTTP_HOST"] != "morphine.sourceforge.net")
		$fragment = "http://morphine.sourceforge.net/presence/projects/rss/".$fragment;

	include($fragment);
?>
<!-- /tpl:put --></td>
	</tr>
</table><div class="footer">Copyright &copy; 2003, 2004 <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>.<br>Version $Id$.</div></body>
</html>
<!-- /tpl:insert -->