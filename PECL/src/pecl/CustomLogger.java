package pecl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;

public class CustomLogger {
    private String path = "./Logs/EvolutionHospital.txt";
    public CustomLogger(){
            
    }
        
    public void log(String message) 
    {
        try 
        {
            File fPath = new File("./Logs");
            if (!fPath.exists()){
                fPath.mkdir();
            }
            FileWriter fw = new FileWriter(path);
            try (PrintWriter write = new PrintWriter(new BufferedWriter(fw))) 
            {
            write.println(message);
            }
        }
        catch (IOException ex) 
        {
            java.util.logging.Logger.getLogger(CustomLogger.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
