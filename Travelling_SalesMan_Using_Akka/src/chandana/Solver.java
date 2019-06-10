package chandana;

import java.io.File;
import java.util.ArrayList;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;

/**
 * This is the main actor and the only actor that is created directly under the
 * {@code ActorSystem} This actor creates 4 child actors
 * {@code Searcher}
 * 
 * @author bala krishna
 *
 */
public class Solver extends UntypedActor {

	  
	    private ActorSystem system;
	    private ArrayList<ActorRef> refList = new ArrayList<ActorRef>();
	    private ActorRef mainActor;
	    private ActorRef agent1;
	    private ActorRef agent2;
	    private ActorRef agent3;
	    private ActorRef agent4;
	    
	    public Solver() {

	    }

	    @Override
	    public void onReceive(Object msg) {
	        ActorRef sender = this.getSender();

	        if (msg instanceof Messages) {
	        	Messages m = (Messages)msg;
	        	if(m.getPathLength() <= User.maxPathLength) {
	        		refList.remove(sender);
	            	for(ActorRef s: refList) {
	            		s.tell("terminate", getSelf());	
	            	}
	                	system.shutdown();
	                	
	                	mainActor.tell(sender.path().name()+" : "+m.getPathLength()+" "+m.getPath() +"\n", sender);
	        	}
	        }


	        if (msg instanceof String) {
	        	if(!((String) msg).equalsIgnoreCase("done")) {
	        		
	        		System.out.println("Creating Actors");
	            	mainActor = this.getSender();
	                system = User.system;
	                
	                
	                Props prop1 = Props.create(Searcher.class);
	                Props prop2 = Props.create(Searcher.class);
	                Props prop3 = Props.create(Searcher.class);
	                Props prop4 = Props.create(Searcher.class);
	                
	                
	                agent1 = system.actorOf(prop1, "agent1");
	                agent2 = system.actorOf(prop2, "agent2");
	                agent3 = system.actorOf(prop3, "agent3");
	                agent4 = system.actorOf(prop4, "agent4");
	                
	                
	                refList.add(agent1);
	                refList.add(agent2);
	                refList.add(agent3);
	                refList.add(agent4);
	                
	                agent1.tell("1", getSelf());
	                agent2.tell("2", getSelf());
	                agent3.tell("3", getSelf());
	                agent4.tell("4", getSelf());
	                
	        	}else {
	        		System.out.println(sender.path().name()+" Has Done");
	        	}

	        }
	    }

}