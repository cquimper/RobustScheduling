
    package robustcumulativescheduling;



import java.util.Arrays;
    import java.util.Random;
    import org.chocosolver.solver.ResolutionPolicy;
    import org.chocosolver.solver.Solver;
    import org.chocosolver.solver.constraints.Constraint;
    import org.chocosolver.solver.constraints.ICF;
    import org.chocosolver.solver.constraints.IntConstraintFactory;
    import org.chocosolver.solver.constraints.LogicalConstraintFactory;
    import org.chocosolver.solver.search.strategy.IntStrategyFactory;
    import org.chocosolver.solver.trace.Chatterbox;
    import org.chocosolver.solver.variables.BoolVar;
    import org.chocosolver.solver.variables.IntVar;
    import org.chocosolver.solver.variables.VariableFactory;
    import static robustcumulativescheduling.newModelForFlexConstraint.MODE_EDGEFINDING;
    import static robustcumulativescheduling.newModelForFlexConstraint.MODE_NO_EDGEFINDING;
    import static robustcumulativescheduling.newModelForFlexConstraint.MODE_NO_OVERLOADCHECK;
    import static robustcumulativescheduling.newModelForFlexConstraint.MODE_OVERLOADCHECK;

    public class BenchmarkBLModelAdaptedWithNewFlexModel {
        public static final int MODE_NO_OVERLOADCHECK = 0;
        public static final int MODE_NO_EDGEFINDING = 0;
        public static final int MODE_NO_NOTFIRSTNOTLAST = 0;
        public static final int NO_Zampelli_CODE = 0;
        public static final int MODE_OVERLOADCHECK = 1;
        public static final int MODE_EDGEFINDING = 1;
        public static final int MODE_NOTFIRSTNOTLAST = 1;
        public static final int Zampelli_CODE = 1;
        public static final int HEURISTIQUE_LEXICO_NEQ_LB = 0;
        public static final int HEURISTIQUE_LEXICOGRAPHIC_LB = 1;
        public static final int HEURISTIQUE_LEXICO_SPLIT = 2;
        public static final int HEURISTIQUE_IMPACT_BASED_SEARCH = 3;
        public static final int MODE_N_CUMULATIVE = 1;
        public static final int MODE_NO_N_CUMULATIVE = 0;
        public static final int BL_INSTANCE = 1;
        public static final int PACK_INSTANCE = 2;
        public static final int J_INSTANCE = 3;
        public int Solution[][];
        public float elapsedTime;
        public int backtracksNum;
        public int nomCranes;
        public int makespanValue;
        public int nomSolutions;
        public int countNonZeros;
        public int countNonZerosOnEachResource[];
        public int zeroDelays[];
        int[][] processingTimesForCumulativeChecking;
        public boolean findAllOptimalSolutions;
        public boolean filterLowerBound;
        public boolean filterUpperBound;

        @SuppressWarnings("empty-statement")
        public BenchmarkBLModelAdaptedWithNewFlexModel(int instance_code, String dataName, int[] d, int mode1, int mode2,  int heuristic, boolean filterLowerBound, boolean filterUpperBound, boolean findAllOptimalSolutions, boolean filterMakespan, boolean printStuffForDebugging, int r) throws Exception {

            Task[][] T;
            int numberOfTasks;
            int numberOfResources;
            int Horizon;
            int[] processingTimes;
            int[] capacities;
            int[][] heights;
            int[][] precedences;
            BLBenchmarkInstances ThisData = null;
            PSPLibBenchmarkInstances ThisData2 = null;
            if (instance_code == BL_INSTANCE)
            {
                ThisData = new BLBenchmarkInstances(dataName);
                numberOfTasks = ThisData.numberOfTasks();
                numberOfResources = ThisData.numberOfResources();
                processingTimes = ThisData.processingTimes();
                heights = ThisData.heights();
                capacities = ThisData.capacity();
                precedences = ThisData.precedences();
                int delaySum = 0;
                for (int i = 0; i < numberOfTasks - 2; i++) {
                    delaySum += d[i];
                }
                Horizon = ThisData.horizon() + delaySum;

            }
            else{
                ThisData2 = new PSPLibBenchmarkInstances(dataName);
                numberOfTasks = ThisData2.numberOfTasks;
                numberOfResources = ThisData2.numberOfResources;
                processingTimes = ThisData2.processingTimes();
                heights = ThisData2.heights();
                capacities = ThisData2.capacity();
                precedences = ThisData2.precedences();
                int delaySum = 0;
                for (int i = 0; i < numberOfTasks - 2; i++) {
                    delaySum += d[i];
                }
                Horizon = ThisData2.horizon() + delaySum;

            }
          //  boolean filterMakespan = false;
          boolean CAPProblemConsidered = false;
            this.countNonZeros = 0;
            this.countNonZerosOnEachResource = new int[numberOfResources];
            this.findAllOptimalSolutions = findAllOptimalSolutions;
            this.filterLowerBound = filterLowerBound;
            this.filterUpperBound = filterUpperBound;
            T = new Task[numberOfTasks][numberOfResources];
            for (int i = 0; i < numberOfTasks - 2; i++) {
                for (int j = 0; j < numberOfResources; j++)   {
                    // T[i][j] = new Task (0, Horizon, processingTimes[i], d[i], heights[j][i]);
                    T[i][j] = new Task (0, Horizon, Horizon + d[i], processingTimes[i], d[i], heights[j][i]);

                //    System.out.println("i:" + i + " j:" + j + " " + T[i][j]);
                }
            }
                  
                      //      for (int j = 0; j < numberOfResources; j++)   
          //  Arrays.sort(T[j], new Task.TaskByHeight());

            Solver solver = new Solver();
            IntVar[] startingTimes = new IntVar[numberOfTasks - 2];
            for (int i = 0; i < numberOfTasks - 2; i++) {
                startingTimes[i] = VariableFactory.bounded("startingTimes1[" + i + "]", T[i][0].earliestStartingTime(), T[i][0].latestStartingTime(), solver);
            }
            IntVar[] processingTimeVariables = new IntVar[numberOfTasks - 2];
            for (int i = 0; i < numberOfTasks - 2; i++) {
                processingTimeVariables[i] = VariableFactory.fixed(processingTimes[i], solver);
            }
            IntVar[] endingTimes = new IntVar[numberOfTasks - 2];
            for (int i = 0; i < numberOfTasks - 2; i++) {
                //  endingTimes[i] = VariableFactory.bounded("endingTimes1[i]", processingTimes[i], Horizon, solver);
                endingTimes[i] = VariableFactory.bounded("endingTimes1[i]", processingTimes[i], T[i][0].latestCompletionTime(), solver);

            }
            IntVar[][] allHeights = new IntVar[numberOfTasks - 2][numberOfResources];
            for (int i = 0; i < numberOfTasks - 2; i++) {
                for (int j = 0; j < numberOfResources; j++) {
                    if (T[i][j].height() != 0) {
                        countNonZeros++;
                        countNonZerosOnEachResource[j]++;
                    }
                    allHeights[i][j] = VariableFactory.fixed(T[i][j].height(), solver);
                }
            }
            IntVar makespan = VariableFactory.bounded("objective", 0, Horizon, solver);
        
                   //    solver.post(IntConstraintFactory.arithm(makespan, "=", 30));

            
            
            
            for (int i = 0; i < numberOfTasks - 2; i++) {
                solver.post(IntConstraintFactory.arithm(endingTimes[i], "-", startingTimes[i], "=", processingTimes[i]));
            }
            for (int i = 0; i < numberOfTasks - 2; i++) {
                solver.post(IntConstraintFactory.arithm(makespan, "-", endingTimes[i], ">=", 0));
            }
            for (int k = 0; k < numberOfTasks - 2; k++) {
                for (int l = 0; l < numberOfTasks - 2; l++) {
                    if (precedences[k][l] == 1)
                        solver.post(IntConstraintFactory.arithm(startingTimes[l], "-", startingTimes[k], ">", (processingTimes[k] - 1)));
                }
            }

            BoolVar[][] A = VariableFactory.boolMatrix("A", numberOfTasks - 2, numberOfTasks - 2, solver);
            for (int i = 0; i < numberOfTasks - 2; i++) {
                for (int j = 0; j < numberOfTasks - 2; j++) {
                   // System.out.println("i = " + i + ", j = " + j + " -> [" + A[i][j].getLB() + ", " + A[i][j].getUB() + "]");
                }
            }
            Constraint[] arrayOfConstraints1 = new Constraint[2];
            for (int i = 0; i < numberOfTasks - 2; i++) {
                for (int j = 0; j < numberOfTasks - 2; j++) {
                    arrayOfConstraints1[0] = IntConstraintFactory.arithm(startingTimes[i], "<=", startingTimes[j], "-", T[i][0].processingTime());
                    arrayOfConstraints1[1] = IntConstraintFactory.arithm(startingTimes[j], "<", startingTimes[i], "+", T[i][0].delayedProcessingTime());
                    Constraint c = LogicalConstraintFactory.and(arrayOfConstraints1);
                    LogicalConstraintFactory.ifThen(c, IntConstraintFactory.arithm(A[i][j], "=", 1));
                    LogicalConstraintFactory.ifThen(IntConstraintFactory.arithm(A[i][j], "=", 1), c);
                }
            }

            for (int i = 0; i < numberOfTasks - 2; i++) {
                solver.post(IntConstraintFactory.arithm(A[i][i], "=", 0));
            }


            IntVar[][][] B = new IntVar[numberOfResources][numberOfTasks - 2][numberOfTasks - 2];
            for (int m = 0; m < numberOfResources; m++) {
                for (int i = 0; i < numberOfTasks - 2; i++) {
                    for (int j = 0; j < numberOfTasks - 2; j++) {
                        B[m][i][j] = VariableFactory.enumerated("B[" + m + ", " + i + ", " + j + "]", new int[]{0, T[i][m].height()}, solver);
                      //  System.out.println("m = " + m + ", i = " + i + ", j = " + j + " -> [" + B[m][i][j].getLB() + ", " + B[m][i][j].getUB() + "]");
                    }
                }
            }
//System.out.println();

            IntVar[][][] LL = new IntVar[numberOfResources][numberOfTasks - 2][numberOfTasks - 2];
            IntVar[][][][] DD = new IntVar[numberOfResources][numberOfTasks - 2][numberOfTasks - 2][numberOfTasks - 2];
            Constraint[] arrayOfConstraints2 = new Constraint[2];
            Constraint[] arrayOfConstraints3 = new Constraint[2];
            Constraint[] arrayOfConstraints4 = new Constraint[2];
            IntVar[] allCapacityVars =  new IntVar[numberOfResources];
            for (int m = 0; m < numberOfResources; m++) {
                allCapacityVars[m] = VariableFactory.fixed(capacities[m], solver);
               // IntVar capacityVar = VariableFactory.fixed(capacities[numberOfResources], solver);
                IntVar[] starting_processing_ending_times_plus_height_variables_and_capacityOfResource_plus_makeSpan = new IntVar[4 * countNonZerosOnEachResource[m] + 2];
                for (int j = 0; j < numberOfTasks - 2; j++) {
                    // solver.post(ICF.sum(B[j], "<=", CapSum));
                    for (int i = 0; i < numberOfTasks - 2; i++) {
                        LL[m][j][i] = B[m][i][j];
                       // System.out.println("m = " + m + ", j = " + j + ", i = " + i + " -> [" + LL[m][j][i].getLB() + ", " + LL[m][j][i].getUB() + "]");
                    }
                    solver.post(ICF.sum(LL[m][j], "<=", allCapacityVars[m]));
                }
                             for (int i = 0; i < numberOfTasks - 2; i++) {
                    solver.post(IntConstraintFactory.arithm(B[m][i][i], "=", T[i][m].height()));
                }
                             for (int j = 0; j < numberOfTasks - 2; j++) {
                    for (int i = 0; i < numberOfTasks - 2; i++) {
                        arrayOfConstraints2[0] = ICF.arithm(startingTimes[i], "<=", startingTimes[j]);
                        arrayOfConstraints2[1] = ICF.arithm(startingTimes[j], "<", startingTimes[i], "+", T[i][0].processingTime());
                        Constraint c = LogicalConstraintFactory.and(arrayOfConstraints2);
                        arrayOfConstraints4[0] = c;
                        arrayOfConstraints3[0] = IntConstraintFactory.arithm(A[i][j], "=", 1);
                        //  IntVar[] DD = new IntVar[i + 1];
                        for (int k = 0; k <= i; k++) {
                            //    DD[k] = A[k][j];
                            DD[m][j][i][k] = A[k][j];
                        }
                        for (int k = i + 1; k < numberOfTasks - 2; k++) {
                            DD[m][j][i][k] = VariableFactory.fixed(0, solver);

                        }
                        Constraint L = ICF.sum(DD[m][j][i], "<=", VariableFactory.fixed(r, solver));
                        arrayOfConstraints3[1] = L;
                        Constraint f = LogicalConstraintFactory.and(arrayOfConstraints3);
                        arrayOfConstraints4[1] = f;
                        Constraint g = LogicalConstraintFactory.or(arrayOfConstraints4);
                        LogicalConstraintFactory.ifThen(g, IntConstraintFactory.arithm(B[m][i][j], "=", T[i][m].height()));
                        LogicalConstraintFactory.ifThen(IntConstraintFactory.arithm(B[m][i][j], "=", T[i][m].height()), g);
                    }
                }


                int tempCounter;
                int[] d2 = new int[countNonZerosOnEachResource[m]];
                tempCounter = 0;
                for (int g = 0; g < numberOfTasks - 2; g++) {
                    if (allHeights[g][m].getLB() != 0) {
                        starting_processing_ending_times_plus_height_variables_and_capacityOfResource_plus_makeSpan[tempCounter] = startingTimes[g];
                        d2[tempCounter] = d[g];
                        tempCounter++;
                    }
                }
                for (int g = numberOfTasks - 2; g < 2 * numberOfTasks - 4; g++) {
                    if (allHeights[g - (numberOfTasks - 2)][m].getLB() != 0) {
                        starting_processing_ending_times_plus_height_variables_and_capacityOfResource_plus_makeSpan[tempCounter] = processingTimeVariables[g - (numberOfTasks - 2)];
                        tempCounter++;
                    }
                }
                for (int g = 2 * numberOfTasks - 4; g < 3 * numberOfTasks - 6; g++) {
                    if (allHeights[g - 2 * (numberOfTasks - 2)][m].getLB() != 0) {
                        starting_processing_ending_times_plus_height_variables_and_capacityOfResource_plus_makeSpan[tempCounter] = endingTimes[g - 2 * (numberOfTasks - 2)];
                        tempCounter++;
                    }
                }
                for (int g = 3 * numberOfTasks - 6; g < 4 * numberOfTasks - 8; g++) {
                    if (allHeights[g - 3 * (numberOfTasks - 2)][m].getLB() != 0) {
                        starting_processing_ending_times_plus_height_variables_and_capacityOfResource_plus_makeSpan[tempCounter] = allHeights[g - 3 * (numberOfTasks - 2)][m];
                        tempCounter++;}
                }
                starting_processing_ending_times_plus_height_variables_and_capacityOfResource_plus_makeSpan[4 * countNonZerosOnEachResource[m]] = allCapacityVars[m];
                starting_processing_ending_times_plus_height_variables_and_capacityOfResource_plus_makeSpan[4 * countNonZerosOnEachResource[m] + 1] = makespan;
                int[] capacityVector = new int[1];
                capacityVector[0] = capacities[m];
                if (mode1 == MODE_OVERLOADCHECK && mode2 == MODE_NO_EDGEFINDING) {
                    solver.post(new overloadCheckingConstraint(starting_processing_ending_times_plus_height_variables_and_capacityOfResource_plus_makeSpan, countNonZerosOnEachResource[m], 1, capacityVector, d2, filterMakespan, r));
                }
                else if (mode1 == MODE_NO_OVERLOADCHECK && mode2 == MODE_EDGEFINDING) {
                  //  solver.post(new edgeFindingConstraint(starting_processing_ending_times_plus_height_variables_and_capacityOfResource_plus_makeSpan, countNonZerosOnEachResource[m], 1, capacityVector, d2, filterMakespan, filterLowerBound, filterUpperBound, false, 0, r));
                }
            }







            IntVar[] arrayOfAllVariables = new IntVar[3 * (numberOfTasks - 2) + countNonZeros + numberOfResources + 1
                    + (numberOfTasks - 2)*(numberOfTasks - 2) + numberOfResources*(numberOfTasks - 2)*(numberOfTasks - 2) +
                    numberOfResources*(numberOfTasks - 2)*(numberOfTasks - 2)*(numberOfTasks - 2) + numberOfResources * (numberOfTasks - 2) * (numberOfTasks - 2)
                    ];
            for (int g = 0; g < numberOfTasks - 2; g++) {
                arrayOfAllVariables[g] = startingTimes[g];
            }
            for (int g = numberOfTasks - 2; g < 2 * numberOfTasks - 4; g++) {
                arrayOfAllVariables[g] = processingTimeVariables[g - (numberOfTasks - 2)];

            }
            for (int g = 2 * numberOfTasks - 4; g < 3 * numberOfTasks - 6; g++) {
                arrayOfAllVariables[g] = endingTimes[g - 2 * (numberOfTasks - 2)];

            }
            int g = 3 * numberOfTasks - 6;
            for (int j = 0; j < numberOfResources; j++) {
                for (int i = 0; i < numberOfTasks - 2; i++) {
                    if (T[i][j].height() != 0) {
                        arrayOfAllVariables[g] = allHeights[i][j];
                        g++;
                    }
                }
            }
            for (int j = 0; j < numberOfResources; j++) {
                arrayOfAllVariables[g] = allCapacityVars[j];
                g++;
            }
            arrayOfAllVariables[3 * (numberOfTasks - 2) + countNonZeros + numberOfResources] = makespan;
            g++;
            for (int i = 0; i < numberOfTasks - 2; i++) {
                for (int j = 0; j < numberOfTasks - 2; j++) {
                    arrayOfAllVariables[g] = A[i][j];
                    g++;
                }
            }

            for (int m = 0; m < numberOfResources; m++) {
                for (int i = 0; i < numberOfTasks - 2; i++) {
                    for (int j = 0; j < numberOfTasks - 2; j++) {

                        arrayOfAllVariables[g] = B[m][i][j];
                        g++;

                    }
                }
            }
           for (int m = 0; m < numberOfResources; m++) {
                for (int i = 0; i < numberOfTasks - 2; i++) {
                    for (int j = 0; j < numberOfTasks - 2; j++) {
                        arrayOfAllVariables[g] = LL[m][i][j];
                        g++;

                    }
                }}
            for (int m = 0; m < numberOfResources; m++) {
                for (int i = 0; i < numberOfTasks - 2; i++) {
                    for (int j = 0; j < numberOfTasks - 2; j++) {
                        for (int k = 0; k < numberOfTasks - 2; k++) {
                            arrayOfAllVariables[g] = DD[m][i][j][k];
                            g++;

                        }
                    }
                }
            }
 
            //             System.out.println("arrayOfAllVariables includes");
            //                    for (int j = 0; j < arrayOfAllVariables.length; j++)
            //                    System.out.println(" j = " + j + " " + arrayOfAllVariables[j].getLB() + " " + arrayOfAllVariables[j].getUB());
            //





            if (heuristic == HEURISTIQUE_LEXICOGRAPHIC_LB)
                solver.set(IntStrategyFactory.lexico_LB(arrayOfAllVariables));
            else if (heuristic == HEURISTIQUE_LEXICO_SPLIT)
                solver.set(IntStrategyFactory.lexico_Split(arrayOfAllVariables));
            else if (heuristic == HEURISTIQUE_LEXICO_NEQ_LB)
                solver.set(IntStrategyFactory.lexico_Neq_LB(arrayOfAllVariables));
            else if (heuristic == HEURISTIQUE_IMPACT_BASED_SEARCH)
                solver.set(IntStrategyFactory.impact(arrayOfAllVariables, 1));


            //solver.set(IntStrategyFactory.domOverWDeg(arrayOfAllVariables, 1));







//            int co = 0;
//            if (solver.findSolution()){
//                do {
//                    co++;
//                    //if (
//                    //co >= 5570
//                    //    startingTimes[0].getValue() == 5
//                    //        &&  startingTimes[16].getValue() == 20
//                    //                && startingTimes[17].getValue() == 19
//                    //   )
//                    {
//                  //  System.out.println();
//                  //  System.out.println("Solution number " + co);
//                    for (int k = 0; k < numberOfTasks - 2; k++) {
//                      //  System.out.print("s[" + k + "] = " + startingTimes[k].getValue() + ", ");
//                    }
//                }
//                    //  if (co >= 7)
//                    // System.out.println("fhf");
//                } while(solver.nextSolution());
//            }








                    if (findAllOptimalSolutions)
                        solver.findAllOptimalSolutions(ResolutionPolicy.MINIMIZE, makespan, true);
                    else
                        solver.findOptimalSolution(ResolutionPolicy.MINIMIZE, makespan);

            //  for (int i = 0; i < numberOfTasks - 2; i++) {
            // System.out.println("s["+ i + "] = " + startingTimes[i].getValue());
            //              System.out.println("e["+ i + "] = " + endingTimes[i].getValue());
            // }
            //   System.out.println();
            Chatterbox.printStatistics(solver);
            makespanValue = makespan.getValue();
            backtracksNum = (int) solver.getMeasures().getBackTrackCount();
            elapsedTime = solver.getMeasures().getTimeCount();
            nomSolutions = (int) solver.getMeasures().getSolutionCount();
            //  System.out.println("Solutions = " + nomSolutions);

            // System.out.println("backtrack numbers = " + backtracksNum);
            //   System.out.println("Elapsed time = " + elapsedTime);
            System.out.println("makespan = " + makespanValue);

            //  System.out.println();
            // nomSolutions = solver.findAllOptimalSolutions.

        }

        public int howManyBacktracks() {
            //    System.out.println("Backtrack number = "+ backtracksNum);
            return backtracksNum;
        }
        public float howMuchTime() {
            // System.out.println("Backtrack number = "+ elapsedTime);
            return elapsedTime;
        }
        public int howManySolutions() {
            //    System.out.println("Backtrack number = "+ backtracksNum);
            return nomSolutions;
        }
        public int makespan() {
            // System.out.println("Backtrack number = "+ backtracksNum);
            return makespanValue;
        }

        public static void main(String[] args) throws Exception  {



            //   PrintStream out = new PrintStream(new FileOutputStream("output2.txt"));
            //System.setOut(out);
            //  for (int seedValue = 55; seedValue < 100; seedValue++)  {

            //  for (int k = 1; k < 30; k++)  {
            //   long seedValue =  System.currentTimeMillis();
           // long seedValue = 2;
            //   long seedValue = 1486584301952L;

            long seedValue = 4;
          //  long seedValue = k;


            double delayFactor = 1.0;
            //    System.out.println("k "+ k);

            System.out.println("seedValue " + seedValue);






          //  String dataName = "newTest0.rcp";

        //    String dataName = "test0222.rcp";
               String dataName = "bl20_4.rcp";



            int instance_code = BL_INSTANCE;
            int[] d;
            if (instance_code == BL_INSTANCE)
            {
                BLBenchmarkInstances A = new BLBenchmarkInstances(dataName);
                int[] processingTimes = A.processingTimes();
                d = new int[processingTimes.length];
                Random randomNumbers = new Random();
                randomNumbers.setSeed(seedValue);
                for (int i = 0; i < processingTimes.length; i++) {
                    d[i] = randomNumbers.nextInt((int) Math.ceil(delayFactor * (processingTimes[i]) + 1));
                    //  d[i] = 0;
                }
            }
            else {
                PSPLibBenchmarkInstances A = new PSPLibBenchmarkInstances(dataName);
                int[] processingTimes = A.processingTimes();
                d = new int[processingTimes.length];
                Random randomNumbers = new Random();
                randomNumbers.setSeed(seedValue);
                for (int i = 0; i < processingTimes.length; i++) {
                    d[i] = randomNumbers.nextInt((int) Math.ceil(delayFactor * (processingTimes[i]) + 1));
                    //  d[i] = 0;
                }

            }

            boolean f = false;
            boolean findAllOptimalSolutions = !true;
            boolean filterLowerBound = true;
            boolean filterUpperBound = true;
            boolean filterMakespan = !true;

            int heuristic = HEURISTIQUE_LEXICOGRAPHIC_LB;

            //HEURISTIQUE_LEXICO_NEQ_LB
            //HEURISTIQUE_LEXICOGRAPHIC_LB
            //HEURISTIQUE_LEXICO_SPLIT
            //  HEURISTIQUE_IMPACT_BASED_SEARCH
            int r = 1;

            new BenchmarkBLModelAdaptedWithNewFlexModel(instance_code, dataName, d, MODE_NO_OVERLOADCHECK, MODE_NO_EDGEFINDING,  heuristic, filterLowerBound, filterUpperBound, findAllOptimalSolutions, filterMakespan, f, r);
            new BenchmarkBLModelAdaptedWithNewFlexModel(instance_code, dataName, d, MODE_OVERLOADCHECK, MODE_NO_EDGEFINDING,  heuristic, filterLowerBound, filterUpperBound, findAllOptimalSolutions, filterMakespan, f, r);
               new BenchmarkBLModelAdaptedWithNewFlexModel(instance_code, dataName, d, MODE_NO_OVERLOADCHECK, MODE_EDGEFINDING,  heuristic, filterLowerBound, filterUpperBound, findAllOptimalSolutions, filterMakespan, f, r);


            System.out.println();
        }

    }

    //}