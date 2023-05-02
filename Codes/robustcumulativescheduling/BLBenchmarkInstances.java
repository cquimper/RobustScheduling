package robustcumulativescheduling;

import java.io.File;
import java.util.Scanner;

public class BLBenchmarkInstances {
    private final int[] capacities;
    private final int numberOfTasks;
    private final int numberOfResources;
    private final int heights[][];
    private final int[] processingTimes;
    private final int[][] precedences;
    private int processingTimesSum;
    private int GreatestProcessingTime;
    
    public BLBenchmarkInstances(String fileName) throws Exception {               
        this.GreatestProcessingTime = 0;       
        Scanner s = new Scanner (new File (fileName)).useDelimiter("\\s+");
        while (!s.hasNextInt() )
            s.nextLine();
        this.numberOfTasks = s.nextInt();        
        this.numberOfResources = s.nextInt();        
        this.capacities = new int[numberOfResources];
        for(int i=0; i<numberOfResources; i++)
        {
            capacities[i] = s.nextInt();
        }        
        this.precedences = new int[numberOfTasks][numberOfTasks];        
        for(int i=0; i<numberOfTasks; i++)
        {
            for(int j=0; j<numberOfTasks; j++)
            {
                precedences[i][j] = -1;
            }
        }
        this.heights = new int[numberOfResources][numberOfTasks];
        this.processingTimes = new int[numberOfTasks];
        this.processingTimesSum = 0;        
        int nbSuccesseurs;
        for(int i=0; i<numberOfTasks; i++)
        {
            int temp = s.nextInt();
            processingTimes[i] = temp;
            if (temp > GreatestProcessingTime)
                GreatestProcessingTime = temp;            
            processingTimesSum += processingTimes[i];            
            for(int j=0; j<numberOfResources; j++) {
                //Height[j][i] = x => la tÃ¢che i utilise x unitÃ©s sur la ressource j
                heights[j][i] = s.nextInt();                
            }
            int k;
            nbSuccesseurs = s.nextInt();
            for(int j=0; j<nbSuccesseurs; j++)
            {
                //i < k
                //Le fichier est en base 1
                k = s.nextInt();                
                if(precedences[i][k-1] == 0 || precedences[k-1][i] == 1 || i == k-1)
                    throw new Exception("The precedence of the problem are inconsistent");                
                precedences[i][k-1] = 1;
                precedences[k-1][i] = 0;
            }
        } 
    }
    
    public int horizon()
    {
        return processingTimesSum;
    }
        
    public int GreatestProcessingTime() {
        return GreatestProcessingTime;
    }
    
    public int[] capacity()
    {        
        return capacities;
    }
        
    public int numberOfResources() {
        
        return numberOfResources;
    }
    
    public int numberOfTasks() {
        
        return numberOfTasks;
    }
        
    public int[][] heights() {
        int[][] He = new int[numberOfResources][numberOfTasks - 2];
        for (int q1 = 1; q1 < numberOfTasks - 1; q1++) {
            for (int q2 = 0; q2 < numberOfResources ; q2++) {
                He[q2][q1 - 1] = heights[q2][q1];
            }
        }
        return He;
    }
    
    public int[] processingTimes() {
        int[] Pr = new int[numberOfTasks - 2];
        for (int q = 1; q < numberOfTasks - 1 ; q++) {
            Pr[q - 1] = processingTimes[q];                        
        }
        return Pr;
    }
        
    public int[][] precedences() {        
        int[][] Prece = new int[numberOfTasks - 2][numberOfTasks - 2];
        for (int i = 0; i < numberOfTasks - 2; i++) {
            for (int j = 0; j < numberOfTasks - 2; j++) {
                Prece[i][j] = -1;
            }
        }    
        for (int i = 1; i < numberOfTasks - 1; i++ ) {
            for (int j = 1; j < numberOfTasks - 1; j++ ) {
                if (precedences[i][j] == 1)
                    Prece[i - 1][j - 1] = 1;
            }
        }
        return Prece;
    }
    
    public int processingTimesSum()
    {
        return processingTimesSum;
    }    
}

