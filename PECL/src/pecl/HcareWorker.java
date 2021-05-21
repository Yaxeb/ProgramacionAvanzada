package pecl;

import java.util.logging.Level;
import java.util.logging.Logger;

public class HcareWorker extends Thread{
    private int hid;
    private int pVaccinated;
    private int iDDesk;
    private Hospital hospital; 
    private boolean beenAwaken;
    public HcareWorker(int id, int pVaccinated, int vPost, Hospital hospital) {
        this.hid = id;
        this.pVaccinated = pVaccinated;
        this.iDDesk = vPost;
        this.hospital = hospital;
        this.beenAwaken = false;
    }
    
    @Override
    public void run(){
        ArrayList<> desks 
        if (beenAwaken)
        {
            
        } 
        else 
        {
            
            try 
            {
                //tomas descanso de 1-3 segundos
                sleep(1000 + (int) (Math.random() * 2001));
            } 
            catch (InterruptedException ex) 
            {
                Logger.getLogger(HcareWorker.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        while (hospital.getObsRoom().getButton() > 0) { // integer > 1
             
             // atomic integer... 
             // deja su mesa libre... ( si estaba en una... )
             // ir a la obs room. (si no esta ahi ya... ) // se sienta en la desk respectiva
             hospital.getObsRoom().getButton().decrease(); // se le resta 1.... 
             
             //sincronizas con el paciente
             synchronizeloquesea();
        }
        
        
        while (true) { // haya paciente
            desks = hospital.getVaccRoom().getDesk();
            if (hospital.getVaccRoom().getVaccines() > 0){
                hospital.getVaccRoom().commentTime(2000); //vacunar tiempo...? 
                // reducir el no de vacunas. 
                Patient patient = getPatient(iDDesk);
                synchronizeloquesea(); //
            }
        } // se duerme... 
        
        /* cuando esten listos, van al desk disponible 
           (está disponible si no hay ni medico ni paciente dentro)
           cuando un paciente llegue al desk, esperan a que haya una vacuna
           cuando está la vacuna, sincronizan con el paciente 
           para determinar el tiempo de 3-5s 
           si el paciente se va, comunican que el sitio está disponible 
           (arrayList sincronizada) y esperan a que venga un paciente. 
           cuando vacunen a 15 pacientes se toman un descanso de 5-8s
           y se dirigen a la sala de descanso.
           los worker podrán ser despertados en caso de que un paciente
           tenga complicaciones y no haya ningun médico trabajando disponible 
           (que no esten poniendo vacunas) 
           (recorremos todas las mesas y miramos que no tengan paciente y tengan worker)
           en caso negativo, se despierta y acude al sitio de la complicacion, se sincronizan
           con el paciente de cuanto tiempo van a descansar, se vuelve a su descanso completo. 
        */
        //wait(){

        
        //} catch InterruptedException(){
            /* observation room tendrá otra lista que sea de urgencia, leyendo esa lista 
               y contrastandola con la lista de mesas (para ver si tiene un medico o no)
               se determinará a qué puesto tiene que dirigirse el médico despertado. 
               ahí se sincroniza con el paciente, para esperar 2-5s y se vuelve a su descanso de nuevo.
               meHanDesperato = true
            */ 
        //    this.start();
        //}
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
        return iDDesk;
    }

    public void setvPost(int iDDesk) {
        this.iDDesk = iDDesk;
    }
    
    
    public void syncronizeloquesea(Patient patient, HcareWorker hcWorker){
        patient.sleep(200);
        hcWorker.sleep(200);
    }
    
    
}
