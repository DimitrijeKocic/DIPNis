<?php

require "connect.php";

$response = array();

$DIP = $_POST["dip"];

$query = "select * from " . $DIP . ";";
$result = mysqli_query($conn, $query);

if ($result)
{
	while($row = mysqli_fetch_array($result))
	{
		if ($DIP == "problem") 
		{
			$img = explode(" ", $row[4])[0];
			if ($img == "")
				$img = "noImage";
			array_push($response, array("id"=>$row[0], "tip"=>$row[1], "lokacija"=>$row[2], 
										"opis"=>$row[3], "img"=>$img));
		}
		else 
		{
			array_push($response, array("id"=>$row[0], "tip"=>$row[1], "vrsta"=>$row[2], "kratakOpis"=>$row[3], 
									"lokacija"=>$row[4], "datumVreme"=>$row[5]));
		}
	}

	echo json_encode(array("response"=>$response));
}
else echo "Error executing query, please try again.";

mysqli_close($conn);

?>