package casapisa;

import static casapisa.Thermostat.*;
import static casapisa.MyFunction.*;
import java.util.*;

/**
 *
 * @author stefano
 */
public class ManualProgram {
    LinkedList<Week> monday;
    LinkedList<Week> tuesday;
    LinkedList<Week> wednesday;
    LinkedList<Week> thursday;
    LinkedList<Week> friday;
    LinkedList<Week> saturday;
    LinkedList<Week> sunday;
    
    public ManualProgram() {
        monday = new LinkedList<>();
        tuesday = new LinkedList<>();
        wednesday = new LinkedList<>();
        thursday = new LinkedList<>();
        friday = new LinkedList<>();
        saturday = new LinkedList<>();
        sunday = new LinkedList<>();
        monday.add(new Week());
        tuesday.add(new Week());
        wednesday.add(new Week());
        thursday.add(new Week());
        friday.add(new Week());
        saturday.add(new Week());
        sunday.add(new Week());
    }
    
    public void setDay(String hstart, String hend, int day) {
        switch(day) {
            case 0:
                monday.get(0).set(hstart, hend, day);
                break;
            case 1:
                tuesday.get(0).set(hstart, hend, day);
                break;
            case 2:
                wednesday.get(0).set(hstart, hend, day);
                break;
            case 3:
                thursday.get(0).set(hstart, hend, day);
                break;
            case 4:
                friday.get(0).set(hstart, hend, day);
                break;
            case 5:
                saturday.get(0).set(hstart, hend, day);
                break;
            case 6:
                sunday.get(0).set(hstart, hend, day);
                break;
            default:
                print("error 125455");
        }
    }
    
    public void startProgram(int day) {
        switch(day) {
            case 0:
                monday.get(0).setState(true);
                insertNewProgram(monday.get(0).toString());
                //writeFile(true, CasaPisa.directory + "/program.txt" , monday.get(0).toString());
                break;
            case 1:
                tuesday.get(0).setState(true);
                insertNewProgram(tuesday.get(0).toString());
                //writeFile(true, CasaPisa.directory + "/program.txt" , tuesday.get(0).toString());
                break;
            case 2:
                wednesday.get(0).setState(true);
                insertNewProgram(wednesday.get(0).toString());
                //writeFile(true, CasaPisa.directory + "/program.txt" , wednesday.get(0).toString());
                break;
            case 3:
                thursday.get(0).setState(true);
                insertNewProgram(thursday.get(0).toString());
                //writeFile(true, CasaPisa.directory + "/program.txt" , thursday.get(0).toString());
                break;
            case 4:
                friday.get(0).setState(true);
                insertNewProgram(friday.get(0).toString());
                //writeFile(true, CasaPisa.directory + "/program.txt" , friday.get(0).toString());
                break;
            case 5:
                saturday.get(0).setState(true);
                insertNewProgram(saturday.get(0).toString());
                //writeFile(true, CasaPisa.directory + "/program.txt" , saturday.get(0).toString());
                break;
            case 6:
                sunday.get(0).setState(true);
                insertNewProgram(sunday.get(0).toString());
                //writeFile(true, CasaPisa.directory + "/program.txt" , sunday.get(0).toString());
                break;            
            default:
                printErr("error 445");
        }
        removeStoppedProgram(Thermostat.directory + "/program.txt");
    }
    
    public void stopProgram(int day) {
        switch(day) {
            case 0:
                monday.get(0).setState(false);
                break;
            case 1:
                tuesday.get(0).setState(false);
                break;
            case 2:
                wednesday.get(0).setState(false);
                break;
            case 3:
                thursday.get(0).setState(false);
                break;
            case 4:
                friday.get(0).setState(false);
                break;
            case 5:
                saturday.get(0).setState(false);
                break;
            case 6:
                sunday.get(0).setState(false);
                break;
            default:
                printErr("error 3555");
        }
        removeStoppedProgram(Thermostat.directory + "/program.txt");
    }
    
    public static void removeStoppedProgram(String path) {
        String file[] = readFile(path);
        String newFile="";
        for (String row:file) {
//Analisi su:   row = hstart + " " + hend + " " + dayWeek + " 1";
            String word[] = row.split(" ");
            if (word[3].equals("1"))
                newFile += row + "\n";
        }
        writeFile(path, newFile);
    }
    
    public static boolean clearsTodaysPrograms() {
        return clearsPrograms(new Date_s().getDayOfWeek());
    }
    
    public static boolean clearsPrograms(int day) {
        String path = directory + "/program.txt";

        String file[] = readFile(path);
        String newFile="";
        boolean removed = false;
        for (String row:file) {
//Analisi su:   row = hstart + " " + hend + " " + dayWeek + " 1";
            String word[] = row.split(" ");
            if (!word[2].equals(String.valueOf(day)))
                newFile += row + "\n";
            else removed = true;
        }
        writeFile(path, newFile);
        return removed;
    }
    
    public static void insertNewProgram(String newProgram) {
        String path = directory + "/program.txt";
        
        if (!existFile(path)) {
            writeFile(path, newProgram.trim());
            return;
        }
        
        String file[] = readFile(path);
        for (String row:file) {
//Analisi su:   row = hstart + " " + hend + " " + dayWeek + " 1";
            if (row.equalsIgnoreCase(newProgram.trim())) //to avoid multiple (same) row
                return;
        }
        writeFile(true, path, newProgram.trim());
    }
        
    public static void writeUser(String path) {
        writeFile(path, userUpdate);
    }
    
    
    public boolean isTimeToTurnOnHeating(int day) {
        switch(day) {
            case 0:
                if (monday.get(0).getState())
                    return monday.get(0).startHeatingNow();
                return false;
            case 1:
                if (tuesday.get(0).getState())
                    return tuesday.get(0).startHeatingNow();
                return false;
            case 2:
                if (wednesday.get(0).getState())
                    return wednesday.get(0).startHeatingNow();
                return false;
            case 3:
                if (thursday.get(0).getState())
                    return thursday.get(0).startHeatingNow();
                return false;
            case 4:
                if (friday.get(0).getState())
                    return friday.get(0).startHeatingNow();
                return false;
            case 5:
                if (saturday.get(0).getState())
                    return saturday.get(0).startHeatingNow();
                return false;
            case 6:
                if (sunday.get(0).getState())
                    return sunday.get(0).startHeatingNow();
                return false;
            default:
                printErr("errore 654820");
                return false;
        }
    }
    
        public boolean isTimeToTurnOffHeating(int day) {
        switch(day) {
            case 0:
                if (monday.get(0).getState())
                    return monday.get(0).stopHeatingNow();
                return false;
            case 1:
                if (tuesday.get(0).getState())
                    return tuesday.get(0).stopHeatingNow();
                return false;
            case 2:
                if (wednesday.get(0).getState())
                    return wednesday.get(0).stopHeatingNow();
                return false;
            case 3:
                if (thursday.get(0).getState())
                    return thursday.get(0).stopHeatingNow();
                return false;
            case 4:
                if (friday.get(0).getState())
                    return friday.get(0).stopHeatingNow();
                return false;
            case 5:
                if (saturday.get(0).getState())
                    return saturday.get(0).stopHeatingNow();
                return false;
            case 6:
                if (sunday.get(0).getState())
                    return sunday.get(0).stopHeatingNow();
                return false;
            default:
                printErr("error 0215841");
                return false;
        }   
    }
    
}

class Week {
    private String hstart, hend;
    private int dayWeek;
    private boolean state;
    
    public Week(String hstart, String hend, int dayWeek) {
        this.hstart = hstart;
        this.hend = hend;
        this.dayWeek = dayWeek;
        state = false;
    }

    public Week() {
    }
    
    public int getDay(){
        return dayWeek;
    }
    
    public void set(String hstart, String hend, int dayWeek) {
        this.hstart = hstart;
        this.hend = hend;
        this.dayWeek = dayWeek;
        print("Istanzio giorno: " + dayWeek+ "\nhstart:" + hstart + "\nhend:" + hend);
    }
    
    public void setState(boolean stat) {
        state=stat;
    }
    public boolean getState() {return state;}
    
    public boolean startHeatingNow() {
        Date_s date = new Date_s();
        //print("Confronto accensione "+ hstart + " " + data.getOrologio() + " giorno: " + dayWeek + " " + data.getGiornoSettimana());
        if (dayWeek == date.getDayOfWeek())
            return hstart.equals(date.getClock());
        return false;
    }

    public boolean stopHeatingNow() {
        Date_s date = new Date_s();
        //print("Confronto spegnimento "+ hend + " " + data.getOrologio() + " giorno: " + dayWeek + " " + data.getGiornoSettimana());
        if (dayWeek == date.getDayOfWeek())
            return hend.equals(date.getClock());
        return false;
    }
    
    @Override
    public String toString() {
        if (state)
            return hstart + " " + hend + " " + dayWeek + " 1" + "\n";
        else
            return hstart + " " + hend + " " + dayWeek + " 0" + "\n";            
    }
}
