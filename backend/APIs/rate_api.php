<?php
require_once "simple_html_dom.php";
$html = file_get_html("https://lirarate.org/", false);

$results = array();

if (!empty($html)) {

    $div_class = $title = "";
    $i = 0;

    foreach ($html->find(".result-container") as $div_class) {
        
        //Extract the review title
        foreach ($div_class->find("span.amount") as $sell) {
            echo "Entered";
            echo $sell;
           /* while($sell->plaintext == "Loading"){
                echo "Waiting";
            }*/
            $results[$i]['sell'] = $sell->plaintext;
        }
        
        
        

        $i++;
    }
}
$json_response = json_encode($results);
echo $json_response;
?>