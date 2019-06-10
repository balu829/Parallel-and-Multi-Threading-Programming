package chandana;

import java.awt.Event;
import java.util.List;

/**
 * 
 * @author Bala Krishna
 */

/**
 * Customers are simulation actors that have two fields: a name, and a list
 * of Food items that constitute the Customer's order.  When running, an
 * customer attempts to enter the coffee shop (only successful if the
 * coffee shop has a free table), place its order, and then leave the 
 * coffee shop when the order is complete.
 */
public class Customer implements Runnable {
	//JUST ONE SET OF IDEAS ON HOW TO SET THINGS UP...
	private final String name;
	private final List<Food> order;
	private final int orderNum;    
	
	private static int runningCounter = 0;
	
	private final int priority;
	private Simulation sim;
	private SimulationEvent simEvent;
	private final int eatTimeMS;

	/**
	 * You can feel free modify this constructor.  It must take at
	 * least the name and order but may take other parameters if you
	 * would find adding them useful.
	 */
	public Customer(String name, List<Food> order, int priority, int eatTimeMS) {
		this.name = name;
		this.order = order;
		this.priority= priority;
		this.eatTimeMS=eatTimeMS;
		this.orderNum = ++runningCounter;
	}
	
	

	public int getPriority() {
		return priority;
	}



	public List<Food> getOrder() {
		return order;
	}



	public int getOrderNum() {
		return orderNum;
	}



	public String toString() {
		return name;
	}

	/** 
	 * This method defines what an Customer does: The customer attempts to
	 * enter the coffee shop (only successful when the coffee shop has a
	 * free table), place its order, and then leave the coffee shop
	 * when the order is complete.
	 */
	public void run() {
		//YOUR CODE GOES HERE...
		
		//Customer Starts
		sim.logEvent(simEvent.customerStarting(this));
		
		//Checking entry conditions
		synchronized (sim.currentCapacity) {
			while(!(sim.currentCapacity.size() < sim.events.get(0).simParams[2])) {
				try {
					sim.currentCapacity.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			sim.currentCapacity.add(this);
			sim.logEvent(simEvent.customerEnteredCoffeeShop(this));
			sim.currentCapacity.notifyAll();
			
		}
		
		// Customer places order
		synchronized (sim.allOrders) {
			sim.allOrders.add(this);
			sim.logEvent(simEvent.customerPlacedOrder(this, this.order, this.orderNum));
			System.out.println(this +"'s priority is" + this.priority);
			sim.allOrders.notifyAll();
			
		}
		
		synchronized (sim.orderStatus) {
			sim.orderStatus.put(this, false);
			sim.orderStatus.notifyAll();
		}
		
		//Customer receives order
		synchronized (sim.orderStatus) {
			while(!sim.orderStatus.get(this)) {
				try {
					sim.orderStatus.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			sim.logEvent(simEvent.customerReceivedOrder(this, this.order, this.orderNum));
			sim.orderStatus.notifyAll();
			try {
				System.out.println("The eat time of "+ this + " is " + this.eatTimeMS + "MS");
				Thread.sleep(this.eatTimeMS);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		
		//Customer Leaves shop
		synchronized (sim.currentCapacity) {
			sim.currentCapacity.remove();
			sim.logEvent(simEvent.customerLeavingCoffeeShop(this));
			sim.currentCapacity.notifyAll();
			
		}
		
		
	}
	
}