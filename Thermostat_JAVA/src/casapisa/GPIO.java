package casapisa;

import static casapisa.Thermostat.*;
import static casapisa.MyFunction.*;
import static java.lang.Integer.*;

public class GPIO {
    /**
     * Use linux file system /sys/class 
     * 
     * In debug mode we don't want to use the gpio then we just print the action
    */
    static int ERROR = 999;
    static boolean debug = false;
    
    public static void initializeGpio(int gpio, String direction) {
        if (debug) {
            print("init gpio: " + gpio);
            return;
        }
        if (existFile("/sys/class/gpio/gpio" + gpio + "/value"))
            return;
        
        if (direction.equals("in") || direction.equals("out")) {
            writeFile("/sys/class/gpio/export", "" + gpio);
            sleep_m(500);
            writeFile("/sys/class/gpio/gpio" + gpio + "/direction", direction);
        } else {
            print("[GPIO->initialize] Direction must be 'in' or 'out' only. It was not possible to initialize this gpio: " + gpio);
        }
    }
    
    public static void writeGpio(int gpio, String value) {
        if (debug) {
            print("write gpio: " + gpio+ "\tvalue:" + value);
            return;
        }
        int actualValue = readGpio(gpio);
        if (actualValue != ERROR) {
            if (value.equals("0") || value.equals("1")) {
                if (actualValue != parseInt(value)) {
                    if (!writeFile("/sys/class/gpio/gpio" + gpio + "/value", value))
                        print("[GPIO->writeGpio] Writing failed");
                }
            } else {
                print("[GPIO->writeGpio] Bad value: " + value + " is not allowed");
            }
        }
    }
    
    public static int readGpio(int gpio) {
        if (debug) {
            print("read gpio: " + gpio);
            return 1;
        }
        try {
            return parseInt(readFile("/sys/class/gpio/gpio" + gpio + "/value")[0]);
        } catch (Exception e) {
            print("[GPIO->readGpio] " + e);
            return ERROR;
        }
    }
    
    public static void turnOnHeating() {
        print("     [GPIO] turn on radiators");
        writeGpio(Thermostat.gpioRelay, "0");
        writeFile(directory + "/heating.txt", "1");
        writeGpio(gpioLedStatusHeating, "1");
    }

    public static void turnOffHeating() {
        print("     [GPIO] turn off radiators");
        writeGpio(Thermostat.gpioRelay, "1");
        writeFile(directory + "/heating.txt", "0");
        writeGpio(gpioLedStatusHeating, "0");
    }
    
}
