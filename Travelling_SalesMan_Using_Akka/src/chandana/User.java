package chandana;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
//import java.time.Duration;
import java.util.Scanner;
//import java.util.concurrent.Future;

import javax.swing.filechooser.FileNameExtensionFilter;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.pattern.Patterns;
import akka.util.Timeout;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
/**
 * Main class for your estimation actor system.
 *
 * @author bala krishna
 *
 */
public class User {
	
	 public static int startCity = 4;
	 
	 public static int maxPathLength = 0;
	 
	 public static int TIMEOUT_MILLISECONDS = 10000;
	 
	 public static String fileName;
	 
	 public static ActorSystem system;
	 
	 public static double[][] pairwiseMatrix;

	public static void main(String[] args) throws Exception {

		/*
		 * Create the Solver Actor and send it the StartProcessing
		 * message. Once you get back the response, use it to print the result.
		 * Remember, there is only one actor directly under the ActorSystem.
		 * Also, do not forget to shutdown the actorsystem
		 */

		    system = ActorSystem.create("EstimationSystem");
	        Props maProps = Props.create(Solver.class);
	        ActorRef maNode = system.actorOf(maProps);
	        
	        Scanner scan = new Scanner(System.in);
	        
	        System.out.println("Enter Starting City :");
	        int strtNode = scan.nextInt();
	        System.out.println("Please enter maximum length of path :");
	        int fnlVal = scan.nextInt();
	        System.out.println("Please enter file name without (.txt Extension):");
	        fileName = scan.next();
	        startCity = strtNode;
	        maxPathLength = fnlVal;
	        
	        String response="";
	        
	      
			String filename = User.fileName;

            try {
				pairwiseMatrix = readFile(System.getProperty("user.dir")+"\\src\\chandana\\"+filename+".txt");
				//System.out.println(pairwiseMatrix+"");
				int rows= pairwiseMatrix.length;
				int columns = pairwiseMatrix[0].length;
				//System.out.println("Rows"+ rows +", Columns" +columns );
				if(rows!=columns) {
					throw new Exception("Please check the input file. The file should have same no.of rows and columns");
				}
				
				if(User.startCity >= rows || User.startCity >= columns ) {
					throw new Exception("Please check the input. The starting city should be within the range of given cities");
				}
				
				Future<Object> f = Patterns.ask(maNode, "StartProcessing", TIMEOUT_MILLISECONDS);
				Timeout timeout = new Timeout(Duration.create(TIMEOUT_MILLISECONDS / 10, "seconds"));
	             response = (String) Await.result(f, timeout.duration());
	            System.out.printf("So the output is:  %s", response);
			} catch (ArrayIndexOutOfBoundsException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ArithmeticException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				System.out.println(e.getMessage().toString());
			} finally {
	            system.terminate();
	        }
           
	        
	}
	
	
	public static double[][] readFile(String filename) throws IOException, ArithmeticException, ArrayIndexOutOfBoundsException {
        BufferedReader buffer = new BufferedReader(new FileReader(filename));
        double[][] matrix = null;
        String line;
        int row = 0;
        int size = 0;

        while ((line = buffer.readLine()) != null) {
            String[] vals = line.trim().split("\\s+");

            // Lazy instantiation.
            if (matrix == null) {
                size = vals.length;
                matrix = new double[size][size];
            }

            for (int col = 0; col < size; col++) {
                if(Integer.parseInt(vals[col])<0)
                    throw new ArithmeticException();
                matrix[row][col] = Integer.parseInt(vals[col]);
            }

            row++;
        }

        return matrix;

    }

}