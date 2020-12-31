package casapisa;

import static java.lang.Integer.parseInt;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.regex.PatternSyntaxException;

public class Date_s {
    private int     
            currentHour,
            currentMinute,
            dayOfMonth_int,
            month_int,
            second,
            year,
            dayOfWeek,
            weekOfYear,
            milliSecond;
    private String dayOfWeek_str, month_str;
    private LinkedList<String> wordDay;
    private LinkedList<String> wordMonth;
    private final int language;
    public final static int EN=0, IT=1;
    
    /**
 * I valori ritornati da questa classe sono relativi a quando la classe stessa è stata istanziata
 * @author Stefano Maugeri
 * @since Marzo 2020    demo
*/  public Date_s() {
        language = EN;
        initVariable();
    }
  
    public Date_s(int lan) {
        language = lan;
        initVariable();
    }

    private void initVariable() {
        int parse_day_italian_week;
        GregorianCalendar cal = new GregorianCalendar();
        dayOfMonth_int = cal.get(Calendar.DAY_OF_MONTH);
        parse_day_italian_week = cal.get(Calendar.DAY_OF_WEEK);

        month_int = cal.get(Calendar.MONTH) + 1;
        year = cal.get(Calendar.YEAR);
        currentHour = cal.get(Calendar.HOUR);
        currentMinute = cal.get(Calendar.MINUTE);
        int am = cal.get(Calendar.AM_PM);
        second = cal.get(Calendar.SECOND);
        weekOfYear = cal.get(Calendar.WEEK_OF_YEAR);
        milliSecond = cal.get(Calendar.MILLISECOND);

        wordDay = new LinkedList<>();
        wordMonth = new LinkedList<>();
        
        createDictionaryWordDay(wordDay, language);
        createDictionaryWordMonth(wordMonth, language);
        
        switch (parse_day_italian_week) {
            case 2: dayOfWeek_str = wordDay.get(0);      parse_day_italian_week = 0; break;
            case 3: dayOfWeek_str = wordDay.get(1);      parse_day_italian_week = 1; break;
            case 4: dayOfWeek_str = wordDay.get(2);      parse_day_italian_week = 2; break;
            case 5: dayOfWeek_str = wordDay.get(3);      parse_day_italian_week = 3; break;
            case 6: dayOfWeek_str = wordDay.get(4);      parse_day_italian_week = 4; break;
            case 7: dayOfWeek_str = wordDay.get(5);      parse_day_italian_week = 5; break;
            case 1: dayOfWeek_str = wordDay.get(6);      parse_day_italian_week = 6; break;
            default: dayOfWeek_str= ">" + parse_day_italian_week + "<";
        }
        
        dayOfWeek = parse_day_italian_week;
        if (am == 1)
            currentHour += 12;
        
        month_str = parseMonth_int2str(month_int, language);
        //pare che funzioni tutto sto casino
    }
    
    private static void createDictionaryWordMonth(LinkedList<String> wordMonth, int language) {
        switch (language) {
            case EN:
                wordMonth.add("January"); 
                wordMonth.add("February");
                wordMonth.add("March"); 
                wordMonth.add("April"); 
                wordMonth.add("May"); 
                wordMonth.add("June"); 
                wordMonth.add("July"); 
                wordMonth.add("August"); 
                wordMonth.add("September"); 
                wordMonth.add("October"); 
                wordMonth.add("November"); 
                wordMonth.add("December");
                break;
            case IT:
                wordMonth.add("Gennario"); 
                wordMonth.add("Febbraio");
                wordMonth.add("Marzo"); 
                wordMonth.add("Aprile"); 
                wordMonth.add("Maggio"); 
                wordMonth.add("Giugno"); 
                wordMonth.add("Luglio"); 
                wordMonth.add("Agosto"); 
                wordMonth.add("Settembre"); 
                wordMonth.add("Ottobre"); 
                wordMonth.add("Novembre"); 
                wordMonth.add("Dicembre");
                break;
        }
    }

    private static void createDictionaryWordDay(LinkedList<String> wordDay, int language) {
        switch (language) {
            case EN:
                wordDay.add("Monday");     //0
                wordDay.add("Tuesday");    //1
                wordDay.add("Wednesday");  //2
                wordDay.add("Thursday");   //3
                wordDay.add("Friday");     //4
                wordDay.add("Saturday");   //5
                wordDay.add("Sunday");     //6
                break;
            case IT:
                wordDay.add("Lunedì");
                wordDay.add("Martedì");
                wordDay.add("Mercoledì");
                wordDay.add("Giovedì");
                wordDay.add("Venerdì");
                wordDay.add("Sabato");
                wordDay.add("Domenica");
                break;
        }
    }

/**
 * @return Stringa col seguente formato:
 * del tipo: 07:09:01       Sono le 7 e 9 minuti e 1 secondo
*/
    public String getTimestamp() {
        return getClock()+ ":" + getSecond();
    }

/**
 * @return il secondo di quando l'oggetto è stato istanziato
 * esempi formato: "05" - "12" - "00" ecc
*/  public String getSecond() {
        return parseValue(second);
    }
    
/**
 * @return il giorno del mese di quando l'oggetto è stato istanziato
 * esempi formato: "05" - "12" - "30" ecc
*/  public String getDayOfMonth() {
        return parseValue(dayOfMonth_int);
    }

/**
 * @return il secondo di quando l'oggetto è stato istanziato
 * esempi formato: "05" - "12" - "00" - "59" ecc
*/  public String getMinute() {
        return parseValue(currentMinute);
    }

/**
 * @return l'ora di quando l'oggetto è stato istanziato
 * esempi formato: "05" - "12" - "00" ecc
*/  public String getHour() {
        return parseValue(currentHour);
    }
    
/**
 * Dipende dalla lingua
 * @return il mese di quando l'oggetto è stato istanziato
 * esempi formato: "05" - "12" - "01" ecc
*/  public String getMonth() {
        return parseValue(month_int);
    }
  
  /**
   * Dipende dalla lingua
   * @return il mese di quando l'oggetto è stato instanzato
   * esempi formato: "gennaio" - "febbraio" - "June"
  */
    public String getMonth_name() {
        return month_str;
    }
    
    /**
    * converti un qualunque intero in stringa di sempre due carattersi
    * parseValue(5) ritorna "05"
    * parseValue(19) ritorna "19"
    * parseValue(1) ritorna "01"@
    * @return 
    */
    public static String parseValue(int value) {
        if (value < 10)
            return "0" + value;
        return value + "";
    }

/**
 * giorno della settimana in int.
 * lunedi -> 0.
 * martedi -> 1.
 * mercoledi-> 2.
 * giovedi-> 3.
 * venerdi-> 4.
 * sabato-> 5.
 * sabato-> 6.
 * 
 * @return numero
*/  public int getDayOfWeek(){
        return dayOfWeek;
    }
     
/**
 * @param date data da confrontare con la data passata. Es: 2 2 2018
 * @return true o false
 * 
*/  public boolean pastDate(String date) {
        try {
            String []data = splitDataInArray(date, " ");
            int pastDay = Integer.parseInt(data[0]);
            int pastMounth  = isNumber(data[1]) ? Integer.parseInt(data[1]) : parseMonth_str2int(data[1]);
            int pastYear = Integer.parseInt(data[2]);
            if (pastDay<0 || pastMounth<0 || pastYear<0)
                throw new NumberFormatException();

            if (pastYear<year)
                return true;
            if (year<pastYear)
                return false;
            if (month_int>pastMounth)
                return true;
            if (month_int==pastMounth){
                if (dayOfMonth_int>pastDay)
                    return true;
            }
            return false;
        } catch (NumberFormatException e){
            System.err.println("[Data] Eccezione funzione \"dataPassata\" La funzione ritorna false in questo caso. Input: " + date);
            return false;
        }
    }
/**
 * @param date "1 1 2000"
 * @return "01 01 2000"
 */
public static String adjustDate(String date) {
    try {    
        String []ret = date.split(" ");
        return parseValue(parseInt(ret[0])) + " " + parseValue(parseInt(ret[1])) + " " + ret[2]; 
    } catch(Exception e) {
        System.err.println("adjustDate error. input: [" + date + "]. Required [1 1 2000]");
        return "adjustDate error. input: [" + date + "]. Required [1 1 2000]";
    }
}

/**
 * @param data "1"
 * @return "01"
 */
    public static String adjustValue(String data) {
        try {
            if (isNumber(data))
                return parseValue(parseInt(data)); 
            return data;
        } catch(Exception e) {
            System.err.println("adjustDate error. input: [" + data + "]. Required [1 1 2000]");
            return "adjustDate error. input: [" + data + "]. Required [1 1 2000]";
        }
    }
    
/**
 * @return secondo attuale relativo all'istanza della chiamata
 * range [0, 59]
*/  public int getSecondValue(){
        return second;
    }
    
/**
 * @return giorno del mese attuale relativo all'istanza della chiamata
 * range [1, 31] in base al mese
*/  public int getDayValue() {
        return dayOfMonth_int;
    }
    
/**
 * @return ora attuale relativo all'istanza della chiamata
 * range [1, 31] in base al mese
*/  public int getHourValue() {
        return currentHour;
    }

/**
 * @return anno attuale relativo all'istanza della chiamata
 * range [1, 31] in base al mese
*/  public int getYearValue() {
        return year;
    }

/**
 * @return millisecondo attuale relativo all'istanza della chiamata
 * range [0, 1000]
*/  public int getMilliSecondValue() {
        return milliSecond;
    }

/**
 * @return mese attuale relativo all'istanza della chiamata
 * range [1, 12] in base al mese
*/  public int getMounthValue() {
        return month_int;
    }

/**
 * Converte il giorno passato come intero nella stringa corrispondente. Es.: 0 -> Lunedì
 * @param day che si vuole convertire
 * @return Giorno convertito in stringa
*/  public String parseDayOfWeek_int2string(int day) {
        return wordDay.get(day);
    }

/**
 * @param day "lunedi", "martedì", "monday"
 * @param language Date.EN, Date.IT
 * @return Il giorno della settimana (da 0 a 6)
 * In italiano è indifferente utilizzare gli accenti finali o meno.
 * Non è case sensitive
 * Esempi:
 * string2int_dayOfWeek("lunedi", Date.IT) -> ottengo: 0
 * string2int_dayOfWeek("martedì", Date.IT) -> ottengo: 1
 * string2int_dayOfWeek("monday", Date.EN) -> ottengo: 0
*/  public static int string2int_dayOfWeek(String day, int language) {
        LinkedList <String> wordDay = new LinkedList();
        createDictionaryWordDay(wordDay, language);        
        for (int i=0; i<wordDay.size(); i++) {
            if (wordDay.get(i).substring(0, wordDay.get(i).length()-1).equalsIgnoreCase(day.substring(0, day.length()-1)))
                return i;
        }
        return -1;
    }

/**
 * NB: non specificando la lingua si sottointende l'inglese
 * @param day "lunedi", "martedì", "monday"
 * @return Il giorno della settimana (da 0 a 6)
 * In italiano è indifferente utilizzare gli accenti finali o meno.
 * Non è case sensitive
 * Esempi:
 * string2int_dayOfWeek("lunedi", Date.IT) -> ottengo: 0
 * string2int_dayOfWeek("martedì", Date.IT) -> ottengo: 1
 * string2int_dayOfWeek("monday", Date.EN) -> ottengo: 0
*/  public static int string2int_dayOfWeek(String day) {
        return string2int_dayOfWeek(day, Date_s.EN);
    }
/**
 * @param dateToUpdate data di partenza
 * @param sum   offset
 * @return dateToUpdate + sum
*/
    public String sumDay_dd_(String dateToUpdate, int sum) {
        int newDay;
        int month;
        int year;
        String dateSplit[] = dateToUpdate.split(" ");
        try {
            newDay = (Integer.parseInt(dateSplit[0]) + sum) % getNumberOfDayInMonth();
            if (newDay==0) newDay = getNumberOfDayInMonth();
            month = Integer.parseInt(dateSplit[1]);
            year = Integer.parseInt(dateSplit[2]);
        } catch (NumberFormatException e){
            System.out.println("Errore " + e.getLocalizedMessage() + ".Errore 36");
            return "[Data->aggiungiGiorno] Formato in input errato. Imput: " + dateToUpdate;
        }
        
        if ((Integer.parseInt(dateSplit[0]) + sum) > getNumberOfDayInMonth()){
            if (month==12){
                month = 1;
                year++;
            } else {
                month++;
            }
        }
        return parseValue(newDay) + " " + parseValue(month) + " " + year;
    }
    
    
    /*
    data la data corrente aggiunge 'dataToAdd' tenendo conto di mesi e anno
    */
    private String countTimeLaps(int dayToAdd) {    //aggiungere try catch
        String soloMese;
        //String []data=ultimoPasto.split(" ");
        boolean aumentaMese=false;
        int giorno = getDayValue();
        soloMese = getMonth_name();
        int anno = getYearValue();

        if (soloMese.equals("aprile") || soloMese.equals("giugno") || soloMese.equals("settembre") 
                || soloMese.equals("novembre")){
            if (giorno+15 > 30) aumentaMese = true;
            giorno = (giorno+15) % 30;
        } else if (soloMese.equals("febbraio")){
            if (anno % 4 == 0) { //se è vero sono in un anno bisestile
                if (giorno+15 > 29) aumentaMese = true;
                giorno = (giorno+15) % 29;
            } else { //altrimenti è un anno NON bisestile
                if (giorno+15 > 28) aumentaMese = true;
                giorno = (giorno+15) % 28;
            }
        } else { //se invece sono gli altri mesi da 31 giorni
            if (giorno+15 > 31) aumentaMese = true;
            giorno = (giorno+15) % 31;
        }

        if (aumentaMese) 
            soloMese = parseMonth_int2str(month_int + 1 == 12 ? 1 : month_int + 1);
        
        
        return giorno + " " + soloMese + " " + anno;
    }
    
/**
 * Data una data in ingresso nel formato "1 10 2018"
 * Dato un intero come offset
 * 
 * ritorna il giorno, mese, anno della data passata come primo argomento più l'offset
 * 
 * Es:              ("1 10 2018", 5)
 * ritorna:         6 ottobre 2018
 * 
 * 
 * 
    */    
    public String countTimeLaps(String data, int dayToAdd) {
        String month_s;
        int day, month, year;
        try {
            day = parseInt(data.split(" ")[0]);
            if (Date_s.isNumber(data.split(" ")[1]))
                month = parseInt(data.split(" ")[1]);
            else
                month = Date_s.parseMonth_str2int(data.split(" ")[1], Date_s.IT);
            year = parseInt(data.split(" ")[2]);
            month_s = parseMonth_int2str(month, Date_s.IT).toLowerCase();
        } catch (Exception e) {
            print("countTimeLaps input fail");
            print("     l'input è: " + data);
            return "countTimeLaps input fail";
        }

        if (month_s.equals("aprile") || month_s.equals("giugno") || month_s.equals("settembre") ||month_s.equals("novembre")) {
            if (day + dayToAdd > 30) {
                if (month == 12) {
                    month = 1;
                    year++;
                } else {
                    month++;
                }
                day-=30;
            }
            day = day + dayToAdd;
            
        } else if (month_s.equals("febbraio")) {
            if (year % 4 == 0) {    //anno bisestile
                if (day + dayToAdd > 29) {
                    if (month == 12) {
                        month = 1;
                        year++;
                    } else {
                        month++;
                    }
                    day-=29;
                }
                day = (day + dayToAdd);

            } else {
                if (day + dayToAdd > 28) {
                    if (month == 12) {
                        month = 1;
                        year++;
                    } else {
                        month++;
                    }
                    day-=28;
                }
                day = (day + dayToAdd);
            }
            
        } else {
            if (day + dayToAdd > 31) {
                if (month == 12) {
                    month=1;
                    year++;
                } else {
                    month++;
                }
                day-=31 ;
            }
            day = (day + dayToAdd);
        }
        
        return parseValue(day) + " " +  parseValue(month) + " " + year;
    }
    
/**
 * che fa? todo
 */
    public int sumDay_boh(String dateToUpdate, int sum) {
        int newDay;
        int month_int;
        int year;
        String dateSplit []=dateToUpdate.split(" ");
        try {
            newDay = (Integer.parseInt(dateSplit[0]) + sum) % getNumberOfDayInMonth();
            if (newDay==0) newDay = getNumberOfDayInMonth();
            month_int = Integer.parseInt(dateSplit[1]);
            year = Integer.parseInt(dateSplit[2]);
        } catch (NumberFormatException e){
            //print("[Data->aggiungiGiorno] Formato in input errato. Imput: " + dateToUpdate);
            //print("Errore " + e.getLocalizedMessage() + ".Errore 36");
            return -1;
        }
        
        if ((Integer.parseInt(dateSplit[0]) + sum) > getNumberOfDayInMonth()) {
            if (month_int==12){
                month_int = 1;
                year++;
            } else {
                month_int++;
            }
        }
        return newDay;
    }
    
    /**
     * @param day giorno di partenza
     * @return il giorno dopo
     * Se gli passi "lunedi" lui ritorna "martedi"
    */
    public static String nextDay(String day) {
        return nextDay(Date_s.EN, day, 1);
    }

    public static String nextDay(String day, int sumDay) {
        return nextDay(Date_s.EN, day, sumDay);
    }

    public static String nextDay(int language, String day) {
        return nextDay(language, day, 1);
    }
    
    /**
     * @param language lingua giorni
     * @param day giorno da incrementare (stringa)
     * @param sumDay giorni da sommare a 'day'
     *  nextDay(Date.EN, "lunedì", 3); -> ritorna "Giovedì"
     *  nextDay(Date.EN, "lunedì", 1); -> ritorna "Martedì"
     *  nextDay(Date.EN, "Domenica", 2); -> ritorna "Martedì"
     */
    public static String nextDay(int language, String day, int sumDay) {
        LinkedList <String> wordDay = new LinkedList();
        createDictionaryWordDay(wordDay, language);
        for (int i=0; i<wordDay.size(); i++) {
            if (wordDay.get(i).equalsIgnoreCase(day)) {
                return wordDay.get((i+sumDay) % wordDay.size());}
        }
        return "nextDay error input. given: " + day;
    }
    

/** TODO fare il contrario
 * Ritorna il numero intero del giorno associato Es.: mercoledi -> 5
 * Questo se quel mese il giorno 5 spunta di mercoledì 
 * Tiene conto dei mesi
 * @param day che si vuole convertire
 * @return Giorno convertito in int. Valori: [0, ..., 31]
*/  public static int getDayOfWeek_str2int(String day) {
        Date_s data = new Date_s();
        int actualDay               =   data.getDayValue();
        String actualDay_str        =   data.dayOfWeek_str;
        int numeroMassimoGiorniMese =   data.getNumberOfDayInMonth();     
        
        int maxIteration = 60;
        while (true){
            //print("Giorno attuale= " + actualDay_str + " a cui corrisponde il numero: " + giornoAttuale);
            //print("Giorno: " + giorno);
            if (actualDay_str.equalsIgnoreCase(day)) {
                return actualDay-1;
            }
           actualDay++;
           day = nextDay(day);
           if (actualDay > numeroMassimoGiorniMese)
               actualDay = 1;
           if (maxIteration == 0){ return -1; } else maxIteration--; //evitiamo loop infiniti
        }
    }

/**
 * @return (currentHour < 7  && currentHour > 22)
*/  public boolean isNight() {
        return currentHour < 7  && currentHour > 22;
    }
    
    public int getMinuteValue() {
        return currentMinute;
    }
    
    public int getWeekOfYear() {
        return weekOfYear;
    }
    
    public int getNextWeek() {
        return (weekOfYear + 1) % 53;
    }

    /**
     * @param month mese 
     * @return 
     * quanti giorni ha marzo? questa funzione risponde alla domanda
     * getNumberOfDayInMonth(3) ritorna 31
    */
    public static int getNumberOfDayInMonth(int month) {
        switch (month) {
            case 1:
            case 3:
            case 12:
            case 5:
            case 7:
            case 8:
            case 10: 
                return 31;
            case 9:
            case 6:
            case 11:
            case 4: 
                return 30;
            default:
                if (new Date_s().getYearValue() % 4 == 0) 
                    return 29;
                return 28;
        }
    }
    
/**
 * Ritorna il numero di giorni del mese corrente tenendo conto degli
 * anni bisestili
 * 
 * @return 28,29,30,31
*/  public int getNumberOfDayInMonth() {
        return getNumberOfDayInMonth(month_int);
    }

    public static boolean isNumber_double(String val) {
        try {
            Float.parseFloat(val);
            return true;
        } catch(NumberFormatException e) {
            return false;
        }
    }

    public static boolean isNumber(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch(NumberFormatException e) {
            return false;
        }
    }

/**
 * Splitta un qualuque formato data specificando la data e lo split
 * Se viene chiamata cosi: stampaDaNumeroAStringa("8*4*1995", "*")
 * L'output sarà "8 4 1995", perchè splitta secondo "*"
 * @param toPrint data da stampare passata come stringa purchè sia in formato numerico, altrimenti eccezione
 * @param split
 * @return Formato: 8 4 1995
*/  public static String splitDate(String toPrint, String split) {
        String arraySplit[];
        try {
            arraySplit = toPrint.split(split);
            int gg = Integer.parseInt(arraySplit[0]);
            String mm;
            if (!isNumber(arraySplit[1]))
                mm = parseMonth_int2str(Integer.parseInt(arraySplit[1]));
            else 
                mm = arraySplit[1];
            int aaaa= Integer.parseInt(arraySplit[2]);
            return gg + " " + mm + " " + aaaa;
        } catch(PatternSyntaxException | NumberFormatException e) {return "EXCEPTION Data->stampaDaNumeroAStringa"; }
    }
    
    private static void print(String s) {
        System.out.println(s);
    }

/**
 * @param data
 * @param split
 * @return
 * Input: "1/5/1995"
 * Output: "1 Maggio 1995"
 * @param language EN o IT
*/  public static String getDataInStandardFormat(String data, String split, int language) {
        String temp[] = Date_s.splitDataInArray(data, split);
        if (isNumber(temp[1]))
            return adjustValue(temp[0]) + " " + parseMonth_int2str(parseInt(temp[1]), language) + " " + adjustValue(temp[2]);
        else
            return adjustValue(temp[0]) + " " + temp[1] + " " + adjustValue(temp[2]);
    }

    public static String getDataInStandardFormat(String data, String split) {
        return getDataInStandardFormat(data, split, EN);
    }

/**
 * Splitta un qualuque formato data specificando la data e lo split E METTE IL RISULTATO IN UN ARRAY
 * Se viene chiamata cosi: stampaDaNumeroAStringa("8*4*1995", "*")
 * L'output sarà "8 4 1995" in un array di String
 * @param toPrint data da stampare passata come stringa purchè sia in formato numerico, altrimenti eccezione
 * @param split
 * @return Formato: 8 4 1995
*/  public static String[] splitDataInArray(String toPrint, String split) {
        String arraySplit[];
        try {
            arraySplit = toPrint.split(split);
            int gg = Integer.parseInt(arraySplit[0]);
            int mm = parseInt(arraySplit[1]);
            int aaaa= Integer.parseInt(arraySplit[2]);
            String []par={gg+"", mm+"", aaaa+""};
            return par;
        }catch(PatternSyntaxException | NumberFormatException e) {
            String []par={"-1", "-1", "-1"};
            System.out.println("Data()->splitDataInArray. input toPrint:" + toPrint + "\tsplit: \"" + split +"\"");
            return par;
        }
    }

/**
 * Splitta un qualuque formato data specificando la data e lo split E METTE IL RISULTATO IN UN ARRAY
 * NOTA BENE: passare un anno superiore di 5 rispetto l'anno corrente è considerato un errore!!!
 *                                  ^^^^^^^^^^^^^^^^^^^
 * Se viene chiamata cosi: isFormatDate("8*4*1995", "*")
 * Ritorna true, perchè i valori sono numeri corretti
 * Se viene chiamata cosi: isFormatDate("8*4*995", "*")
 * Se viene chiamata cosi: isFormatDate("8**995", "*")
 * Se viene chiamata cosi: isFormatDate("-8*4*995", "*")
 * Ritorna false perchè non sono formati validi
 * @param data orario da verificare
 * @param split controlla splittando
 * @return booelan 
*/  public static boolean isFormatDate(String data, String split) {
        String value[] = splitDataInArray(data, split);    //tutti i controlli sono fatti dalla "splitDataInArray"
        if (parseInt(value[0])<0 || parseInt(value[0])>31) return false; 
        if (parseInt(value[1])<0 || parseInt(value[1])>12) return false;
        if (Integer.parseInt(value[2]) < new Date_s().getYearValue()|| Integer.parseInt(value[2]) > new Date_s().getYearValue()+5) return false;
        return true;
}
    public static boolean isFormatHour(String ora, String split) {
        try{
            String orario [] = ora.split(split);
            int h = Integer.parseInt(orario[0]);
            int m = Integer.parseInt(orario[1]);
            if (h<0 || h>23) return false;
            if (m<0 || m>60) return false;
            return true;
        } catch (Exception e){
            return false;
        }
    }

    public int getNExtWeek(int stepWeek) {
        return (weekOfYear + stepWeek) % 53;
    }

/**
 *Formato: 17:52 Lunedì 8/4/1995
 *@return Stringa nel formato sopra
*/  @Override
    public String toString() {
        return getTimestamp()+ " " + wordDay.get(getDayOfWeek()) + " "+ getDayOfMonth() + "/" + getMonth()+ "/" + year;
    }
    
/**
     * @param orologioSecondo
     * @return 
 * @deprecated
     * @param orologioPrimo
 * Doesn't Work
*/  public boolean isIntervalloOrologio(String orologioPrimo, String orologioSecondo) {
        String tempPrimo[]=orologioPrimo.split(":");            //NON FUNZIONA
        int oraPrima = Integer.parseInt(tempPrimo[0]);              //Continua a non funzionare
        int minutoPrimo = Integer.parseInt(tempPrimo[1]);
        String tempSecondo[]=orologioSecondo.split(":");
        int oraSecondo = Integer.parseInt(tempSecondo[0]);
        int minutoSecondo = Integer.parseInt(tempSecondo[1]);
        
        if (oraSecondo >= currentHour && oraPrima <= currentHour){
            if (oraSecondo >= currentHour && currentMinute < minutoSecondo)
                return true;
            if (oraPrima <= currentHour && minutoPrimo > currentMinute)
                return true;
            if (oraPrima <= currentHour && minutoSecondo < currentMinute)
                return true;
        }
        return false;
    }

/**
 * Formato: 08 04 1995
 * @return data nel formato come sopra
*/  public String getData() {
        return parseValue(dayOfMonth_int) + " " + parseValue(month_int) + " " + year;
    }

/**
 * @param orologio , il formato deve essere del tipo h:m. Il metodo si occuperà di trasformarlo in hh:mm se serve
 * @return se gli si da in input 8:5 lui ritorna 08:05
*/  public static String adjustFormatClock(String orologio) {
        try{
            String []orario = orologio.split(":");
            int minutoConv = Integer.parseInt(orario[0]);
            int oraConv = Integer.parseInt(orario[1]);
            String ora;
            String minuto;
            if (minutoConv<10)
                minuto = "0"+minutoConv;
            else 
                minuto=minutoConv + "";
            if (oraConv < 10)
                ora = "0"+oraConv;
            else 
                ora=oraConv + "";
            return minuto + ":" + ora;
        } catch(Exception e){System.err.println("Errore dalla classe Data nella funzione getFormatClock. Input: " + orologio);}
        return "-1:-1";
    }


/**
 * Esempi formato: 07:01, 17:04, 08:54 
 * @return Orologio nel formato sopra
 */ public String getClock() {
     return  getHour() + ":" + getMinute();
//     return getFormatClock(currentMinute + ":" + currentHour);
    }
    
/**
 * Converte mese in formato numerico in stringa
 * @param month mese che si vuole convertire
 * @param language
 * @return nome del mese corrispondente
 *         switch(month){
            case 1: return "Gennaio";
            case 2: return "Febbraio";
            case 3: return "Marzo";
            case 4: return "Aprile";
            case 5: return "Maggio";
            case 6: return "Giugno";
            case 7: return "Luglio";
            case 8: return "Agosto";
            case 9: return "Settembre";
            case 10: return "Ottobre";
            case 11: return "Novembre";
            default: return "Dicembre";
        }
 */ public static String parseMonth_int2str(int month, int language) {
        LinkedList <String> wordMonth = new LinkedList();
        createDictionaryWordMonth(wordMonth, language);
        return wordMonth.get((month - 1) % wordMonth.size());
    }
    
    public static String parseMonth_int2str(int month) {
        return parseMonth_int2str(month, Date_s.EN);
    }

/**
 * Converte mese in formato numerico in stringa
 * @param month mese che si vuole convertire
 * @param language
 * @return nome del mese corrispondente
 *         switch(month.toLowerCase()) {
            case "gennaio": return 1;
            case "febbraio": return 2;
            case "marzo": return 3;
            case "aprile": return 4;
            case "maggio": return 5;
            case "giugno": return 6;
            case "luglio": return 7;
            case "agosto": return 8;
            case "settembre": return 9;
            case "ottobre": return 10;
            case "novembre": return 11;
            case "dicembre": return 12;
        }
 */ public static int parseMonth_str2int(String month, int language) {
        LinkedList <String> wordMonth = new LinkedList();
        createDictionaryWordMonth(wordMonth, language);
        for (int i=0; i<wordMonth.size(); i++) {
            if (wordMonth.get(i).equalsIgnoreCase(month))
                return i+1;
        }
        System.out.println("Errore Data->getMeseString2Int. Ritorna -1, input: " + month);
        return -1;
    }
 
    public static int parseMonth_str2int(String month) {
        return parseMonth_str2int(month, Date_s.EN);
    }
  
/**
 * Formato: 05 04 2018
 * @return data nel formato sopra
 */ public String getDate() {
        return getDayOfMonth() + " " + getMonth()+ " " + year;
    }

 /**
 * Formato: "05 Aprile 2018" o "05 April 2018" in base a che lingua è stata scelta al momento di istanziare questa classe
 * @return data nel formato sopra
 */ public String getDateStandardFormat() {
        return getDayOfMonth() + " " + getMonth_name()+ " " + year;
    }

 /**
 * Il formato dipende dal delimeter
 *              getDate("/")  -> 08/04/1995
 *              getDate(" ")  -> 08 04 1995
 * @return 
 * 
 */ public String getDate(String delimeter) {
        return getDayOfMonth()+ delimeter + parseValue(month_int) + delimeter +year;
    }
    
/**
 * Formato: Lunedì, Martedì, ecc
 * @return Giorno della settimana come stringa
 */ public String getDayOfWeek_str() {
        return dayOfWeek_str;
    }

/**
 * Formato: 5-12-2018_12:30
 * @return Stringa nel formato sopra
*/
    public String getData_gg_mm_aaaa_hh_mm_ss() {
        return dayOfMonth_int + "-" + month_int + "-" + year + "_" + currentHour + "-" + currentMinute + "-" + second;
    }

/**
 * Formato: 5_12_2018 ovvero gg_mm_aaaa
 * @return Stringa nel formato sopra
*/
    public String getData_gg_mm_aaaa() {
        return dayOfMonth_int + "_" + month_int + "_" + year;
    }
}
