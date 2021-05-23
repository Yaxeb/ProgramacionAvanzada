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
                     if (counter == maximum)
                     {
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
                              counter = 0;
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
                } 
                catch (InterruptedException ex) 
                {
                    Logger.getLogger(AuxWorker.class.getName()).log(Level.SEVERE, null, ex);
                }
                if (counter % maximum == 0)
                {
                    try 
                    {
                        sleep(1000 + (int) (Math.random() * 4001));
                    } 
                    catch (InterruptedException ex) 
                    {
                        Logger.getLogger(AuxWorker.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            
            
            /* crea vacunas con un intervalo de 0.5-1 segundos
               cuando llega a 20 vacunas se toma un descanso de 1-4 segundos
               vuelve al trabajo
             */
        }
    }
    
    public synchronized int availableDesk(){
        ArrayList<Patient> enteringQueue = hospital.getReception().getEnteringQueue();
        int nrDesk = hospital.getVaccRoom().getAvailableDesk();
        int timeToSleep = 500 + (int) (Math.random() * 500);
        enteringQueue.get(0).setTimeToGetDesk(aid);
        
        try 
        {   // checking the desk
            AuxWorker.sleep(timeToSleep);
        }
        catch (InterruptedException ex) 
        {
            Logger.getLogger(AuxWorker.class.getName()).log(Level.SEVERE, null, ex);
        }
        return nrDesk;
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
        }catch(Exception e){}
        finally{
            semCounter.release();
        }
    }
}
