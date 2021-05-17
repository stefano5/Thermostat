package casapisa;


import static casapisa.GPIO.*;
import static casapisa.Thermostat.*;
import static casapisa.MyFunction.*;
import static java.lang.Integer.*;
import java.util.LinkedList;

public class Thermostat {
    //path website with apache on raspbian. don't touch it
    public static String directory = "/var/www/html";
    
    /*****************  Pay attention please. 
     * only gmail mail are supported
     * you have to enable "access to less secure apps", see https://support.google.com/a/answer/6260879?hl=en
     * 
     * in userMail variable don't put "@gmail.com"
     * Therfore if your mail is "mariorossi@gmail.com" just put in userMail variable: "mariorossi" like below
     * userPassword variable is the password that you use to login in gmail
     * 
     * 
     * This software use apache. See https://www.raspberrypi.org/documentation/remote-access/web-server/apache.md
     * 
    */
    public static String userMail = "mariorossi";
    public static String userPassword = "passwordaccaount";
    
    ////##############   ENTER HERE THE GPIOs VALUE
    public static int gpioRelay = 21;                           //gpio reley (mandatory), of course, you can choose another gpio
    public static int gpioLedStatusSystem = 23;                 //led status system (thermostat is on or off?)
    public static int gpioLedStatusHeating = 24;                 //led status heating (radiators are on or off?)
    ////////////////////////////////////////////////////////////////////////////

    // Software's variable, don't touch them
    public static boolean systemIsOff;
    static LinkedList userUpdate;
    static Mail handleMail;
    static ManualProgram manualProgram;
    
    //Constants value
    private final static int SYSTEM_OFF = -1;
    private final static int INTERNAL_ERROR = -2;
    private final static int SYSTEM_ON_TABLE_MODE = 1;
    private final static int SYSTEM_ON_MANUAL_MODE = 2;
    public final static int TASK_MAIL = 1;
    public final static int PERIOD_MAIL_TASK = 1; //should be in [s], negative value means that we don't want choose it
    // Mail service required a precise jvm version!!

    public static void main(String[] args) {
        GPIO.debug = true;  //                                                  see me!!
        if (args.length != 0 && args[0].contains("d")) {
            print("GPIO enabled by users");
            GPIO.debug = false;
        }
        initialize();
        directoryControl();
        shutdown();
    }
    
    public static void initialize() {
        if (GPIO.debug)
            print("\n\n\t\tGPIO are disabled - debug mode. To enable gpio use 'd' parameter: java -jar Thermostat.jar d \n\n");
        
        //Solve all permission problem => deprecated
        //exe("sudo chmod u+w " + directory + "/*");
        //exe("sudo chown pi:pi " + directory + "/*");
        
        //Initialize gpio
        GPIO.initializeGpio(gpioRelay, "out");
        GPIO.initializeGpio(gpioLedStatusSystem, "out");
        GPIO.initializeGpio(gpioLedStatusHeating, "out");
        
        //Create user Structure 
        userUpdate = new LinkedList();
        userUpdate.add("Start: " + new Date_s().getData_gg_mm_aaaa());
        
        //Instace Mail service
        removeFile(directory + "/mail.txt");
        //new temporaryThread(TASK_MAIL, PERIOD_MAIL_TASK).start();
        
        //instance class
        manualProgram = new ManualProgram();    //here management functions
        
        //Load program
        loadOldProgram();
                
        //turn off heating
        GPIO.turnOffHeating();
    }
    
    
    
    public static void directoryControl() {
        int changedState = 0;
        boolean mutex = true;
        while (true) {
            Date_s date = new Date_s();
            getCommandFromShell();
            getAsyncCommunication();
            writeCheckInternet();            
           
            //print("##############################################");
            switch (controlStateSystem()) {
                case SYSTEM_OFF:    //system is off
                    if (changedState != SYSTEM_OFF) {
                        print("System off");
                        changedState = SYSTEM_OFF;
                        writeGpio(gpioLedStatusSystem, "0");
                        turnOffProgram();
                        GPIO.turnOffHeating();
                    }
                    break;
                case INTERNAL_ERROR:    //internal error. It's management independently
                    if (changedState != INTERNAL_ERROR) {
                        print("Errore rilevato e teoricamente corretto");
                        changedState = INTERNAL_ERROR;
                        writeGpio(gpioLedStatusSystem, "0");
                        GPIO.turnOffHeating();
                    }
                    break;
                case SYSTEM_ON_TABLE_MODE:     //system is on, TABLE MODE   => deprecated
                    if (changedState != SYSTEM_ON_TABLE_MODE) {
                        print("System on, table mode");
                        changedState = SYSTEM_ON_TABLE_MODE;
                        writeGpio(gpioLedStatusSystem, "1");
                    }
                    managementSystemTable(date);                
                    break;
                case SYSTEM_ON_MANUAL_MODE:     //system is on, MANUAL MODE
                    if (changedState != SYSTEM_ON_MANUAL_MODE) {
                        print("System on, manual mode");
                        changedState = SYSTEM_ON_MANUAL_MODE;
                        writeGpio(gpioLedStatusSystem, "1");
                    }
                    managementSystemManual(date);
                    break;
                default :
                    printErr("Error by controlStateSystem(). Internal error");
            }

            sleep_m(100);   //run 10 Hz
            if (!GPIO.debug)
                writeFile("/home/pi/data_termostato", date.getData() + " " + date.getTimestamp()+ "\nState: " + controlStateSystem() + " (manual mode: 2)\n");
        }
    }
    
    public static void loadOldProgram() {
        if (existFile(directory + "/program.txt")) {
            //read the row: 17:00 19:30 0 1
            String file[] = readFile(directory + "/program.txt");
            try {
                for (String row:file) {
                    String dump[] = row.split(" ");
                    manualProgram.setDay(dump[0], dump[1], parseInt(dump[2]));
                    manualProgram.startProgram(parseInt(dump[2]));
                }
            } catch (Exception e) {
                printErr("Failed to store old program. Clear file program.txt");
                writeFile(directory + "/program.txt", "");
            }
            print("All old program were stored");
        } else {
            printErr(directory + "/program.txt missed");
            //writeFile(directory + "/program.txt", "");
        }
    }
    
    
    public static void getAsyncCommunication() {
        String path_on = directory + "/turnOnHeating.txt";
        String path_off = directory + "/turnOffHeating.txt";

        if (existFile(path_on)) {
            print("User (by web) want to turn on the heating");
            GPIO.turnOnHeating();
            removeFile(path_on);
        }
        if (existFile(path_off)) {
            print("User (by web) want to turn off the heating");
            GPIO.turnOffHeating();
            removeFile(path_off);
        }
    }
    
    public static void writeCheckInternet() {
        if (checkInternet()) {
            writeFile(directory + "/checkInternet.txt", "1");
        } else {
            writeFile(directory + "/checkInternet.txt", "0");
        }
    }

/**
 * There is another C software that wants to interact with this software
 * C softrware provides async request, here we have to management then remove these request (request = file)
*/  public static void getCommandFromShell() {
        String home = "/home/pi/";
        if (existFile(home + "turnOnNow")) {
            GPIO.turnOnHeating();
            removeFile(home + "turnOnNow");
        }
        
        if (existFile(home + "turnOffNow")) {
            GPIO.turnOffHeating();
            removeFile(home + "turnOffNow");
        }
        
        if (existFile(home + "stopSystem")) {
            writeFile(directory + "/systemOn.txt", "0");
            removeFile(home + "stopSystem");
        }

        if (existFile(home + "startSystem")) {
            writeFile(directory + "/systemOn.txt", "1");
            removeFile(home + "stopSystem");
        }

    }
        
     /**
     *  @deprecated 
    */   
    public static void turnOnProgram() {
        for(int i=0; i<7; i++) {
            removeFile(directory + "/" + i + ".txt");
            for (int j=0; j<24; j++)
                writeFile(true, directory + "/" + i + ".txt", "1");
        }
    }

    /**
     *  @deprecated 
    */
    public static void turnOffProgram() {
        for(int i=0; i<7; i++) {
            removeFile(directory + "/" + i + ".txt");
            for (int j=0; j<24; j++)
                writeFile(true, directory + "/" + i + ".txt", "0");
        }
    }
    
    public static void checkWhoDoLastUpdate() {
        if (existFile(directory + "/user.txt")) {
            try {
                String user = readFile(directory + "/user.txt")[0];
                removeFile(directory + "/user.txt");
                userUpdate.add(user + " in " + new Date_s().getData_gg_mm_aaaa());
                print("Last update from: " + user);
            } catch(Exception e) {
                printErr("Fail to get the last user update. " + e);
            }
        }
    }
    
    public static int controlStateSystem() {
        if (!existFile(directory + "/systemOn.txt")) {
            writeFile(directory + "/systemOn.txt", "0");
            systemIsOff = true;
            return SYSTEM_OFF;
        }
        try {
            int stateSystem = parseInt(readFile(directory + "/systemOn.txt")[0]);
            if (stateSystem == 1) {
                systemIsOff = false;
                int stateType = parseInt(readFile(directory + "/type.txt")[0]);
                if (stateType == 1) {
                    return SYSTEM_ON_TABLE_MODE;
                } else {
                    return SYSTEM_ON_MANUAL_MODE;
                }
            } else {
                systemIsOff = true;
                return SYSTEM_OFF;
            }
        } catch (Exception e) {
            printErr("Exception controlStateSystem: " + e);
            writeFile(directory + "/systemOn.txt", "0");
            systemIsOff = true;
            return INTERNAL_ERROR;
        }
    }
    
    private static boolean mutexOn = true;
    private static boolean mutexOff = true;
    public static void managementSystemManual(Date_s date) {
        for (int i=0; i<7; i++) {
            String path = directory + "/request" + i + ".txt";
            try {
                if (existFile(path)) {
                    String hstart = readFile(path)[0];
                    String hend = readFile(path)[1];
                    //print("Day: " + i);
                    //print("Hstart: " + hstart);
                    //print("Hend: " + hend);
                    if (hstart.equals("999")) {
                        printErr("Internal error - communication php failed");
                    } else {
                        print("New program stored");
                        manualProgram.setDay(hstart, hend, i);
                        manualProgram.startProgram(i);
                    }
                }
            } catch(Exception e) {
                printErr("Internal error. Function: managementSystemManual, day: " + i + " failed");
                printErr(e.toString());
            } finally {
                removeFile(path);
            }
        }
        //manualProgram.removeStoppedProgram(directory + "/program.txt");
        

        if (manualProgram.isTimeToTurnOnHeating(date.getDayOfWeek())){
            if (mutexOn){    
                GPIO.turnOnHeating();        
                mutexOn = false;        
            }
        } else mutexOn = true;
        
        if (manualProgram.isTimeToTurnOffHeating(date.getDayOfWeek())){
            if (mutexOff) {
                manualProgram.stopProgram(date.getDayOfWeek());
                GPIO.turnOffHeating();
                mutexOff = false;
            }
        } else mutexOff = true;
    }

    
    /**
     * @deprecated   
    */
    public static void managementSystemTable(Date_s date) {
        try {
            String state[] = readFile(directory + "/" + date.getDayOfWeek() + ".txt");
            //print("Guardo il file: " + directory + "/" + date.giornoDellaSettimana + ".txt");
            for (int i=0; i<24; i++) {
                if (i == date.getHourValue()) {
                    //print("Guardo il valore: " + state[i]);
                    //print ("Sono le " + date.getOra());
                    if (state[i].equals("1")) {
                        GPIO.turnOnHeating();
                    } else {
                        GPIO.turnOffHeating();
                    }
                }
            }
        } catch (Exception e) {
            printErr("Exception managementSystem: " + e);
            printErr("Try to re-initialize file system ");
            initFileSystemTable(date.getDayOfWeek());
        }
    }
    
    /**
     * @deprecated   
    */
    public static void initFileSystemTable(int day) {
        String pathFile = directory + "/" + day;
        if (existFile(pathFile))
            removeFile(pathFile);        
        for (int j=0; j<24; j++)
            writeFile(true, directory + "/" + day + ".txt", "0\n");
    }
    
    
    public static void shutdown() {
        print("Shtdown java software");
        GPIO.turnOffHeating();
    }
}
