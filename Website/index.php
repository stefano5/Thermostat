<!DOCTYPE html>
  
<?php session_start();
usleep(500000);
$system = true;
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

if (isset($_GET["success"])) {
    $success = $_GET["success"];
    if ($success==1) {
echo "<div class = 'alert alert-success'>
        <a href ='#' class = 'close' data-dismiss = 'alert'>&times;</a>
        <strong>Fatto!</strong> Programmazione avviata con successo
      </div>";
    } else if ($success == -1) {
echo "<div class = 'alert alert-danger'>
        <a href ='#' class = 'close' data-dismiss = 'alert'>&times;</a>
        Rimozione fallita. È un errore interno, riprova. Se il problema persiste chiama stefano (file program missed? check: ls -l /var/www/html/program.txt, we need -wr)
      </div>";
        
    } else if ($success == 2) { 
echo "<div class = 'alert alert-success'>
        <a href ='#' class = 'close' data-dismiss = 'alert'>&times;</a>
        <strong>Fatto!</strong> Rimozione avviata con successo
      </div>";
    
    } else {
echo "<div class = 'alert alert-danger'>
        <a href ='#' class = 'close' data-dismiss = 'alert'>&times;</a>
        <strong>Mannaggia la peppa!</strong> Programmazione fallita :((((((, controlla i campi. Lo stesso giorno deve contenere l'orario di avvio del riscaldamento e l'orario di spegnimento del riscaldamento, altrimenti viene generato un errore.<br>
        $success
      </div>";
    }
}
?>
<html lang="en">
<head>
  <title>Termostato casa</title>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <!--link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/css/bootstrap.min.css"-->
  <link rel="stylesheet" href="bootstrap.css">
  <!--script src="https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js"></script-->
  <script src="bootstrap.js"></script>
  <script src="jquery.js"></script>
  <!--script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/js/bootstrap.min.js"></script-->
  <style>
    /* Remove the navbar's default margin-bottom and rounded borders */ 
    .navbar {
      margin-bottom: 0;
      border-radius: 0;
    }
    
    /* Set height of the grid so .sidenav can be 100% (adjust as needed) */
    .row.content {height: 780px}
    
    /* Set gray background color and 100% height */
    .sidenav {
      padding-top: 20px;
      background-color: #f1f1f1;
      height: 100%;
    }
    
    /* Set black background color, white text and some padding */
    footer {
      background-color: #555;
      color: white;
      padding: 15px;
    }
    
    /* On small screens, set height to 'auto' for sidenav and grid */
    @media screen and (max-width: 767px) {
      .sidenav {
        height: auto;
        padding: 15px;
      }
      .row.content {height:auto;} 
    }
  </style>
</head>
<body>

<nav class="navbar navbar-inverse">
  <div class="container-fluid">
    <div class="navbar-header">
      <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#myNavbar">
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>                        
      </button>
      <a class="navbar-brand" href="turnOffSystem.php">Spegni il termostato</a>
    </div>
    <div class="collapse navbar-collapse" id="myNavbar">
<?php
if ($system)
    echo "
      _termostato<ul class='nav navbar-nav'>
        <li class='active'><a href='turnOnNowAndForEver.php'>Accendi i termosifoni ora</a></li>
        <li><a href='turnOffNowAndForEver.php'>Spegni i termosifoni ora</a></li>
      </ul>
    ";
?>

<!--li><a href="#">altro</a></li>
        <li><a href="#">altro2</a></li-->
        
      <!--ul class="nav navbar-nav navbar-right">
        <li><a href="#"><span class="glyphicon glyphicon-log-in"></span> Login</a></li>
      </ul-->
    </div>
  </div>
</nav>
  
<div class="container-fluid text-center">    
  <div class="row content">
    <div class="col-sm-2 sidenav">
        <br>
        <br>
        <br>
        <br>
        <br>
        <br>
        <br>
        <br>
        <!--img src="foto.png" alt="foto" style="max-width:100%;max-width:100%;"-->
    </div>
    <div class="col-sm-8 text-left"> 
      <h1>Termostato</h1>
<?php
//echo "Ciao " . get_current_user();

function get_state_connection() {
        $f = fopen("/var/www/html/checkInternet.txt", "r");
        if ($f) {
            $result = fgets($f);
            fclose($f);
            return $result == 1;
	}
	echo "get_state_connection failed";
	return false;
}


if ($system) {
    $f = fopen("/var/www/html/heating.txt", "r");
    if (!$f) {
        echo "<p>Il termostato è acceso, ma non so se il riscaldamento è acceso o spento :D [è un bug, se aggiornate la pagina si dovrebbe risolvere]</p>";
        $f = fopen("/var/www/html/heating.txt", "w");
        if (!$f) {
            echo "Chiamate stefano, check: ls -l /var/html/heating.txt <br>";
            return false;
        }
        fwrite($f, "-1");
        fclose($f);
    } else {
        $result = trim(fgets($f));
        fclose(f);
        if ($result == 1)
            echo "<p>Il termostato è acceso, il riscaldamento è attualmente <strong><font color='green'>acceso</font></strong></p>";
        else
            echo "<p>Il termostato è acceso, il riscaldamento è attualmente <strong><font color=black>spento</font></strong></p>";
        fclose($f);
        $f = fopen("/var/www/html/checkInternet.txt", "r");
        echo "<p>Stato connessione internet: ";
        if ($f) {
            $result = fgets($f);
            fclose($f);
            if ($result == 1)        
                echo "<strong><font color='green'>connesso</font></strong>. È possibile quindi utilizzare i comandi via mail</p>";
            else
                echo "<strong><font color=red>disconnesso</font></strong>. Non è quindi possibile utilizzare i comandi via mail</p>";
        } else {
            echo "unknow</p>";
        }

    }
} else {
    echo "<p>Il termostato è spento, il riscaldamento non può quindi essere acceso</p>";
    echo "<form action=turnOnSystem.php> <input type='submit' value='Accendi termostato adesso'> </form>";
}

function getTypeSite() {
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
    return $result == 1;
}

echo "<hr>
     <h3>Programmazione settimanale</h3>";

if (getTypeSite()) {
    echo "<p>seleziona l'intervallo orario in cui vuoi che il riscaldamento sia acceso</p>";
    echo "<form action=switchTypeState.php> <input type='submit' value='Passa alla modalità inserimento manuale'> </form>";
} else {
    echo "<p>Inserisci l'intervallo orario in cui vuoi che il riscaldamento sia acceso</p>";
    //echo "<form action=switchTypeState.php> <input type='submit' value='Passa alla modalità tabella'> </form>";
}

if ($system)
    if (getTypeSite())
        echo "<form action='updateFileFromTable.php' method=GET>";
    else
        echo "<form action='updateFileFromCommand.php' method=GET>";
?>

  <p>È possibile inviare i seguenti comandi alla mail 'viabonannopisano20@gmail.com':</p>
    <ul>
        <li>Oggetto mail: 'Accendi'      accende ora il riscaldamento. Non serve scrivere nulla come testo</li>
        <li>Oggetto mail: 'Spegni'       spegni ora il riscaldamento. Non serve scrivere nulla come testo</li>
        <li>Oggetto mail: 'Programma'<br>Testo mail: <br>ora inizio su prima riga <br> ora fine su seconda riga<br>Nota che la programmazione tramite mail è fatta solo sul giorno corrente</li>
    </ul>    
    <br>

<?php


function getDay() {
    switch (date('l')) {
        case "Monday": return "Lunedì";
        case "Tuesday": return "Martedì";
        case "Wednesday": return "Mercoledì";
        case "Thursday": return "Giovedì";
        case "Friday": return "Venerdì";
        case "Saturday": return "Sabato";
        case "Sunday": return "Domenica";
        default: return date('l');
    } 
}

if (get_state_connection() == true)
	echo "<p align=center><b>Oggi è ".getDay()." e sono le ".date('H.i')."</b></p>";
else 
	echo "<p align=center><b>Oggi è ".getDay()." e sono le ".date('H.i')." [NB. Senza connessione ad internet questi dati potrebbero essere errati :( ]</b></p>";

function debug_to_console($data) {
    $output = $data;
    if (is_array($output))
        $output = implode(',', $output);

    echo "<script>console.log('Debug Objects: " . $output . "' );</script>";
}

function initFile($file) {
    $f = fopen("/var/www/html/$file.txt", "w");
    if (!$f) {
        debug_to_console("Impossibile creare il file $file.txt");
        return false;
    }
    debug_to_console("Creo il file $file.txt");
    for ($i=0; $i<24; $i++)
        fwrite($f, "0\n");
    fclose($f); 
}

function isSelected($i_d, $j_d) {
    $f = fopen("/var/www/html/$i_d.txt", "r");
    if (!$f) {
        debug_to_console("File $i_d.txt not found. Now initialize");
        initFile($i_d);
        return false;
    }
    $count=0;
    while(!feof($f)) {
        $result = fgets($f);
        #echo "file $i_d.txt, stato: $result <br>";
        if ($count == $j_d) {
            fclose($f);
            return trim($result) == "1";
        }
        $count++;
    }
    debug_to_console("file: $i_d.txt have not $j_d row");
    fclose($f);
    return false;
}

$hours=0;
$minuts=0;

$count = 0;
if ($system) {
    echo "
        <table class='table table-bordered table-dark'>
          <thead>
            <tr>
              <th scope='col'></th>
              <th scope='col'>Lunedì</th>
              <th scope='col'>Martedì</th>
              <th scope='col'>Mercoledì</th>
              <th scope='col'>Giovedì</th>
              <th scope='col'>Venerdì</th>
              <th scope='col'>Sabato</th>
              <th scope='col'>Domenica</th>
            </tr>
          </thead>
          <tbody>";
    if (getTypeSite()) {
                echo "<tr>";
        //table mode
        for ($i=0; $i<24; $i++) {
            if ($hours == 9)
                echo "  <th scope=\"row\"> 0$hours:0$minuts - ".($hours+1).":0$minuts </th>\n";
            elseif ($hours < 10) 
                echo "  <th scope=\"row\"> 0$hours:0$minuts - 0".($hours+1).":0$minuts </th>\n";
            else
                echo "  <th scope=\"row\"> $hours:0$minuts - ".($hours+1).":0$minuts </th>\n";
            $hours = $hours + 1;

            for ($j=0; $j<7; $j++) {
                if (isSelected($j, $i)) {
                    //se è selezionato è perchè si è scelto in precedenza di attivarlo
                    echo "      <input type='hidden' value='5' name='checkbox$count'>";    //se arriva questo devo ereditare il valore senza modificarlo [settato]
                    echo "      <td> <input type='checkbox' name=checkbox$count value='10' checked> Disattiva</td>\n";   //se arriva questo l'utente ha chiesto di disattivarlo
                } else {
                    echo "      <input type='hidden' value='-10' name='checkbox$count'>";    //se arriva questo devo ereditare il valore senza modificarlo [non settato]
                    echo "      <td> <input type='checkbox' name=checkbox$count value='1'> Attiva</td>\n";   //se arriva questo l'utente vuole attivare
                }
                $count++;
            }
            echo "</tr>";
        }
        echo" <input type='submit' name='submit' value='Aggiorna'> <br>";
        echo "</form>";
        echo "
            </tbody>
          </table>";
    } else {
        //manual mode

        echo "  <th scope=\"row\">Inizio:</th>\n";
        for ($j=0; $j<7; $j++) {
            echo "      <td> <input type='input' name=start$j size=15 maxlength=5 placeholder='formato: 07:00' _value=12:00> </td>\n";
        }
        echo "</tr>";
        echo "  <th scope=\"row\">Fine:</th>\n";
        for ($j=0; $j<7; $j++) {
            echo "      <td> <input type='input' name=end$j size=15 maxlength=5 placeholder='formato: 08:05' _value=14:00> </td>\n";
        }
        echo "</tr>";
        echo "
            </tbody>
          </table>";

        echo" <input type='submit' name='submit' value='Aggiorna'> <br>";
        echo "</form>";
        echo "
<div class='container'>
  <h2>Programmazioni esistenti</h2>
  <p>Il riscaldamento viene acceso e spento automaticamente solo e solta negli orari di Start e di Fine. Negli orari intermedi nulla viene fatto.</p>
  <table class='table table-bordered'>
    <thead>
      <tr>
        <th></th>
        <th scope='col'>Lunedì</th>
        <th scope='col'>Martedì</th>
        <th scope='col'>Mercoledì</th>
        <th scope='col'>Giovedì</th>
        <th scope='col'>Venerdì</th>
        <th scope='col'>Sabato</th>
        <th scope='col'>Domenica</th>
      </tr>
    </thead>
    <tbody>";

        $f = fopen("/var/www/html/program.txt", "r");
        $count=1;
        while(!feof($f)) {
            if (!$f) {echo "file program.txt not found"; break;}
            $result = trim(fgets($f));
            if ($result == "") break;
            //echo "file: $result <br>";
            $word = explode(" ", $result);
            //row = hstart + " " + hend + " " + dayWeek + " 1";
            echo "<tr>\n";
            //echo "controllo: ". trim($word[2])."<br>";
            switch (trim($word[2])) {
            case 0:
                echo "<td>".$count."</td>";
                echo "<td>Start: " . $word[0] . "<br>Fine: " . $word[1] . " 
                    <form action='removeProgram.php' method=GET> 
                        <input type='hidden' value='$count' name='removeId'>
                        <input type='submit' value='Rimuovi'> <br>
                    </form> </td>";
                echo "<td></td>";
                echo "<td></td>";
                echo "<td></td>";
                echo "<td></td>";
                echo "<td></td>";
                echo "<td></td>";
                break; 
            case 1:
                echo "<td>".$count."</td>";
                echo "<td></td>";
                echo "<td>Start: " . $word[0] . "<br>Fine: " . $word[1] . " 
                    <form action='removeProgram.php' method=GET> 
                        <input type='hidden' value='$count' name='removeId'>
                        <input type='submit' value='Rimuovi'> <br>
                    </form> </td>";
                echo "<td></td>";
                echo "<td></td>";
                echo "<td></td>";
                echo "<td></td>";
                echo "<td></td>";
                break;
            case 2:
                echo "<td>".$count."</td>";
                echo "<td></td>";
                echo "<td></td>";
                echo "<td>Start: " . $word[0] . "<br>Fine: " . $word[1] . " 
                    <form action='removeProgram.php' method=GET> 
                        <input type='hidden' value='$count' name='removeId'>
                        <input type='submit' value='Rimuovi'> <br>
                    </form> </td>";
                echo "<td></td>";
                echo "<td></td>";
                echo "<td></td>";
                echo "<td></td>";
                break;
            case 3:
                echo "<td>".$count."</td>";
                echo "<td></td>";
                echo "<td></td>";
                echo "<td></td>";
                echo "<td>Start: " . $word[0] . "<br>Fine: " . $word[1] . " 
                    <form action='removeProgram.php' method=GET> 
                        <input type='hidden' value='$count' name='removeId'>
                        <input type='submit' value='Rimuovi'> <br>
                    </form> </td>";
                echo "<td></td>";
                echo "<td></td>";
                echo "<td></td>";
                break;
            case 4:
                echo "<td>".$count."</td>";
                echo "<td></td>";
                echo "<td></td>";
                echo "<td></td>";
                echo "<td></td>";
                echo "<td>Start: " . $word[0] . "<br>Fine: " . $word[1] . " 
                    <form action='removeProgram.php' method=GET> 
                        <input type='hidden' value='$count' name='removeId'>
                        <input type='submit' value='Rimuovi'> <br>
                    </form> </td>";
                echo "<td></td>";
                echo "<td></td>";
                break;
            case 5:
                echo "<td>".$count."</td>";
                echo "<td></td>";
                echo "<td></td>";
                echo "<td></td>";
                echo "<td></td>";
                echo "<td></td>";
                echo "<td>Start: " . $word[0] . "<br>Fine: " . $word[1] . " 
                    <form action='removeProgram.php' method=GET> 
                        <input type='hidden' value='$count' name='removeId'>
                        <input type='submit' value='Rimuovi'> <br>
                    </form> </td>";
                echo "<td></td>";
                break;
            case 6:
                echo "<td>".$count."</td>";
                echo "<td></td>";
                echo "<td></td>";
                echo "<td></td>";
                echo "<td></td>";
                echo "<td></td>";
                echo "<td></td>";
                echo "<td>Start: " . $word[0] . "<br>Fine: " . $word[1] . " 
                    <form action='removeProgram.php' method=GET> 
                        <input type='hidden' value='$count' name='removeId'>
                        <input type='submit' value='Rimuovi'> <br>
                    </form> </td>";
                break;
            }
            echo "</tr>\n";
            $count++;
        } //fine while
        fclose($f);
        echo "
            </tbody>
            </table>
            </div>";
    }
}
?>

    </div>
    <div class="col-sm-2 sidenav">
      <div class="well">
        <br>
        <br>
        <br>
        <br>
      <p>Meteo attuale</p>
<?php
if (get_state_connection() == true) {
	echo "
<iframe width='250' height='128' scrolling='no' frameborder='no' noresize='noresize' src='https://www.ilmeteo.it/box/previsioni.php?citta=5282&type=real1&width=250&ico=1&lang=ita&days=6&font=Arial&fontsize=12&bg=FFFFFF&fg=000000&bgtitle=0099FF&fgtitle=FFFFFF&bgtab=F0F0F0&fglink=1773C2'></iframe>";
} else {
	echo "Connessione assente, impossibile scaricare meteo";
}
?>
      </div>
      <!--div class="well">
      </div-->
    </div>
  </div>
</div>

<footer class="container-fluid text-center">
  <p></p>
</footer>

</body>
</html>

