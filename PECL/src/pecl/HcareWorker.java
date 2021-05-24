package pecl;

import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HcareWorker extends Thread{
    private int hid;
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
    private boolean isVaccinating;
   // private CustomLogger clogger;
    
    /**
     * Constructor of the HcareWorker
     * @param id
     * @param pVaccinated
     * @param hospital
     */
    public HcareWorker(int id, Hospital hospital) {
        this.hid = id;
        this.hospital = hospital;
        this.beenAwaken = false;
        this.maximum = 2;
        this.lock = new ReentrantLock();
        this.noWorkToDo = lock.newCondition();
        this.working = false;
        this.iDDeskVacc = -1;
        this.isVaccinating = false;
       // this.clogger = hospital.getLogger();
    }
    
    @Override
    public void run(){
        //System.out.println("haha xd");
        ArrayList<Desk> desksVaccRoom;
        ArrayList<Desk> desksObsRoom;
        int timeWithComplications;
        if (beenAwaken)
        {
            lock.lock();
            desksObsRoom = hospital.getObsRoom().getDesks();
            iDDeskObs = hospital.getObsRoom().checkComplications(hospital.getPatients()).get(0);
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
                //hospital.getLogger().log("Healthcare Worker " + hid + " started his schedule. ");
                sleep(1000 + (int) (Math.random() * 2001));
            } 
            // impossible to happen since HcareWorkers are created first
            catch (InterruptedException ex) {} 
        }
        
        try {
             desksVaccRoom = hospital.getVaccRoom().getDesks(); 
             
             int i = 1;
             while (iDDeskVacc == -1)
             {
                if (i >= desksVaccRoom.size() + 1) 
                {
                    i = 1; 
                }
              
                Desk desk = desksVaccRoom.get(i-1);
                if (desk.getWorker() == -1)
                {
                    desk.setWorker(hid);
                    desksVaccRoom.set(i-1, desk);
                    iDDeskVacc = i;
                    hospital.getVaccRoom().setDesks(desksVaccRoom);
                }
                i++;
             }
        }
        catch(Exception e){}
        finally
        {
            //lock.unlock();
        }
        try
        {
             lock.lock();
             desksVaccRoom = hospital.getVaccRoom().getDesks();
             while (desksVaccRoom.get(iDDeskVacc - 1).getPatient() == -1) 
             {
                  working = false;
                  noWorkToDo.await();
                  
             }
             // Worker has work to do (vaccinate patient). 
             working = true;
             
             isVaccinating = true;
             timeToVaccine = 3000 + (int) Math.random() * 2000;
             sleep(timeToVaccine); // vaccinating
             int pid = desksVaccRoom.get(iDDeskVacc-1).getPatient();
             isVaccinating = false;
             
             hospital.getVaccRoom().notifyVaccine(hospital.getPatient(pid));
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
            System.out.println("Interrupted while on condition of not working. ");
        }
        finally
        {
            lock.unlock();
        }
             // checking if any patient is requesting help due to complications
             while (!hospital.getObsRoom().checkComplications(hospital.getPatients()).isEmpty()) {
                 System.out.println("COMPLICATIONS APPEARED! ");
                 lock.lock();
                 desksObsRoom = hospital.getObsRoom().getDesks();
                 desksVaccRoom = hospital.getVaccRoom().getDesks();
                 iDDeskObs = hospital.getObsRoom().checkComplications(hospital.getPatients()).get(0);
                 desksVaccRoom.get(iDDeskVacc-1).setWorker(-1);
                 desksObsRoom.get(iDDeskObs-1).setWorker(hid);
                 hospital.getObsRoom().checkComplications(hospital.getPatients()).remove(0);
                 iDDeskVacc = -1;
                 int idPatient = desksObsRoom.get(iDDeskObs-1).getPatient();
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
    public void signalNoWorkToDo(){
        lock.lock();
        try {
            noWorkToDo.signal();
        } finally {
            lock.unlock();
        }
    }

    public synchronized boolean isVaccinating() {
        return isVaccinating;
    }
    
    public boolean isWorking(){
        return this.working;
    }
    
    
    public void setTimeToVaccine(int time){
        this.timeToVaccine = time;
    }
    
    
    public int getHId() {
        return hid;
    }


}