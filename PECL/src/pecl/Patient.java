package pecl;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Patient extends Thread{
    private final int pid;
    private final int randomChance;
    private Hospital hospital;
    private int timeToVaccine;
    
    public Patient(int pid, Hospital hospital) {
        this.pid = pid;
        this.randomChance = (int) (Math.random() * 101);
        
    }
    
    @Override
    public void run(){
        hospital.enterHospital(this);
        int iDDesk = hospital.enterReception(this, hospital.getReception().getAuxWorker());
        if ( iDDesk != 0) { // tengo que obtener el hcareworker que esté ahi 
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
        }
    }
    
    public void setTimeToVaccine(int time){
        this.timeToVaccine = time;
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
