package pecl;

import java.util.HashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class Hospital {
    private final Reception reception;
    private final VaccRoom vaccRoom;
    private final ObservationRoom obsRoom;
    private final AtomicInteger capacity;
    HashMap<Integer, Patient>  patients;
    private Semaphore semEnterVacc = new Semaphore(10);
    private Semaphore semEnterObs = new Semaphore(20);
    private Semaphore semPatients = new Semaphore(1);
    
    public Hospital(Reception reception, VaccRoom vaccRoom, ObservationRoom obsRoom) {
        this.capacity = new AtomicInteger();
        this.reception = reception;
        this.vaccRoom = vaccRoom;
        this.obsRoom = obsRoom;
        this.patients = new HashMap<>();
    }
    
    public void enterHospital(Patient patient){
        capacity.set(capacity.addAndGet(1));
        addPatient(patient);
        reception.enterWaitingQueue(patient);
        // queda hacer las comprobaciones y mandarle a dormir        
        // quitarlo de la lista? 
    }
    
    public synchronized int enterReception(Patient patient, AuxWorker aWorker){
        while(aWorker.isResting()){ //if the recepcionist is on break, the 
            try{                    //patient will wait until it comes back
                wait();
            }catch(Exception e){}
        }
        if (patient.hasAppointment()){
            reception.exitWaitingQueue(patient);
            reception.enterEnteringQueue(patient);
            //If there is any available desk
            try{
                semEnterVacc.acquire();
            }catch(Exception e){}
            //auxworker tells patient the desk id
            int vacDesk = aWorker.availableDesk();
            reception.exitEnteringQueue(patient);// the patient leaves the reception room
            return vacDesk;                      // the id of its desk is returned
        }else{
            reception.exitWaitingQueue(patient); //the patient didn't have an appointment
            removePatient(patient);              // so it leaves the hospital
            return 0;                           
            
        }
        /*
        hablar con el asistente y mirar si está en lista...
        Si está en lista se va a la enteringQ y sigue normal, return el numero de la mesa
        al final else return 0. 
        Una vez despierto, obtener el numero de mesa al que ir. 
        Salir de la recepcion.
        */
        //while (semaforoEntrarMesas.acquire() ){
            
        //}
        //aworker.tellMeTheDesk()
    }
    
    
    public int enterVaccRoom(Patient patient, int iDDesk){
        vaccRoom.sitPatient(patient, iDDesk);
        //communication between the worker and the patient to know the 
        //duration of the vaccine
        try{                            //we try to enter the observation room
                semEnterObs.acquire();
            }catch(Exception e){}
        int obsDesk = obsRoom.getAvailableDesk();
        vaccRoom.exitPatient(patient,iDDesk); // it leaves the desk
        semEnterVacc.release();
        return obsDesk;
        
        /*    
        ir de golpe a la mesa iDDesk la que tenga que ir. (sentarse)
        Comunicarse con el trabajador de cuanto tiempo va a 
        durar la vacuna, esperar conjuntamente...
        comprobar si hay sitio en el observationRoom, si no,
        esperar a que haya un hueco.
        salir de la mesa
        salir de la vacc room
        dirigirse al nuevo iDDesk
        */
    }
    
    
    public void enterObservationRoom(Patient patient, int iDDesk){
        obsRoom.sitPatient(patient, iDDesk);
        try{
        patient.sleep(10000);
        }catch(Exception e){
            //in case something goes wrong, still to implement
        }
        obsRoom.exitPatient(patient, iDDesk);
        semEnterObs.release();
        capacity.addAndGet(-1);
        removePatient(patient);
        /*
        entrar a la mesa sin paciente 
        esperar 10 segundos, mirar si hay comlpicaciones
            si hay complicaciones, llamar al primer asistente disponible
            en caso de que haya uno durmiendo, se le despierta (IEException)
            comunicarse con el trabajador de cuanto tiempo necesitará esperar
            esperar conjuntamente
            el trabajador vuelve a descansar todo su turno de descanso
        salir de la mesa
        salir de la observation room
        salir del hospital
        */
    }
    
    public VaccRoom getVaccRoom(){
        return this.vaccRoom;
    }
    
    public Reception getReception(){
        return this.reception;
    }
    
    public ObservationRoom getObsRoom(){
        return this.obsRoom;
    }
    
    public void addPatient(Patient patient){
        try
        {
            semPatients.acquire();
        }catch(Exception e){}
        patients.put(patient.getPid(), patient);
        semPatients.release();
    }
    public void removePatient(Patient patient){
        try
        {
            semPatients.acquire();
        }catch(Exception e){}
        patients.remove(patient.getPid());
        semPatients.release();
    }
    public Patient getPatient(int patientID){
        try
        {
            semPatients.acquire();
        }catch(Exception e){}
        Patient patient = patients.get(patientID);
        semPatients.release();
        return patient;
    }
}
