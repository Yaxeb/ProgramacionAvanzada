package pecl;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HcareWorker extends Thread{
    private int hid;
    private int pVaccinated;
    private int iDDeskVacc;
    private int iDDeskObs;
    private int timeToVaccine;
    private int counter;
    private final int maximum;
    private Hospital hospital; 
    private boolean beenAwaken;
    

    public HcareWorker(int id, int pVaccinated, Hospital hospital) {
        this.hid = id;
        this.pVaccinated = pVaccinated;
        this.hospital = hospital;
        this.beenAwaken = false;
        maximum = 15;
    }
    
    @Override
    public void run(){
        ArrayList<Desk> desksVaccRoom = hospital.getVaccRoom().getDesks();
        ArrayList<Desk> desksObsRoom = hospital.getObsRoom().getDesks();
        
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
              
        
        synchronized (this)
        {
             while (iDDeskVacc == -1)
             {
                 for (int i = 0 ; i < desksVaccRoom.size() ; i++) 
                 {
                     Desk desk = desksVaccRoom.get(i);
                     if (desk.getWorker() != -1)
                     {
                         if (Math.random() > 0.5)
                         {
                             desk.setWorker(hid);
                             desksVaccRoom.set(i, desk);
                             iDDeskVacc = i;
                          }
                     }
                 }
             }
         }
//        while (hospital.getObsRoom().getButton() > 0) { // integer > 1
//             
//             // atomic integer... 
//             // deja su mesa libre... ( si estaba en una... )
//             // ir a la obs room. (si no esta ahi ya... ) // se sienta en la desk respectiva
//             hospital.getObsRoom().getButton().decrease(); // se le resta 1.... 
//             
//             //sincronizas con el paciente
//             synchronizeloquesea();
//        }
            
             // sitting the worker in a working post. 
             
            
             // mientras no haya paciente, se duerme...
             // tocará hacer un lock y toda la movida...
             while (desksVaccRoom.get(iDDeskVacc).getPatient() == -1) {
                 condition.await(); // dormir
                 /*
                   crear un booleano que diga que hay alguien en la condicion de await
                   esto nos permitirá crear un signal de forma segura
                   en caso de que ese booleano no esté en true, no podremos hacer signal tampoco
                   y nos tocará despertar a alguien quien esté durmiendo.
                 */
                 
                 /*
                    tiene que irse a dormir pase lo que pase, evita active waiting
                    si le despiertan (puede despertarle con un signal tanto una persona
                    del observation room o un paciente sentado en frente suyo que viene a 
                    vacunarse). comprobamos si hay alguien sentado en frente suyo, y si no...
                    se va a ayudar al observation room.
                 */
                 
             }
             // despierto y con paciente...          
             timeToVaccine = 3000 + (int) Math.random() * 2000;
             int pid = desksVaccRoom.get(iDDeskVacc).getPatient();
            
             vaccinatePatient(hospital.getPatient(pid), this, timeToVaccine);
             counter++;
             
             if (counter % 15 == 0){
                try {
                    sleep(5000 + (int) Math.random() * 3000); 
                }
                catch (InterruptedException ex) 
                {
                    // being required in the observation room.
                    beenAwaken = true;
                    
                }
             }
            
             // le tiene que hacer signal auxWorker de que alguien va...
            // una clase extra para la comunicacion de ambas.      
       
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
    
    public void vaccinatePatient(Patient patient, HcareWorker hcWorker, int time){
        patient.setTimeToVaccine(time);
        hcWorker.setTimeToVaccine(time);
    }
    
    public void setTimeToVaccine(int time){
        this.timeToVaccine = time;
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

}