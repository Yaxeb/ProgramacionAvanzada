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
    
}
