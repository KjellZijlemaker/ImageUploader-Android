<?php

$base = $_REQUEST["image"];

if (isset($base)) {

$suffix = createRandomID();
$image_name = "img_".$suffix."_".date("Y-m-d-H-m-s").".jpg";

// base64 encoded utf-8 string
$binary = base64_decode($base);

// binary, utf-8 bytes

header("Content-Type: bitmap; charset=utf-8");

// $filepath = "images/" . $image_name;
// if (!file_exists($filepath)) {
// touch($filepath);
// }

$file = fopen("images/" . $image_name, "wb");

fwrite($file, $binary);

fclose($file);

die($image_name);

} else {

die("No POST");
}

function createRandomID() {

$chars = "abcdefghijkmnopqrstuvwxyz0123456789?";
//srand((double) microtime() * 1000000);

$i = 0;

$pass = "";

while ($i <= 5) {

$num = rand() % 33;

$tmp = substr($chars, $num, 1);

$pass = $pass . $tmp;

$i++;
}
return $pass;
}
?>
