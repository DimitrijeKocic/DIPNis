<?php

require "connect.php";

$response = new stdClass();
$id = $_POST["id"];
$DIP = $_POST["dip"];

$query = "select * from " . $DIP . " where id = '" . $id . "';";
$result = mysqli_query($conn, $query);

if ($result)
{
	$row = mysqli_fetch_array($result);
	$response->id = $row[0];
	$response->tip = $row[1];
	if ($DIP == "problem") 
	{
		$response->lokacija = $row[2];
		$response->opis = $row[3];
		$response->slike = $row[4];
	}
	else 
	{
		$response->vrsta = $row[2];
		$response->kratakOpis = $row[3];
		$response->lokacija = $row[4];
		$response->datumVreme = $row[5];
		$response->opis = $row[6];
		$response->slike = $row[7];
	}

	echo json_encode($response);
}
else echo "Error executing query, please try again.";

mysqli_close($conn);

?>