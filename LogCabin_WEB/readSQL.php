

<!DOCTYPE html PUBLIC "-//WAPFORUM//DTD XHTML Mobile 1.0//EN" "http://www.wapforum.org/DTD/xhtml-mobile10.dtd">

<?php
include("conn.php");

$sql="select * from test_faceback order by pid desc "  ;
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
    
      <?Php echo "$row[name]";?> 
      <?Php echo "$row[email]";?>
                
      <?Php echo "$row[description]";?>
               <?Php
               
    }
        
     
 }
    
?>
