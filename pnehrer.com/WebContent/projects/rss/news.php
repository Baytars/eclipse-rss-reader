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
	class="searchForm"><!-- siteedit:navbar target="topchildren" spec="/presence/theme/topnav.html" --><!-- /siteedit:navbar -->Search: <input
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
		<td valign="top" align="center" width="150" rowspan="2"><!-- siteedit:sitemap spec="/presence/theme/leftnav.html" onlychilden="true" targetlevel="1-4" --><!-- /siteedit:sitemap --></td>
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