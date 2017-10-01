<?php

require "connect.php";

$response = array();

$DIP = $_POST["dip"];
$type = $_POST["tip"];

$query = "select vrsta from " . $DIP . " where tip = '". $type ."';";
$result = mysqli_query($conn, $query);

if ($result)
{
	while($row = mysqli_fetch_array($result))
	{
		array_push($response, array("vrsta"=>$row[0]));
	}
	
	echo json_encode(array("vrste"=>$response));
}
else echo "Error executing query, please try again.";

mysqli_close($conn);

?>