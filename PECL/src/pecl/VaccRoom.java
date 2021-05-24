package pecl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class VaccRoom {
    private final AtomicInteger vaccines; 
    private ArrayList<Desk> desks;
    private AuxWorker aWorker;
    private Lock desksLock;
    private Condition availableDesk;
    private Lock vaccLock;
    private Condition vaccinating;
    private Semaphore semDesks = new Semaphore(10);
    
    /**
     * This method initializes the object
     * @param hospital The hospital this room belongs to
     */
    public VaccRoom() {
        
        this.vaccines = new AtomicInteger();
        this.desks = new ArrayList<>(10);
        for(int i = 0; i<10; i++)
        {
            this.desks.add(new Desk(i+1));
        }
        this.desksLock = new ReentrantLock();
        this.availableDesk = desksLock.newCondition();
        
        this.vaccLock = new ReentrantLock();
        this.vaccinating = vaccLock.newCondition();
    }

    /**
     * This method locates a patient into the desk with the ID given
     * It uses a lock to avoid data corruption and race conditions when
     * accessing the desks attribute
     * @param patient The patient who's going to sit in the desk
     * @param iDDesk The ID of the desk
     */
    public synchronized void sitPatient(Patient patient, int iDDesk, HashMap<Integer ,HcareWorker> workers){
        Desk d = desks.get(iDDesk-1);
        d.setPatient(patient.getPid());
        desks.set(iDDesk-1, d);
        workers.get(d.getWorker()).signalNoWorkToDo();
    }
    
    /**
     * This method prepares a patient to leave the observation room
     * It uses a lock to avoid data corruption and race conditions when
     * accessing the desks attribute
     * It sets the Patient's id in the desk as -1, indicating that this desk
     * has no patients.
     * It also signals that a desk could be available to the method 
     * getAvailableDesk()
     * @param patient The patient who's going to sit in the desk
     * @param iDDesk The ID of the desk
     */
    public synchronized void exitPatient(Patient patient, int iDDesk){
        Desk d = desks.get(iDDesk-1);
        d.setPatient(-1);
        desks.set(iDDesk-1, d);
        semDesks.release();
    }
    
    /**
     * This method looks at all the desks sequentially, and, in case there is an
     * available one, it returns its ID.
     * It uses a lock to avoid data corruption and race conditions when
     * accessing the desks attribute.
     * It checks whether the room doesn't have a patient, but it has a worker
     *(by checking that Patient's ID is -1 and Worker's ID is not -1) 
     * and, if it is, it will return that desk's ID
     * Otherwise, the method will be stopped using await() until it is signaled
     * by the method exitPatient() that there is a possible new free desk
     * @return The ID of the first available desk
     */
    public int getAvailableDesk(){
        int i = 0;
        try{
            semDesks.acquire();
            boolean found = false;
            while(!found)
            {
                
                if (i >= desks.size() -1 )
                {
                    i = 0;
                }
                Desk d = desks.get(i);
                if(d.getPatient() == -1 && d.getWorker() != -1)
                {//the desk has a worker and no patients
                    found = true;
                    d.setPatient(0); //We reserve the desk
                }
                i++; //we do it regardless, because the desk's ID is id+1, so even if
                     // we have found the first available desk, we still need to add 1
            }
        }catch(Exception e){}
        return i;
    }
    
    public void vaccinate(Patient patient, HcareWorker worker)
    {
        try
        {
            vaccLock.lock();
            while(!worker.isVaccinating())
            {   
                try 
                {
                vaccinating.await();
                } catch (InterruptedException ex) {}
            }
        }
        catch(Exception e){}
        finally
        {
            vaccLock.unlock();
        }
    }
    
    public void notifyVaccine(Patient patient)
    {
         try
        {
            vaccLock.lock();
            vaccinating.signal();
        }catch(Exception e){}
        finally
        {
            vaccLock.unlock();
        }
    }
    /**
     * This method returns the number of available vaccines
     * @return The number of available vaccines
     */
    public int getVaccines(){
        return vaccines.get();
    }
    
    /**
     * This method increments the number of available vaccines by 1
     */
    public void createVaccine(){
        vaccines.set(vaccines.addAndGet(1));
    }
    
    /**
     * This method decrements the number of available vaccines by 1
     */
    public void takeVaccine(){
        vaccines.set(vaccines.decrementAndGet());
    }
    
    /**
     * This method returns all the desks from the Vaccination Room
     * @return An ArrayList containing all desks from the Vaccination Room
     */
    public synchronized ArrayList<Desk> getDesks(){
        return this.desks;
    }

    public void setDesks(ArrayList<Desk> desks) {
        ArrayList<Desk> d = new ArrayList<>();
        try{
            desksLock.lock();
            this.desks = desks;
        }catch(Exception e){}
        finally{
            desksLock.unlock();
        }       
    }

    public AuxWorker getAuxWorker() {
        return aWorker;
    }

    public void setAuxWorker(AuxWorker aWorker) {
        this.aWorker = aWorker;
    }
    
    
    
}
