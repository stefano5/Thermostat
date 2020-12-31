<?php 
session_start();

function debug_to_console($data) {
    $output = $data;
    if (is_array($output))
        $output = implode(',', $output);

    echo "<script>console.log('Debug Objects: " . $output . "' );</script>";
}



function turnOnNow($file) {
    $f = fopen("/var/www/html/$file.txt", "w");
    if (!$f) {
        debug_to_console("I cannot open file $file.txt, permsission?");
        return;
    }
    debug_to_console("Force on now");
    for ($i=0; $i<24; $i++)
        fwrite($f, "1\n");
    fclose($f); 
    #$f = fopen("/var/www/html/ack_on_now.txt", "w");
    #fwrite($f, "1");
    #fclose($f);
}

function isSystemOn() {
    $f = fopen("/var/www/html/systemOn.txt", "r");
    if (!$f) {
        $f = fopen("/var/www/html/systemOn.txt", "w");
        fwrite($f, "0");
        fclose($f);
        $system = false;
    } else {
        $result = "";
        while(!feof($f)) {
            $result = trim(fgets($f));
        }
        fclose($f);
        $system = trim($result) == 1;
    }
    return $system;
}

function forceTypeSite() {
    $f = fopen("/var/www/html/type.txt", "w");
    if (!$f) {
        return false;
    } 
    fwrite($f, "1");
    fclose($f);
}

function turnOnSystem() {
    $f = fopen("/var/www/html/turnOnHeating.txt", "w");
    if (!$f) {
        echo "Internal error";
        sleep(2);
    } 
    fwrite($f, "1");
    fclose($f);
}

if (isSystemOn()) {
    turnOnSystem();
}

?>
<script>
window.location.replace("index.php");
</script>

