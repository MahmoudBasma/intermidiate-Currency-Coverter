<?php

//This php API role is to scrape the website a retrieve 4 exchange rate values

require_once "simple_html_dom.php";
$html = file_get_html("https://www.thelebaneseguide.com/lira-rate", false);

$results = array();

if (!empty($html)) {

    $div_class= "";

    foreach ($html->find(".container") as $div_class) {

        foreach ($div_class->find("h6") as $rate) {
            $rate_value = intval(substr($rate->plaintext, 0 ,1));
            /
            if($rate_value == 1){
                $value = substr($rate->plaintext, 55,13);
                $results['Black Market rate-high'] = intval(substr($value, 0, 5));
                $results['Black Market rate-low'] = intval(substr($value, 8, 5));
            }

            elseif($rate_value == 2){
                $value = intval(substr($rate->plaintext, 18,5));
                $results['Bank rate'] = $value;
            }

            elseif($rate_value == 3){
                $value = intval(substr($rate->plaintext, 33,5));
                $results['Official rate'] = $value;
            }
        }
    }
}
$json_response = json_encode($results);
echo $json_response;
?>