/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pecl;
/**
 *
 * @author Oqueo
 */
public class Patient extends Thread{
    private final int pid;
    private final int randomChance;
    private Hospital hospital;
    
    public Patient(int pid, Hospital hospital) {
        this.pid = pid;
        this.randomChance = (int) (Math.random() * 101);
        
    }
    
    @Override
    public void run(){
        hospital.enterHospital(this);
        if (hospital.enterReception(this, hospital.get) == 1) {
            hospital.enterVaccRoom(this);
            hospital.enterObservationRoom(this);
        } 
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
