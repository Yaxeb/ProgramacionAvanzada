package pecl;

import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;


public class AuxWorker extends Thread {

    private int aid;
    private int counter; 
    private int totalCount;
    private Hospital hospital;
    private boolean isResting;
    private Semaphore semCounter = new Semaphore(1);

    public AuxWorker(int aid, int maximum, Hospital hospital) {
        this.aid = aid;
        this.hospital = hospital;
        this.counter = 0;
        this.totalCount = 0; 
        this.isResting = false;
    }

    @Override
    public void run() {
        // reception assistant
        if (aid == 1) 
        {
             while (totalCount != 2000) 
             {  
                if (counter == 15)
                {  
                     System.out.println("entering queue size: " + hospital.getReception().getEnteringQueue().size());
                     System.out.println("Auxiliary A1 begins his rest");
                     hospital.getClogger().write("Auxiliary A1 begins his rest", "Reception");
                     isResting = true;
                     try 
                     {
                         sleep(3000 + (int) (Math.random() * 2001));
                     } 
                     catch (InterruptedException ex) 
                     {
                         Logger.getLogger(AuxWorker.class.getName()).log(Level.SEVERE, null, ex);
                     }
                     finally 
                     {
                         System.out.println("Auxiliary A1 ends his rest");
                         hospital.getClogger().write("Auxiliary A1 ends his rest", "Reception");
                         isResting = false;
                         resetCounter();
                    }
                }
                hospital.getReception().getNextPatient();
             }
        }
        else 
        {
            while (totalCount != 2000) 
            {
                try 
                {
                    sleep(500 + (int) (Math.random() * 501));
                    hospital.getVaccRoom().createVaccine();
                    addToCounter();
                } 
                catch (InterruptedException ex) 
                {
                    Logger.getLogger(AuxWorker.class.getName()).log(Level.SEVERE, null, ex);
                }
                if (counter == 20)
                {
                    System.out.println("Auxiliary A2 begins his rest");
                    hospital.getClogger().write("Auxiliary A2 begins his rest", "Vaccination Room");
                    isResting = true;
                    try 
                    {
                        sleep(1000 + (int) (Math.random() * 4001));
                    } 
                    catch (InterruptedException ex) 
                    {
                        Logger.getLogger(AuxWorker.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    finally
                    {
                        System.out.println("Auxiliary A2 ends his rest");
                        hospital.getClogger().write("Auxiliary A2 ends his rest", "Vaccination Room");
                        isResting = false;
                        resetCounter();
                    }
                }
            }
        }
    }
    
    public synchronized int availableDesk(Patient patient){       
        if (patient.hasAppointment()){
            hospital.getReception().exitWaitingQueue(patient);
            hospital.getReception().getAuxWorker().addToCounter();
            int timeToSleep = 500 + (int) (Math.random() * 500);
            patient.setTimeToGetDesk(timeToSleep);
            try 
            {   // checking the desk
                AuxWorker.sleep(timeToSleep);
            }
            catch (InterruptedException ex) 
            {
            Logger.getLogger(AuxWorker.class.getName()).log(Level.SEVERE, null, ex);
            }
            hospital.getReception().enterEnteringQueue(patient);
            int vacDesk = hospital.getVaccRoom().getAvailableDesk();
            hospital.getReception().exitEnteringQueue(patient);
            return vacDesk;
        }
        else
        {
            hospital.getReception().exitWaitingQueue(patient); //the patient didn't have an appointment
            hospital.removePatient(patient);                   // so it leaves the hospital
            return 0;                           
        }
    }
    
    public int getAid() {
        return aid;
    }

    public int checkArrivingPatient() {

        return 0;
    }
    
    public boolean isResting(){
        return this.isResting;
    }
    
    public void addToCounter(){
        try
        {
            semCounter.acquire();
            counter++;
            totalCount++;
        }catch(Exception e){}
        finally{
            semCounter.release();
        }
    }
    
    public void resetCounter(){
        try
        {
            semCounter.acquire();
            counter = 0; 
        }catch(Exception e){}
        finally{
            semCounter.release();
        }
    }
}
