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
    
    /*
     * When the ID is -1, it means that no thread is occupying it
     */
    public void leavePatient(){
        this.iDPatient = -1;
    }
    
    /*
     * When the ID is -1, it means that no thread is occupying it
     */
    public void leaveWorker(){
        this.iDWorker = -1;
    }
    
    
    public int getPatient(){
        return this.iDPatient;
    }
    
    public int getWorker(){
        return this.iDWorker;
    }
    
}