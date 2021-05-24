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
    }
    
    public synchronized int enterReception(Patient patient, AuxWorker aWorker){
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
    }
    
    
    public int enterVaccRoom(Patient patient, int iDDesk){
        try 
        {
            semEnterVacc.acquire();
        }
        catch (InterruptedException ex)
        {
            Logger.getLogger(Hospital.class.getName()).log(Level.SEVERE, null, ex);
        }
        vaccRoom.sitPatient(patient, iDDesk, hcareWorkers);
        vaccRoom.vaccinate(patient, hcareWorkers.get(getVaccRoom().getDesks().get(iDDesk-1).getWorker()));
        try //we try to enter the observation room
        {                            
             semEnterObs.acquire();
        }
        catch(Exception e){}
        int obsDesk = obsRoom.getAvailableDesk();
        vaccRoom.exitPatient(patient,iDDesk); // it leaves the desk
        semEnterVacc.release();
        return obsDesk;
        
    }
    
    
    public void enterObservationRoom(Patient patient, int iDDesk){
        boolean allWorkersBusy = true;
        System.out.println("Patient P"+String.format("%04d", patient.getPid())+" in desk "+iDDesk + "controlled by " + getObsRoom().getDesks().get(iDDesk-1).getWorker());
        clogger.write("Patient P"+String.format("%04d", patient.getPid())+" in desk "+iDDesk + "controlled by " + getObsRoom().getDesks().get(iDDesk-1).getWorker(), "Observation Room");
        obsRoom.sitPatient(patient, iDDesk);
        try
        {
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
                          }
                     }
                 }
                
                 // second case, all workers were busy and are some workers sleeping.
                 if (allWorkersBusy && !restRoom.isEmpty()){
                      restRoom.get(0).interrupt();
                 }

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
    
    public HashMap<Integer, HcareWorker> getHcareWorkers()
    {
        return hcareWorkers;
    }
    
    public synchronized void addWorker(HcareWorker worker)
    {
        hcareWorkers.put(worker.getHId(), worker);
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
