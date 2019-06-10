package chandana;

/**
 *  @author Bala Krishna Chandana 
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;



public class AuctionServer
{
	/**
	 * Singleton: the following code makes the server a Singleton. You should
	 * not edit the code in the following noted section.
	 * 
	 * For test purposes, we made the constructor protected. 
	 */

	/* Singleton: Begin code that you SHOULD NOT CHANGE! */
	protected AuctionServer()
	{
	}

	private static AuctionServer instance = new AuctionServer();

	public static AuctionServer getInstance()
	{
		return instance;
	}

	/* Singleton: End code that you SHOULD NOT CHANGE! */





	/* Statistic variables and server constants: Begin code you should likely leave alone. */


	/**
	 * Server statistic variables and access methods:
	 */
	private int soldItemsCount = 0;
	private int revenue = 0;

	public int soldItemsCount()
	{
		// TODO: IMPLEMENT CODE HERE
		
		 // new array
		 // Loop through items And IDs
		 // 
		 // 	If bidding is completed AND item is in the highestBids
		 //     Store it to new array
		 // 
		 // return soldItems

		List<Item> soldItems =new ArrayList<Item>();
		for(int item: itemsAndIDs.keySet()) {
			if(!itemsAndIDs.get(item).biddingOpen() && highestBids.containsKey(item)) {
				soldItems.add(itemsAndIDs.get(item));
				soldItemsCount ++;
			}
		}
		return this.soldItemsCount;
	}

	public int revenue()
	{
		// TODO: IMPLEMENT CODE HERE
		// Iterate through sold items from soldItemsCount method
		// 		add highestBidAmount to revenue
		// return revenue
		for(int item: itemsAndIDs.keySet()) {
			if(!itemsAndIDs.get(item).biddingOpen() && highestBids.containsKey(item)) {
				revenue = revenue + highestBids.get(item);
			}
		}

		return this.revenue;
	}



	/**
	 * Server restriction constants:
	 */
	public static final int maxBidCount = 10; // The maximum number of bids at any given time for a buyer.
	public static final int maxitemsofSeller = 20; // The maximum number of items that a seller can submit at any given time.
	public static final int serverCapacity = 80; // The maximum number of active items at a given time.


	/* Statistic variables and server constants: End code you should likely leave alone. */



	/**
	 * Some variables we think will be of potential use as you implement the server...
	 */

	// List of items currently up for bidding (will eventually remove things that have expired).
	private List<Item> itemsUpForBidding = new ArrayList<Item>();


	// The last value used as a listing ID.  We'll assume the first thing added gets a listing ID of 0.
	private int lastListingID = -1; 

	// List of item IDs and actual items.  This is a running list with everything ever added to the auction.
	private HashMap<Integer, Item> itemsAndIDs = new HashMap<Integer, Item>();

	// List of itemIDs and the highest bid for each item.  This is a running list with everything ever added to the auction.
	private HashMap<Integer, Integer> highestBids = new HashMap<Integer, Integer>();

	// List of itemIDs and the person who made the highest bid for each item.   This is a running list with everything ever bid upon.
	private HashMap<Integer, String> highestBidders = new HashMap<Integer, String>(); 
	
	// List of itemIDs and seller .
	private HashMap<String,Integer> itemsofSeller = new HashMap<String,Integer>();
	
	private HashMap<String,Integer> UnSoldItemsPerSeller = new HashMap<String,Integer>();


	// List of sellers and how many items they have currently up for bidding.
	private HashMap<String, Integer> itemsPerSeller = new HashMap<String, Integer>();

	// List of buyers and how many items on which they are currently bidding.
	private HashMap<String, Integer> itemsPerBuyer = new HashMap<String, Integer>();

	private List<String> sellersBlackListed = new ArrayList<String>();

	// Object used for instance synchronization if you need to do it at some point 
	// since as a good practice we don't use synchronized (this) if we are doing internal
	// synchronization.
	//
	private Object instanceLock1 = new Object();
	private Object instanceLock2 = new Object();
	private Object instanceLock3 = new Object();
	



	
	
	



	/*
	 *  The code from this point forward can and should be changed to correctly and safely 
	 *  implement the methods as needed to create a working multi-threaded server for the 
	 *  system.  If you need to add Object instances here to use for locking, place a comment
	 *  with them saying what they represent.  Note that if they just represent one structure
	 *  then you should probably be using that structure's intrinsic lock.
	 */


	/**
	 * Attempt to submit an <code>Item</code> to the auction
	 * @param sellerName Name of the <code>Seller</code>
	 * @param itemName Name of the <code>Item</code>
	 * @param lowestBiddingPrice Opening price
	 * @param biddingDurationMs Bidding duration in milliseconds
	 * @return A positive, unique listing ID if the <code>Item</code> listed successfully, otherwise -1
	 */

	 /** 
	  * @param instanceLock1 
	 * @INVARIANT : 1) Item price must not expire
	  *				 2) Items should not expire
	  * @PRECONDITION: 1) itemsofSeller should be less than max sellers
	  *					2)itemsUpForBidding should be less then serverCapacity
	  *					3)sellerName should not be in blacklist
	  *					
	  * @POSTCONDITION : 1) The item is submitted successfully and returns unique listingID
	  *  @EXCEPTION :    1) It throws IllegalArgumentException for the Pre-conditions 1) and 2)
	  						    
	 */
	 
	public int submitItem(String sellerName, String itemName, int lowestBiddingPrice, int biddingDurationMs)
	{

		// TODO: IMPLEMENT CODE HERE
		// Some reminders:
		//   Make sure there's room in the auction site.
		//   If the seller is a new one, add them to the list of sellers.
		//   If the seller has too many items up for bidding, don't let them add this one.
		//   Don't forget to increment the number of things the seller has currently listed.

		// Validate sellerName, biddingDurationMs, lowestBiddingPrice, itemName
		// Check for server capacity
		//		check for sellers max capacity
		// 			increment unique id and create new item object with variables
		//			add item object and listingID to itemsAndID
		//			add item to itemsupforbidding
		//			
		//			Check whether a seller exists or a seller is new in itemsperseller
		//			increment itemsperseller accordingly or create new seller and add
		//return listingID

				if((itemsUpForBidding.size() > serverCapacity)) {
					return -1;
				}			
				if(itemsPerSeller.containsKey(sellerName) && itemsPerSeller!=null ) {
					if(itemsPerSeller.get(sellerName) >= maxitemsofSeller || sellersBlackListed.contains(sellerName)) {
						return -1;
					}else {	
						if(lowestBiddingPrice < 5) {
							if(itemsofSeller.containsKey(sellerName)) {
								int sellercurrentlevel = itemsofSeller.get(sellerName);
								if(sellercurrentlevel == 2) {
									sellersBlackListed.add(sellerName);
									//System.out.println("Seller "+sellerName+" is added to blacklist");
									return -1;
								}
							}	
						}else {
							if(itemsofSeller.containsKey(sellerName)) {
								itemsofSeller.put(sellerName,0);
							}
						}
						
						
						synchronized (instanceLock1) {
							lastListingID++;
							
						}
						
						Item newitem = new Item(sellerName, itemName, lastListingID, lowestBiddingPrice, biddingDurationMs);
						
						synchronized (instanceLock1) {
							itemsUpForBidding.add(newitem);
						}
						synchronized (instanceLock1) {
							
						
							itemsAndIDs.put(lastListingID,newitem);
						}	
						
						
						int sellers = itemsPerSeller.get(sellerName);
						sellers++;
						
						synchronized (instanceLock2) {
							itemsPerSeller.put(sellerName,sellers);
							
							if(lowestBiddingPrice < 5) {
								if(itemsofSeller.containsKey(sellerName)) {
									int slcount = itemsofSeller.get(sellerName);
									slcount++;
									itemsofSeller.put(sellerName, slcount);
								}else {
									itemsofSeller.put(sellerName, 1);
								}
							}					
						}
						return lastListingID;
					}
				}else {
					synchronized (instanceLock1) {
						lastListingID++;
					}
					
					Item newitem = new Item(sellerName, itemName, lastListingID, lowestBiddingPrice, biddingDurationMs);
					
					synchronized (instanceLock1) {
						itemsUpForBidding.add(newitem);
					}
					synchronized (instanceLock1) {
						
					
						itemsAndIDs.put(lastListingID,newitem);
					}
					
					synchronized (instanceLock2) {
						itemsPerSeller.put(sellerName, 1);
						
						if(lowestBiddingPrice < 10) {
							if(itemsofSeller.containsKey(sellerName)) {
							int slcount = itemsofSeller.get(sellerName);
							slcount++;
							itemsofSeller.put(sellerName, slcount);
							}else {
								itemsofSeller.put(sellerName, 1);
							}
						}
					}
					
					return lastListingID;
				}
				
	}



	/**
	 * Get all <code>Items</code> active in the auction
	 * @return A copy of the <code>List</code> of <code>Items</code>
	 */

	 /**
	 * @INVARIENTS  : 	 1) Check whether item is active
	 * 				 	 
	 * @PRECONDITION :  1) itemsUpForBidding should not be NULL
	 * 					 
	 * 
	 * @POSTCONDITION : 1) The items in the list should be returned
	 * 
	 * @EXCEPTION :       1) It throws NullPointerException for the Pre-condition 
	 *  					
	 */
	public List<Item> getItems()
	{
		// TODO: IMPLEMENT CODE HERE
		// Some reminders:
		//    Don't forget that whatever you return is now outside of your control.

		// check for itemsupforbidding size greater than zero
		//	Create new Arraylist
		//		Iterate through itemsUpforbidding
		//		check if bidding is open
		//			copy to new arraylist
		//	return the created array list
		
		ArrayList<Item> returnitems = new ArrayList<Item>();
		
		synchronized (instanceLock1) {
			for(Iterator<Item> it = itemsUpForBidding.iterator(); it.hasNext();) {
				Item item = it.next();
				if(item.biddingOpen())
					returnitems.add(item);
			}
		}
		
		return returnitems;
		
	}


	/**
	 * Attempt to submit a bid for an <code>Item</code>
	 * @param bidderName Name of the <code>Bidder</code>
	 * @param listingID Unique ID of the <code>Item</code>
	 * @param biddingAmount Total amount to bid
	 * @return True if successfully bid, false otherwise
	 */

	 /**
	 * @INVARIENTS  : 	 1) BidAmount must be positive
	 *
	 * @POSTCONDITION : 1) The item is successfully bided
	 *
	 * 	this method is synchronized to allow only one bid submission at a time				
	 */
	public boolean submitBid(String bidderName, int listingID, int biddingAmount)
	{
		// TODO: IMPLEMENT CODE HERE
		// Some reminders:
		//   See if the item exists.
		//   See if it can be bid upon.
		//   See if this bidder has too many items in their bidding list.
		//   Get current bidding info.
		//   See if they already hold the highest bid.
		//   See if the new bid isn't better than the existing/opening bid floor.
		//   Decrement the former winning bidder's count
		//   Put your bid in place

		// Validate biddeName, listingID, biddingAmount
		// Get item using itemsAndIDs by listingID
		//	Check if item is not null 
		//  AND Check if itemsUpForBidding contains item
		//  AND Check if the item is open for bidding 
		// 	AND Check if (itemsPerBuyer does not contain bidderName  OR Check if the maxBidCount is greater than items count in itemsPerBuyer )
		// 	AND Check if (highestBidders don't have listingID OR bidderAmount is greater than the highestBids)
		//
		//	Get the previous bid value
		//		Check if that Bid is not null and itemsPerBuyer has that Bidder name
		// 
		// 					Decrement the item count from the Bidder
		//
		// 				Update the highestBidders with current bidderName
		// 
		// 				Update the highestBids with biddingAmount
		// 
		// 				Check if itemsPerBuyer has the bidderName
		// 					Increment the item count for the bidder
		// 
		// 				ELSE
		// 
		// 					Add the bidderName to the itemsPerBuyer
		if(itemsPerBuyer.containsKey(bidderName)) {
			
			if(itemsPerBuyer.get(bidderName) >= maxBidCount) {
				return false;
			}
		}
		if(itemsAndIDs.containsKey(listingID)) {
		if(!itemsAndIDs.get(listingID).biddingOpen()) {
			return false;
			}
		}
		
		if(itemsPerBuyer.containsKey(bidderName)) {
			if(highestBidders.containsKey(listingID)) {
				if(highestBidders.get(listingID).equals(bidderName)) {
					return false;
				}
				if(highestBids.containsKey(listingID)) {
					int presentHighestBid = highestBids.get(listingID);
					if(presentHighestBid < biddingAmount) {
						highestBids.put(listingID, biddingAmount);
						String lastBidder = highestBidders.get(listingID);
						if(itemsPerBuyer.containsKey(lastBidder)){
							int prevBidCount = itemsPerBuyer.get(lastBidder);
							highestBidders.put(listingID, bidderName);
							prevBidCount--;
							itemsPerBuyer.put(lastBidder, prevBidCount);						
						}
						int bidCount = itemsPerBuyer.get(bidderName);
						bidCount++;
						itemsPerBuyer.put(bidderName, bidCount);
						return true;
					}else {
						return false;
					}
				}else {
					highestBids.put(listingID, biddingAmount);
					highestBidders.put(listingID, bidderName);
					int bidCount = itemsPerBuyer.get(bidderName);
					bidCount++;
					itemsPerBuyer.put(bidderName, bidCount);
					return true;
				}			
			}
			
		}else {
			if(highestBids.containsKey(listingID)){
				int highestBid = highestBids.get(listingID);
				if(highestBids.get(listingID) < biddingAmount ){
					highestBids.put(listingID, biddingAmount);
					
					highestBidders.put(listingID, bidderName);

					String lastBidder = highestBidders.get(listingID);
					if(itemsPerBuyer.containsKey(lastBidder)){
						int prevBidCount = itemsPerBuyer.get(lastBidder);
						highestBidders.put(listingID, bidderName);
						prevBidCount--;
						itemsPerBuyer.put(lastBidder, prevBidCount);						
					}
					itemsPerBuyer.put(bidderName, 1);
					return true;

				} else {
					return false;
				}

			} 
			else {
				highestBids.put(listingID, biddingAmount);
				highestBidders.put(listingID, bidderName);
				itemsPerBuyer.put(bidderName, 1);
				return true;
			}
		}
		
		
		return false;
	}

	/**
	 * Check the status of a <code>Bidder</code>'s bid on an <code>Item</code>
	 * @param bidderName Name of <code>Bidder</code>
	 * @param listingID Unique ID of the <code>Item</code>
	 * @return 1 (success) if bid is over and this <code>Bidder</code> has won<br>
	 * 2 (open) if this <code>Item</code> is still up for auction<br>
	 * 3 (failed) If this <code>Bidder</code> did not win or the <code>Item</code> does not exist
	 */

	 /** @INVARIANTS  : 	 1) Item must be present in the file
	 * 				 	 
	 * @PRECONDITION :  1) listingID should be valid
	 * 					 
	 * 
	 * @POSTCONDITION : 1) Checks the status and returns 1 (success),2 (open), 3(failure)
	 * 
	 * @EXCEPTION :       1) It throws IllegalArgumantException for the Pre-condition 1) 
	 * 			
	 * Synchronized so that mean while no bid has submitted to the item		
	 */
	public int checkBidStatus(String bidderName, int listingID)
	{
		// TODO: IMPLEMENT CODE HERE
		// Some reminders:
		//   If the bidding is closed, clean up for that item.
		//     Remove item from the list of things up for bidding.
		//     Decrease the count of items being bid on by the winning bidder if there was any...
		//     Update the number of open bids for this seller
		
		//Validate bidderName and listingID
		// Get item using using listingID
		//	If item is returned and the bidding is open
		//		Get the seller name from item
		//		If highest bidder == bidderName
		//			return 1
		//		else
		//			return 3
		//	else
		//		return 2


		if(itemsAndIDs.containsKey(listingID)) {
			if(!itemsAndIDs.get(listingID).biddingOpen()) {
				
				if(itemsUpForBidding.contains(itemsAndIDs.get(listingID)))
					itemsUpForBidding.remove(itemsAndIDs.get(listingID));
				if(highestBidders.get(listingID).equals(bidderName)) {
					
					int buyers = itemsPerBuyer.get(bidderName);
					buyers--;
					itemsPerBuyer.put(bidderName, buyers);
					String sellername = itemsAndIDs.get(listingID).seller();
					int sellers = itemsPerSeller.get(sellername);
					sellers--;
					itemsPerSeller.put(sellername, sellers);
					
					
					return 1;
				}else {
					if(!highestBids.containsKey(listingID)) {

					String sellername = itemsAndIDs.get(listingID).seller();
					if(UnSoldItemsPerSeller.containsKey(sellername)) {
						if(UnSoldItemsPerSeller.get(sellername) >= 3) {
							sellersBlackListed.add(sellername);
						}else {
							Integer unsoldcount = UnSoldItemsPerSeller.get(sellername);
							unsoldcount++;
							UnSoldItemsPerSeller.put(sellername, unsoldcount);
						}
					}else {
						UnSoldItemsPerSeller.put(sellername, 1);
					}
					}
					
					return 3;
				}
			}else {
				return 2;
			}
		}
		
		
		return 3;
	}

	/**
	 * Check the current bid for an <code>Item</code>
	 * @param listingID Unique ID of the <code>Item</code>
	 * @return The highest bid so far or the opening price if no bid has been made,
	 * -1 if no <code>Item</code> exists
	 */

	 /** 
	 * @INVARIENTS  : 	 1) Item must be present in the file
	 * 				 	 
	 * @PRECONDITION :  1) listingID should be valid
	 * 					 
	 * 
	 * @POSTCONDITION : 1) Returns item price
	 * 
	 * @EXCEPTION :       1) It throws IllegalArgumantException for the Pre-condition 1) 
	 * 					
	 */
	public int itemPrice(int listingID)
	{
		// TODO: IMPLEMENT CODE HERE
		if(highestBids.containsKey(listingID)) 
			return highestBids.get(listingID);
		else {
			for(Item item : getItems()){
				if(item.listingID() == listingID)
					return item.lowestBiddingPrice();			
			}
		}
		
		return -1;
	}

	/**
	 * Check whether an <code>Item</code> has been bid upon yet
	 * @param listingID Unique ID of the <code>Item</code>
	 * @return True if there is no bid or the <code>Item</code> does not exist, false otherwise
	 */

	/**
	 * 
	 */
	public Boolean itemUnbid(int listingID)
	{
		// TODO: IMPLEMENT CODE HEre
		
		if(highestBids.containsKey(listingID)) 
			return false;
		else
			return true;
	}


}
 
