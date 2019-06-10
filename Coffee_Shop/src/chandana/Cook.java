package chandana;

import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * 
 * @author Bala Krishna
 */

/**
 * Cooks are Simulation actors that have at least one field, a name.
 * When running, a cook attempts to retrieve outstanding orders placed
 * by Eaters and process them.
 */
public class Cook implements Runnable {
	private final String name;
	public Customer currentCustomer;
	public List<Food> currentOrder = new LinkedList<Food>();
	//private static Simulationulation Simulation;
	//private static SimulationulationEvent SimulationEvent;

	/**
	 * You can feel free modify this constructor.  It must
	 * take at least the name, but may take other parameters
	 * if you would find adding them useful. 
	 *
	 * @param: the name of the cook
	 */
	public Cook(String name) {
		this.name = name;
	}

	public String toString() {
		return name;
	}

	/**
	 * This method executes as follows.  The cook tries to retrieve
	 * orders placed by Customers.  For each order, a List<Food>, the
	 * cook submits each Food item in the List to an appropriate
	 * Machine, by calling makeFood().  Once all machines have
	 * produced the desired Food, the order is complete, and the Customer
	 * is notified.  The cook can then go to process the next order.
	 * If during its execution the cook is interrupted (i.e., some
	 * other thread calls the interrupt() method on it, which could
	 * raise InterruptedException if the cook is blocking), then it
	 * terminates.
	 */
	public void run() {

		Simulation.logEvent(SimulationEvent.cookStarting(this));
		try {
			while(true) {
				//YOUR CODE GOES HERE...
				synchronized (Simulation.allOrders) {
					while(Simulation.allOrders.isEmpty()){
						Simulation.allOrders.wait();
					}
					
					currentCustomer = getCustomerPriority(Simulation.allOrders);
					
					Simulation.logEvent(SimulationEvent.cookReceivedOrder(this, currentCustomer.getOrder(), currentCustomer.getOrderNum()));
					Simulation.allOrders.notifyAll();
				}
				
				for(int i= 0; i<currentCustomer.getOrder().size(); i++ ) {
					
					Food currItem = currentCustomer.getOrder().get(i);
					
					switch(currItem.toString()) {
					
					case "burger":
						synchronized (Simulation.grill.foodList) {
							while(!(Simulation.grill.foodList.size() < Simulation.grill.capacity)) {
								Simulation.grill.foodList.wait();
							}
							
							Simulation.grill.makeFood(this, currentCustomer.getOrderNum());;
							Simulation.logEvent(SimulationEvent.cookStartedFood(this, currItem, currentCustomer.getOrderNum()));
							Simulation.grill.foodList.notifyAll();
						}
						break;
					case "fries":
						synchronized (Simulation.frier.foodList) {
							while(!(Simulation.frier.foodList.size() < Simulation.frier.capacity)) {
								Simulation.frier.foodList.wait();
							}
							
							Simulation.frier.makeFood(this, currentCustomer.getOrderNum());;
							Simulation.logEvent(SimulationEvent.cookStartedFood(this, currItem, currentCustomer.getOrderNum()));
							Simulation.frier.foodList.notifyAll();
						}
						break;
					default:
						synchronized (Simulation.star.foodList) {
							while(!(Simulation.star.foodList.size() < Simulation.star.capacity)) {
								Simulation.star.foodList.wait();
							}
							
							Simulation.star.makeFood(this, currentCustomer.getOrderNum());;
							Simulation.logEvent(SimulationEvent.cookStartedFood(this, currItem, currentCustomer.getOrderNum()));
							Simulation.star.foodList.notifyAll();
						}
					
					}
				}
				
				synchronized (currentOrder) {
					while(currentOrder.size() != currentCustomer.getOrder().size()) {
						currentOrder.wait();
					}
					Simulation.logEvent(SimulationEvent.cookCompletedOrder(this, currentCustomer.getOrderNum()));
					currentOrder.notifyAll();
				}
				
				synchronized (Simulation.orderStatus) {
					Simulation.orderStatus.put(currentCustomer, true);
					Simulation.orderStatus.notifyAll();					
				}
				
				currentOrder = new LinkedList<Food>();
				
			}
		}
		catch(InterruptedException e) {
			// This code assumes the provided code in the Simulation class
			// that interrupts each cook thread when all customers are done.
			// You might need to change this if you change how things are
			// done in the Simulation class.
			Simulation.logEvent(SimulationEvent.cookEnding(this));
		}
	}
	
	private Customer getCustomerPriority(Queue<Customer> clist) {
		
		for(Customer cust: clist) {
			if(cust.getPriority()==1) {
				Customer c = cust;
				clist.remove(cust);
				return c;
			}
		}
		
		for(Customer cust: clist) {
			if(cust.getPriority()==2) {
				Customer c = cust;
				clist.remove(cust);
				return c;
			}
		}
		
		for(Customer cust: clist) {
			if(cust.getPriority()==3) {
				Customer c = cust;
				clist.remove(cust);
				return c;
			}
		}
		
		return null;
		
	}
}