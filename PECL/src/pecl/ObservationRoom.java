package pecl;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class ObservationRoom {
    ArrayList<Desk> desks = new ArrayList(20);
    private Semaphore desksSemaphore = new Semaphore(1);
    
    public void sitPatient(Patient patient, int iDDesk){
        try{
            desksSemaphore.acquire();
        }catch(Exception e){}
        Desk d = desks.get(iDDesk-1);
        d.setPatient(patient.getPid());
        desks.set(iDDesk-1, d);
        desksSemaphore.release();
    }
    
    public void exitPatient(Patient patient, int iDDesk){
        try{
            desksSemaphore.acquire();
        }catch(Exception e){}
        Desk d = desks.get(iDDesk-1);
        d.setPatient(-1);
        desks.set(iDDesk-1, d);
        desksSemaphore.release();
    }    
    
    public int getAvailableDesk(){
        try{
            desksSemaphore.acquire();
        }catch(Exception e){}
        boolean found = false;
        int i = 0;
        while (i < desks.size() && !found){
            Desk d = desks.get(i);
            if(d.getPatient() == -1 && d.getWorker() != -1){//the desk has a worker and no patients
                found = true;
            }
            i++; //we do it regardless, because the desk's ID is id+1, so even if
        }       // we have found the first available desk, we still need to add 1
        desksSemaphore.release();
        if (found){
            return i;
        }
        else{
            return -1;
        }
    }    
}
