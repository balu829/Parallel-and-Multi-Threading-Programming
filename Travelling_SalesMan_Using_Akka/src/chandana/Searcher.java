package chandana;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.jws.soap.SOAPBinding.Use;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;

/**
 * this actor implements the search for a path that satisfies the project requirements 
 *
 * @author bala krishna
 *
 */
public class Searcher extends UntypedActor {

	 public Searcher() {
	        // TODO
	    }
	    
	    ArrayList<Integer> path = new ArrayList<Integer>();
	    Messages distancePath = new Messages();
	    
	    @Override
	    public void onReceive(Object msg) throws Exception {
	    	

	        if(msg instanceof String) {
	        	if(((String) msg).equalsIgnoreCase("terminate")) {
	        		
	        		System.out.println(getSelf().path().name()+" LOST : "+distancePath.getPath());
	        		
	        		getContext().stop(getSelf());
	        		
	        		System.out.println(getSelf().path().name()+" is Stopped");
	        		
	        	}else {
	        		
                	TSP tsp = new TSP(User.pairwiseMatrix);
	        		
	            	ActorRef sender = this.getSender();
	            	
	            	distancePath = tsp.tspTry();
	            	
	            	sender.tell(distancePath, getSelf());
	        	}
	        	
	        	}
			}

}
