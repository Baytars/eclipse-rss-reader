<!-- tpl:insert page="/theme/home.htpl" --><!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<HTML>
<HEAD>
<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
<META name="GENERATOR" content="IBM WebSphere Studio">
<META http-equiv="Content-Style-Type" content="text/css">
<SCRIPT language="JavaScript" src="/presence/theme/updates.js" type="text/javascript"></SCRIPT>
<LINK href="/presence/theme/blue.css" rel="stylesheet" type="text/css">
<LINK rel="shortcut icon" href="/presence/theme/icons/favicon.ico" type="image/x-icon">
<!-- tpl:put name="head" --><TITLE>Eclipse RSS Reader Blog</TITLE><!-- /tpl:put -->
</HEAD>
<BODY onload="javascript:return body_onload();">
<DIV class="extLinkDiv"><SPAN style="vertical-align: bottom"><A class="extLink" href="http://pnehrer.freeshell.org/presence/projects/rss/index.html">pnehrer.com</A> | <A class="extLink" href="http://sourceforge.net/projects/morphine">SourceForge.net</A></SPAN></DIV>
<DIV class="header"><A href="/presence/projects/rss/index.html"><IMG border="0"
	src="/presence/theme/icons/rss-banner.png" width="500" height="64" alt="Eclipse RSS Reader"></A></DIV>
<!-- siteedit:navbar target="topchildren" spec="/presence/theme/leftnav.html" -->
<UL class="menuBox">
<LI class="menu" style='list-style-image: url("/presence/theme/icons/textinput_16.png")'><A class="leftNavLink" href="/presence/projects/rss/index.html">About</A></LI>
<LI class="menu" style='list-style-image: url("/presence/theme/icons/item_new_16.gif")'><A class="leftNavLink" href="/presence/projects/rss/news.php">News</A></LI>
<LI class="menu" style='list-style-image: url("/presence/theme/icons/update_16.png")'><A class="leftNavLink" href="/presence/projects/rss/download.html">Download</A></LI>
<LI class="menu" style='list-style-image: url("/presence/theme/icons/navigator_16.png")'><A class="leftNavLink" href="/presence/projects/rss/features.html">Features</A></LI>
<LI class="menu" style='list-style-image: url("/presence/theme/icons/browse_16.png")'><A class="leftNavLink" href="/presence/projects/rss/usage.html">Usage</A></LI>
<LI class="menu" style='list-style-image: url("/presence/theme/icons/view_16.png")'><A class="leftNavLink" href="/presence/projects/rss/screenshots.html">Screenshots</A></LI>
<LI class="menuSelf" style='list-style-image: url("/presence/theme/icons/detail_16.png")'>Blog</LI>
</UL>
<!-- /siteedit:navbar -->
<DIV style="float: right; clear: right; margin: 4px"><script type="text/javascript"><!--
google_ad_client = "pub-5464643717210088";
google_ad_width = 160;
google_ad_height = 600;
google_ad_format = "160x600_as";
google_ad_channel ="";
google_color_border = "FF6600";
google_color_bg = "FFFFFF";
google_color_link = "FF6600";
google_color_url = "FF6600";
google_color_text = "000000";
//--></script>
<script type="text/javascript"
  src="http://pagead2.googlesyndication.com/pagead/show_ads.js">
</script></DIV>
<DIV class="content"><!-- tpl:put name="body" -->
<?php
	$fragment = "blog.html";
	if($_SERVER["HTTP_HOST"] != "morphine.sourceforge.net")
		$fragment = "http://morphine.sourceforge.net/presence/projects/rss/".$fragment;

	include($fragment);
?>
<!-- /tpl:put --></DIV>
<DIV class="footer">Copyright &copy; 2003, 2004 <a
	href="mailto:pnehrer@users.sourceforge.net">Peter Nehrer</a>.<br>
Version $Id$.</DIV>
</BODY>
</HTML>
<!-- /tpl:insert -->