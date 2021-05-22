package pecl;
import hospitalInterface.*;
import java.util.ArrayList;

public class StringManager extends Thread{
    Hospital hospital;
    MainWindow window;
    /**
     * Constructor method for StringManager
     * @param hospital The hospital which information is displayed
     * @param window The window to display the information
     */
    public StringManager(Hospital hospital, MainWindow window) {
        this.hospital = hospital;
        this.window = window;
    }
    
    @Override
    public void run(){
        while(true){
            textSetter();
        }
    }
    /**
     * Method that updates the interface. It sequentially updates all parameters
     */
    public void textSetter(){
        //Reception
        window.getReception().setText(hospital.getReception().allPatientsToString()); // Reception
        window.getReceptionPatient().setText("P" + String.format("%04d", hospital.getReception().getWaitingQueue().get(0).getPid())); //Patient
        if (hospital.getReception().getAuxWorker().isResting()) //Auxiliary
        {
            window.getReceptionAux().setText("");
        }
        else
        {
            window.getReceptionAux().setText("A"+hospital.getReception().getAuxWorker().getAid());
        }
        //Restroom
        window.getRestRoom().setText(hospital.restRoomToString());
        //Vaccination Room
        ArrayList<javax.swing.JTextArea> vDesks = window.getVDesks();
        for(int i = 0;i<vDesks.size();i++)
        {
            vDesks.get(i).setText(hospital.getVaccRoom().getDesks().get(i).toString());
        }
        window.getVaccines().setText(""+hospital.getVaccRoom().getVaccines()); //Vaccines
        if (hospital.getVaccRoom().getAuxWorker().isResting()) //Auxiliary
        {
            window.getVaccRoomAux().setText("");
        }
        else
        {
            window.getVaccRoomAux().setText("A"+hospital.getVaccRoom().getAuxWorker().getAid());
        }
        //Observation Room
        ArrayList<javax.swing.JTextArea> oDesks = window.getODesks();
        for(int i = 0;i<oDesks.size();i++)
        {
            oDesks.get(i).setText(hospital.getObsRoom().getDesks().get(i).toString());
        }
        
    }
}
