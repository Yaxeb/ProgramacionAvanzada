package pecl;

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
               y anteder al siguiente paciente.
               sincronizando una arrayList, comprueba la lista de mesas despues de que atienda
               a un paciente y antes de que se vaya a dormir (descanso)
               si hay un hueco libre, despierta a un paciente y le pasa a que mesa ir.
               cuando atiende a 10 usuarios se toma un descanso de 3-5 segundos
             */
        } else { // vaccination assistant
            /* crea vacunas con un intervalo de 0.5-1 segundos
               cuando llega a 20 vacunas se toma un descanso de 1-4 segundos
               vuelve al trabajo
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
