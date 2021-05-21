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
     * 
     * @param patient 
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
    
    public AuxWorker getAuxWorker(){
        return this.auxWorker1;
    }
    
    public void setAuxWorker(AuxWorker auxWorker1){
        this.auxWorker1 = auxWorker1;
    }
}
