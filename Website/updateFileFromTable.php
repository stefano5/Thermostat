 <html>
<body>

<?php 
session_start();

function doChange($i_d, $j_d) {
    $f = fopen("/var/www/html/$i_d.txt", "r");
    if (!$f) {
        debug_to_console("File $i_d.txt not found. I cannt initialize it");
        return false;
    }
    $count=0;
    $stateOfDay="";
    while(!feof($f)) {
        $result = trim(fgets($f));
        #echo "file $i_d.txt, stato: $result <br>";
        if ($count == $j_d) {
            #echo "confronto: <". $result."> <br>";
            if ($result == "0")
                $stateOfDay .= "1";
            else
                $stateOfDay .= "0";
        } else $stateOfDay .= $result;
        $stateOfDay .= "\n";
        $count++;
    }
    fclose($f);

    $f = fopen("/var/www/html/$i_d.txt", "w");
    if (!$f) {
        debug_to_console("File $i_d.txt not found. May be is a permission problem?");
        return false;
    }
    fwrite($f, trim($stateOfDay));
    fclose($f);
    return true;
}


$count=0;
for ($i=0; $i<24; $i++) {
    for ($j=0; $j<7; $j++) {
        if (isset($_GET["checkbox".$count])) {
            #echo "      name=$count -> value: " . $_GET["checkbox".$count]  . "<br>";
            $value = $_GET["checkbox".$count];
            #echo "checkbox$count vale: ".$value. "<br><br>";
            if ($value == "5" || $value == "1")
                doChange($j, $i);
        } else {
            echo "      name=$count non arriva <br>";
        }
        $count++;
    }
}

$f=fopen("/var/www/html/user.txt", "w");
fwrite($f, get_current_user());
fclose($f);

?>
<script>
window.location.replace("index.php");
</script>
</body>
</html> 
