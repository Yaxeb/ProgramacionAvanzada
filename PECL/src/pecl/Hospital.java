package pecl;

import java.util.concurrent.atomic.AtomicInteger;

public class Hospital {
    private final Reception reception;
    private final VaccRoom vaccRoom;
    private final AtomicInteger capacity;
    
    public Hospital(Reception reception, VaccRoom vaccRoom) {
        this.capacity = new AtomicInteger();
        this.reception = reception;
        this.vaccRoom = vaccRoom;
    }
    
    public void enterHospital(Patient patient){
        capacity.set(capacity.addAndGet(1));
        
        // queda hacer las comprobaciones y mandarle a dormir        
        // quitarlo de la lista? 
    }
    
    public int enterReception(Patient patient, AuxWorker aWorker){
        /*
        hablar con el asistente y mirar si está en lista...
        Si está en lista se va a la enteringQ y sigue normal, return 1 al final else return 0 . 
        Una vez despierto, obtener el numero de mesa al que ir. 
        Salir de la recepcion.
        */
        //while (semaforoEntrarMesas.acquire() ){
            
        //}
        //aworker.tellMeTheDesk()
        
        return 1;   
    }
    
    
    public void enterVaccRoom(Patient patient){
        /*
        ir de golpe a la mesa la que tenga que ir. (sentarse)
        Comunicarse con el trabajador de cuanto tiempo va a 
        durar la vacuna, esperar conjuntamente...
        comprobar si hay sitio en el observationRoom, si no,
        esperar a que haya un hueco.
        salir de la mesa
        salir de la vacc room
        */
    }
    
    
    public void enterObservationRoom(Patient patient){
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
    
    
    
}
