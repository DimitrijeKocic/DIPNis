<?php

require "connect.php";

$response = array();

$DIP = $_POST["dip"];
$type = $_POST["tip"];
$kind = $_POST["vrsta"];

$query = "select * from " . $DIP . " where tip = '". $type ."' and vrsta = '". $kind ."';";
$result = mysqli_query($conn, $query);

if ($result)
{
	while($row = mysqli_fetch_array($result))
	{
		array_push($response, array("id"=>$row[0], "tip"=>$row[1], "vrsta"=>$row[2], "kratakOpis"=>$row[3], 
									"lokacija"=>$row[4], "datumVreme"=>$row[5]));
	}
	
	echo json_encode(array("response"=>$response));
}
else echo "Error executing query, please try again.";

mysqli_close($conn);

?>