<?php 
session_start();

$f = fopen("/var/www/html/systemOn.txt", "w");
fwrite($f, "1");
fclose($f);

?>

<script>
window.location.replace("index.php");
</script>
