package pecl;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class VaccRoom {
    private final AtomicInteger vaccines = new AtomicInteger(); 
    private final ArrayList<Desk> desks = new ArrayList(10);
    
    
    
    
    
    public int getVaccines(){
        return vaccines.get();
    }
    
    public void createVaccine(){
        vaccines.set(vaccines.addAndGet(1));
    }
    
    
    
    
    
}
