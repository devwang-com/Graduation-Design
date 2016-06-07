<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<metahttp-equiv="Content-Type"content="text/html; content="no-cache"charset=utf-8" />
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>@devwang|毕业设计</title>
<link rel="shortcut icon" href="./imgs/favicon.ico"/>
<link rel="bookmark" href="./imgs/favicon.ico"/>

<?php
include("conn.php");
$sql="select * from logcabin_sql order by id desc "  ;
$query=mysql_query($sql);

global $id_var;
global $id_flag;

$id_flag=0
?>

    
<style type="text/css">
/*

*/
h1,h2 {
	
	text-align:center}

h1{
	color:blue;
	font-size:40px;  
	font-family:"黑体";    
	}
	
h2{
	color:#999;
	font-family:"宋体";
	font-size:"12px";
	}

p{
	font-size:14px;
	line-height:150%;
	text-indent:28px;
	font-family:"楷体"；
	}
.img1{
	text-align:center;
}

.pcopy p{
	font-size:12px;
	text-align:center;
}
</style>

</head>



<body>
<h1>基于Android的智能家居系统</h1>

<h2>指导老师：黄晓峰 学生：李博、王炜、陆福红、李浩、庞海霞、杨融融 日期：2015年12月</h2>

<hr style="border-bottom:1px dashed #000;">
<div class="img1">
<img  src="imgs/home_model.jpg" height="240px" width="320px"/>
</div>
    
    
    <!--  使用ajax 更新数据而不刷新页面  -->
<table width="100%" border="0" cellspacing="0" cellpadding="5">
        <tr>
            <td id="LatestNews" height="330" align="center" valign="top" class="font14" style="border: #7baed9 solid 1px;border-top: none; padding: 10px;">
            </td>
        </tr>
    </table>

	<script src="jquery-1.11.3.min.js"></script>
    <script type="text/javascript">
        $(document).ready(function(){
    setInterval(updata,500);//每0.5秒执行一次update方法
});
    	function updata() {
            $.ajax({
                type: "POST",
                url: "LogCabin_Sql.php",
                contentType: "application/text;charset=utf-8",//这里要注意contentType数据类型 是text 不是json
                dataType: "text",//这里要注意dataType数据类型 是text 不是json
                success: function(data) {
                    $("#LatestNews").html(data);
                }, 
                error: function(error) {
                $("#LatestNews").html("未发布任何信息"+error);
                }
            });
        }
    </script>

                <!--  使用ajax 更新数据而不刷新页面  --> 
    
<P>
    以Android移动智能终端为上位机，TI的MSP430G2553为主控器，51单片机为子节点，设计一个智能家居系统；并开发一款APP用于控制整个系统；
    家庭环境的一系列参数可以传到WEB端，实现家居的远程监测。
 </P>   
<p>
	基于Android移动智能终端的APP开发，基于新浪云的数据库和WEB服务设计，基于TI的MSP430主控器程序设计，
    蓝牙和315M无线通信设计，单片机子模块电路设计，单片机子模块程序设计，直流稳压电源电路设计。
</p>
<br/>
<br/>
<br/>


<hr/>

<div class="pcopy">
<p>基于Android的智能家居系统<br/>Copyright &copy;2015 www.devwang.com</p>
</div>
</body>
</html>
