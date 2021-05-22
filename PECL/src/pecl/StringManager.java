/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pecl;
import hospitalInterface.*;
import java.util.ArrayList;

/**
 *
 * @author Oqueo
 */
public class StringManager {
    Hospital hospital;
    MainWindow window;

    public StringManager(Hospital hospital, MainWindow window) {
        this.hospital = hospital;
        this.window = window;
    }
    
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
