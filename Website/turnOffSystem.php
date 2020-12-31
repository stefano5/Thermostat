<?php 
session_start();

$f = fopen("/var/www/html/systemOn.txt", "w+");
fwrite($f, "0");
fclose($f);

echo "
<script>
window.location.replace('index.php');
</script>
";
?>
