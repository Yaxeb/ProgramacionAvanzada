package pecl;

import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ObservationRoom {
    ArrayList<Desk> desks = new ArrayList(20);
    private Hospital hospital; 
    private Lock desksLock = new ReentrantLock();
    private Condition availableDesk = desksLock.newCondition();
    /**
     * This method initializes the object
     * @param hospital The hospital this room belongs to
     */
    public ObservationRoom(Hospital hospital){
        this.hospital = hospital;
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
     * It checks whether the room is empty (checking both Patient's ID and
     * Worker's ID are -1) and, if it is, it will return that desk's ID
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
                    if(d.getPatient() == -1 && d.getWorker() == -1)
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

    /**
     * This method checks if there is any desk with a patient with complications
     * due to the vaccine
     * It uses the HashMap of patients to retrieve the Patient using the ID stored
     * in the desk, and while checking it, if it's infected with a mutation due to the 
     * vaccine, it adds the ID of their desk to an ArrayList.
     * After traversing all desks, the counter is returned
     * @return An ArrayList with all the Desks' ID that have a patient with complications
     */
    public ArrayList<Integer> checkComplications(){
        ArrayList<Integer> desksWithComplications = new ArrayList<>();
        try{
            desksLock.lock();
            for (int i = 0; i<desks.size(); i++)
            {
                if (hospital.getPatient(desks.get(i).getPatient()).isInfected()){
                    desksWithComplications.add(i+1);
                }
            }
        }catch(Exception e){}
        finally{
            desksLock.unlock();
        }
        return desksWithComplications;
    }
    
    /**
     * Returns the ArrayList of desks which are located in the Observation Room
     * @return ArrayList<Desk>
     */
    public ArrayList<Desk> getDesks(){
        return this.desks;
    }
    
}
