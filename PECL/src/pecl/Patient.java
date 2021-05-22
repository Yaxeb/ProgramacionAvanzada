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
    public Patient(int pid, Hospital hospital) {
        this.pid = pid;
        this.randomChance = (int) (Math.random() * 101);
        this.timeWithComplications = 0;
    }
    
    @Override
    public void run(){
        hospital.enterHospital(this);
        int iDDesk = hospital.enterReception(this, hospital.getReception().getAuxWorker());
        if ( iDDesk != 0) { 
            
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
    
    public void setTimeToVaccine(int time){
        this.timeToVaccine = time;
    }
    
    public void setTimeWithComplications(int time){
        this.timeWithComplications = time;
    }
    
    
    public int getPid() {
        return pid;
    }

    public boolean isInfected() {
        return randomChance <= 5;
    }
    
    public boolean hasAppointment(){
        return randomChance == 0;
    }
    
}
