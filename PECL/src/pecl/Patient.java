package pecl;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Patient extends Thread{
    private final int pid;
    private final int randomChance;
    private Hospital hospital;
    private int timeToGetDesk;
    private int timeToVaccine;
    private int timeWithComplications;
    
    /**
     * Method constructor for Patient
     * @param pid The patient's ID
     * @param hospital The hospital the patient is attending
     */
    public Patient(int pid, Hospital hospital) {
        this.pid = pid;
        this.randomChance = (int) (Math.random() * 101);
        this.timeWithComplications = 0;
        this.hospital = hospital;
    }
    
    @Override
    public void run(){
        hospital.enterHospital(this);
        int iDDesk = hospital.enterReception(this, hospital.getReception().getAuxWorker());
        if (iDDesk != 0) { 
            
            try {
            // tengo que obtener el hcareworker que esté ahi
                sleep(timeToGetDesk);
            } 
            catch (InterruptedException ex) 
            {
                Logger.getLogger(Patient.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            int obsDesk = hospital.enterVaccRoom(this, iDDesk);
            
            try 
            {
                sleep(timeToVaccine);
            } 
            catch (InterruptedException ex)
            {
                Logger.getLogger(Patient.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            hospital.enterObservationRoom(this, obsDesk);
            
            try { 
                sleep(timeWithComplications);
            } 
            catch (InterruptedException ex) 
            {
                Logger.getLogger(Patient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    
    public void setTimeToGetDesk(int time){
        this.timeToGetDesk = time;
    }
    
    /**
     * Sets the time it takes to vaccine
     * @param time The time it takes to vaccine
     */
    public void setTimeToVaccine(int time){
        this.timeToVaccine = time;
    }
    /**
     * Sets the time it takes to solve the complications
     * @param time Time it takes to solve the complications
     */
    public void setTimeWithComplications(int time){
        this.timeWithComplications = time;
    }
    
    /**
     * Returns the Patient's ID
     * @return Patient's ID
     */
    public int getPid() {
        return pid;
    }
    /**
     * Returns whether the Patient is Infected or not.
     * For that, when the Patient is created, a random number is generated
     * If the number is less or equal than 5 (5% chance) the patient will react to
     * the vaccine and would need to be treated
     * @return True if the Patient will have a reaction, False otherwise   
     */
    public boolean isInfected() {
        return randomChance <= 5;
    }
    /**
     * Returns whether the Patient has an appointment or not.
     * For that, when the Patient is created, a random number is generated
     * If the number is different from 0 (99% chance) the patient will have 
     * an appointment and would enter the entering queue. Otherwise it will
     * leave the hospital
     * @return True if the patient has an appointment, False otherwise
     */
    public boolean hasAppointment(){
        return randomChance != 0;
    }
    
}
