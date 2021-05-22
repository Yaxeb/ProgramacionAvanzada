package pecl;

import java.util.ArrayList;

import java.util.concurrent.Semaphore;

public class Reception {
    // listas para imprimirlos...

    private final ArrayList<Patient> waitingQ = new ArrayList(); 
    private AuxWorker auxWorker1;
    private final ArrayList<Patient> enteringQ = new ArrayList();
    private Semaphore waitingSemaphore = new Semaphore(1);
    private Semaphore enteringSemaphore = new Semaphore(1);
    
    // while not atendidos.... que se vayan a dormir... 
    // semaforos... 
    
    // cuando les atiendan... se van a entering Q... y cuando les den un notify o lo que usemos
    // que entre el primero...
    /**
     * Method that inserts a patient in a waiting queue. This queue represents
     * the patients waiting to be attended by the auxiliar to check if they have
     * an appointment.
     * @param patient The patient entering the hospital
     */
    public synchronized void enterWaitingQueue(Patient patient){
        try{
            waitingSemaphore.acquire();
            waitingQ.add(patient);
            patient.wait();
        }catch(Exception e){}
        finally
        {
            waitingSemaphore.release();
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
            waitingSemaphore.acquire();
            patient.notify();
            waitingQ.remove(patient);
        }catch(Exception e){}
        finally
        {
            waitingSemaphore.release();
        }
    }
    
    /**
     * Method that inserts a patient in an entering queue. This queue represents
     * the patients waiting to go to the vaccination Room
     * @param patient The patient waiting to enter the vaccination room
     */
    public synchronized void enterEnteringQueue(Patient patient){
        try{
            enteringSemaphore.acquire();
            enteringQ.add(patient);
            patient.wait();
        }catch(Exception e){}
        finally
        {
            waitingSemaphore.release();
        }
    }
    /**
     * Method that removes a patient from the entering queue. This queue represents
     * the patients waiting to go to the vaccination Room
     * @param patient The patient waiting to enter the vaccination room
     */
    public synchronized void exitEnteringQueue(Patient patient){
        try{
            enteringSemaphore.acquire();
            patient.notify();
            enteringQ.remove(patient);
        }catch(Exception e){}
        finally
        {
            waitingSemaphore.release();
        }
    }
    /**
     * Returns the Entering queue. This queue represents
     * the patients waiting to go to the vaccination Room
     * @return The Entering Queue
     */
    public ArrayList<Patient> getEnteringQueue(){
        ArrayList<Patient> eQueue = new ArrayList<>();
        try{
            waitingSemaphore.acquire();
            eQueue = this.enteringQ;
        }catch(Exception e){}
        finally
        {
            waitingSemaphore.release();
        }
        return eQueue;
    }
    /**
     * Returns the Waiting queue. This queue represents
     * the patients waiting to be attended by the auxiliar to check if they have
     * an appointment.
     * @return The Waiting queue
     */
    public ArrayList<Patient> getWaitingQueue(){
        ArrayList<Patient> wQueue = new ArrayList<>();
        try{
            waitingSemaphore.acquire();
            wQueue = this.waitingQ;
        }catch(Exception e){}
        finally
        {
            waitingSemaphore.release();
        }
        return wQueue;
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
