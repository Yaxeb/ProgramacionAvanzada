package pecl;

import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ObservationRoom {
    ArrayList<Desk> desks = new ArrayList(20);
    //private Semaphore desksSemaphore = new Semaphore(1);
    private Hospital hospital; 
    private Lock desksLock = new ReentrantLock();
    private Condition availableDesk = desksLock.newCondition();
    
    public ObservationRoom(Hospital hospital){
        this.hospital = hospital;
    }
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
                    if(d.getPatient() == -1 && d.getWorker() == -1)
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
    
    public int checkComplications(){
        int counter = 0;
        try{
            desksLock.lock();
            for (int i = 0; i<desks.size(); i++)
            {
                if (hospital.getPatient(desks.get(i).getPatient()).isInfected()){
                    counter++;
                }
            }
        }catch(Exception e){}
        finally{
            desksLock.unlock();
        }
        return counter;
    }
}
