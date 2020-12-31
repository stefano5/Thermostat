<?php
error_reporting(E_ALL);
ini_set('display_errors',1);

function connect_to_db() {
    $host = "127.0.0.1";
    $nome_db = "root";
    $password_db = "raptor";
    $seldb = "studenti_magistrale";
    $db = mysqli_connect($host, $nome_db, $password_db) or die ("Failed connection: " . mysqli_error($db));
    mysqli_select_db($db, $seldb);
    return $db;
}


function query_1($query, $par1){
    $db=connect_to_db();
    $result = mysqli_query($db, $query)
        or die("Query failed: " . mysqli_error($db)."<br>Query: ".$query);
    $ret=[];
    $ind=0;
    while ($row = mysqli_fetch_array($result, MYSQL_ASSOC)){
        $ret[$ind] = array("$par1" => $row["$par1"]);
        $ind=$ind+1;
    }
    mysqli_close($db);		
    return $ret;
}

function query_2($query, $par1,$par2){
    $db=connect_to_db();
    $result = mysqli_query($db, $query)
        or die("Query failed: " . mysqli_error($db)."<br>Query: ".$query);
    $ret=[];
    $ind=0;
    while ($row = mysqli_fetch_array($result, MYSQL_ASSOC)){
        $ret[$ind] = array("$par1" => $row["$par1"], "$par2" => $row["$par2"]);
        $ind=$ind+1;
    }
    mysqli_close($db);
    return $ret;
}

function query_3($query, $par1,$par2, $par3){
    $db=connect_to_db();
    $result = mysqli_query($db, $query)
        or die("Query failed: " . mysqli_error($db)."<br>Query: ".$qyery);
    $ret=[];
    $ind=0;
    while ($row = mysqli_fetch_array($result, MYSQL_ASSOC)){
        $ret[$ind] = array("$par1" => $row["$par1"], "$par2" => $row["$par2"], "$par3" => $row["$par3"]);
        $ind=$ind+1;
    }
    mysqli_close($db);
    return $ret;
}

function query_4($query, $par1,$par2, $par3, $par4){
    $db=connect_to_db();
    $result = mysqli_query($db, $query)
        or die("Query failed: " . mysqli_error($db)."<br>Query: ".$qyery);
    $ret=[];
    $ind=0;
    while ($row = mysqli_fetch_array($result, MYSQL_ASSOC)){
        $ret[$ind] = array("$par1" => $row["$par1"], "$par2" => $row["$par2"], "$par3" => $row["$par3"], "$par4" => $row["$par4"]);
        $ind=$ind+1;
    }
    mysqli_close($db);
    return $ret;
}

function query_insert($query) {
    $db=connect_to_db();
    if (mysqli_query($db, $query)){
        mysqli_close($db);
        return true;
    }
    //per debug, se vuoi stampa metti a true
    if (true)	echo "INSERT failed: " . mysqli_error($db) . "<br>Query: ". $query;
    mysqli_close($db);
    return false;
}

function query_insert_ret_es($query) {//semplice query, ritorna l'esito senza controllarlo
    $db=connect_to_db();
    $esito = mysqli_query($db, $query);
    if (true)	echo "INSERT failed: " . mysqli_error($db) . "<br>Query: ". $query;
    mysqli_close($db);
    return $esito;
}

function app_log_d($app) {
    $f=fopen("/home/stefano/Scrivania/log.txt", "a");
    if (!$f) {
        echo "<script>console.log('[fucntion.php] File log.txt non found.	Action: write in to the log.txt are disabled');</script>";
    }else {
        fwrite($f, $app);
        fclose($f);
    }
}

function simple_query($query){
    $db=connect_to_db();
    $result = mysqli_query($db, $query);
    if (!$result){
        app_log_d("Simple query fallita: $query\n");
        if (true) echo"Simple Query failed: " . mysqli_error($db)."<br>Query: ".$query. "<br>";
        mysqli_close($db);		
        return false;
    }
    mysqli_close($db);
    return $result;
}

//		$pd= query_4("select * from USERS", "name", "surname", "birth", "email");
//		print_r("Nome: " .$pd[0]["name"] . "<br>");
//		print_r("cogNome: " .$pd[0]["surname"] . "<br>");
//		print_r("birth: " .$pd[0]["birth"] . "<br>");
//		print_r("mail: " .$pd[0]["email"] . "<br>");

//		print_r( query("select * from USERS", "name")[0]);
?>
