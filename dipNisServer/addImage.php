<?php

require "connect.php";

$id = $_POST["id"];
$homeUrl = $_POST["homeUrl"];
$DIP = $_POST["dip"];

$query1 = "select slike from " . $DIP . " where id = '". $id ."';";
$result1 = mysqli_query($conn, $query1);
if ($result1)
{
	$row = mysqli_fetch_array($result1);
	$strings = explode(" ", $row[0]);
	$imgNumber;
	if ($strings[0] == "")
		$imgNumber = 1;
	else $imgNumber = count($strings) + 1;
	
	$imgString = $_POST["slika"];
	$imgName = $DIP . "ID" . $id . "imgNum" . $imgNumber . ".jpg";

	$img = base64_decode("$imgString");
	header('Content-Type: bitmap; charset=utf-8');
	$file = fopen("images/" . $imgName, 'w');
	fwrite($file, $img);
	fclose($file);

	if ($imgNumber == 1)
		$stringToUpdate = $homeUrl . "images/" . $imgName;
	else $stringToUpdate = $row[0] . " " . $homeUrl . "images/" . $imgName;
	$query2 = "update " . $DIP . " set slike = '$stringToUpdate' where id = '". $id ."';";
	$result2 = mysqli_query($conn, $query2);

	if ($result2)
	{
		echo "Success";
	}
	else echo "Error executing query, please try again.";
}
else echo "Error executing query, please try again.";

mysqli_close($conn);

?>