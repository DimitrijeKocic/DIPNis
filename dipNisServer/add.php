<?php

require "connect.php";

$homeUrl = $_POST["homeUrl"];
$DIP = $_POST["dip"];
$type = $_POST["tip"];
$location = $_POST["lokacija"];
$description = $_POST["opis"];
if ($DIP != "problem") 
{
	$kind = $_POST["vrsta"];
	$shortDesc = $_POST["kratakOpis"];
	$date = $_POST["datumVreme"];
	
	$query1 = "insert into " . $DIP . " (tip, vrsta, kratakOpis, lokacija, datumVreme, opis, slike)
		  values ('$type', '$kind', '$shortDesc', '$location', '$date', '$description', '');";
}
else 
{
	$query1 = "insert into " . $DIP . " (tip, lokacija, opis, slike)
		  values ('$type', '$location', '$description', '');";
}
$result1 = mysqli_query($conn, $query1);
if ($result1)
{
	$id = mysqli_insert_id($conn);
	
	$imgString = $_POST["slika"];
	
	if ($imgString != "")
	{
		$imgName = $DIP . "ID" . $id . "imgNum1.jpg";
		
		$img = base64_decode("$imgString");
		header('Content-Type: bitmap; charset=utf-8');
		$file = fopen("images/" . $imgName, 'w');
		fwrite($file, $img);
		fclose($file);
		
		$stringToUpdate = $homeUrl . "images/" . $imgName;
		$query2 = "update " . $DIP . " set slike = '$stringToUpdate' where id = '". $id ."';";
		$result2 = mysqli_query($conn, $query2);
		
		if ($result2)
		{
			echo "Success";
		}
		else echo "Error executing query, please try again.";
	}
	else echo "Success";
}
else echo "Error executing query, please try again.";

mysqli_close($conn);

?>