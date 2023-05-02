
    package robustcumulativescheduling;
    import java.io.FileNotFoundException;
    import java.io.PrintWriter;
    import java.io.UnsupportedEncodingException;
    import java.util.ArrayList;
    import java.util.Arrays;
    import java.util.Comparator;
    import java.util.List;
    import java.util.Random;
    import java.util.Vector;

    public class createRandomInstances {
        private static List<Task> randomlyGeneratedTasks;
        int numberOfGeneratedTasks;
        private int totalEnergy;
        private final int C;
        Task[] T;
      
        @SuppressWarnings("static-access")
        public createRandomInstances(int horizon, int intendenNumOfTasks
              //  int lowerBoundOfProcessingTimeSelection, int upperBoundOfProcessingTimeSelection
        ) throws FileNotFoundException, UnsupportedEncodingException {
           
            this.randomlyGeneratedTasks = new ArrayList<  >();
            this.totalEnergy = 0;
            //    this.C = getRandomNumberFrom(3 , 10);
            this.C = 30;
            
            do {
              //  int getAFeasibleReleaseTime = getRandomNumberFrom(0 , horizon - 1);
                              int getAFeasibleReleaseTime = 0;

                int getAFeasibleDeadline = getRandomNumberFrom(1 , horizon);
                               // int getAFeasibleDeadline = 150;

                if (getAFeasibleReleaseTime < getAFeasibleDeadline) {
                    // int getAProcessingTime = getRandomNumberFrom(1, getAFeasibleDeadline - getAFeasibleReleaseTime);
                    int getAProcessingTime = getRandomNumberFrom(5, 800);
                    
                    
                    if (getAFeasibleReleaseTime + getAProcessingTime  <= getAFeasibleDeadline) {
                        //  int getADelayValue = getRandomNumberFrom(1 , getAProcessingTime);
                          int getADelayValue = getRandomNumberFrom(0 , (int) Math.floor(0.25 * (getAProcessingTime) + 1));
// (int) Math.floor(0.25 * (processingTimes[s]) + 1)
// int getADelayValue = getRandomNumberFrom(0 , 4);
                        int lowerBoundToChooseHeight = 1;
                        int upperBoundToChooseHeight =  C;
                        if (upperBoundToChooseHeight > lowerBoundToChooseHeight)
                        {
                            // int getAHeight = getRandomNumberFrom(lowerBoundToChooseHeight , upperBoundToChooseHeight);
                          //  int getAHeight = getRandomNumberFrom(1 , 5);
                           int getAHeight = 1;
                           Task L = new Task (getAFeasibleReleaseTime, getAFeasibleDeadline, getAFeasibleDeadline + getADelayValue, getAProcessingTime, getADelayValue, getAHeight);
                            randomlyGeneratedTasks.add(L);
                            totalEnergy += getAHeight * (getAProcessingTime + getADelayValue);
                        }
                    }
                }
            }
             //while (totalEnergy <= 0.8 * (horizon * C));
            while (randomlyGeneratedTasks.size() < intendenNumOfTasks);
            this.numberOfGeneratedTasks = randomlyGeneratedTasks.size();
            this.T = new Task[numberOfGeneratedTasks];
            for (int r = 0; r < numberOfGeneratedTasks; r++)
                T[r] = randomlyGeneratedTasks.get(r);
            PrintWriter writer = new PrintWriter("randomRobustTasks.txt", "UTF-8");
            writer.println("A randomly generated instance!");
            writer.println(numberOfGeneratedTasks + " " + C);
            for (int t = 0; t < numberOfGeneratedTasks; t++)
                writer.println(T[t].earliestStartingTime() + " " + T[t].latestCompletionTime() + " " + T[t].delayedLatestCompletionTime() + " " + T[t].processingTime()+ " " + T[t].allowedDelay() + " " + T[t].height());
            writer.close();
        }

        public List <Task> generatedRandomTasks() {
            return randomlyGeneratedTasks;
        }

        public Task[] arrayOfTasks() {
            return T;
        }

        public int howManyTasks() {
            return numberOfGeneratedTasks;
        }

        public int energy() {
            return totalEnergy;
        }
        public int C() {
            return C;
        }

        public void printTaskks(Task[] T) {
            System.out.println("Your resource has a capacity of " + C);
            for (int j = 0; j < T.length; j++)
                System.out.println("j - > " + j + " " + T[j]);
        }

        public static int getRandomNumberBetween(int min, int max) {
            Random L = new Random();
            int randomNumber = L.nextInt(max - min) + min;
            if (randomNumber == min) {
                // Since the random number is between the min and max values, simply add 1
                return min + 1;
            }
            else {
                return randomNumber;
            }

        }

        public static int getRandomNumberFrom(int min, int max) {

            Random L = new Random();
            //  L.setSeed(max);
            int randomNumber = L.nextInt((max + 1) - min) + min;
            return randomNumber;
        }

        public static int RandomlyGenerateded(int min, int max) {
            Random rand = new Random();
            return rand.nextInt(max) + min;

        }

        public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {

            Task[] T;
                        int H = 15000; //H: max allowed length of horizon

                        int intendenNumOfTasks = 40;
           // int lowerBoundOfProcessingTimeSelection, 
                    //int upperBoundOfProcessingTimeSelection            
                        
                        createRandomInstances B = new createRandomInstances(H, intendenNumOfTasks);
            T = B.arrayOfTasks();
            B.printTaskks(T);
        }
    }