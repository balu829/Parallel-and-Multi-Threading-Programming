package chandana;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * This file needs to hold your solver to be tested. 
 * You can alter the class to extend any class that extends MazeSolver.
 * It must have a constructor that takes in a Maze.
 * It must have the solve() method that returns the datatype List<Direction>
 *   which will either be a reference to a list of steps to take or will
 *   be null if the maze cannot be solved.
 */
public class StudentMTMazeSolver extends SkippingMazeSolver
{
	public ExecutorService executorPool;

	public StudentMTMazeSolver(Maze maze)
	{
		super(maze);
	}

	public List<Direction> solve() 
	{
		LinkedList<DepthFirstSearch> tasksList = new LinkedList<DepthFirstSearch>();
		
		List<Direction> result = null;
		
		int processors = Runtime.getRuntime().availableProcessors();
		executorPool = Executors.newFixedThreadPool(processors);
		List<Future<List<Direction>>> futures = new LinkedList<Future<List<Direction>>>();
		long totalChoices = 0;
		try{
			Choice begin = firstChoice(maze.getStart());
			
			int size = begin.choices.size();
			for(int i = 0; i < size; i++){
				//
				Choice currentChoice = follow(begin.at, begin.choices.peek());
				
				tasksList.add(new DepthFirstSearch(currentChoice, begin.choices.pop()));
				
			}
		}catch (SolutionFound e){
			System.out.println("Error caught while looping through the choices: "+ e.getMessage().toString());
		}
		try {
			//Invoking all tasks
			futures = executorPool.invokeAll(tasksList);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		executorPool.shutdown();
		for(Future<List<Direction>> answer : futures){
			try {
				
				if(answer.get() != null){
					result = answer.get();
					
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		for (DepthFirstSearch d : tasksList) {
			totalChoices += d.choiceCount;
		}
		
		System.out.println("No.of Choices to find the path are: " + (totalChoices - 1));

		return result;
	}

	private class DepthFirstSearch implements Callable<List<Direction>>{
		Choice head;
		Direction choiceDir;
		public long choiceCount = 0;
		public DepthFirstSearch(Choice head, Direction choiceDir){
			this.head = head;
			this.choiceDir = choiceDir;
			
		}  

		@Override
		public List<Direction> call() {
			// TODO Auto-generated method stub
			LinkedList<Choice> choices = new LinkedList<Choice>();
			Choice currentChoice;

			try{
				choices.push(this.head);
				
				while(!choices.isEmpty()){
					currentChoice = choices.peek();
					choiceCount++;
					if(currentChoice.isDeadend()){
						
						choices.pop();
						if (!choices.isEmpty()) choices.peek().choices.pop();
						continue;
					}
					choices.push(follow(currentChoice.at, currentChoice.choices.peek()));
				}
				return null;
			}catch (SolutionFound e){
				Iterator<Choice> it = choices.iterator();
	            LinkedList<Direction> solutionPath = new LinkedList<Direction>();
	        
	           
	            while (it.hasNext())
	            {
	            	currentChoice = it.next();
	                solutionPath.push(currentChoice.choices.peek());
	            }
	            solutionPath.push(choiceDir);
	            if (maze.display != null) {
	            	
	            	maze.setColor(maze.getStart(),1);
	            	
	            	markPath(solutionPath, 1);
	            	
	            	maze.display.updateDisplay();
	            }
	            
	            return pathToFullPath(solutionPath);
			}

		}

	}
}
