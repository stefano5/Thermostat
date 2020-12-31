<html>
<body>

<?php 
session_start();

$codeError="";
$success = 0;

function validFormatH($time) {
    $hours = explode(":", $time);

    if (!is_numeric($hours[0])) return false;
    if (!is_numeric($hours[1])) return false;
    if (((int)$hours[0]) < 0 || ((int)$hours[0]) >= 24) return false;
    if (((int)$hours[1]) < 0 || ((int)$hours[1]) >= 60) return false;
    return true;
}

function writeNewRequest($hstart, $hend, $day) {
    if (!validFormatH($hstart)) {
        return "ERROR 4:Hai inserito [$hstart] che non è un formato corretto. Dei formati corretti sono: 15:10    oppure: 07:05   oppure: 06:00   e cosi via";
    } else 

    if (!validFormatH($hend)) {
        return "ERROR 5:Hai inserito [$hend] che non è un formato corretto. Dei formati corretti sono: 15:10    oppure: 07:05   oppure: 06:00   e cosi via";
    }

    if (strlen($hstart) != 5 || strlen($hend) != 5) {
        echo "Bad request [$day] <br>";
        echo "$start: ".$hstart. "  len: ".strlen($hstart);
        echo "$end: ".$hend. "  len: ". strlen($hend);
        return false;
    }
    $f = fopen("/var/www/html/request$day.txt", "w");
    if (!$f) {
	echo "Cannot create new request -> /var/www/html/request$day miss permission?";
	return false;
    }
    
    fwrite($f, "$hstart\n$hend\n");
    fclose($f);
    return 1;
}

function cancelAllRequest($day) {
    $f = fopen("/var/www/html/request$day.txt", "w");
    fwrite($f, "999\n999");
    fclose($f);
}

for ($j=0; $j<7; $j++) {
    if (isset($_GET["start".$j])) {
        $hstart = trim($_GET["start".$j]);
        if (strlen($hstart) == 5) {
            if (isset(($_GET["start".$j]))) {
                $hend = trim($_GET["end".$j]);
                if (strlen($hend) == 5) {
                    $res = writeNewRequest($hstart, $hend, $j);
                    if ($res == 1) {
                        $success = 1;
                    } else {
                        #cancelAllRequest($j);
                        $success = 0;
                        $codeError=$res;
                        break;
                    }
                } else { $codeError = "ERROR 1: Formato errato. Hai scritto: [$hend] ma deve essere di 5 caratteri, esempio: 05:00."; break; }
            } else { $codeError = "ERROR 2: Campi mancanti. Devi dirmi quando staccare i termosifoni. "; break;}
        } else if (strlen($hstart) == 0) {
            continue;
        } else  { $codeError = "ERROR 3: Formato errato. Hai scritto: [$hstart] ma deve essere di 5 caratteri, esempio: 09:00. "; break;}
    }
}
echo "<br>";
for ($j=0; $j<7; $j++) {
    if (isset($_GET["end".$j])) {
        $value = $_GET["end".$j];
        //echo "value: ". $value;
    }
}

sleep(1);
if ($success == 1 && $codeError == "") {
    echo "<script>
        window.location.replace('index.php?success=1');
          </script>
        </body>
     </html> ";
} else {
    echo "<script>
        window.location.replace('index.php?success=\"$codeError\"');
          </script>
        </body>
     </html> ";
    //echo "<form action='index.php'> 
    //        <input type='hidden' value='$codeError' name='success'>
    //        <input type='submit' value='Premimi con violenza per tornare indietro e per sfogarti un pochetto'> 
    //      </form>";
}
?>
