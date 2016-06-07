<?php
// array for JSON response
$response = array();
include("conn.php");
// check for required fields
if (isset($_POST['realtimetemphumd']) && isset($_POST['windowstate']) && isset($_POST['ledcolorstate'])&&isset($_POST['state_vapour'])&&isset($_POST['state_ir'])&&isset($_POST['state_sound'])&&isset($_POST['action_heat'])&&isset($_POST['action_cold'])&&isset($_POST['identify_text'])&&isset($_POST['identify_vioce'])&&isset($_POST['home_mode'])) {
    
    $realtimetemphumd = $_POST['realtimetemphumd'];
    $windowstate = $_POST['windowstate'];
    $ledcolorstate = $_POST['ledcolorstate'];
    
    $state_vapour = $_POST['state_vapour'];
    $state_ir = $_POST['state_ir'];
    $state_sound = $_POST['state_sound'];
    
    $action_heat = $_POST['action_heat'];
    $action_cold = $_POST['action_cold'];
    
    $identify_text = $_POST['identify_text'];
    $identify_vioce = $_POST['identify_vioce'];
    
    $home_mode = $_POST['home_mode'];
    
    
    $result = mysql_query("INSERT INTO logcabin_sql(realtimetemphumd, windowstate, ledcolorstate,state_vapour,state_ir,state_sound,action_heat,action_cold,identify_text,identify_vioce,home_mode,lasttime) VALUES('$realtimetemphumd', '$windowstate', '$ledcolorstate','$state_vapour','$state_ir','$state_sound','$action_heat','$action_cold','$identify_text','$identify_vioce','$home_mode',now())");// check if row inserted or not
    
    if ($result) {
        // successfully inserted into database
        $response["success"] = 1;
        $response["message"] = "Product successfully created.";

        // echoing JSON response
        echo json_encode($response);
    } else {
        // failed to insert row
        $response["success"] = 0;
        $response["message"] = "Oops! An error occurred.";
        
        // echoing JSON response
        echo json_encode($response);
    }
} else {
    // required field is missing
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";

    // echoing JSON response
    echo json_encode($response);
}
?>