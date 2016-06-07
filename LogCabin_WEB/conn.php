<?php
error_reporting(0);
$conn= mysql_connect ( SAE_MYSQL_HOST_M . ':' . SAE_MYSQL_PORT, SAE_MYSQL_USER, SAE_MYSQL_PASS );
mysql_select_db("app_devwang",$conn);
mysql_query("set names 'utf-8'");
function htmltocode($content) {
	$content = str_replace("\n", "<br>", str_replace(" ","&nbsp;", $content));
	return $content;
}
?>

