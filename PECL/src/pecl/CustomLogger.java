package pecl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.locks.*;
public class CustomLogger {
    private Lock locj = new ReentrantLock();
    private String path = "./Logs/EvolutionHospital.txt";
    private BufferedWriter bw;
    
    //File path = new File("./Logs/EvolutionHospital.txt");
    
//    public synchronized void log(String message) {
//        try {
//            FileWriter myWriter = new FileWriter(path);
//            myWriter.write(message);
//            myWriter.close();
//        } catch (IOException e) {
//            System.out.println("An error occurred while logging.");
//            e.printStackTrace();
//        }
//    }

    public synchronized void log(String message) {
        try {
            this.bw = new BufferedWriter(new FileWriter(path));
        }catch(Exception e){
            System.out.println("Error");
        }
    }
    
    public void write(String line, String room)
    {
        try
        {
            String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("[MM-dd-yyyy][HH:mm:ss]"));
            bw.write(time + " | " + room + " --- " + line);
            bw.newLine();
            bw.flush();
        }catch(Exception e){
            System.out.println("Error");
        }
    }
}
