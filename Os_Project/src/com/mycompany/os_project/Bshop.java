/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.os_project;


import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author zakim
 */
public  class Bshop {
    private final AtomicInteger totalHairCuts = new AtomicInteger(0);
	private final AtomicInteger customersLost = new AtomicInteger(0);
	int nchair, noOfBarbers, availableBarbers;
    List<Customer> listCustomer;
    
    Random r = new Random();	 
    
    public Bshop(int noOfBarbers, int noOfChairs){
    
        this.nchair = noOfChairs;														//number of chairs in the waiting room
        listCustomer = new LinkedList<Customer>();						//list to store the arriving customers
        this.noOfBarbers = noOfBarbers;									//initializing the the total number of barbers
        availableBarbers = noOfBarbers;
    }
 
    public  AtomicInteger getTotalHairCuts() {
    	
    	totalHairCuts.get();
    	return totalHairCuts;
    }
    
    public AtomicInteger getCustomerLost() {
    	
    	customersLost.get();
    	return customersLost;
    }
    
    public void cutHair(int barberId)
    {
        Customer customer;
        synchronized (listCustomer) {									//listCustomer is a shared resource so it has been synchronized to avoid any
        															 	//unexpected errors in the list when multiple threads access it
            while(listCustomer.size()==0) {
            
                System.out.println("\nemployee "+barberId+" is waiting "
                		+ "for the customer and sleeps in his chair");
                Working.txt += "\nemployee "+barberId+" is waiting "
                		+ "for the customer and sleeps in his chair\n" ;                
                try {
                
                    listCustomer.wait();								//barber sleeps if there are no customers in the shop
                }
                catch(InterruptedException iex) {
                
                    iex.printStackTrace();
                }
            }
            
            customer = (Customer)((LinkedList<?>)listCustomer).poll();	//takes the first customer from the head of the list for haircut
            
            System.out.println("Customer "+customer.getCustomerId()+
            		" finds the employee asleep and wakes up "
            		+ "the barber "+barberId);
            Working.txt += "\nCustomer "+customer.getCustomerId() + " finds the barber asleep and wakes up the barber " + barberId;
            
        }
        
        int millisDelay=0;
                
        try {
        	
        	availableBarbers--; 										//decreases the count of the available barbers as one of them starts 
        																//cutting hair of the customer and the customer sleeps
            System.out.println("employee "+barberId+" work to solve problem for "+
            		customer.getCustomerId()+ " so customer sleeps");
            Working.txt += "\n\nBarber "+barberId+" work to solve problem for "+
            		customer.getCustomerId()+ " so customer sleeps" ;
            double val = r.nextGaussian() * 2000 + 4000;				//time taken to cut the customer's hair has a mean of 4000 milliseconds and
        	millisDelay = Math.abs((int) Math.round(val));				//and standard deviation of 2000 milliseconds
        	Thread.sleep(millisDelay);
        	
        	System.out.println("\nCompleted serving "+
        			customer.getCustomerId()+" by employee " + 
        			barberId +" in "+millisDelay+ " milliseconds.");
            Working.txt += "\nCompleted serving "+
        			customer.getCustomerId()+" by employee " + 
        			barberId +" in "+millisDelay+ " milliseconds." ;
        	totalHairCuts.incrementAndGet();
            															//exits through the door
            if(listCustomer.size()>0) {									
            	System.out.println("employee "+barberId+					//barber finds a sleeping customer in the waiting room, wakes him up and
            			" wakes up a customer in the "					//and then goes to his chair and sleeps until a customer arrives
            			+ "waiting room");	
                Working.txt += "\nemployee "+barberId+					//barber finds a sleeping customer in the waiting room, wakes him up and
            			" wakes up a customer in the "					//and then goes to his chair and sleeps until a customer arrives
            			+ "waiting room" ;
            }
            
            availableBarbers++;											//barber is available for haircut for the next customer
        }
        catch(InterruptedException iex) {
        
            iex.printStackTrace();
        }
        
    }
 
    public void add(Customer customer) {
    
        System.out.println("\nCustomer "+customer.getCustomerId()+
        		" enters through the entrance door in the the Bank at "
        		+customer.getInTime());
        Working.txt += "\nCustomer "+customer.getCustomerId()+
        		" enters through the entrance door in the the Bank at "
        		+customer.getInTime() ;
 
        synchronized (listCustomer) {
        
            if(listCustomer.size() == nchair) {							//No chairs are available for the customer so the customer leaves the shop
            
                System.out.println("\nNo chair available "
                		+ "for customer "+customer.getCustomerId()+
                		" so customer leaves the Bank");
                Working.txt += "\nNo chair available "
                		+ "for customer "+customer.getCustomerId()+
                		" so customer leaves the Bank" ;
              customersLost.incrementAndGet();
                
                return;
            }
            else if (availableBarbers > 0) {							//If barber is available then the customer wakes up the barber and sits in
            															//the chair
            	((LinkedList<Customer>)listCustomer).offer(customer);
				listCustomer.notify();
			}
            else {														//If barbers are busy and there are chairs in the waiting room then the customer
            															//sits on the chair in the waiting room
            	((LinkedList<Customer>)listCustomer).offer(customer);
                
            	System.out.println("All employee(s) are busy so "+
            			customer.getCustomerId()+
                		" takes a chair in the waiting room");
                 Working.txt += "All employee(s) are busy so "+
            			customer.getCustomerId()+
                		" takes a chair in the waiting room" ;
                if(listCustomer.size()==1)
                    listCustomer.notify();
            }
        }
    }
}
