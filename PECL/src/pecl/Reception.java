package pecl;

import java.util.ArrayList;

import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Reception {
    // listas para imprimirlos...

    private final ArrayList<Patient> waitingQ; 
    private AuxWorker auxWorker1;
    private final ArrayList<Patient> enteringQ;
    private Semaphore waitingSemaphore;
    private Semaphore enteringSemaphore;
    private Hospital hospital;
    private Lock wLock = new ReentrantLock();
    private Lock eLock = new ReentrantLock();
    
    public Reception(){
        this.waitingQ = new ArrayList<>();
        this.enteringQ = new ArrayList<>();
        this.waitingSemaphore = new Semaphore(1);
        this.enteringSemaphore = new Semaphore(1);   
    }
    

    /**
     * Method that inserts a patient in a waiting queue. This queue represents
     * the patients waiting to be attended by the auxiliar to check if they have
     * an appointment.
     * @param patient The patient entering the hospital
     */
    public synchronized void enterWaitingQueue(Patient patient){
        try{
            wLock.lock();
            //waitingSemaphore.acquire();
            waitingQ.add(patient);
<<<<<<< HEAD
           // System.out.println("EntraWaiting");
=======
>>>>>>> 60c137525e56baf51d4e19388a920b50bd00a8fe
        }catch(Exception e){}
        finally
        {
            wLock.unlock();
            //waitingSemaphore.release();
        } 
    }
    
    /**
     * Method that removes a patient from the waiting queue. This queue represents
     * the patients waiting to be attended by the auxiliar to check if they have
     * an appointment.
     * @param patient  The patient exiting the waiting queue
     */
    public synchronized void exitWaitingQueue(Patient patient){
        try{
            wLock.lock();
            //waitingSemaphore.acquire();
            waitingQ.remove(patient);
<<<<<<< HEAD
        //    System.out.println("SaleWaiting");
=======
>>>>>>> 60c137525e56baf51d4e19388a920b50bd00a8fe
        }catch(Exception e){}
        finally
        {
            wLock.unlock();
            //waitingSemaphore.release();
        }
    }
    
    /**
     * Method that inserts a patient in an entering queue. This queue represents
     * the patients waiting to go to the vaccination Room
     * @param patient The patient waiting to enter the vaccination room
     */
    public synchronized void enterEnteringQueue(Patient patient){
        try{
            eLock.lock();
            //enteringSemaphore.acquire();
            enteringQ.add(patient);
<<<<<<< HEAD
           // System.out.println("EntraEnter");
=======
>>>>>>> 60c137525e56baf51d4e19388a920b50bd00a8fe
        }catch(Exception e){}
        finally
        {
            eLock.unlock();
            //enteringSemaphore.release();
        }
    }
    
    /**
     * Method that removes a patient from the entering queue. This queue represents
     * the patients waiting to go to the vaccination Room
     * @param patient The patient waiting to enter the vaccination room
     */
    public synchronized void exitEnteringQueue(Patient patient){
        try{
            eLock.lock();
            //enteringSemaphore.acquire();
            enteringQ.remove(patient);
          //  System.out.println("SaleEntering");
        }catch(Exception e){}
        finally
        {
            eLock.unlock();
            //enteringSemaphore.release();
        }
    }
    
    /**
     * Returns the Entering queue. This queue represents
     * the patients waiting to go to the vaccination Room
     * @return The Entering Queue
     */
    public ArrayList<Patient> getEnteringQueue(){
        return this.enteringQ;
        
    }
    
    /**
     * Returns the Waiting queue. This queue represents
     * the patients waiting to be attended by the auxiliar to check if they have
     * an appointment.
     * @return The Waiting queue
     */
    public ArrayList<Patient> getWaitingQueue(){
        return this.waitingQ;
    }
    
    public String allPatientsToString(){
        ArrayList<Patient> all = new ArrayList<>();
        String text = "";
        all.addAll(getEnteringQueue());
        all.addAll(getWaitingQueue());
        for (Patient patient : all)
        {
            text+= "P"+String.format("%04d", patient.getPid()) + ", ";
        }
        return text;
    }
    
    /**
     * Returns the auxiliary worker who is in charge of checking the appointments
     * @return The auxiliary worker who is in charge of checking the appointments
     */
    public AuxWorker getAuxWorker(){
        return this.auxWorker1;
    }
    
    /**
     * Sets the auxiliary worker who will be in charge of checking the appointments
     * @param auxWorker1 The auxiliary worker who will be in charge of checking the appointments
     */
    public void setAuxWorker(AuxWorker auxWorker1){
        this.auxWorker1 = auxWorker1;
    }
}
