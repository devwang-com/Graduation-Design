

<!DOCTYPE html PUBLIC "-//WAPFORUM//DTD XHTML Mobile 1.0//EN" "http://www.wapforum.org/DTD/xhtml-mobile10.dtd">

<?php
include("conn.php");

$sql="select * from logcabin_sql order by id desc "  ;//通过id递减 查询数据库 logcabin_sql表单 的数据
$query=mysql_query($sql);

global $id_flag;

$id_flag=0

?>


<htmlmanifest="mymanifest.manifest">
  <html xmlns="http://www.w3.org/1999/xhtml" lang="en">
    <head>
         <meta http-equiv="content-type" content="text/html; charset=UTF-8" />
<metahttp-equiv="Content-Type"content="text/html; content="no-cache"charset=utf-8" />
      </head>
      
      
<script>
$(document).ready(function(){      
 
    databaseTest();
});
 
function databaseTest(){

     var db = openDatabase('mydb', '1.0', 'Test DB', 2 * 1024 * 1024); 
 
      db.transaction(function (tx) {            
      tx.executeSql('CREATE TABLE IF NOT EXISTS testHtml (id unique, 

contentText)');
      tx.executeSql('INSERT INTO testHtml (contentText) VALUES 

("insert data test!")');  
       });  
 
     db.transaction(function(tx){           
     tx.executeSql('SELECT * FROM testHtml',[],function(tx,result){
            var len=result.rows.length;
            var msg = "<p>Found rows: " + len + "</p>";  
             $("#testinfo").append(msg);
        },null);
     });   
 
 
}
</script>          
                
                <?Php
while ($row=mysql_fetch_array($query))
{ ?>
    <?Php
 
    if(!$id_flag){
  
$id_flag++;
?>
      
     
 <p>室内实时温湿度：
    &nbsp;&nbsp;<?Php echo "$row[realtimetemphumd]";?></p>
 <p>当窗户的状态：
                &nbsp;&nbsp;<?Php echo "$row[windowstate]";?></p>
 <p>当前灯的颜色状态：
                &nbsp;&nbsp;<?Php echo "$row[ledcolorstate]";?></p>
 <p>监测 烟雾传感器：
                &nbsp;&nbsp;<?Php echo "$row[state_vapour]";?></p>
 <p>监测 红外传感器：
                &nbsp;&nbsp;<?Php echo "$row[state_ir]";?></p>
 <p>监测 声音传感器：
                &nbsp;&nbsp;<?Php echo "$row[state_sound]";?></p>
 <p>加热状态：
                &nbsp;&nbsp;<?Php echo "$row[action_heat]";?></p>
 <p>制冷状态：
                &nbsp;&nbsp;<?Php echo "$row[action_cold]";?></p>
 <p>文字识别：
                &nbsp;&nbsp;<?Php echo "$row[identify_text]";?></p>
 <p>语音识别：
                &nbsp;&nbsp;<?Php echo "$row[identify_vioce]";?></p>
 <p>情景模式：
                &nbsp;&nbsp;<?Php echo "$row[home_mode]";?></p>


      			
                
                
      			
      			
      			
     			
     			
     			
     

<?Php
   }       
     
 }   
?>
