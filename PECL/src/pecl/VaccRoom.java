package pecl;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class VaccRoom {
    private final AtomicInteger vaccines = new AtomicInteger(); 
    private final ArrayList<Desk> desks = new ArrayList(10);
    private Semaphore desksSemaphore = new Semaphore(1);
    private Lock desksLock = new ReentrantLock();
    private Condition availableDesk = desksLock.newCondition();
    
    public void sitPatient(Patient patient, int iDDesk){
        try{
            desksLock.lock();
        }catch(Exception e){}
        Desk d = desks.get(iDDesk-1);
        d.setPatient(patient.getPid());
        desks.set(iDDesk-1, d);
        desksLock.unlock();
    }
    
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
    public int getAvailableDesk(){
        int i = 0;
        try{
            desksLock.lock();
            boolean found = false;
        // while !found
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
                    // if !found: await
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
    public int getVaccines(){
        return vaccines.get();
    }
    
    /// Increments the number of vaccines in 1.
    public void createVaccine(){
        vaccines.set(vaccines.addAndGet(1));
    }
    
    /// Reduces the number of vaccines in 1.
    public void takeVaccine(){
        vaccines.set(vaccines.decrementAndGet());
    }
    
    /// returns all the desks from the Vaccination Room
    public ArrayList<Desk> getDesks(){
        return this.desks;
    }
    
    
    
}
