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
    private boolean isVaccinating;
   // private CustomLogger clogger;
    
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
           // System.out.println("Mequierosentar");
            // System.out.println("HcareWorker " + hid + " entered ");
             //lock.lock();
             desksVaccRoom = hospital.getVaccRoom().getDesks(); // metodo sincronizado. 
             // sitting in a post. 
             
             int i = 1;
             while (iDDeskVacc == -1)
             {
              //  System.out.println("ejecucion del while primero: id hcare =  " + hid);
                if (i >= desksVaccRoom.size() + 1) 
                {
                  //  System.out.println("ejecucion del bucle while por worker: " + hid);
                    i = 1; 
                }
              
                Desk desk = desksVaccRoom.get(i-1);
                if (desk.getWorker() == -1)
                {
                        desk.setWorker(hid);
                        desksVaccRoom.set(i-1, desk);
                        iDDeskVacc = i;
                        //System.out.println("iDDesk: " + iDDeskVacc);
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
        System.out.println("LANDMARK 1");
        try
        {
             lock.lock();
             desksVaccRoom = hospital.getVaccRoom().getDesks();
             while (desksVaccRoom.get(iDDeskVacc - 1).getPatient() == -1) 
             {
                  working = false;
                  System.out.println("claro chavalote, nadie te despierta...");
                  noWorkToDo.await();
                  
             }
             // Worker has work to do (vaccinate patient). 
             working = true;
             isVaccinating = true;
             timeToVaccine = 3000 + (int) Math.random() * 2000;
             sleep(timeToVaccine);
             int pid = desksVaccRoom.get(iDDeskVacc-1).getPatient();
             System.out.println("Antes de notificar la vacuna");
             hospital.getVaccRoom().notifyVaccine(hospital.getPatient(pid));
             System.out.println("Se notifica la vacuna");
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
             while (!hospital.getObsRoom().checkComplications(hospital.getPatients()).isEmpty()) {
                 System.out.println("COMPLICATIONS APPEARED! ");
                 lock.lock();
                 desksObsRoom = hospital.getObsRoom().getDesks();
                 desksVaccRoom = hospital.getVaccRoom().getDesks();
                 iDDeskObs = hospital.getObsRoom().checkComplications(hospital.getPatients()).get(0);
                 desksVaccRoom.get(iDDeskVacc).setWorker(-1);
                 desksObsRoom.get(iDDeskObs).setWorker(hid);
                 hospital.getObsRoom().checkComplications(hospital.getPatients()).remove(0);
                 iDDeskVacc = -1;
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
    public void signalNoWorkToDo(){
        lock.lock();
        try {
            noWorkToDo.signal(); // puede dar illegal monitor exception, ver si sucede o no.
        } finally {
            lock.unlock();
        }
    }

    public boolean isVaccinating() {
        return isVaccinating;
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