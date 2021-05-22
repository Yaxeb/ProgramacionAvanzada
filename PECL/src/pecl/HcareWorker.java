package pecl;

import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
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
    private Lock lock; 
    private Condition noWorkToDo; 
    private boolean working;
    

    public HcareWorker(int id, int pVaccinated, Hospital hospital) {
        this.hid = id;
        this.pVaccinated = pVaccinated;
        this.hospital = hospital;
        this.beenAwaken = false;
        this.maximum = 15;
        this.lock = new ReentrantLock();
        this.noWorkToDo = lock.newCondition();
        this.working = false;
    }
    
    @Override
    public void run(){
        ArrayList<Desk> desksVaccRoom;
        ArrayList<Desk> desksObsRoom;     
        if (beenAwaken)
        {
            // ve a la obs room, que te han despertado para eso. 
            // hace la sincronizacion con el/los usuario/s, y se va a dormir de nuevo. 
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
              
        try {
            lock.lock();
            desksVaccRoom = hospital.getVaccRoom().getDesks();

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
            
             while (desksVaccRoom.get(iDDeskVacc).getPatient() == -1) 
             {
                 working = false;
                 noWorkToDo.await();
             }

             // Worker has work to do (vaccinate patient). 
             working = true;
             timeToVaccine = 3000 + (int) Math.random() * 2000;
             int pid = desksVaccRoom.get(iDDeskVacc).getPatient();
             vaccinatePatient(hospital.getPatient(pid), this, timeToVaccine);
             counter++;
             
             if (counter % 15 == 0)
             {
                 try 
                 {
                      sleep(5000 + (int) Math.random() * 3000); 
                 }
                 catch (InterruptedException ex) 
                 {
                    // awaken while vaccinating. (unlikely to happen)
                    System.out.println("Been awaken while vaccinating. ");
                 }
             }
             
             // checking if any patient is requesting help due to complications
             while (!hospital.getObsRoom().checkComplications().isEmpty()) {
                 // mirar los locks por ahi...
                 desksObsRoom = hospital.getObsRoom().getDesks();
                 desksVaccRoom = hospital.getVaccRoom().getDesks();
                 iDDeskObs = hospital.getObsRoom().checkComplications().get(0);
                 
                 desksVaccRoom.get(iDDeskVacc).setWorker(-1);
                 desksObsRoom.get(iDDeskObs).setWorker(hid);
                 hospital.getObsRoom().checkComplications().remove(0);
                 
                 int idPatient = desksObsRoom.get(iDDeskObs).getPatient();
                 int timeWithComplications = 2000 + (int) Math.random() * 3001;
                 hospital.getPatient(idPatient).setTimeWithComplications(timeWithComplications);
                 // treating the patient. 
                 sleep(timeWithComplications);
             }
                                      
        }
        catch (InterruptedException ex) 
        {
             // no work to do but been interrupted because the worker is needed 
             beenAwaken = true;
             this.start();
        }
        finally
        {
            lock.unlock();
        }
        
            /*
               tiene que irse a dormir pase lo que pase, evita active waiting
               si le despiertan (puede despertarle con un signal tanto una persona
               del observation room o un paciente sentado en frente suyo que viene a 
               vacunarse). comprobamos si hay alguien sentado en frente suyo, y si no...
               se va a ayudar al observation room.
            */
                 
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
    
    /**
     * Method which signalls if the HcareWorker has work to do.
     * 
     */
    public synchronized void signalNoWorkToDo(){
        
        noWorkToDo.signal(); // puede dar illegal monitor exception, ver si sucede o no.
    }
    
    
    
    public void vaccinatePatient(Patient patient, HcareWorker hcWorker, int time){
        patient.setTimeToVaccine(time);
        hcWorker.setTimeToVaccine(time);
    }
    
    public boolean isWorking(){
        return this.isWorking();
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