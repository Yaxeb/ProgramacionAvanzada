package pecl;

import hospitalInterface.MainWindow;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Hospital {
    private Reception reception;
    private VaccRoom vaccRoom;
    private ObservationRoom obsRoom;
    private AtomicInteger capacity;
    private HashMap<Integer, Patient>  patients;
    private HashMap<Integer, HcareWorker> hcareWorkers;
    private ArrayList<HcareWorker> restRoom;
    private Semaphore semEnterVacc = new Semaphore(10);
    private Semaphore semEnterObs = new Semaphore(20);
    private Semaphore semPatients = new Semaphore(1);
    private Semaphore semException = new Semaphore(1);
    private Semaphore semVacc = new Semaphore(1);
    private Semaphore semRec = new Semaphore(1);
    private Semaphore semObs = new Semaphore(1);
    private MainWindow window;
    private CustomLogger clogger;
    
    public Hospital(Reception reception, VaccRoom vaccRoom, ObservationRoom obsRoom, MainWindow window) {
        this.reception = reception;
        this.vaccRoom = vaccRoom;
        this.obsRoom = obsRoom;
        this.capacity = new AtomicInteger();
        this.patients = new HashMap<>();
        this.hcareWorkers = new HashMap<>(); 
        this.restRoom = new ArrayList<>();
        this.window = window;
        this.clogger = new CustomLogger();
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
            try
            {                    //patient will wait until it comes back
                wait();
            }
            catch(Exception e){}
        }
        int desk = aWorker.availableDesk(patient);
        if(desk == 0)
        {
            System.out.println("Patient P"+String.format("%04d", patient.getPid()) + " has come without an appointment");
            clogger.write("Patient P"+String.format("%04d", patient.getPid()) + " has come without an appointment", "Reception");
        }
        else
        {
            System.out.println("Patient P"+String.format("%04d", patient.getPid())+" in desk "+desk + "controlled by " + getVaccRoom().getDesks().get(desk-1).getWorker());
            clogger.write("Patient P"+String.format("%04d", patient.getPid())+" in desk "+desk + "controlled by " + getVaccRoom().getDesks().get(desk-1).getWorker(), "Reception");
        }
        return desk;
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
        try //we try to enter the observation room
        {                            
             semEnterObs.acquire();
        }
        catch(Exception e){}
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
        boolean allWorkersBusy = true;
        System.out.println("Patient P"+String.format("%04d", patient.getPid())+" in desk "+iDDesk + "controlled by " + getObsRoom().getDesks().get(iDDesk-1).getWorker());
        clogger.write("Patient P"+String.format("%04d", patient.getPid())+" in desk "+iDDesk + "controlled by " + getObsRoom().getDesks().get(iDDesk-1).getWorker(), "Observation Room");
        obsRoom.sitPatient(patient, iDDesk);
        try{
        patient.sleep(10000);
        } catch(Exception e) {
             try {
                 semException.acquire();
                 ArrayList<Desk> desksVaccRoom = vaccRoom.getDesks();
                
                 for (Desk desk : desksVaccRoom) 
                 {
                    // In case that there is a worker and not a patient
                     if (desk.getWorker() != -1 && desk.getPatient() == -1) 
                     {
                          allWorkersBusy = false;
                          // first case, the worker is sleeping because it has no work to do
                          if (hcareWorkers.get(desk.getWorker()).isWorking())
                          {
                              hcareWorkers.get(desk.getWorker()).signalNoWorkToDo(); 
                              // will it work? 
                              
                              // we should call a method to signal the worker
                              // INSIDE HcareWorker.java
                          }
                     }
                 }
                
                // second case, all workers were busy and are some workers sleeping.
                 if (allWorkersBusy && !restRoom.isEmpty()){
                      restRoom.get(0).interrupt();
                 }
                
                // third case, all workers are busy and none are sleeping. 
                else if (allWorkersBusy && restRoom.isEmpty()) {
                    
                }
                
                
                //in case something goes wrong, still to implement
                /*
                todo esto dentro de un lock, o un semaforo...
                miramos todas las mesas una a una, comprobando si hay algun worker
                que no tenga paciente, en caso de que lo haya, se llama al worker
                para que venga a visitar al usuario con complicaciones.
                
                en caso de que todos esos workers estén trabajando y haya workers durmiendo
                se despierta al primer worker durmiendo (posicion 0 del arraylist de gente descansando)
                en ese momento, se activa un flag en hcareWorker que diga que esta activo en modo
                especial, y tiene que obviar su comportamiento natural y dirigirse a la sala
                de observacion donde tenga que visitar al paciente con problemas, despues, se desactiva
                el flag y se vuelve a descansar.
                
                en caso de que todos los workers esten trabajando y no haya workers durmiendo,
                se espera a que haya un worker que termine de vacunar, este comprobara de forma
                natural siempre antes de que pueda pasar nadie a la mesa si existe un paciente con
                complicaciones, en caso afirmativo, se irá de la mesa y se dirigirá al observation room.
                
                */
            } catch (InterruptedException ex) {
                Logger.getLogger(Hospital.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        obsRoom.exitPatient(patient, iDDesk);
        System.out.println("Patient P"+String.format("%04d", patient.getPid())+" exits the hospital");
        clogger.write("Patient P"+String.format("%04d", patient.getPid())+ " exits the hospital", "Observation Room");
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
    
    public void setVaccRoom(VaccRoom vaccRoom){
        this.vaccRoom = vaccRoom;
    }
    
    public Reception getReception(){
        return this.reception;
    }
    
    public void setReception(Reception reception){
        this.reception = reception;
    }
    
    public ObservationRoom getObsRoom(){
        return this.obsRoom;
    }
    
    public void setObsRoom(ObservationRoom obsRoom){
        this.obsRoom = obsRoom;

    }

    public CustomLogger getClogger() {
        return clogger;
    }

    public void setClogger(CustomLogger clogger) {
        this.clogger = clogger;
    }
    
    public ArrayList<HcareWorker> getRestRoom(){
        return this.restRoom;
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
    
    public HashMap<Integer, Patient> getPatients(){
        try
        {
            semPatients.acquire();
        }catch(Exception e){}
        HashMap<Integer, Patient> p = patients;
        semPatients.release();
        return p;
    }
    
    public String restRoomToString(){
        String text = "";
        for (HcareWorker worker : restRoom) {
            text += "H" + worker.getHId() + ", ";
        }
        return text;
        //restRoomText.setText(text);
    }
    
    public CustomLogger getLogger(){
        return this.clogger;
    }
    
    public void startWindow()
    {
        StringManager updater = new StringManager(this,this.window);
        updater.start();
    }
    
}
