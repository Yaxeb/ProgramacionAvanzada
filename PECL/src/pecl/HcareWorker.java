package pecl;

import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HcareWorker extends Thread{
    private int hid;
    private int pVaccinated;
    private int iDDeskVacc;
    private int iDDeskObs;
    private int timeToVaccine;
    private int timeToRest;
    private int counter;
    private final int maximum;
    private boolean beenAwaken;
    private boolean working;
    private Lock lock;
    private Condition noWorkToDo;
    private Hospital hospital;
    
    /**
     * Constructor of the HcareWorker
     * @param id
     * @param pVaccinated
     * @param hospital
     */
    public HcareWorker(int id, int pVaccinated, Hospital hospital) {
        this.hid = id;
        this.pVaccinated = pVaccinated;
        this.hospital = hospital;
        this.beenAwaken = false;
        this.maximum = 15;
        this.lock = new ReentrantLock();
        this.noWorkToDo = lock.newCondition();
        this.working = false;
    }
    
    @Override
    public void run(){
        ArrayList<Desk> desksVaccRoom;
        ArrayList<Desk> desksObsRoom;
        int timeWithComplications;
        if (beenAwaken)
        {
            // ve a la obs room, que te han despertado para eso. 
            // hace la sincronizacion con el/los usuario/s, y se va a dormir de nuevo. 
            lock.lock();
            desksObsRoom = hospital.getObsRoom().getDesks();
            iDDeskObs = hospital.getObsRoom().checkComplications().get(0);
            desksObsRoom.get(iDDeskObs).setWorker(hid);
            int idPatient = desksObsRoom.get(iDDeskObs).getPatient();
            timeWithComplications = 2000 + (int) Math.random() * 3001;
            hospital.getPatient(idPatient).setTimeWithComplications(timeWithComplications);
            try {
                sleep(timeWithComplications);
            }
            catch (InterruptedException ex) 
            {
                System.out.println("Interrupted while helping the patient with complications #1");
            }
            finally
            {
                lock.unlock();
            }
            
        }
        else 
        {
            try 
            {
                //starting the schedule. 
                sleep(1000 + (int) (Math.random() * 2001));
            } 
            // impossible to happen since HcareWorkers are created first
            catch (InterruptedException ex) {} 
        }
        
        try {
             lock.lock();
             desksVaccRoom = hospital.getVaccRoom().getDesks();
             
             // sitting in a post. 
             while (iDDeskVacc == -1)
             {
                 for (int i = 0 ; i < desksVaccRoom.size() ; i++) 
                 {
                     Desk desk = desksVaccRoom.get(i);
                     if (desk.getWorker() != -1)
                     {
                          if (Math.random() > 0.5)
                          {
                              desk.setWorker(hid);
                              desksVaccRoom.set(i, desk);
                              iDDeskVacc = i;
                          }
                     }
                 }
             }
            
             while (desksVaccRoom.get(iDDeskVacc).getPatient() == -1) 
             {
                 working = false;
                 noWorkToDo.await();
             }

             // Worker has work to do (vaccinate patient). 
             working = true;
             timeToVaccine = 3000 + (int) Math.random() * 2000;
             int pid = desksVaccRoom.get(iDDeskVacc).getPatient();
             vaccinatePatient(hospital.getPatient(pid), this, timeToVaccine);
             counter++;
             
             if (counter % maximum == 0)
             {
                 try 
                 {
                      hospital.getRestRoom().add(this);
                      timeToRest = 5000 + (int) Math.random() * 3000;
                      sleep(timeToRest); 
                 }
                 catch (InterruptedException ex) 
                 {
                     // awaken while taking a break.
                     beenAwaken = true;
                     this.start();
                     System.out.println("Awaken while having a break...");
                 }
             }
             
        }
        catch (InterruptedException ex) 
        {
             // interrupted exception in case that it got 
            System.out.println("Interrupted while on condition of not working. ");
        }
        finally
        {
            lock.unlock();
        }
             // checking if any patient is requesting help due to complications
             while (!hospital.getObsRoom().checkComplications().isEmpty()) {
                 lock.lock();
                 desksObsRoom = hospital.getObsRoom().getDesks();
                 desksVaccRoom = hospital.getVaccRoom().getDesks();
                 iDDeskObs = hospital.getObsRoom().checkComplications().get(0);
                 iDDeskVacc = -1;
                 desksVaccRoom.get(iDDeskVacc).setWorker(-1);
                 desksObsRoom.get(iDDeskObs).setWorker(hid);
                 hospital.getObsRoom().checkComplications().remove(0);
                 
                 int idPatient = desksObsRoom.get(iDDeskObs).getPatient();
                 timeWithComplications = 2000 + (int) Math.random() * 3001;
                 hospital.getPatient(idPatient).setTimeWithComplications(timeWithComplications);
                 lock.unlock();
                 
                 try 
                 {
                     // treating the patient.
                     sleep(timeWithComplications);
                 }
                 catch (InterruptedException ex) 
                 {
                     System.out.println("Interrupted while helping the patient with complications #2");
                     Logger.getLogger(HcareWorker.class.getName()).log(Level.SEVERE, null, ex);
                 }
             }
        }
    
    /**
     * Method which signalls if the HcareWorker has work to do.
     * 
     */
    public synchronized void signalNoWorkToDo(){
        noWorkToDo.signal(); // puede dar illegal monitor exception, ver si sucede o no.
    }
    
    
    
    public void vaccinatePatient(Patient patient, HcareWorker hcWorker, int time){
        patient.setTimeToVaccine(time);
        hcWorker.setTimeToVaccine(time);
    }
    
    public boolean isWorking(){
        return this.isWorking();
    }
    
    
    public void setTimeToVaccine(int time){
        this.timeToVaccine = time;
    }
    
    public void takeBreak(){
        
    }
    
    public int getHId() {
        return hid;
    }

    public int getpVaccinated() {
        return pVaccinated;
    }

    public void setpVaccinated(int pVaccinated) {
        this.pVaccinated = pVaccinated;
    }  

}