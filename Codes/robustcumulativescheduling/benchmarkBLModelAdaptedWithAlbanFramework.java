package robustcumulativescheduling;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.Random;
import org.chocosolver.solver.ResolutionPolicy;
import org.chocosolver.solver.Settings;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.ICF;
import org.chocosolver.solver.constraints.IntConstraintFactory;
import org.chocosolver.solver.search.loop.ISearchLoop;
import org.chocosolver.solver.search.loop.monitors.IMonitorSolution;
import org.chocosolver.solver.search.solution.Solution;
import org.chocosolver.solver.search.strategy.IntStrategyFactory;
import org.chocosolver.solver.trace.Chatterbox;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.VariableFactory;
import org.chocosolver.solver.search.strategy.selectors.variables.Smallest;
import org.chocosolver.solver.search.strategy.selectors.values.IntDomainMin;

import static org.chocosolver.solver.search.strategy.ISF.*;

/**
 *
 * @author Hamed This model is only adapted for delaynumber = 1 according to
 * Alban's framework
 */
public class benchmarkBLModelAdaptedWithAlbanFramework {

    private static final int MODE_NO_OVERLOADCHECK = 0;
    private static final int MODE_NO_EDGEFINDING = 0;
    private static final int MODE_OVERLOADCHECK = 1;
    private static final int MODE_EDGEFINDING = 1;
    private static final int HEURISTIQUE_LEXICOGRAPHIC_LB = 1;
    private static final int HEURISTIQUE_LEXICO_NEQ_LB = 2;
    private static final int HEURISTIQUE_LEXICO_SPLIT = 3;
    private static final int HEURISTIQUE_IMPACT_BASED_SEARCH = 4;
    private static final int HEURISTIQUE_DOMOVERWDEG = 5;
    private static final int HEURISTIQUE_Smallest = 6;
    private static final int BL_INSTANCE = 1;
    private static final int PACK_INSTANCE = 2;
    private static final int J_INSTANCE = 3;
    public float elapsedTime;
    public int backtracksNum;
    private final int makespanValue;
    private final int nomSolutions;
    private int countNonZeros;
    private int countNonZerosOnEachResource[];
    int[][] processingTimesForCumulativeChecking;
    private static boolean activateImprovingDetectionLowerBound;
    //  private static boolean activateImprovingDetectionUpperBound;    
    private static int counterSolutions;

    @SuppressWarnings("empty-statement")
    public benchmarkBLModelAdaptedWithAlbanFramework(int instance_code, String dataName, int[] d, int mode1, int mode2, int heuristic, boolean filterLowerBound, boolean filterUpperBound, boolean activateImprovingDetectionLowerBound, boolean findAllOptimalSolutions, boolean printSolutionInformatioin, int r) throws Exception {

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
        if (instance_code == BL_INSTANCE) {
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
        } else {
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
        boolean filterMakespan = true;
        boolean CAPProblemConsidered = false;
        this.countNonZeros = 0;
        this.countNonZerosOnEachResource = new int[numberOfResources];
        T = new Task[numberOfTasks][numberOfResources];
        for (int i = 0; i < numberOfTasks - 2; i++) {
            for (int j = 0; j < numberOfResources; j++) {
                T[i][j] = new Task(0, Horizon, Horizon + d[i], processingTimes[i], d[i], heights[j][i]);
             //   T[i][j] = new Task(0, Horizon + d[i], Horizon + d[i], processingTimes[i] + d[i], 0, heights[j][i]);

                //   System.out.println("i:" + i + " j:" + j + " " + T[i][j]);
            }
        }
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
        IntVar[] allCapacityVars = new IntVar[numberOfResources];
        IntVar makespan = VariableFactory.bounded("objective", 0, Horizon, solver);
        for (int i = 0; i < numberOfTasks - 2; i++) {
            solver.post(IntConstraintFactory.arithm(endingTimes[i], "-", startingTimes[i], "=", processingTimes[i]));
        }
        for (int i = 0; i < numberOfTasks - 2; i++) {
            solver.post(IntConstraintFactory.arithm(makespan, "-", endingTimes[i], ">=", d[i]));
        }
        for (int k = 0; k < numberOfTasks - 2; k++) {
            for (int l = 0; l < numberOfTasks - 2; l++) {
                if (precedences[k][l] == 1) {
                    solver.post(IntConstraintFactory.arithm(startingTimes[l], "-", startingTimes[k], ">", (processingTimes[k] - 1)));
                }
            }
        }

        //  solver.post(IntConstraintFactory.arithm(makespan, "=", 57));
        for (int m = 0; m < numberOfResources; m++) {
            //   System.out.println("this is machine " + m);
            IntVar[] starting_processing_ending_times_plus_height_variables_and_capacityOfResource = new IntVar[4 * countNonZerosOnEachResource[m] + 1];
            IntVar[] starting_processing_ending_times_plus_height_variables_and_capacityOfResource_plus_makeSpan = new IntVar[4 * countNonZerosOnEachResource[m] + 2];
            int C = capacities[m];
            IntVar capacityVar = VariableFactory.fixed(C, solver);
            int[] capacityVector = new int[1];
            capacityVector[0] = C;
            allCapacityVars[m] = VariableFactory.fixed(C, solver);
            int tempCounter;
            int[] d2 = new int[countNonZerosOnEachResource[m]];
            tempCounter = 0;
            for (int g = 0; g < numberOfTasks - 2; g++) {
                if (allHeights[g][m].getLB() != 0) {
                    starting_processing_ending_times_plus_height_variables_and_capacityOfResource[tempCounter] = startingTimes[g];
                    starting_processing_ending_times_plus_height_variables_and_capacityOfResource_plus_makeSpan[tempCounter] = startingTimes[g];
                    d2[tempCounter] = d[g];
                    tempCounter++;
                }
            }
            for (int g = numberOfTasks - 2; g < 2 * numberOfTasks - 4; g++) {
                if (allHeights[g - (numberOfTasks - 2)][m].getLB() != 0) {
                    starting_processing_ending_times_plus_height_variables_and_capacityOfResource[tempCounter] = processingTimeVariables[g - (numberOfTasks - 2)];
                    starting_processing_ending_times_plus_height_variables_and_capacityOfResource_plus_makeSpan[tempCounter] = processingTimeVariables[g - (numberOfTasks - 2)];
                    tempCounter++;
                }
            }
            for (int g = 2 * numberOfTasks - 4; g < 3 * numberOfTasks - 6; g++) {
                if (allHeights[g - 2 * (numberOfTasks - 2)][m].getLB() != 0) {
                    starting_processing_ending_times_plus_height_variables_and_capacityOfResource[tempCounter] = endingTimes[g - 2 * (numberOfTasks - 2)];
                    starting_processing_ending_times_plus_height_variables_and_capacityOfResource_plus_makeSpan[tempCounter] = endingTimes[g - 2 * (numberOfTasks - 2)];
                    tempCounter++;
                }
            }
            for (int g = 3 * numberOfTasks - 6; g < 4 * numberOfTasks - 8; g++) {
                if (allHeights[g - 3 * (numberOfTasks - 2)][m].getLB() != 0) {
                    starting_processing_ending_times_plus_height_variables_and_capacityOfResource[tempCounter] = allHeights[g - 3 * (numberOfTasks - 2)][m];
                    starting_processing_ending_times_plus_height_variables_and_capacityOfResource_plus_makeSpan[tempCounter] = allHeights[g - 3 * (numberOfTasks - 2)][m];
                    tempCounter++;
                }
            }
            starting_processing_ending_times_plus_height_variables_and_capacityOfResource[4 * countNonZerosOnEachResource[m]] = capacityVar;
            starting_processing_ending_times_plus_height_variables_and_capacityOfResource_plus_makeSpan[4 * countNonZerosOnEachResource[m]] = capacityVar;
            starting_processing_ending_times_plus_height_variables_and_capacityOfResource_plus_makeSpan[4 * countNonZerosOnEachResource[m] + 1] = makespan;
            if (mode1 == MODE_NO_OVERLOADCHECK && mode2 == MODE_NO_EDGEFINDING) {
                solver.post(new PropTTDynamicSweep_RobustConstraint(starting_processing_ending_times_plus_height_variables_and_capacityOfResource, countNonZerosOnEachResource[m], 1, capacityVector, d2));
            } else if (mode1 == MODE_OVERLOADCHECK && mode2 == MODE_NO_EDGEFINDING) {
                solver.post(new PropTTDynamicSweep_RobustConstraint(starting_processing_ending_times_plus_height_variables_and_capacityOfResource, countNonZerosOnEachResource[m], 1, capacityVector, d2));
                solver.post(new overloadCheckingConstraint(starting_processing_ending_times_plus_height_variables_and_capacityOfResource_plus_makeSpan, countNonZerosOnEachResource[m], 1, capacityVector, d2, filterMakespan, r));

            } else if (mode1 == MODE_NO_OVERLOADCHECK && mode2 == MODE_EDGEFINDING) {
                solver.post(new PropTTDynamicSweep_RobustConstraint(starting_processing_ending_times_plus_height_variables_and_capacityOfResource, countNonZerosOnEachResource[m], 1, capacityVector, d2));
                solver.post(new edgeFindingConstraint(starting_processing_ending_times_plus_height_variables_and_capacityOfResource_plus_makeSpan, countNonZerosOnEachResource[m], 1, capacityVector, d2, filterMakespan, filterLowerBound, filterUpperBound, activateImprovingDetectionLowerBound, false, 0, CAPProblemConsidered, r));

            } else if (mode1 == MODE_OVERLOADCHECK && mode2 == MODE_EDGEFINDING) {

                solver.post(new PropTTDynamicSweep_RobustConstraint(starting_processing_ending_times_plus_height_variables_and_capacityOfResource, countNonZerosOnEachResource[m], 1, capacityVector, d2));
                solver.post(new overloadCheckingConstraint(starting_processing_ending_times_plus_height_variables_and_capacityOfResource_plus_makeSpan, countNonZerosOnEachResource[m], 1, capacityVector, d2, filterMakespan, r));
                solver.post(new edgeFindingConstraint(starting_processing_ending_times_plus_height_variables_and_capacityOfResource_plus_makeSpan, countNonZerosOnEachResource[m], 1, capacityVector, d2, filterMakespan, filterLowerBound, filterUpperBound, activateImprovingDetectionLowerBound, false, 0, CAPProblemConsidered, r));

            }
        }

        IntVar[] arrayOfAllVariables = new IntVar[3 * (numberOfTasks - 2) + countNonZeros + numberOfResources + 1];
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

        //             System.out.println("arrayOfAllVariables includes");
        //                    for (int j = 0; j < arrayOfAllVariables.length; j++)
        //                    System.out.println(" j = " + j + " " + arrayOfAllVariables[j].getLB() + " " + arrayOfAllVariables[j].getUB());
        //
        switch (heuristic) {

            case HEURISTIQUE_LEXICOGRAPHIC_LB:
                solver.set(IntStrategyFactory.lexico_LB(arrayOfAllVariables));
                break;
            case HEURISTIQUE_LEXICO_SPLIT:
                solver.set(IntStrategyFactory.lexico_Split(arrayOfAllVariables));
                break;
            case HEURISTIQUE_LEXICO_NEQ_LB:
                solver.set(IntStrategyFactory.lexico_Neq_LB(arrayOfAllVariables));
                break;
            case HEURISTIQUE_IMPACT_BASED_SEARCH:
                solver.set(IntStrategyFactory.impact(arrayOfAllVariables, 1));
                break;
            case HEURISTIQUE_DOMOVERWDEG:
                solver.set(IntStrategyFactory.domOverWDeg(arrayOfAllVariables, 1));
                break;
            case HEURISTIQUE_Smallest:
                solver.set(IntStrategyFactory.custom(new Smallest(), new IntDomainMin(), arrayOfAllVariables));
                break;
            default:
                break;
        }

        //
        //        int co = 0;
        //                if (solver.findSolution()){
        //            do {
        //                co++;
        // //if (
        //         //co >= 5570
        //      //    startingTimes[0].getValue() == 5
        ////        &&  startingTimes[16].getValue() == 20
        ////                && startingTimes[17].getValue() == 19
        //            //   )
        // {
        //                System.out.println();
        //                System.out.println("Solution number " + co);
        //                for (int k = 0; k < numberOfTasks - 2; k++) {
        //                    System.out.print("s[" + k + "] = " + startingTimes[k].getValue() + ", ");
        //                }
        //                                }
        //              //  if (co >= 7)
        //                   // System.out.println("fhf");
        //            } while(solver.nextSolution());
        //        }
        
/*  counterSolutions = 0;
        // list of solutions
        final LinkedList<Solution> optSols = new LinkedList<>();
        solver.getSearchLoop().plugSearchMonitor(new IMonitorSolution() {
            int bestObj=1073741824;
            @Override
            public void onSolution() {
                //   System.out.println("solution "+a + ":" + b+" has been found");
                     System.out.print("solution number " + ++counterSolutions + ": ");
               
                 //  if (counterSolutions == 1)
                   //    System.out.println("stop");
                     for (int k = 0; k < numberOfTasks - 2; k++) {
                    System.out.print("s[" + k + "] = " + startingTimes[k].getValue() + ", ");
                }
             //   System.out.print("\nmakespan = " + makespan.getValue() );
              //  System.out.print("\nmakespan in [" + makespan.getLB() + "]");
             //   System.out.print("\nmakespan in [" + makespan.getUB() + "]");

System.out.println();
                int currentObj = makespan.getValue();
                // try to get better or equal solutions
                solver.post(ICF.arithm(makespan,"<=",currentObj));
                // only keeps the best solutions found so far
                if(bestObj>currentObj){
                    bestObj = currentObj;
                    optSols.clear();
                }
                Solution sol = new Solution();
                sol.record(solver);
                optSols.add(sol);
            }
        });
      //  solver.findAllSolutions();
solver.findAllOptimalSolutions(ResolutionPolicy.MINIMIZE, makespan, true);
      */   
        // counterSolutions = 0;
        // access all optimal solutions
//        System.out.println("Optimal solutions:");
//        for(Solution s:optSols){
//            System.out.println(s.getIntVal(makespan));
//        }
       if (findAllOptimalSolutions) {
            solver.findAllOptimalSolutions(ResolutionPolicy.MINIMIZE, makespan, true);
        } else {
            solver.findOptimalSolution(ResolutionPolicy.MINIMIZE, makespan);
        }
      

        for (int i = 0; i < numberOfTasks - 2; i++) {
            // System.out.println("s["+ i + "] = " + startingTimes[i].getValue());
            //  System.out.println("e["+ i + "] = " + endingTimes[i].getValue());
        }
        //   System.out.println();
        if (printSolutionInformatioin) {
            Chatterbox.printStatistics(solver);
        }
        makespanValue = makespan.getValue();
        backtracksNum = (int) solver.getMeasures().getBackTrackCount();
        elapsedTime = solver.getMeasures().getTimeCount();
        nomSolutions = (int) solver.getMeasures().getSolutionCount();
        //  System.out.println("Solutions = " + nomSolutions);

        // System.out.println("backtrack numbers = " + backtracksNum);
        //   System.out.println("Elapsed time = " + elapsedTime);
        //  System.out.println("makespan = " + makespanValue);
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

    public static void main(String[] args) throws Exception {
   //    System.out.println(" working directory = " + System.getProperty("user.dir"));

        //      PrintStream out = new PrintStream(new FileOutputStream("output2.txt"));
        //     System.setOut(out);
        testAllTheseInstances();
    }

    public static void testAllTheseInstances() throws Exception {

        double delayFactor;
        int instance_code = BL_INSTANCE;
        long seedValue;
        String dataName;
        boolean findAllOptimalSolutions = true;
        boolean filterLowerBound = true;
        boolean filterUpperBound = true;
        int r = 1;
        int[] d;

        //HEURISTIQUE_LEXICO_NEQ_LB HEURISTIQUE_LEXICOGRAPHIC_LB    
         // PrintStream out = new PrintStream(new FileOutputStream("myoutput.txt"));
         //  System.setOut(out);
        dataName = "test022.rcp";
        seedValue = System.currentTimeMillis();
     // seedValue = 1667383215745L;
  //   seedValue = 1670654520146L;
        delayFactor = 1.8;
        d = randomGeneration(dataName, instance_code, seedValue, delayFactor);
        runTTAndOCTTAndEFTT(instance_code, dataName, d, HEURISTIQUE_LEXICOGRAPHIC_LB, filterLowerBound, filterUpperBound, findAllOptimalSolutions, r);
/*
        dataName = "test20.rcp";
        seedValue = System.currentTimeMillis();

    //    seedValue = 1593759119296L;
        delayFactor = 0.1;
        d = randomGeneration(dataName, instance_code, seedValue, delayFactor);
        runTTAndOCTTAndEFTT(instance_code, dataName, d, HEURISTIQUE_IMPACT_BASED_SEARCH, filterLowerBound, filterUpperBound, findAllOptimalSolutions, r);

        dataName = "test01.rcp";
        seedValue = System.currentTimeMillis();
        //  seedValue = 1593356472674L;
        delayFactor = 1.78;
        d = randomGeneration(dataName, instance_code, seedValue, delayFactor);
        runTTAndOCTTAndEFTT(instance_code, dataName, d, HEURISTIQUE_LEXICOGRAPHIC_LB, filterLowerBound, filterUpperBound, findAllOptimalSolutions, r);

//HEURISTIQUE_Smallest
      
seedValue = 3;
        dataName = "bl20_1.rcp";
        // seedValue =  1583845363420L;
        delayFactor = 1.0;
        d = randomGeneration(dataName, instance_code, seedValue, delayFactor);
        runTTAndOCTTAndEFTT(instance_code, dataName, d, HEURISTIQUE_IMPACT_BASED_SEARCH, filterLowerBound, filterUpperBound, findAllOptimalSolutions, r);

        dataName = "bl20_1.rcp";
        seedValue = 6;
        delayFactor = 0.6;
        d = randomGeneration(dataName, instance_code, seedValue, delayFactor);
        runTTAndOCTTAndEFTT(instance_code, dataName, d, HEURISTIQUE_DOMOVERWDEG, filterLowerBound, filterUpperBound, findAllOptimalSolutions, r);

        dataName = "bl20_5.rcp";

        seedValue = 9;
        delayFactor = 0.9;
        d = randomGeneration(dataName, instance_code, seedValue, delayFactor);
        runTTAndOCTTAndEFTT(instance_code, dataName, d, HEURISTIQUE_DOMOVERWDEG, filterLowerBound, filterUpperBound, findAllOptimalSolutions, r);

        dataName = "bl20_8.rcp";
        seedValue = 6;
        delayFactor = 0.6;
        d = randomGeneration(dataName, instance_code, seedValue, delayFactor);
        runTTAndOCTTAndEFTT(instance_code, dataName, d, HEURISTIQUE_DOMOVERWDEG, filterLowerBound, filterUpperBound, findAllOptimalSolutions, r);

        dataName = "bl20_2.rcp";
        seedValue = 1;
        delayFactor = 0.1;
        d = randomGeneration(dataName, instance_code, seedValue, delayFactor);
        runTTAndOCTTAndEFTT(instance_code, dataName, d, HEURISTIQUE_DOMOVERWDEG, filterLowerBound, filterUpperBound, findAllOptimalSolutions, r);

        dataName = "bl20_1.rcp";
        seedValue = 7;
        delayFactor = 0.7;
        d = randomGeneration(dataName, instance_code, seedValue, delayFactor);
        runTTAndOCTTAndEFTT(instance_code, dataName, d, HEURISTIQUE_DOMOVERWDEG, filterLowerBound, filterUpperBound, findAllOptimalSolutions, r);

        seedValue = 8;
        delayFactor = 0.8;
        d = randomGeneration(dataName, instance_code, seedValue, delayFactor);
        runTTAndOCTTAndEFTT(instance_code, dataName, d, HEURISTIQUE_DOMOVERWDEG, filterLowerBound, filterUpperBound, findAllOptimalSolutions, r);

        //out of memory
        d = randomGeneration(dataName, instance_code, seedValue, delayFactor);
        runTTAndOCTTAndEFTT(instance_code, dataName, d, HEURISTIQUE_DOMOVERWDEG, filterLowerBound, filterUpperBound, findAllOptimalSolutions, r);
        seedValue = 11;
        delayFactor = 1.1;

        seedValue = 15;
        delayFactor = 1.5;
        d = randomGeneration(dataName, instance_code, seedValue, delayFactor);
        runTTAndOCTTAndEFTT(instance_code, dataName, d, HEURISTIQUE_DOMOVERWDEG, filterLowerBound, filterUpperBound, findAllOptimalSolutions, r);

        seedValue = 16;
        delayFactor = 1.6;
        d = randomGeneration(dataName, instance_code, seedValue, delayFactor);
        runTTAndOCTTAndEFTT(instance_code, dataName, d, HEURISTIQUE_DOMOVERWDEG, filterLowerBound, filterUpperBound, findAllOptimalSolutions, r);

        //OUT OF MEMORY ERROR
        d = randomGeneration(dataName, instance_code, seedValue, delayFactor);
        runTTAndOCTTAndEFTT(instance_code, dataName, d, HEURISTIQUE_DOMOVERWDEG, filterLowerBound, filterUpperBound, findAllOptimalSolutions, r);
        seedValue = 17;
        delayFactor = 1.7;

        seedValue = 19;
        delayFactor = 1.9;
        d = randomGeneration(dataName, instance_code, seedValue, delayFactor);
        runTTAndOCTTAndEFTT(instance_code, dataName, d, HEURISTIQUE_DOMOVERWDEG, filterLowerBound, filterUpperBound, findAllOptimalSolutions, r);

        dataName = "bl20_2.rcp";

        //OUT OF MEMORY ERROR
        d = randomGeneration(dataName, instance_code, seedValue, delayFactor);
        runTTAndOCTTAndEFTT(instance_code, dataName, d, HEURISTIQUE_DOMOVERWDEG, filterLowerBound, filterUpperBound, findAllOptimalSolutions, r);
        seedValue = 2;
        delayFactor = 0.2;
        d = randomGeneration(dataName, instance_code, seedValue, delayFactor);
        runTTAndOCTTAndEFTT(instance_code, dataName, d, HEURISTIQUE_DOMOVERWDEG, filterLowerBound, filterUpperBound, findAllOptimalSolutions, r);
        seedValue = 3;
        delayFactor = 0.3;

        seedValue = 4;
        delayFactor = 0.4;
        d = randomGeneration(dataName, instance_code, seedValue, delayFactor);
        runTTAndOCTTAndEFTT(instance_code, dataName, d, HEURISTIQUE_DOMOVERWDEG, filterLowerBound, filterUpperBound, findAllOptimalSolutions, r);

        dataName = "bl20_2.rcp";

        seedValue = 8;
        delayFactor = 0.8;
        d = randomGeneration(dataName, instance_code, seedValue, delayFactor);
        runTTAndOCTTAndEFTT(instance_code, dataName, d, HEURISTIQUE_DOMOVERWDEG, filterLowerBound, filterUpperBound, findAllOptimalSolutions, r);

        seedValue = 10;
        delayFactor = 1.0;
        d = randomGeneration(dataName, instance_code, seedValue, delayFactor);
        runTTAndOCTTAndEFTT(instance_code, dataName, d, HEURISTIQUE_DOMOVERWDEG, filterLowerBound, filterUpperBound, findAllOptimalSolutions, r);

        dataName = "bl20_3.rcp";

        seedValue = 5;
        delayFactor = 0.5;
        d = randomGeneration(dataName, instance_code, seedValue, delayFactor);
        runTTAndOCTTAndEFTT(instance_code, dataName, d, HEURISTIQUE_DOMOVERWDEG, filterLowerBound, filterUpperBound, findAllOptimalSolutions, r);

        dataName = "bl20_3.rcp";
        seedValue = 6;
        delayFactor = 0.6;
        d = randomGeneration(dataName, instance_code, seedValue, delayFactor);
        runTTAndOCTTAndEFTT(instance_code, dataName, d, HEURISTIQUE_DOMOVERWDEG, filterLowerBound, filterUpperBound, findAllOptimalSolutions, r);

        seedValue = 11;
        delayFactor = 1.1;
        d = randomGeneration(dataName, instance_code, seedValue, delayFactor);
        runTTAndOCTTAndEFTT(instance_code, dataName, d, HEURISTIQUE_DOMOVERWDEG, filterLowerBound, filterUpperBound, findAllOptimalSolutions, r);

        seedValue = 16;
        delayFactor = 1.6;
        d = randomGeneration(dataName, instance_code, seedValue, delayFactor);
        runTTAndOCTTAndEFTT(instance_code, dataName, d, HEURISTIQUE_DOMOVERWDEG, filterLowerBound, filterUpperBound, findAllOptimalSolutions, r);

        seedValue = 17;
        delayFactor = 1.7;
        d = randomGeneration(dataName, instance_code, seedValue, delayFactor);
        runTTAndOCTTAndEFTT(instance_code, dataName, d, HEURISTIQUE_DOMOVERWDEG, filterLowerBound, filterUpperBound, findAllOptimalSolutions, r);

        seedValue = 20;
        delayFactor = 2.0;
        d = randomGeneration(dataName, instance_code, seedValue, delayFactor);
        runTTAndOCTTAndEFTT(instance_code, dataName, d, HEURISTIQUE_DOMOVERWDEG, filterLowerBound, filterUpperBound, findAllOptimalSolutions, r);

        dataName = "bl20_5.rcp";

        seedValue = 1;
        delayFactor = 0.1;
        d = randomGeneration(dataName, instance_code, seedValue, delayFactor);
        runTTAndOCTTAndEFTT(instance_code, dataName, d, HEURISTIQUE_DOMOVERWDEG, filterLowerBound, filterUpperBound, findAllOptimalSolutions, r);

        seedValue = 2;
        delayFactor = 0.2;
        d = randomGeneration(dataName, instance_code, seedValue, delayFactor);
        runTTAndOCTTAndEFTT(instance_code, dataName, d, HEURISTIQUE_DOMOVERWDEG, filterLowerBound, filterUpperBound, findAllOptimalSolutions, r);

        seedValue = 3;
        delayFactor = 0.3;
        d = randomGeneration(dataName, instance_code, seedValue, delayFactor);
        runTTAndOCTTAndEFTT(instance_code, dataName, d, HEURISTIQUE_DOMOVERWDEG, filterLowerBound, filterUpperBound, findAllOptimalSolutions, r);

        seedValue = 4;
        delayFactor = 0.4;
        d = randomGeneration(dataName, instance_code, seedValue, delayFactor);
        runTTAndOCTTAndEFTT(instance_code, dataName, d, HEURISTIQUE_DOMOVERWDEG, filterLowerBound, filterUpperBound, findAllOptimalSolutions, r);

        seedValue = 5;
        delayFactor = 0.5;
        d = randomGeneration(dataName, instance_code, seedValue, delayFactor);
        runTTAndOCTTAndEFTT(instance_code, dataName, d, HEURISTIQUE_DOMOVERWDEG, filterLowerBound, filterUpperBound, findAllOptimalSolutions, r);

        seedValue = 6;
        delayFactor = 0.6;
        d = randomGeneration(dataName, instance_code, seedValue, delayFactor);
        runTTAndOCTTAndEFTT(instance_code, dataName, d, HEURISTIQUE_DOMOVERWDEG, filterLowerBound, filterUpperBound, findAllOptimalSolutions, r);

        seedValue = 7;
        delayFactor = 0.7;
        d = randomGeneration(dataName, instance_code, seedValue, delayFactor);
        runTTAndOCTTAndEFTT(instance_code, dataName, d, HEURISTIQUE_DOMOVERWDEG, filterLowerBound, filterUpperBound, findAllOptimalSolutions, r);

        seedValue = 18;
        delayFactor = 1.8;
        d = randomGeneration(dataName, instance_code, seedValue, delayFactor);
        runTTAndOCTTAndEFTT(instance_code, dataName, d, HEURISTIQUE_DOMOVERWDEG, filterLowerBound, filterUpperBound, findAllOptimalSolutions, r);

        //  out of memory
        dataName = "bl20_6.rcp";
        d = randomGeneration(dataName, instance_code, seedValue, delayFactor);
        runTTAndOCTTAndEFTT(instance_code, dataName, d, HEURISTIQUE_DOMOVERWDEG, filterLowerBound, filterUpperBound, findAllOptimalSolutions, r);
        seedValue = 2;
        delayFactor = 0.2;

        dataName = "bl20_7.rcp";
        seedValue = 2;
        delayFactor = 2.0;
        d = randomGeneration(dataName, instance_code, seedValue, delayFactor);
        runTTAndOCTTAndEFTT(instance_code, dataName, d, HEURISTIQUE_DOMOVERWDEG, filterLowerBound, filterUpperBound, findAllOptimalSolutions, r);

        dataName = "bl20_8.rcp";

        seedValue = 1;
        delayFactor = 0.1;
        d = randomGeneration(dataName, instance_code, seedValue, delayFactor);
        runTTAndOCTTAndEFTT(instance_code, dataName, d, HEURISTIQUE_DOMOVERWDEG, filterLowerBound, filterUpperBound, findAllOptimalSolutions, r);

        seedValue = 2;
        delayFactor = 0.2;
        d = randomGeneration(dataName, instance_code, seedValue, delayFactor);
        runTTAndOCTTAndEFTT(instance_code, dataName, d, HEURISTIQUE_DOMOVERWDEG, filterLowerBound, filterUpperBound, findAllOptimalSolutions, r);

        seedValue = 3;
        delayFactor = 0.3;
        d = randomGeneration(dataName, instance_code, seedValue, delayFactor);
        runTTAndOCTTAndEFTT(instance_code, dataName, d, HEURISTIQUE_DOMOVERWDEG, filterLowerBound, filterUpperBound, findAllOptimalSolutions, r);

        seedValue = 4;
        delayFactor = 0.4;
        d = randomGeneration(dataName, instance_code, seedValue, delayFactor);
        runTTAndOCTTAndEFTT(instance_code, dataName, d, HEURISTIQUE_DOMOVERWDEG, filterLowerBound, filterUpperBound, findAllOptimalSolutions, r);

        seedValue = 5;
        delayFactor = 0.5;
        d = randomGeneration(dataName, instance_code, seedValue, delayFactor);
        runTTAndOCTTAndEFTT(instance_code, dataName, d, HEURISTIQUE_DOMOVERWDEG, filterLowerBound, filterUpperBound, findAllOptimalSolutions, r);

        seedValue = 7;
        delayFactor = 0.7;
        d = randomGeneration(dataName, instance_code, seedValue, delayFactor);
        runTTAndOCTTAndEFTT(instance_code, dataName, d, HEURISTIQUE_DOMOVERWDEG, filterLowerBound, filterUpperBound, findAllOptimalSolutions, r);

        seedValue = 17;
        delayFactor = 1.7;
        d = randomGeneration(dataName, instance_code, seedValue, delayFactor);
        runTTAndOCTTAndEFTT(instance_code, dataName, d, HEURISTIQUE_DOMOVERWDEG, filterLowerBound, filterUpperBound, findAllOptimalSolutions, r);

        dataName = "bl20_9.rcp";
        seedValue = 1;
        delayFactor = 0.1;
        d = randomGeneration(dataName, instance_code, seedValue, delayFactor);
        runTTAndOCTTAndEFTT(instance_code, dataName, d, HEURISTIQUE_DOMOVERWDEG, filterLowerBound, filterUpperBound, findAllOptimalSolutions, r);

        seedValue = 7;
        delayFactor = 0.7;
        d = randomGeneration(dataName, instance_code, seedValue, delayFactor);
        runTTAndOCTTAndEFTT(instance_code, dataName, d, HEURISTIQUE_DOMOVERWDEG, filterLowerBound, filterUpperBound, findAllOptimalSolutions, r);

        seedValue = 10;
        delayFactor = 1.0;
        d = randomGeneration(dataName, instance_code, seedValue, delayFactor);
        runTTAndOCTTAndEFTT(instance_code, dataName, d, HEURISTIQUE_DOMOVERWDEG, filterLowerBound, filterUpperBound, findAllOptimalSolutions, r);

        seedValue = 13;
        delayFactor = 1.3;
        d = randomGeneration(dataName, instance_code, seedValue, delayFactor);
        runTTAndOCTTAndEFTT(instance_code, dataName, d, HEURISTIQUE_DOMOVERWDEG, filterLowerBound, filterUpperBound, findAllOptimalSolutions, r);

        seedValue = 14;
        delayFactor = 1.4;
        d = randomGeneration(dataName, instance_code, seedValue, delayFactor);
        runTTAndOCTTAndEFTT(instance_code, dataName, d, HEURISTIQUE_DOMOVERWDEG, filterLowerBound, filterUpperBound, findAllOptimalSolutions, r);

        dataName = "bl20_10.rcp";

//out of memory
        seedValue = 1;
        delayFactor = 0.1;
        d = randomGeneration(dataName, instance_code, seedValue, delayFactor);
        runTTAndOCTTAndEFTT(instance_code, dataName, d, HEURISTIQUE_DOMOVERWDEG, filterLowerBound, filterUpperBound, findAllOptimalSolutions, r);

        seedValue = 3;
        delayFactor = 0.3;
        d = randomGeneration(dataName, instance_code, seedValue, delayFactor);
        runTTAndOCTTAndEFTT(instance_code, dataName, d, HEURISTIQUE_DOMOVERWDEG, filterLowerBound, filterUpperBound, findAllOptimalSolutions, r);

        seedValue = 4;
        delayFactor = 0.4;
        d = randomGeneration(dataName, instance_code, seedValue, delayFactor);
        runTTAndOCTTAndEFTT(instance_code, dataName, d, HEURISTIQUE_DOMOVERWDEG, filterLowerBound, filterUpperBound, findAllOptimalSolutions, r);

        seedValue = 8;
        delayFactor = 0.8;
        d = randomGeneration(dataName, instance_code, seedValue, delayFactor);
        runTTAndOCTTAndEFTT(instance_code, dataName, d, HEURISTIQUE_DOMOVERWDEG, filterLowerBound, filterUpperBound, findAllOptimalSolutions, r);

//works
        seedValue = 10;
        delayFactor = 1.0;
        d = randomGeneration(dataName, instance_code, seedValue, delayFactor);
        runTTAndOCTTAndEFTT(instance_code, dataName, d, HEURISTIQUE_DOMOVERWDEG, filterLowerBound, filterUpperBound, findAllOptimalSolutions, r);

        seedValue = 14;
        delayFactor = 1.4;
        d = randomGeneration(dataName, instance_code, seedValue, delayFactor);
        runTTAndOCTTAndEFTT(instance_code, dataName, d, HEURISTIQUE_DOMOVERWDEG, filterLowerBound, filterUpperBound, findAllOptimalSolutions, r);

        seedValue = 18;
        delayFactor = 1.8;
        d = randomGeneration(dataName, instance_code, seedValue, delayFactor);
        runTTAndOCTTAndEFTT(instance_code, dataName, d, HEURISTIQUE_DOMOVERWDEG, filterLowerBound, filterUpperBound, findAllOptimalSolutions, r);

        dataName = "bl20_11.rcp";
        d = randomGeneration(dataName, instance_code, seedValue, delayFactor);
        runTTAndOCTTAndEFTT(instance_code, dataName, d, HEURISTIQUE_DOMOVERWDEG, filterLowerBound, filterUpperBound, findAllOptimalSolutions, r);

        seedValue = 2;
        delayFactor = 0.2;

        d = randomGeneration(dataName, instance_code, seedValue, delayFactor);
        runTTAndOCTTAndEFTT(instance_code, dataName, d, HEURISTIQUE_DOMOVERWDEG, filterLowerBound, filterUpperBound, findAllOptimalSolutions, r);

        seedValue = 5;
        delayFactor = 0.5;

        d = randomGeneration(dataName, instance_code, seedValue, delayFactor);
        runTTAndOCTTAndEFTT(instance_code, dataName, d, HEURISTIQUE_DOMOVERWDEG, filterLowerBound, filterUpperBound, findAllOptimalSolutions, r);

        seedValue = 6;
        delayFactor = 0.6;

        d = randomGeneration(dataName, instance_code, seedValue, delayFactor);
        runTTAndOCTTAndEFTT(instance_code, dataName, d, HEURISTIQUE_DOMOVERWDEG, filterLowerBound, filterUpperBound, findAllOptimalSolutions, r);

        seedValue = 7;
        delayFactor = 0.7;

        d = randomGeneration(dataName, instance_code, seedValue, delayFactor);
        runTTAndOCTTAndEFTT(instance_code, dataName, d, HEURISTIQUE_DOMOVERWDEG, filterLowerBound, filterUpperBound, findAllOptimalSolutions, r);

        seedValue = 8;
        delayFactor = 0.8;

        d = randomGeneration(dataName, instance_code, seedValue, delayFactor);
        runTTAndOCTTAndEFTT(instance_code, dataName, d, HEURISTIQUE_DOMOVERWDEG, filterLowerBound, filterUpperBound, findAllOptimalSolutions, r);

        seedValue = 12;
        delayFactor = 1.2;

        d = randomGeneration(dataName, instance_code, seedValue, delayFactor);
        runTTAndOCTTAndEFTT(instance_code, dataName, d, HEURISTIQUE_DOMOVERWDEG, filterLowerBound, filterUpperBound, findAllOptimalSolutions, r);

        seedValue = 13;
        delayFactor = 1.3;

        d = randomGeneration(dataName, instance_code, seedValue, delayFactor);
        runTTAndOCTTAndEFTT(instance_code, dataName, d, HEURISTIQUE_DOMOVERWDEG, filterLowerBound, filterUpperBound, findAllOptimalSolutions, r);
        seedValue = 14;
        delayFactor = 1.4;

        dataName = "bl20_12.rcp";
        d = randomGeneration(dataName, instance_code, seedValue, delayFactor);
        runTTAndOCTTAndEFTT(instance_code, dataName, d, HEURISTIQUE_DOMOVERWDEG, filterLowerBound, filterUpperBound, findAllOptimalSolutions, r);
        seedValue = 1;
        delayFactor = 0.1;

        d = randomGeneration(dataName, instance_code, seedValue, delayFactor);
        runTTAndOCTTAndEFTT(instance_code, dataName, d, HEURISTIQUE_DOMOVERWDEG, filterLowerBound, filterUpperBound, findAllOptimalSolutions, r);
        seedValue = 2;
        delayFactor = 0.2;

        d = randomGeneration(dataName, instance_code, seedValue, delayFactor);
        runTTAndOCTTAndEFTT(instance_code, dataName, d, HEURISTIQUE_DOMOVERWDEG, filterLowerBound, filterUpperBound, findAllOptimalSolutions, r);
*/
    }

    public static void runTTAndOCTTAndEFTT(int instance_code, String dataName, int[] d, int heuristic, boolean filterLowerBound, boolean filterUpperBound, boolean findAllOptimalSolutions, int r) throws Exception {
        boolean printSolutionInformatioin = true;

        activateImprovingDetectionLowerBound = true;

        benchmarkBLModelAdaptedWithAlbanFramework X = new benchmarkBLModelAdaptedWithAlbanFramework(instance_code, dataName, d, MODE_NO_OVERLOADCHECK, MODE_NO_EDGEFINDING, heuristic, filterLowerBound, filterUpperBound, activateImprovingDetectionLowerBound, findAllOptimalSolutions, printSolutionInformatioin, r);
        System.out.println("Number of solutions with TT method : " + X.nomSolutions);

        benchmarkBLModelAdaptedWithAlbanFramework Y = new benchmarkBLModelAdaptedWithAlbanFramework(instance_code, dataName, d, MODE_OVERLOADCHECK, MODE_NO_EDGEFINDING, heuristic, filterLowerBound, filterUpperBound, activateImprovingDetectionLowerBound, findAllOptimalSolutions, printSolutionInformatioin, r);
        System.out.println("Number of solutions with OC-TT method : " + Y.nomSolutions);

        benchmarkBLModelAdaptedWithAlbanFramework Z = new benchmarkBLModelAdaptedWithAlbanFramework(instance_code, dataName, d, MODE_NO_OVERLOADCHECK, MODE_EDGEFINDING, heuristic, filterLowerBound, filterUpperBound, activateImprovingDetectionLowerBound, findAllOptimalSolutions, printSolutionInformatioin, r);
        System.out.println("Number of solutions with EF-TT method : " + Z.nomSolutions);

        // benchmarkBLModelAdaptedWithAlbanFramework W = new benchmarkBLModelAdaptedWithAlbanFramework(instance_code, dataName, d, MODE_OVERLOADCHECK, MODE_EDGEFINDING,  heuristic, filterLowerBound, filterUpperBound, findAllOptimalSolutions, printSolutionInformatioin,  r);
        // System.out.println("Number of solutions with OC-EF-TT method : " + W.nomSolutions);
        //    System.out.print("It took " + X.elapsedTime + "s to obtain all optimal solutions of this instance with TT method.\n");
        // System.out.print("It took " + Y.elapsedTime + "s to obtain all optimal solutions of this instance with OC-TT method.\n");
        // System.out.print("It took " + Z.elapsedTime + "s to obtain all optimal solutions of this instance with EF-TT method.\n");
       
        
        
        if (X.makespanValue == Y.makespanValue
                && Y.makespanValue == Z.makespanValue) {
            System.out.println("TT Makespan = " + X.makespanValue);
            System.out.println("TT-OT Makespan = " + Y.makespanValue);
            System.out.println("TT-EF Makespan = " + Z.makespanValue);

            System.out.println("The makespans are equal as well!");
            System.out.println("Got this instance over with!");
        } else {
            System.out.println("X.makespanValue = " + X.makespanValue);
            System.out.println("Y.makespanValue = " + Y.makespanValue);
            System.out.println("Z.makespanValue = " + Z.makespanValue);

            System.out.println("Something is wrong!");

        }
        System.out.println();

    }

    public static int[] randomGeneration(String dataName, int instance_code, long seedValue, double delayFactor) throws Exception {

        System.out.println("dataName: " + dataName);

        System.out.println("seedValue: " + seedValue);

        System.out.println("delayFactor: " + delayFactor);

        int[] d;

        if (instance_code == BL_INSTANCE) {
            BLBenchmarkInstances A = new BLBenchmarkInstances(dataName);
            int[] processingTimes = A.processingTimes();
            d = new int[processingTimes.length];
            Random randomNumbers = new Random();
            randomNumbers.setSeed(seedValue);
            for (int i = 0; i < processingTimes.length; i++) {
                d[i] = randomNumbers.nextInt((int) Math.ceil(delayFactor * (processingTimes[i]) + 1));
                //  d[i] = 0;
                //   System.out.println("d[" + i + "] = " + d[i]);
            }
        } else {
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

        return d;
    }

}
