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
public class HcareWorker extends Thread{
    private int hid;
    private int pVaccinated;
    private int vPost;

    public HcareWorker(int id, int pVaccinated, int vPost) {
        this.hid = id;
        this.pVaccinated = pVaccinated;
        this.vPost = vPost;
    }
    
    public void vaccinatePatient(Patient p, int number){
        
    }
    
    public void takeBreak(){
        
    }

    public int getHId() {
        return hid;
    }

    public int getpVaccinated() {
        return pVaccinated;
    }

    public void setpVaccinated(int pVaccinated) {
        this.pVaccinated = pVaccinated;
    }

    public int getvPost() {
        return vPost;
    }

    public void setvPost(int vPost) {
        this.vPost = vPost;
    }
    
    
}
