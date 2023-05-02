package robustcumulativescheduling;

import java.io.FileNotFoundException;
import java.util.Formatter;
import java.util.FormatterClosedException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.lang.SecurityException;
import java.util.Formatter;
import java.util.FormatterClosedException;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Scanner;
/**
 *
 * @author hamed
 */
public class createCAPRandomInstances {
    
private static Formatter output; // outputs text to a file
    public static void main(String[] args) {
        openTextFile();
        addTextRecords();
        closeTextFile();    }
    public static void openTextFile() {
        try {
            output = new Formatter("generatedCAPBenchmark.txt"); // open the file
        } catch (SecurityException securityException) {
            System.err.println("Write permission denied. Terminating.");
            System.exit(1); // terminate the program}
        } catch (FileNotFoundException fileNotFoundException) {
            System.err.println("Error opening file. Terminating.");
            System.exit(1); // terminate the program
        }
    }
    public static void addTextRecords() {
        Scanner input = new Scanner(System.in);
        Random randomNumbers = new Random();
               int k = 61;
        long seedValue = 0;
        int max_k = 81;
        while (k < max_k) // loop until end-of-file indicator
        {
          if (System.currentTimeMillis() != seedValue){
            seedValue  =  System.currentTimeMillis();
        //   int numberOfActivities = 12 + randomNumbers.nextInt(6);
                      int numberOfActivities = 14;
        
           int min_p = 1;
int max_p = 12 + randomNumbers.nextInt(4);
            try {                
                output.format("%d %d %d %d %d %s%n", k, numberOfActivities, 2, min_p, max_p, 
                        seedValue);
            }
            catch (FormatterClosedException formatterClosedException) {
                System.err.println("Error writing to file. Terminating.");
              //  break;
            }
            catch (NoSuchElementException elementException) {
                System.err.println("Invalid input. Please try again.");
             //   input.nextLine(); // discard input so user can try again
            }
          }
          else
              continue;
      ++k; 
        }
            }
    
    public static void closeTextFile() {
        if (output != null)
            output.close();
    }
}

