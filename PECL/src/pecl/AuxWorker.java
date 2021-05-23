package pecl;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;


public class AuxWorker extends Thread {

    
    private int aid;
    private int maximum; // maximum means how many items/patients
    // it must attend before taking a break
    private int counter; // current counter of items/patients attended
    private Hospital hospital;
    private boolean isResting;
    private Semaphore semCounter = new Semaphore(1);

    public AuxWorker(int aid, int maximum, Hospital hospital) {
        this.aid = aid;
        this.maximum = maximum;
        this.hospital = hospital;
        this.counter = 0;
        this.isResting = false;
    }

    @Override
    public void run() {
        // reception assistant
        Patient patient;
        ArrayList<Patient> enteringQueue;
        ArrayList<Patient> waitingQueue;
        if (aid == 1) 
        {
             while (counter != 2000) { //total number of patients generated = 2000   
                 
//                 enteringQueue = hospital.getReception().getEnteringQueue();
/*                 waitingQueue = hospital.getReception().getWaitingQueue();
                 while (!waitingQueue.isEmpty()) 
                 {
                     patient = waitingQueue.get(0);
                     if (patient.hasAppointment()) 
                     {
                          counter++;
//                          waitingQueue.remove(0); // no se si aqui o está hecho en otro lado
//                          enteringQueue.add(patient);
                     }
                     // check if there is a desk so the user will go.
*/
                     if (counter >= maximum)
                     {
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
                              resetCounter();
                              isResting = false;
                          }  
                     }
                 }                
            //}
        } 
        else 
        { // vaccination assistant
            
            while (counter != 2000) 
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
                if (counter >= maximum)
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
                        resetCounter();
                        isResting = false;
                    }
                }
            }
            
            
            /* crea vacunas con un intervalo de 0.5-1 segundos
               cuando llega a 20 vacunas se toma un descanso de 1-4 segundos
               vuelve al trabajo
             */
        }
    }
    
    public synchronized int availableDesk(Patient patient){
       /* int nrDesk = hospital.getVaccRoom().getAvailableDesk();
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
        return nrDesk;*/
        
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
            //If there is any available desk
            //auxworker tells patient the desk id
            int vacDesk = hospital.getVaccRoom().getAvailableDesk();
            hospital.getReception().exitEnteringQueue(patient);// the patient leaves the reception room
            return vacDesk;                                    // the id of its desk is returned
        }
        else
        {
            
            
            hospital.getReception().exitWaitingQueue(patient); //the patient didn't have an appointment
            hospital.removePatient(patient);              // so it leaves the hospital
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
    
    public void resetCounter(){
        try
        {
            semCounter.acquire();
            counter=0;
        }catch(Exception e){}
        finally{
            semCounter.release();
        }
    }
    
    public void addToCounter(){
        try
        {
            semCounter.acquire();
            counter++;
        }catch(Exception e){}
        finally{
            semCounter.release();
        }
    }
}
