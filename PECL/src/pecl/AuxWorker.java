package pecl;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AuxWorker extends Thread {

    private int aid;
    private int maximum; // maximum means how many items/patients
    // it must attend before taking a break
    private int counter; // current counter of items/patients attended
    private Hospital hospital;
    private boolean isResting;

    public AuxWorker(int aid, int maximum, Hospital hospital) {
        this.aid = aid;
        this.maximum = maximum;
        this.hospital = hospital;
        this.counter = 0;
        this.isResting = false;
    }

    @Override
    public void run() {
        // reception assistant
        Patient patient;
        ArrayList<Patient> enteringQueue;
        ArrayList<Patient> waitingQueue;
        if (aid == 1) 
        {
             while (counter != 2000) { //total number of patients generated = 2000
                 enteringQueue = hospital.getReception().getEnteringQueue();
                 waitingQueue = hospital.getReception().getWaitingQueue();
                
                 while (!enteringQueue.isEmpty()) 
                 {
                     patient = waitingQueue.get(1);
                     if (patient.hasAppointment()) 
                     {
                          counter++;
                          waitingQueue.remove(0); // no se si aqui o está hecho en otro lado
                          enteringQueue.add(patient);
                    }
                    // check if there is a desk so the user will go.
                     int nrDesk = hospital.getVaccRoom().getAvailableDesk();
                     while (nrDesk == -1) 
                     {
                          // tienen que dormir tanto el paciente como el trabajador
                     } 
                        
                     if (counter % maximum == 0)
                     {
                          isResting = true;
                          try 
                          {
                              sleep(3000 + (int) (Math.random() * 2001));
                          } 
                          catch (InterruptedException ex) 
                          {
                              Logger.getLogger(AuxWorker.class.getName()).log(Level.SEVERE, null, ex);
                          }
                          finally 
                          {
                              isResting = false;
                          }
                        
                     }
                 } 
                // entering Queue no está vacio 
                // dormir al recepcionista
                
            }

            /* atender al usuario, en caso de que no haya ninguno, a dormir...
               si hay alguno, mirar si tiene cita (atributo randomChance)
               subir el contador counter en 1, enviar al paciente a otra cola 
               y anteder al siguiente paciente.
               sincronizando una arrayList, comprueba la lista de mesas despues de que atienda
               a un paciente y antes de que se vaya a dormir (descanso)
               si hay un hueco libre, despierta a un paciente y le pasa a que mesa ir.
               cuando atiende a 10 usuarios se toma un descanso de 3-5 segundos
             */
        } 
        else 
        { // vaccination assistant
            
            while (counter != 2000) 
            {
                try 
                {
                    sleep(500 + (int) (Math.random() * 501));
                    hospital.getVaccRoom().createVaccine();
                } 
                catch (InterruptedException ex) 
                {
                    Logger.getLogger(AuxWorker.class.getName()).log(Level.SEVERE, null, ex);
                }
                if (counter % maximum == 0)
                {
                    try 
                    {
                        sleep(1000 + (int) (Math.random() * 4001));
                    } 
                    catch (InterruptedException ex) 
                    {
                        Logger.getLogger(AuxWorker.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            
            
            /* crea vacunas con un intervalo de 0.5-1 segundos
               cuando llega a 20 vacunas se toma un descanso de 1-4 segundos
               vuelve al trabajo
             */
        }
    }
    
    public synchronized void availableDesk(){
        ArrayList<Patient> enteringQueue = hospital.getReception().getEnteringQueue();
        int timeToSleep = 500 + (int) (Math.random() * 500);
        enteringQueue.get(0).setTimeToGetDesk(aid);
    }

    public int getAid() {
        return aid;
    }

    public int checkArrivingPatient() {

        return 0;
    }
    
    public boolean isResting(){
        return this.isResting;
    }
    
}
