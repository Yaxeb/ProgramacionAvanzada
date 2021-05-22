package pecl;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class VaccRoom {
    private final AtomicInteger vaccines; 
    private final ArrayList<Desk> desks;
    private AuxWorker aWorker;
    private Lock desksLock;
    private Condition availableDesk;
    private Hospital hospital;
    
    /**
     * This method initializes the object
     * @param hospital The hospital this room belongs to
     */
    public VaccRoom(Hospital hospital) {
        
        this.vaccines = new AtomicInteger();
        this.desks = new ArrayList(10);
        this.hospital = hospital;
        this.desksLock = new ReentrantLock();
        this.availableDesk = desksLock.newCondition();
    }
    
    /**
     * This method locates a patient into the desk with the ID given
     * It uses a lock to avoid data corruption and race conditions when
     * accessing the desks attribute
     * @param patient The patient who's going to sit in the desk
     * @param iDDesk The ID of the desk
     */
    public void sitPatient(Patient patient, int iDDesk){
        try{
            desksLock.lock();
            Desk d = desks.get(iDDesk-1);
            d.setPatient(patient.getPid());
            desks.set(iDDesk-1, d);
        }catch(Exception e){}
        finally{
            desksLock.unlock();
        }
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
    public void exitPatient(Patient patient, int iDDesk){
        try{
            desksLock.lock();
            Desk d = desks.get(iDDesk-1);
            d.setPatient(-1);
            desks.set(iDDesk-1, d);
            availableDesk.signal();
        }catch(Exception e){}
        finally{
            desksLock.unlock();
        }
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
            desksLock.lock();
            boolean found = false;
            while(!found)
            {
                i = 0;
                while (i < desks.size() && !found)
                {
                    Desk d = desks.get(i);
                    if(d.getPatient() == -1 && d.getWorker() != -1)
                    {//the desk has a worker and no patients
                        found = true;
                    }
                i++; //we do it regardless, because the desk's ID is id+1, so even if
                     // we have found the first available desk, we still need to add 1
                }
                if (!found){
                    try{
                    availableDesk.await();
                    }catch(Exception e){}
                }
            }
        }catch(Exception e){}
        finally
        {
            desksLock.unlock();
        }
        return i;
    }
    
    
    /// returns the number of available vaccines
    /**
     * This method returns the number of available vaccines
     * @return The number of available vaccines
     */
    public int getVaccines(){
        return vaccines.get();
    }
    
    /// Increments the number of vaccines in 1.
    /**
     * This method increments the number of available vaccines by 1
     */
    public void createVaccine(){
        vaccines.set(vaccines.addAndGet(1));
    }
    
    /// Reduces the number of vaccines in 1.
    /**
     * This method decrements the number of available vaccines by 1
     */
    public void takeVaccine(){
        vaccines.set(vaccines.decrementAndGet());
    }
    
    /// returns all the desks from the Vaccination Room
    /**
     * This method returns all the desks from the Vaccination Room
     * @return An ArrayList containing all desks from the Vaccination Room
     */
    public ArrayList<Desk> getDesks(){
        ArrayList<Desk> d = new ArrayList<>();
        try{
            desksLock.lock();
            d = this.desks;
        }catch(Exception e){}
        finally{
            desksLock.unlock();
        }
        return d;
    }

    public AuxWorker getAuxWorker() {
        return aWorker;
    }

    public void setAuxWorker(AuxWorker aWorker) {
        this.aWorker = aWorker;
    }
    
    
    
}
