package robustcumulativescheduling;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import static robustcumulativescheduling.createRandomInstances.getRandomNumberFrom;

/**
 *
 * @author hamedfahimi
 */
public class createRandomBenchmarkTextFiles {
    
    
    
    
    
    public void generateTextFile() throws FileNotFoundException, UnsupportedEncodingException {
      //int l = 1;
     
      for (int l = 0; l < 1; l++) {
      PrintWriter writer = new PrintWriter("newFile" + l + ".txt");
        int n = 10;
        int m = 1;
        int C = 30;
        int[] avDelays = new int[n];

        boolean AveraegIsFour = false;       
                                int itr = 0;
                    while (!AveraegIsFour) {
                        itr++;
                        int sum = 0;
                        for (int u = 0; u < n; u++) {
                            {
                                avDelays[u] = getRandomNumberFrom(1 , 5);
                                sum += avDelays[u];
                            }
                            double average = sum / n;
                            if (average == 4)
                                AveraegIsFour = true;
                        }
                    }
                    System.out.println("iterated " + itr + " times to achieve average = 4 for delay array.");
        
        for (int u = 0; u < n; u++)
        {
            //    avDelays[u] = getRandomNumberFrom(1 , 5);
          //  avDelays[u] = getRandomNumberFrom(1 , 4);
            
        }
        
        int[] soHeights = new int[n];
        for (int o = 0; o < n; o++) {
           // soHeights[o] = getRandomNumberFrom(1 , C);
            soHeights[o] = getRandomNumberFrom(1 , 5);
            
        }
        Arrays.sort(soHeights);
        writer.println(n + " " + m);
        writer.println(C);
        // int getAProcessingTime = getRandomNumberFrom(5, 10);
        //  int getAHeight = getRandomNumberFrom(1 , 5);
        for (int i = n - 1; i >= 0; i--)
            writer.println(getRandomNumberFrom(5, 10) + " " + avDelays[i] + " " + soHeights[i]);
        writer.close();
        
    }
    }
    
    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
        
        createRandomBenchmarkTextFiles F = new createRandomBenchmarkTextFiles();
        F.generateTextFile();
    }
}
