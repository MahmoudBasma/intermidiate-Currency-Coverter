<?php

include("db_info.php")

//retrieving to url attributes

//The actual ammount that we wanted to conver
$amount = floatval($_GET["amount"]);
//The rate that was used to convert i.e black market rate, bank rate or official
$rate = $_GET["rate"];
//The currency of the money before converting
$currency = $_GET["currency"];

//Adding to the database table in a secure way
$query = $mysqli->prepare("INSERT INTO conversions (amount, rate, currency) VALUES (?, ?, ?)");
$query->bind_param("iss", $amount, $rate, $currency);
$query->execute();

$response = [];
$response["status"] = "Success";

$json_response = json_encode($response);
echo $json_response;


?>