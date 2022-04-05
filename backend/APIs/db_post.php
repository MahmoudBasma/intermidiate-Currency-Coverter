<?php

include("db_info.php");
$json = file_get_contents('php://input');
echo $json;
$data = json_decode($json);
echo $data;


$amount = $data->"amount";
$rate = $data->"rate";
$currency = $data->"currency";

$query = $mysqli->prepare("INSERT INTO conversions (amount, rate, currency) VALUES (?, ?, ?)");
$query->bind_param("iss", $amount, $rate, $currency);
$query->execute();

$response = [];
$response["status"] = "Success";

$json_response = json_encode($response);
echo $json_response;


?>