package chandana;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * 
 * @author Bala Krishna
 */
/**
 * A Machine is used to make a particular Food.  Each Machine makes
 * just one kind of Food.  Each machine has a capacity: it can make
 * that many food items in parallel; if the machine is asked to
 * produce a food item beyond its capacity, the requester blocks.
 * Each food item takes at least item.cookTimeMS milliseconds to
 * produce.
 */
public class Machine {
	public final String machineName;
	public final Food machineFoodType;
	

	//YOUR CODE GOES HERE...

	public static Queue<Food> foodList;
	public static int capacity;

	/**
	 * The constructor takes at least the name of the machine,
	 * the Food item it makes, and its capacity.  You may extend
	 * it with other arguments, if you wish.  Notice that the
	 * constructor currently does nothing with the capacity; you
	 * must add code to make use of this field (and do whatever
	 * initialization etc. you need).
	 */
	public Machine(String nameIn, Food foodIn, int capacityIn) {
		this.machineName = nameIn;
		this.machineFoodType = foodIn;
		
		
		//YOUR CODE GOES HERE...
		this.foodList = new LinkedList<Food>();
		this.capacity = capacityIn;

	}
	
	
	

	

	/**
	 * This method is called by a Cook in order to make the Machine's
	 * food item.  You can extend this method however you like, e.g.,
	 * you can have it take extra parameters or return something other
	 * than Object.  It should block if the machine is currently at full
	 * capacity.  If not, the method should return, so the Cook making
	 * the call can proceed.  You will need to implement some means to
	 * notify the calling Cook when the food item is finished.
	 */
	public void makeFood(Cook c, int ordNum) throws InterruptedException {
		//YOUR CODE GOES HERE...
		
		foodList.add(machineFoodType);
		Thread cookItem = new Thread(new CookAnItem(this,c, ordNum, foodList, machineFoodType));
		cookItem.start();
	}

	//THIS MIGHT BE A USEFUL METHOD TO HAVE AND USE BUT IS JUST ONE IDEA
	private class CookAnItem implements Runnable {
		Machine machine;
		Cook cook;
		int ordNum;
		Queue<Food> itemList;
		Food machineFoodType;
		Simulation sim;
		
		public CookAnItem(Machine machine, Cook cook, int ordNum, Queue<Food> itemList, Food food ) {
			this.cook = cook;
			this.ordNum= ordNum;
			this.machine=machine;
			this.itemList=itemList;
			this.machineFoodType= food;
		}
		
		public void run() {
			try {
				//YOUR CODE GOES HERE...
				Simulation.logEvent(SimulationEvent.machineCookingFood(machine, machineFoodType));
				//Machine takes time to cook food
				Thread.sleep(machineFoodType.cookTimeMS);
				
				synchronized (itemList) {
					Simulation.logEvent(SimulationEvent.machineDoneFood(machine, machineFoodType));
					itemList.remove();
					itemList.notifyAll();
				}
				
				synchronized(cook.currentOrder) {
					cook.currentOrder.add(machineFoodType);
					Simulation.logEvent(SimulationEvent.cookFinishedFood(cook, machineFoodType, ordNum));
					cook.currentOrder.notifyAll();
				}
			} catch(InterruptedException e) { 
				e.printStackTrace();
			}
		}
	}
 

	public String toString() {
		return machineName;
	}
}