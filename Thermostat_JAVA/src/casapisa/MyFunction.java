package casapisa;

import static casapisa.Thermostat.*;
import static casapisa.MyFunction.*;
import java.io.*;
import java.net.*;
import java.util.*;

/**
 *
 * @author stefano
 */
public class MyFunction {
    
    public static int randNumber(int max) {
        return ((int)(Math.random()*100)) % max; 
    }

    /**
     * Crea un nome valido sia per windows che per sistemi unix sostituendo eventuali caratteri non ammessi dai SO
     * @param path  percorso del file
     * @param name  nome che si vuole dare al file
     * @return secondo te?
     * La funzione non aggiunge nessuna estensione al file di sua iniziativa, va passata quindi nel secondo argomento
    */
    public static String createNamePath(String path, String name) {
        if (System.getProperty("os.name").equalsIgnoreCase("linux")) {
            return path + name;
        } else {    //Do per scontato sia windows quindi
            return path + name.replaceAll(":", "-");
        }
    }
    
    public static String getUserName() {
        return System.getProperty("user.name");
    }

    public static String getUserName_noroot() {
        return "pi";
    }

    public static String getHome() {
        return System.getProperty("user.home");
    }
    
    public static String getSO() {
        return System.getProperty("path.separator");
    }
    
    public static String getDesktopPath() {
        if (getUserName().equalsIgnoreCase("root")) {
            //print("[WARNING] Esecuzione 'Mail' con utente SU. È stato bypassato e per i file sarà trattato come user: pi");
            //se siamo qui è perchè allora siamo su linux, e non vogliamo che vengano generati path del tipo: /root/Desktop/
            return "/home/pi" + System.getProperty("file.separator") + "Desktop" + System.getProperty("file.separator");
        }
        return getHome() + System.getProperty("file.separator") + "Desktop" + System.getProperty("file.separator");
    }


    public static String getDesktopPath(boolean withRoot) { //questa funzione può generare il path: /root/Desktop
        return getHome() + System.getProperty("file.separator") + "Desktop" + System.getProperty("file.separator");
    }
    
    public static boolean removeFile(String path){
        return new File(path).delete();
    }
    
    
    public static boolean checkInternet() {
        String site = "www.google.it";
        try (Socket socket = new Socket()) {
            InetSocketAddress addr = new InetSocketAddress(site, 80);
            socket.connect(addr, 3000);
            return socket.isConnected();
        } catch (IOException ex) {
            return false;
        }
    }
    
    public static String getIp(){
        URL whatismyip;
        while (!checkInternet()) sleep_s(1);
        for (int i=0; i<20; i++) {
            try {
                whatismyip = new URL("http://checkip.amazonaws.com");
                BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
                String ip = in.readLine(); //you get the IP as a String
                in.close();
                return ip;
            } catch (MalformedURLException ex) {
                //Logger.getLogger(MyFunction.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                //Logger.getLogger(MyFunction.class.getName()).log(Level.SEVERE, null, ex);
            }
            sleep_s(i);
        }
        return "getip() failed after 20 attemps";
    }
    
    public static synchronized boolean isComputer(){
        try{
            BufferedReader file= new BufferedReader(new FileReader("../file/pc.txt"));
            file.close();
            return true;
        }catch (IOException e){ return false; }
    }

    public static boolean isNumber_double(String val){
        try{
            Float.parseFloat(val);
            return true;
        }catch(Exception e){
            return false;
        }
    }

    public static boolean isNumber(String value){
        try{
            Integer.parseInt(value);
            return true;
        }catch(Exception e){
            return false;
        }
    }
    
    public static Process exe(String cmd){
        try {
            if (stringConteins(cmd, "rm")) { return null; }
            Process p = Runtime.getRuntime().exec(cmd);
            return p;
        } catch (IOException e) {
            printErr("Error by exe. Command: " + cmd);
            return null;
        }
    }
    
    
/**
 * Dice se esiste un certo file
 * @param path 
 * @return true se il file esiste, false altrimenti
*/  public static boolean existFile(String path){
        try {
            BufferedReader f = new BufferedReader(new FileReader(path));
            f.close();
            return true;
        } catch (IOException e){ return false;}
    }

/**
 * Leggi il file passato come argomento. OVERLOAD, scrive sulla lista di tipo String passata
 * @param path percorso più nome file del file da leggere
 * @param file è la lista in cuu viene salvato il contenuto del file
 * @return vettore delle righe lette se non ci sono errori altrimenti viene eseguita una stampa dell'errore e la funzione ritorna il valore "_0_"
 */
    public static synchronized String[] readFile(String path, LinkedList<String> file){
        try{
            BufferedReader f = new BufferedReader(new FileReader(path));
            while(true){
                String buffer = f.readLine();
                if (buffer == null) break;
                file.add(buffer);
            }
            f.close();
            return list2array(file);
        } catch(IOException e){
            String []err={"_0_"};
            printErr(e.getMessage()); 
            return err;
        }
    }

/**
 * Leggi il file passato come argomento. OVERLOAD, scrive su una variabile di tipo String e, ad ogni spazio, lei mette un '\n'
 * La variabile text viene inizializzata
 * @param path percorso più nome file del file da leggere
 * @param text è la stringa in cui viene salvato il contenuto del file. Verrà inizializzata!
 * @return Stringa contentenente le righe lette se non ci sono errori altrimenti viene eseguita una stampa dell'errore e la funzione ritorna il valore "_0_"
 */
    public static synchronized String readFile(String path, String text){
        try{
            BufferedReader f = new BufferedReader(new FileReader(path));
            LinkedList<String> file = new LinkedList<>();
            while(true){
                String buffer = f.readLine();
                if (buffer == null) break;
                file.add(buffer + "\n");
            }
            f.close();
            return array2string(list2array(file));
        } catch(IOException e){
            String err="file [" + path +"] not found";
            printErr(e.getMessage()); 
            return err;
        }
    }

/**
 * Leggi il file passato come argomento.
 * @param path percorso più nome file del file da leggere
 * @return vettore delle righe lette se non ci sono errori altrimenti viene eseguita una stampa dell'errore e la funzione ritorna una stringa di errore
 */
    public static synchronized String[] readFile(String path){
        try{
            BufferedReader f = new BufferedReader(new FileReader(path));
            LinkedList<String> file = new LinkedList<>();
            while(true){
                String buffer = f.readLine();
                if (buffer == null) break;
                file.add(buffer);
            }
            f.close();
            return list2array(file);
        } catch(IOException e){
            String []err={"file [" + path +"] not found"};
            printErr(e.getMessage()); 
            return err;
        }
    }
    
/**
 * Scrivi in un file la STRINGA
 * @param path è il percorso al file
 * @param toPrint è la stringa da stampare
 * @return true se riesce a scrivere, false se fallisce. Inoltre, se fallisce, stampa a video l'errore
*/
    public static synchronized boolean writeFile(String path, String toPrint){
        try {
            BufferedWriter f = new BufferedWriter(new FileWriter(path));
            f.write(toPrint);
            f.close();
            return true;
        } catch(IOException e){
            printErr(e.getMessage()); 
            return false;
        }
    }

/**
 * Scrivi in un file la STRINGA in APPENDED
 * @param appended booleano, sia se true che se false viene attivata questo metodo
 * @param path è il percorso al file
 * @param toPrint è la stringa da stampare
 * @return true se riesce a scrivere, false se fallisce. Inoltre, se fallisce, stampa a video l'errore.
*/
    public static synchronized boolean writeFile(boolean appended, String path, String toPrint){
        try{
            BufferedWriter f = new BufferedWriter(new FileWriter(path, true));
            f.write(toPrint);
            f.close();
            return true;
        } catch(IOException e){
            printErr(e.getMessage()); 
            return false;
        }
    }

/**
 * Scrivi in un file l'array passato 
 * @param path è il percorso al file
 * @param toPrint è la stringa da stampare
 * @return numero righe se riesce a scrivere, -1 se fallisce. Inoltre, se fallisce, stampa a video l'errore.
*/
    public static synchronized int writeFile(String path, String toPrint[]){
        try{
            BufferedWriter f = new BufferedWriter(new FileWriter(path));
            for (int i=0; i<toPrint.length; i++)
                f.write(toPrint[i] + "\n");
            f.close();
            return toPrint.length;
        } catch(IOException e){
            printErr(e.getMessage()); 
            return -1;
        }
    }

/**
 * Scrive in appended in un file
 * @param appended sia se true sia se false scrive in appended
 * @param path del file su cui scrivere
 * @param toPrint array da scrivere
 * @toPrint Array di String da scrivere
 * @return numero di righe scritte nel file, se -1 errore
*/
    public static synchronized int writeFile(boolean appended, String path, String toPrint[]){
        try{
            BufferedWriter f = new BufferedWriter(new FileWriter(path, true));
            for (int i=0; i<toPrint.length; i++)
                f.write(toPrint[i] + "\n");
            f.close();
            return toPrint.length;
        } catch(IOException e){
            printErr(e.getMessage()); 
            return -1;
        }
    }

    public static synchronized int writeFile(String path, List list){
        //It works with:      LinkedList<String>
        String toPrint[] = list2array(list);
        try{
            BufferedWriter f = new BufferedWriter(new FileWriter(path));
            for (int i=0; i<toPrint.length; i++)
                f.write(toPrint[i] + "\n");
            f.close();
            return toPrint.length;
        } catch(IOException e){
            printErr(e.getMessage()); 
            return -1;
        }
    }

/**
 * Questa funzione non è mai stata testata
 * @deprecated 
 * @return 
 * @param path 
 * @param list
 * @param unless  
*/  public static synchronized boolean writeFile(String path, List list, boolean unless){ //Pericolosa!
        //cambia il parametro di ritorno
        print("This writeFie version was never tested, be careful. #0120");
        String toPrint[] = list2array(list);
        try{
            BufferedWriter f = new BufferedWriter(new FileWriter(path));
            for (int i=0; i<toPrint.length; i++)
                f.write(toPrint[i] + "\n");
            f.close();
            return true;
        } catch(IOException e){
            printErr(e.getMessage()); 
            return false;
        }
    }
    
/**
 * Dato un file rimuove una riga. ATTENZIONE: se il file non esiste viene tornato FALSE 
 * Se sono presenti più righe uguali vengono cancellate tutte
 * @param path del file da cui si vuole eliminare una riga
 * @param row riga da cancellare, DEVE ESSERE ASSOLUTAMENTE UGUALE ANCHE NEL CASE
 *      Questa funzione è case sensitive
 * @return true se trova la riga e riesce a cancellare, false se il file o la riga non esiste
*/  public static int removeRowToFile(String path, String row){
        if (!existFile(path)) {
            printErr("[ERRORE] removeRowToFile failed. [" + path + "] does not exist");
            return -1;
        }
        row = row.trim();
        String [] file = readFile(path);
        String content="";
        boolean tick=false;
        for (String f:file) {
            if (row.contains(f.trim()))
                tick=true;                  //è la riga che vogliamo cancellare
            else 
                content+=f+"\n";            //queste righe vogliamo salvarle
        }
        if (content.equals("")) {
            writeFile(path, "");
            return 0;
        }
        writeFile(path, content);
        if (tick) return 1;
        else return 10;
    }
    
/**
 * Data una stringa sostituisce gli spazi con il secondo parametro passsato
 * @param text generico testo da modificare
 * @param mark carattere da inserire al posto dello spazio
 * @return Stringa modificata
*/  public static String removeSpace(String text, String mark){
        String []vet=text.split(" ");
        String s="";
        for (int i=0; i<vet.length;i++){
            s+=vet[i]+mark;
        }
        return s.substring(0, s.length()-1);
    }

/**
 * Converte un array in una stringa equivalente
 * @param arrayToConverte se capisce dai
 * @return la stringa
*/
    public static String array2string(int []arrayToConverte){
        String s = "";
        if (arrayToConverte != null) {
            for (int a:arrayToConverte)
                s += a + " ";
        }
        return s;
    }
    
    
/**
 * Converte un array in una stringa equivalente
 * @param arrayToConverte se capisce dai
 * @return la stringa
*/
    public static String array2string(String []arrayToConverte){
        String s="";
        for (String a:arrayToConverte)
            s+=a+" ";
        return s;
    }
    
/**
 *@param master Stringa su cui effettuare la ricerca
 *@param toSearch Stringa da ricercare sulla stringa master
 *@return Ritorna vero se esiste almeno una volta la parola nella stringa master
*/  public static boolean stringConteins(String master, String toSearch){
        String parole[]=master.split(" ");
        for(String word:parole){
            if (word.equalsIgnoreCase(toSearch.toLowerCase().trim()))
                return true;
        }
        return false;
    }
    
/**
 * NOTA BENE: la lista passata viene splittata sugli spazi!!!!!
 *@param lis lista su cui effettuare la ricerca
 *@param ele stringa da cercare nella lista
 *@return ritorna vero se esiste in almeno una occorenza la parola cercata ignorando le lettere maiuscole e minuscole
*/  public static boolean listConteins(LinkedList<String> lis, String ele){
        if (lis.isEmpty()) return false;
        String []lista=list2array(lis);                                       //Converti in array per semplicità
        for (String l:lista){                                                 //Scorri tutti gli elementi della lista
            String []riga= l.split(" ");                                      //Ogni elemento è una riga, splittala
            for (String r:riga){                                               //Ogni riga ha più parole, splitta ogni riga
                if (r.equalsIgnoreCase(ele.toLowerCase().trim()))  //E vedi se una delle parole è contenuta nella riga
                    return true;
            }                                                 //le parole confrontate sono "trimmate" e "lowerate"
        }
        return false;
    }
    
    public static void sleep_s(int sec){
        try{
            Thread.sleep(sec*1000);
        } catch (InterruptedException e){}
    }

    public static void sleep_m(int ms){
        try{
            Thread.sleep(ms);
        } catch (InterruptedException e){}
    }

    public static List array2list(String []array){
        return new LinkedList(Arrays.asList(array));
    }
    
    public static String[] list2array(List<String> list){
        return list.toArray(new String[list.size()]);
    }
    
    public static boolean _enablePrint = true;
    public static void disablePrint(){
        _enablePrint = false;
    }
    public static void enablePrint(){
        System.out.println("All print are enabled");
        _enablePrint = true;
    }
    public static void print(String s){
        if (_enablePrint)
            System.out.println(s);
    }
    
    public static void printErr(String s) {
        System.err.println(s);
    }

    public static void print(){
        if (_enablePrint)
            System.out.println();
    }
    
    
}


class temporaryThread extends Thread {
    private final int durata;
    private final int comando;
    private Object parametro;
    private Object arrayParametri[];

    public temporaryThread(int com, int d){
        comando = com;
        durata=d;
    }

    public temporaryThread(int com, int durat, Object par){
        comando = com;
        durata=durat;
        parametro = par;
    }
    
//new ThreadTemporaneo(3, -1, toPrint).start();
//new ThreadTemporaneo(10, -1);
    public temporaryThread(int com, int durat, Object []par){
        comando = com;
        durata=durat;
        arrayParametri = par;
    }
    
    @Override
    public void run() {
        switch(comando) {
            case TASK_MAIL:
                boolean mutex = true;
                handleMail = new Mail(userMail, userPassword);
                String adminAddress = handleMail.mailsAccepted.get(0).getMail();
                if (GPIO.debug == false)
                    handleMail.SendMail(adminAddress, "Avvio termostato", "Il termostato è stato avviato alle " + new Date_s().getTimestamp());
                
                writeFile(directory + "/mail.txt", userMail + "@gmail.com");
                
                while (true) {
                    Date_s date = new Date_s();
                    if (date.getHourValue()== 5 && date.getMinuteValue()== (27 + (int)(Math.random()*10))) { //required by google
                        if (mutex) {
                            writeFile(true, getDesktopPath() + "log_restart_mail.txt", "Si sta istanziando nuovamente la classe Mail(). Ora:" + date.getTimestamp() + 
                                    " - mi aspetto siano le 5 e 27+-10\n");
                            handleMail = null;
                            sleep_s(30);
                            handleMail = new Mail(userMail, userPassword);
                            sleep_s(30);
                        }
                        mutex = false;
                    } else mutex = true;
                                        
                    handleMail.managementMail();
                    
                    sleep_s(1);
                }
             case 2:
                // others task...
                break;
        }
    }    
}
        