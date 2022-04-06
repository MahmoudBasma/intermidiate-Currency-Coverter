<?php

//This php API role is to scrape the website a retrieve 4 exchange rate values

require_once "simple_html_dom.php";

//This scraper uses the simple HTML DOM parser which basically search for innerHTML elements using tags, classes and ID
$html = file_get_html("https://www.thelebaneseguide.com/lira-rate", false);

$results = array();

if (!empty($html)) {

    $div_class= "";

    //Searching for the values
    foreach ($html->find(".container") as $div_class) {
        foreach ($div_class->find("h6") as $rate) {
            $rate_value = intval(substr($rate->plaintext, 0 ,1));
            if($rate_value == 1){
                $value = substr($rate->plaintext, 55,13);
                //Adding to the array
                $results['Black Market rate-high'] = intval(substr($value, 0, 5));
                $results['Black Market rate-low'] = intval(substr($value, 8, 5));
            }

            elseif($rate_value == 2){
                $value = intval(substr($rate->plaintext, 18,5));
                //Adding to the array
                $results['Bank rate'] = $value;
            }

            elseif($rate_value == 3){
                $value = intval(substr($rate->plaintext, 33,5));
                //Adding to the array
                $results['Official rate'] = $value;
            }
        }
    }
}

//Converting the array to a JSON object to send to the APP

$json_response = json_encode($results);
echo $json_response;
?>