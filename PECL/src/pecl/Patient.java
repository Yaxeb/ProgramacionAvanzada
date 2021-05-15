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
public class Patient extends Thread{
    private int pid;
    private int randomGenetic;

    public Patient(int pid) {
        this.pid = pid;
        this.randomGenetic = (int) (Math.random() * 100);
    }

    public int getPid() {
        return pid;
    }

    public boolean isInfected() {
        return randomGenetic <= 5;
    }
}
