<?php

$db_name = "dipnis";
$username = "root";
$password = "";
$server_name = "localhost";

$conn = mysqli_connect($server_name, $username, $password, $db_name);

if (!$conn)
	echo "Error connecting to the database, please try again."

?>