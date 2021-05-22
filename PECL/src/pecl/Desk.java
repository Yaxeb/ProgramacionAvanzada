package pecl;

public class Desk {
    private int iDDesk;
    private int iDPatient;
    private int iDWorker;
    
    public Desk(int iDDesk){
        this.iDDesk = iDDesk;
        this.iDPatient = -1;
        this.iDWorker = -1;
    }
    
    /**
     * When the ID is -1, it means that no thread is occupying it
     */
    public void leavePatient(){
        this.iDPatient = -1;
    }
    
    /**
     * When the ID is -1, it means that no thread is occupying it
     */
    public void leaveWorker(){
        this.iDWorker = -1;
    }
    /**
     * Sets the Patient ID on the desk
     * @param idPatient The ID of the patient who is sitting on the desk
     */
    public void setPatient(int idPatient){
        this.iDPatient = idPatient;
    }
    /**
     * Returns the ID of the patient that is on the desk.
     * If the number returned is -1, it means there is no patient on the desk
     * @return The ID of the patient or -1 if there is no patient
     */
    public int getPatient(){
        return this.iDPatient;
    }
    /**
     * Sets the Worker ID on the desk
     * @param idWorker The ID of the worker who is sitting on the desk
     */
    public void setWorker(int idWorker){
        this.iDWorker = idWorker;
    }
    /**
     * Returns the ID of the worker that is on the desk
     * If the number returned is -1, it means there is no worker on the desk
     * @return The ID of the worker or -1 if there is no worker
     */
    public int getWorker(){
        return this.iDWorker;
    }
    
}
