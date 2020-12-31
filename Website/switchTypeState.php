<?php

function switchTypeSite() {
    $f = fopen("/var/www/html/type.txt", "r");
    if (!$f) {
        $f = fopen("/var/www/html/type.txt", "w");
        if (!$f) {
            echo "Failed init type site";
            return false;
        }
        fwrite($f, "0");
        fclose($f);
    }
    $result = trim(fgets($f));
    fclose($f);
    #echo "res: ".$result."<br>";
    $f = fopen("/var/www/html/type.txt", "w");
    if (!$f) {
        echo "Failed init type site_2";
        return false;
    }
    if ($result == "1")
        fwrite($f, "0");
    else
        fwrite($f, "1");
    fclose($f);

echo "
<script>
window.location.replace('index.php?var=$result');
</script>";
}

switchTypeSite();
?>
