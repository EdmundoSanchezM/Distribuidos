/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Hilos;

/**
 *
 * @author josue
 */
public class A extends Thread{

    static long n;
    static Object obj = new Object();
    
    public void run(){
        for(int i = 0; i<100000;i++)
            synchronized(obj){
            n++;
        }
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception{
        A t1 = new A();
        A t2 = new A();
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        System.out.println(n);
    }
    
}
