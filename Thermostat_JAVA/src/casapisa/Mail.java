package casapisa;
import static casapisa.Thermostat.*;
import static casapisa.Date_s.*;
import static casapisa.GPIO.readGpio;
import static casapisa.MyFunction.*;
import java.io.IOException;
import java.util.Properties;
import javax.mail.*;
import com.sun.mail.smtp.SMTPTransport;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.security.Security;
import java.util.Date;  
import java.util.LinkedList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import jdk.nashorn.internal.objects.NativeString;
import jdk.nashorn.internal.parser.TokenKind;


/**
 *
 * @author stefano
 */
public class Mail {
    private String d1, d2;
    private Properties props;
    static Mail handleMail;
    static LinkedList<User> mailsAccepted;
    private boolean mutexStart;
    private static final int ADMIN = 0;
    
    public Mail(String d1, String d2) {
        this.d1 = d1;
        this.d2 = d2;
        int port = 465;
        mailsAccepted = new LinkedList<>();
        if (existFile(directory + "/user.txt")) {
            String users[] = readFile(directory + "/user.txt");
            for (String u:users) {
                String row[] = u.split(" ");
                mailsAccepted.add(new User(row[0], row[1], row[2]));
            }
        }
        
        if (mailsAccepted.size() == 0) {
            print("#################################");
            print("ALERT: No one user was stored.");
            print("#################################");
        }
        
        print("\nUsers: " + mailsAccepted.size());
        for (User u:mailsAccepted){
            print("\t* " + u.getName() + " has mail: " + u.getMail());
        }
        print();
        
        for (int i=0; i<60; i++) {
            if (checkInternet()) break;
            sleep_s(1);
        }
        props = new Properties();
        props.put("mail.smtps.auth", "true");
        props.put("mail.smtps.user", d1+"@gmail.com");
        props.put("mail.smtps.host", "smtp.gmail.com");
        props.put("mail.smtps.port", port);
        props.put("mail.smtps.starttls.enable", "true");
        props.put("mail.smtps.socketFactory.port", port);
        props.put("mail.smtps.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtps.socketFactory.fallback", "false");
        print("Mail service instanced");
        if (mutexStart) {
            mutexStart = false;
            SendMail(mailsAccepted.get(ADMIN).getMail(),"Termostato", "Il termostato di via bonanno pisano 20 è stato acceso");
        }
    }

    public static String getSenderMail(String dump) {
        try {
            return dump.substring(dump.indexOf('<') + 1, dump.indexOf('>'));
        } catch (Exception e) {
            return dump;
        }
    }

    public void managementCommandFromMail(String sender, String object, String text) {
        Date_s data = new Date_s();
        object = object.toLowerCase();
        //print("ricevo il comando: " + object + " mandato da " + sender);
        writeFile(true, "/home/pi/allMail.txt",
                        "{\ttimestamp: " + data.getTimestamp()+ "\n" +
                        "mittente [" + sender + "]\n" + 
                        "oggetto [" + object + "]\n" +
                        "testo:     " + text + "\n" +
                        "}\n"                                
        );
        
        
        if (object.contains("accend")) {
            if (systemIsOff) {
                print("turn on radiators via mail, but system is off");
                SendMail(sender, "Errore", "Impossibile accendere il riscaldamento, il termostato è spento. \n"
                        + "Per accenderlo mandami una mail con oggetto \"start\" e senza testo. Dopo potrai inviare una nuova mail per richiedere l'accensione del riscaldamento");                
            } else {
                print("turn on radiators via mail");
                turnOnHeating();
                SendMail(sender, "Conferma accensione mail", "I termosifoni sono stati accesi alle ore: " + data.getClock());
            }
        } else if (object.contains("spegn") || object.contains("speng")) {
            if (systemIsOff) {
                print("turn off radiators via mail, but system is off");
                SendMail(sender, "Errore", "Impossibile spegnere il riscaldamento, il termostato è spento."
                        + "\nPer accenderlo mandami una mail con oggetto \"start\" e senza testo. Dopo potrai inviare una nuova mail per richiedere lo spegnimento del riscaldamento"
                        + "\n[debug] Is " + (readGpio(gpioRelay) == 0) + " that heating is on");
            } else {
                print("turn off radiators via mail");
                turnOffHeating();
                SendMail(sender, "Conferma spegnimento mail", "I termosifoni sono stati spenti alle ore: " + data.getClock());
            }
        } else if (object.contains("cancella") || object.contains("annulla")) {
            if (manualProgram.clearsTodaysPrograms())
                SendMail(sender, "Conferma cancellazione programmazione", "Ti confermo che le programmazioni relative ad oggi (e solo ad oggi) sono state eliminate");
            else
                SendMail(sender, "Nessuna programmazione da cancellare", "Oggi non c'erano programmazioni da cancellare, quindi non ho cancellato niente");
        } else if (object.contains("programma")) {
            if (systemIsOff) {
                SendMail(sender, "Errore", "Impossibile programmare il riscaldamento, il termostato è spento. \n"
                        + "Per accenderlo mandami una mail con oggetto \"start\" e senza testo. Dopo potrai inviare una nuova mail per richiedere una nuova programmazione del riscaldamento");                
            } else {
                print("Program via mail");
                if (program(sender, text))
                    SendMail(sender, "Conferma programmazione", "La programmazione richiesta è stata elaborata correttamente");
            }
        } else if (object.contains("stat")) {
            print("request status recived");
            if (readGpio(gpioRelay) == 0)
                SendMail(sender, "Stato riscaldamento", "Il riscaldamento risulta sicuramente acceso via software");
            else
                SendMail(sender, "Stato riscaldamento", "Il riscaldamento risulta sicuramente spento via software");                
        } else if (object.contains("start")) {
            writeFile(directory + "/systemOn.txt", "1");
        } else if (object.contains("aiuto")) {
            SendMail(sender, "Guida termostato", 
                    "I comandi via mail sono i seguenti: \n" 
                            + "accendi/spegni         per accendere/spegnere il riscaldamento immediatamente SE il termostato è acceso\n"
                            + "start        accende il termostato se è stato spento\n"
                            + "stato        dice se in quel momento i termosifoni sono accesi o spenti\n"
                            + "programma    permette di programmare il riscaldamento in una fascia oraria a scelta. Per farlo bisogna rispettare il formato comando seguente\n"
                            + "             oggetto della mail: programmazione\n"
                            + "             testo della mail, primo rigo: ora inizio (es: 08:10)\n"
                            + "             testo della mail, secondo rigo: ora fine (es:09:05)\n"
                            + "cancella     questo comando permette di cancellare le programmazioni relative alla giornata corrente (se è oggi domenica e tu usi questo comando verranno cancellate tutte le programmazioni di domenica)\n"
                            + "\n"
                            + "NOTA BENE: dopo ad ognuno di questi comandi viene inviata una mail di risposta entro pochi(ssimi) minuti, se così non fosse c'è uno dei seguenti problemi:\n"
                            + "1) Non c'è internet a casa (controllare connessione internet a casa)\n"
                            + "2) Sistema in manutenzione (chiedere conferma a Stefano)\n"
                            + "3) C'è stato un errore (chiedera a Stefano)" + 
                    "");

        } else if (isAdminCommand(sender, object, text)) {
            return;
        } else {
            print("Comando via mail ricevuto: " + object);
            SendMail(sender, "Comando sconosciuto", "Comandi noti:\naccendi\nspegni\nprogramma\nstato\nstart. Hai inviato: " + object
                    +"Mandami una mail con oggetto \"aiuto\" per vare info più dettagliate");
        }
    }
    
    public boolean isAdminCommand(String sender, String object, String text) {
        if (sender.equals(mailsAccepted.get(ADMIN).getMail())) {
            if (object.contains("ip")) {
                SendMail(sender, "Richiesta ip pisa via bonanno pisano 20", getIp());
            } else if (object.contains("")) {
                
            }
        } else return false;
        return true;
    }
    
    public boolean program(String sender, String text) {
        String hstart="";
        String hend="";
        try {
            String allWord[] = text.split("\n");
            hstart = allWord[0].trim();
            hend = allWord[1].trim();
        } catch (Exception e) {
            printErr("Command not known [" + text + "]");
            SendMail(sender, "Comando errato", "Hai richiesto una programmazione, la sintassi è come segue: \n" + 
                    "Oggetto: \"Programmazione\"\n" + 
                    "Testo, primo rigo: 08:00\n" +
                    "     secondo rigo: 21:10\n" +
                    "Non aggiungere nessun altro carattere"
            );
            return false;
        }
        Date_s data = new Date_s();
        //print("Start: " + hstart);
        //print("End: " + hend);
//verrà nalizzato:   row = hstart + " " + hend + " " + dayWeek + " 1";
        if (!isFormatHour(hstart, ":")) {
            SendMail(sender, "Errore formato", "La data inserita nel primo rigo è errata.\nIl formato deve essere ad esempio: 08:10.\nTu hai scritto: " + hstart);
            return false;
        }
        
        if (!isFormatHour(hend, ":")) {
            SendMail(sender, "Errore formato", "La data inserita nel secondo rigo è errata.\nIl formato deve essere ad esempio: 08:10.\nTu hai scritto: " + hend);
            return false;
        }
        
        if (!writeFile(true, directory + "/program.txt", adjustFormatClock(hstart) + " " + adjustFormatClock(hend) + " " + data.getDayOfWeek() + " 1\n")) {
            SendMail(sender, "Errore interno", 
                    "Colpa mia (ovvero stefano), non è stata fatta nessuna programmazione dei termosifoni. Per il momento accendete il riscaldamento con il comando \"accendi\". "
                            + "Mi è appena arrivata una mail (a me stefano eh :D) con i dettagli riguardo l'errore, ricordatemi di sistemare la cosa :D :D");
            SendMail(mailsAccepted.get(ADMIN).getMail(), "Errore validazione orario", "hstart: " + hstart + "\nhend:" + hend + 
                    "\nÈ fallita la scrittura del file al path: " + directory + "/program.txt\nProgrammazione tentata da: " + sender);
            return false;
        }
        return true;
    }
    
    public void turnOnHeating() {
        GPIO.turnOnHeating();
    }
    
    public void turnOffHeating() {
        GPIO.turnOffHeating();
        manualProgram.stopProgram(new Date_s().getDayOfWeek());
    }
    
    
    public void managementMail(){
        try {
            Message m = ReadMail();                 //questo metodo rilancia in eccezione NUll se non ci sono nuove mail
            debugMail(m, false);    //Per avere stampa della mail ricevuta abilitare questa funzione
            if (m == null) return;
            //Siamo qui se è arrivata una mail
            String mittente = getSenderMail(m.getFrom()[0].toString());
            String oggetto = (m.getSubject() + "").trim();
            String testo = (getTextFromMessage(m) + "").trim();
            managementCommandFromMail(mittente, oggetto, testo);
        } catch (MessagingException ex) {
            printErr("[Eccezione]: MessagingException");
        } catch (Exception ex) {
            printErr("[Eccezione]: IOException");
        }
    }

    public static void debugMail(Message m, boolean on){
        if (!on) return;
        try {
            String mittente = m.getFrom()[0].toString();
            String oggetto = m.getSubject();
            String testo = getTextFromMessage(m);
            print("mail arrivata alle ore " + new Date_s().getClock()+ " " + new Date_s().getSecond());
            print("Oggetto: " + oggetto);
            print("Mittente: "+mittente);
            print("Testo: ");
            print(testo);
        } catch (MessagingException ex) {
            printErr("Eccezione debuugMail");
        } catch (IOException ex) {
        } catch (NullPointerException e){ }
    }
    private static String getTextFromMessage(Message message) throws MessagingException, IOException {
        String result = "";
        if (message.isMimeType("text/plain")) {
            result = message.getContent().toString();
        } else if (message.isMimeType("multipart/*")) {
            MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
            result = getTextFromMimeMultipart(mimeMultipart);
        }
        return result;
    }
    
    private static String getTextFromMimeMultipart(MimeMultipart mimeMultipart)  throws MessagingException, IOException{
        String result = "";
        int count = mimeMultipart.getCount();
        for (int i = 0; i < count; i++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            if (bodyPart.isMimeType("text/plain")) {
                result = result + "\n" + bodyPart.getContent();
                break;
            } else if (bodyPart.isMimeType("text/html")) {
                String html = (String) bodyPart.getContent();
                result = result + "\n" ;//+ org.jsoup.Jsoup.parse(html).text(); because i don't have the library
            } else if (bodyPart.getContent() instanceof MimeMultipart){
                result = result + getTextFromMimeMultipart((MimeMultipart)bodyPart.getContent());
            }
        }
        return result;
    }
    
    public Message ReadMail() {
        try {
          Session session = Session.getInstance(props, null); 
          Store store = session.getStore("imaps");
          store.connect("smtp.gmail.com", 
                        d1 + "@gmail.com", 
                        d2);

          Folder cartella = store.getFolder("inbox");
          cartella.open(Folder.READ_WRITE);

          //recupero dei messaggi
          Message[] messaggi = cartella.getMessages(); 

          InternetAddress indirizzo = null;
          for(int i=0; i<messaggi.length; i++) {
            indirizzo = (InternetAddress) messaggi[i].getFrom()[0];
            String userMail = indirizzo.getAddress();
            if(!messaggi[i].isSet(Flags.Flag.SEEN)) {
                messaggi[i].setFlag(Flags.Flag.SEEN, true);
                messaggi[i].setFlag(Flags.Flag.DELETED, true);
                for (User user:mailsAccepted) {
                    if (userMail.equals(user.getMail())) {
                        return messaggi[i];
                    }
                }
            }
          }
        } catch(Exception e){
//          System.out.println("Eccezione ReadMail");
        }
        return null;
    }

    public void SendMail(String recipientEmail, String title, String message){
        try {
            Mail.SendMail(d1, d2, recipientEmail, "", title, message);
        } catch (Exception e){
            printErr("La mail [" + title + "] con destinatario [" + recipientEmail + "] non è stata inviata a causa di una eccezione");
            writeFile(true, "/home/pi/log_sendmail.txt", "Eccezione mail: " + new Date_s().getData_gg_mm_aaaa_hh_mm_ss() + " L'eccezione è: \n" + e.getMessage() + 
                    ". Data: " + new Date_s().getDate() + " " + new Date_s().getClock()+ " \n");
        }
    }    
    

    public static void SendMail(final String username, final String password, String recipientEmail, String ccEmail, String title, String message) throws AddressException, MessagingException, Exception {
        Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
        final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";

        // Get a Properties object
        Properties props = System.getProperties();
        props.setProperty("mail.smtps.host", "smtp.gmail.com");
        props.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
        props.setProperty("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.port", "465");
        props.setProperty("mail.smtp.socketFactory.port", "465");
        props.setProperty("mail.smtps.auth", "true");

        /*
        If set to false, the QUIT command is sent and the connection is immediately closed. If set 
        to true (the default), causes the transport to wait for the response to the QUIT command.

        ref :   http://java.sun.com/products/javamail/javadocs/com/sun/mail/smtp/package-summary.html
                http://forum.java.sun.com/thread.jspa?threadID=5205249
                smtpsend.java - demo program from javamail
        */
        props.put("mail.smtps.quitwait", "false");

        Session session = Session.getInstance(props, null);

        // -- Create a new message --
        final MimeMessage msg = new MimeMessage(session);

        // -- Set the FROM and TO fields --
        msg.setFrom(new InternetAddress(username + "@gmail.com"));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail, false));

        if (ccEmail.length() > 0) {
            msg.setRecipients(Message.RecipientType.CC, InternetAddress.parse(ccEmail, false));
        }

        msg.setSubject(title);
        msg.setText(message, "utf-8");
        msg.setSentDate(new Date());

        SMTPTransport t = (SMTPTransport)session.getTransport("smtps");

        t.connect("smtp.gmail.com", username, password);
        t.sendMessage(msg, msg.getAllRecipients());      
        t.close();
    }
}

class User {
    private String name, surname, mail, phone;

    public User(String name, String surname, String mail) {
        this.name = name;
        this.surname = surname;
        this.mail = mail;
    }
    
    public User(String name, String surname, String mail, String phone) {
        this.name = name;
        this.surname = surname;
        this.mail = mail;
        this.phone = phone;        
    }
    
    @Override
    public String toString() {
        return name + " " + surname + ". Mail: " + mail;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }
    
    public String getPhone() {
        return this.name + " -> " + this.phone;       
    }
}
