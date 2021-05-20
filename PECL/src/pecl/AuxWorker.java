/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pecl;

/**
 *
 * @author Oqueo
 */
public class AuxWorker extends Thread {
    private int aid;
    private int maximum; // maximum means how many items/patients
                         // it must attend before taking a break
    private int counter; // current counter of items/patients attended
    
    public AuxWorker(int aid, int maximum) {
        this.aid = aid;
        this.maximum = maximum;
        this.counter = 0;
    }
    
    @Override
    public void run(){
        // reception assistant
        if (aid == 1){
            /* atender al usuario, en caso de que no haya ninguno, a dormir...
               si hay alguno, mirar si tiene cita (atributo randomChance)
               subir el contador counter en 1, enviar al paciente a otra cola 
               y anteder al siguiente, 
             */
        } else { // vaccination assistant
            /*
            
            */
        }
    }
    

    public int getAid() {
        return aid;
    }

    public int checkArrivingPatient() {
        
        return 0;
    }
}
