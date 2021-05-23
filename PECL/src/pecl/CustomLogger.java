package pecl;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CustomLogger {
    private String path = "./Logs/EvolutionHospital.txt";
    private BufferedWriter bw;
    
    public CustomLogger() {
        try {
            this.bw = new BufferedWriter(new FileWriter(path));
        }catch(Exception e){
            System.out.println("Error");
        }
    }
    
    public synchronized void  write(String line, String room)
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
