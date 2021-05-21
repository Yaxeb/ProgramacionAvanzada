package pecl;

import java.util.ArrayList;

import java.util.concurrent.Semaphore;

public class Reception {
    // listas para imprimirlos...

    private final ArrayList<Patient> waitingQ = new ArrayList(); 
    private final AuxWorker auxWorker1 = new AuxWorker(1, 10);
    private final ArrayList<Patient> enteringQ = new ArrayList();
    private Semaphore waitingSemaphore = new Semaphore(1);
    private Semaphore enteringSemaphore = new Semaphore(1);
    
    // while not atendidos.... que se vayan a dormir... 
    // semaforos... 
    
    // cuando les atiendan... se van a entering Q... y cuando les den un notify o lo que usemos
    // que entre el primero...
    
    public void enterWaitingQueue(Patient patient){
        try{
            waitingSemaphore.acquire();
        }catch(Exception e){}
        waitingQ.add(patient);
        waitingSemaphore.release();
        
    }
    public void exitWaitingQueue(Patient patient){
        try{
            waitingSemaphore.acquire();      
        }catch(Exception e){}
        waitingQ.remove(patient);
        waitingSemaphore.release();
    }
    
    public void enterEnteringQueue(Patient patient){
        try{
            enteringSemaphore.acquire();
        }catch(Exception e){}
        enteringQ.add(patient);
        enteringSemaphore.release();
    }
    public void exitEnteringQueue(Patient patient){
        try{
            enteringSemaphore.acquire();      
        }catch(Exception e){}
        enteringQ.remove(patient);
        enteringSemaphore.release();
    }
    
}
