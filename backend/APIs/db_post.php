<?php

include("db_info.php");

$amount = $_POST["amount"];
$rate = $_POST["rate"];
$currency = $_POST["currency"];

$query = $mysqli->prepare("INSERT INTO conversions (amount, rate, currency) VALUES (?, ?, ?)");
$query->bind_param("iss", $amount, $rate, $currency);
$query->execute();

$response = [];
$response["status"] = "Mabrouk!";

$json_response = json_encode($response);
echo $json_response;


?>