<?php
session_start();

if (isset($_GET["removeId"])) {
    $toRemove = $_GET["removeId"];

    $f = fopen("/var/www/html/program.txt", "r");
    $count=0;
    $newFile="";
    $success = -1;
    while(!feof($f)) {
        if (!$f) { echo "file program.txt not found"; break; }
        $result = trim(fgets($f));
        #echo "leggo " . $result . "<br>";
        if ($result == "") break;
        if ($count+1 != $toRemove) {
            $newFile .= $result . "\n";
            #echo "salvo: ". $newFile . "<br>";
        } else {
            $success = 2;
        }
        $count++;
    }
    fclose($f);
    #echo "nuovo file: <br>".$newFile;
    $f = fopen("/var/www/html/program.txt", "w");
    fwrite($f, $newFile);
    fclose($f);
}

    echo "<script>
        window.location.replace('index.php?success=$success');
          </script>
        </body>
     </html> ";

?>
