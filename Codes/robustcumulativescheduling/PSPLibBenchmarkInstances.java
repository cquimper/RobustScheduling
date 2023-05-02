package robustcumulativescheduling;

import java.io.File;
import java.util.Scanner;

public class PSPLibBenchmarkInstances {
    public int[] capacities;
    public int numberOfTasks;
    public int numberOfResources;
    public int heights[][];
    public int[] processingTimes;
    public int[][] precedences;
    int processingTimesSum;
    int GreatestProcessingTime = 0;
    
    public PSPLibBenchmarkInstances(String fileName) throws Exception {
        this.GreatestProcessingTime = 0;
        
        Scanner s = new Scanner (new File (fileName)).useDelimiter("\\s+");
        String line = s.nextLine();
        
        while (!line.contains("jobs")) //Nb Tasks
            line = s.nextLine();
        numberOfTasks = Integer.parseInt(line.split(":\\s+")[1]);
  //  System.out.println("numberOfTasks"+numberOfTasks);
        while (!line.contains("renewable")) //Nb ressources
            line = s.nextLine();
        numberOfResources = Integer.parseInt(line.split(":\\s+")[1].split("\\s+")[0]);
       //     System.out.println("numberOfResources"+numberOfResources);

        while (!s.hasNextInt() )
            s.nextLine();
        s.nextLine(); //Project Information (Pas important)
        while (!s.hasNextInt() )
            s.nextLine();
        
        //On initialise le tableau de prÃ©cÃ©dences
        precedences = new int[numberOfTasks][numberOfTasks];
        for(int i=0; i<numberOfTasks; i++)
        {
            for(int j=0; j<numberOfTasks; j++)
            {
                precedences[i][j] = -1;
            }
        }
        
        int nbSuccesseurs;
        for(int i=0; i<numberOfTasks; i++)
        {
            s.nextInt();//jobnr
            s.nextInt();//mode ?
            
            int k;
            nbSuccesseurs = s.nextInt();
            for(int j=0; j<nbSuccesseurs; j++)
            {
                //i < k
                //Le fichier est en base 1
                k = s.nextInt();
                
                if(precedences[i][k-1] == 0 || precedences[k-1][i] == 1 || i == k-1)
                    throw new Exception("Les prÃ©cÃ©dences du problÃ¨me sont incohÃ©rentes.");
                
                precedences[i][k-1] = 1;
                precedences[k-1][i] = 0;
            }
        }
        
        while (!s.hasNextInt() )
            line = s.nextLine();
        
        heights = new int[numberOfResources][numberOfTasks];
        processingTimes = new int[numberOfTasks];
        processingTimesSum = 0;
        for(int i=0; i<numberOfTasks; i++)
        {
            s.nextInt();//jobnr
            s.nextInt();//mode ?
            int temp = s.nextInt();
            processingTimes[i] = temp;
            if (temp > GreatestProcessingTime)
                GreatestProcessingTime = temp;
            
            
            
            
        //    System.out.println(processingTimes[i]);
            processingTimesSum += processingTimes[i];
            
            for(int j=0; j<numberOfResources; j++)
            {
                //Height[j][i] = x => la tÃ¢che i utilise x unitÃ©s sur la ressource j
                heights[j][i] = s.nextInt();
             //    System.out.println(heights[j][i]);
            }
        }
        while (!s.hasNextInt() )
            s.nextLine();
        
        capacities = new int[numberOfResources];
        for(int i=0; i<numberOfResources; i++)
        {
            capacities[i] = s.nextInt();
           //  System.out.println(capacities[i]);
        }
    }
    public int horizon()
    {
        return processingTimesSum;
        
    } public int GreatestProcessingTime() {
        return GreatestProcessingTime;
    }
    
    
    
    
    public int[] capacity()
    {
        
       //   for (int o = 0; o < capacities.length; o++)
           //   System.out.println(capacities[o]);
        
        return capacities;
    }
    
    
    public int numberOfResources() {
       //  System.out.println(numberOfResources);
        return numberOfResources;
    }
    
    public int numberOfTasks() {
      // System.out.println(numberOfTasks);
        return numberOfTasks;
    }
    
    
    public int[][] heights() {
        int[][] He = new int[numberOfResources][numberOfTasks - 2];
        for (int q1 = 1; q1 < numberOfTasks - 1; q1++) {
            for (int q2 = 0; q2 < numberOfResources ; q2++) {
                He[q2][q1 - 1] = heights[q2][q1];
                //  System.out.println("height of task " + (q1 - 1) + " over resource " + q2 + " is " + He[q2][q1 - 1]);
            }
        }
        return He;
    }
    
   public int[] processingTimes() {
        int[] Pr = new int[numberOfTasks - 2];
        for (int q = 1; q < numberOfTasks - 1 ; q++) {
            Pr[q - 1] = processingTimes[q];
            //  System.out.println("processing Time of task " + (q - 1) + " is " + Pr[q-1]);
            
            
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
             //   System.out.println("task " + i + "  with " + j + " " + precedences[i][j]);
            }
        }

        return Prece;
    }
   
   
}

