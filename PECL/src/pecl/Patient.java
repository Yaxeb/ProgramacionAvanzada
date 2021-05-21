package pecl;

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
        int iDDesk = hospital.enterReception(this, hospital.getReception().getAuxWorker());
        if ( iDDesk != 0) { // tendre que comprobarlo varias veces en bucle
            int obsDesk = hospital.enterVaccRoom(this, iDDesk);
            hospital.enterObservationRoom(this, obsDesk);
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
