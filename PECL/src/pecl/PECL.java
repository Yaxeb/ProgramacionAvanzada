package pecl;
import hospitalInterface.*;
import java.util.ArrayList;
public class PECL {

    public static void main(String[] args) {
        // TODO code application logic here
        MainWindow window = new MainWindow();
        window.setVisible(true);
        Reception reception = new Reception();
        VaccRoom vaccRoom = new VaccRoom();
        ObservationRoom obsRoom = new ObservationRoom();
        Hospital hospital = new Hospital(reception, vaccRoom, obsRoom, window);
        AuxWorker a1 = new AuxWorker(1,10,hospital);
        hospital.getReception().setAuxWorker(a1);
        hospital.startWindow();
        a1.start();
        AuxWorker a2 = new AuxWorker(2,20,hospital);
        hospital.getVaccRoom().setAuxWorker(a2);
        a2.start();
        ArrayList<HcareWorker> l = new ArrayList<>();
        for (int i = 1; i<= 10; i++)
        {
            HcareWorker worker = new HcareWorker(i, 0,hospital);
            hospital.addWorker(worker);
            worker.start();
        }
        for(int i = 1; i <= 100; i++)
        {
            Patient patient = new Patient(i, hospital);
            patient.start();
        }
    }
    
}
