package robustcumulativescheduling;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.Random;
import org.chocosolver.solver.ResolutionPolicy;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.ICF;
import org.chocosolver.solver.constraints.IntConstraintFactory;
import org.chocosolver.solver.search.loop.monitors.IMonitorSolution;
import org.chocosolver.solver.search.solution.Solution;
import org.chocosolver.solver.search.strategy.IntStrategyFactory;
import org.chocosolver.solver.trace.Chatterbox;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.VariableFactory;

/**
 *
 * @author Hamed This model is only adapted for delaynumber = 1 according to
 * Alban's framework
 */
public class benchmarkBLModelAdaptedWithAlbanFrameworkForNCumulative {

    public static final int MODE_NO_OVERLOADCHECK = 0;
    public static final int MODE_NO_EDGEFINDING = 0;
    public static final int MODE_NO_NOTFIRSTNOTLAST = 0;
    public static final int NO_Zampelli_CODE = 0;
    public static final int MODE_OVERLOADCHECK = 1;
    public static final int MODE_EDGEFINDING = 1;
    public static final int MODE_NOTFIRSTNOTLAST = 1;
    public static final int Zampelli_CODE = 1;
    public static final int HEURISTIQUE_LEXICO_NEQ_LB = 6;

    public static final int HEURISTIQUE_IMPACT_BASED_SEARCH = 1;
    public static final int HEURISTIQUE_LEXICOGRAPHIC = 0;
    public static final int HEURISTIQUE_LEXICO_SPLIT = 2;
    public static final int MODE_N_CUMULATIVE = 1;
    public static final int MODE_NO_N_CUMULATIVE = 0;
    public int Solution[][];
    public float elapsedTime;
    public int backtracksNum;
    public int nomCranes;
    public int makespa;
    public int nomSolutions;
    public int countNonZeros;
    public int countNonZerosOnEachResource[];
    public int zeroDelays[];
    int[][] processingTimesForCumulativeChecking;
    public boolean filterLowerBound;
    public boolean filterUpperBound;
    private static int counterSolutions;

    public static boolean activateImprovingDetectionLowerBound;

    @SuppressWarnings("empty-statement")
    public benchmarkBLModelAdaptedWithAlbanFrameworkForNCumulative(String dataName, int[] d, int mode1, int mode2, int mode3, int heuristic, boolean filterLowerBound, boolean filterUpperBound, boolean findAllOptimalSolutions, boolean printStuffForDebugging, int r) throws Exception {

        Task[][] T;
        int numberOfTasks;
        int numberOfResources;
        int HorizonForModelIncludingDelayedValues;
        int[] processingTimes;
        int[] capacities;
        int[][] heights;
        int[][] precedences;
        BLBenchmarkInstances ThisData = new BLBenchmarkInstances(dataName);
        numberOfTasks = ThisData.numberOfTasks();
        numberOfResources = ThisData.numberOfResources();
        this.filterLowerBound = filterLowerBound;
        this.filterUpperBound = filterUpperBound;
        activateImprovingDetectionLowerBound = false;
        boolean CAPProblemConsidered = false;
        T = new Task[numberOfTasks][numberOfResources];
        processingTimes = ThisData.processingTimes();
        heights = ThisData.heights();
        capacities = ThisData.capacity();
        precedences = ThisData.precedences();
        this.countNonZeros = 0;
        this.countNonZerosOnEachResource = new int[numberOfResources];
        int delaySum = 0;
        for (int i = 0; i < numberOfTasks - 2; i++) {
            delaySum += d[i];
        }
        HorizonForModelIncludingDelayedValues = ThisData.horizon() + delaySum;
        for (int i = 0; i < numberOfTasks - 2; i++) {
            for (int j = 0; j < numberOfResources; j++) {
                T[i][j] = new Task(0, HorizonForModelIncludingDelayedValues, HorizonForModelIncludingDelayedValues + d[i], processingTimes[i], d[i], heights[j][i]);
                //  System.out.println("i:" + i + " j:" + j + " " + T[i][j]);
            }
        }
        Solver solver = new Solver();
        IntVar[] startingTimes = new IntVar[numberOfTasks - 2];
        IntVar[] processingTimeVariables = new IntVar[numberOfTasks - 2];
        IntVar[] endingTimes = new IntVar[numberOfTasks - 2];
        for (int i = 0; i < numberOfTasks - 2; i++) {
            startingTimes[i] = VariableFactory.bounded("startingTimes1[" + i + "]", T[i][0].earliestStartingTime(), T[i][0].latestStartingTime(), solver);
            processingTimeVariables[i] = VariableFactory.fixed(processingTimes[i], solver);
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
        IntVar makespan = VariableFactory.bounded("objective", 0, 10000000, solver);
        int[] horizonForCumulative;
        horizonForCumulative = new int[numberOfTasks - 2];
        IntVar[] processingTimeVariablesForNCumulative = new IntVar[(numberOfTasks - 2)];
        IntVar[] endingTimesForNCumulative = new IntVar[(numberOfTasks - 2)];
        IntVar[] makespanForNCumulative = new IntVar[numberOfTasks - 2];
        for (int i = 0; i < numberOfTasks - 2; i++) {
            horizonForCumulative[i] = ThisData.horizon() + d[i];
            processingTimeVariablesForNCumulative[i] = VariableFactory.fixed(processingTimes[i] + d[i], solver);
            endingTimesForNCumulative[i] = VariableFactory.bounded("endingTimesForNCumulative1[i]", processingTimes[i] + d[i], T[i][0].delayedLatestCompletionTime(), solver);
            makespanForNCumulative[i] = VariableFactory.bounded("objective", 0, 10000000, solver);
        }
        for (int i = 0; i < numberOfTasks - 2; i++) {
            solver.post(IntConstraintFactory.arithm(endingTimes[i], "-", startingTimes[i], "=", processingTimes[i]));
            solver.post(IntConstraintFactory.arithm(endingTimesForNCumulative[i], "-", startingTimes[i], "=", processingTimes[i] + d[i]));
            solver.post(IntConstraintFactory.arithm(makespan, "-", endingTimes[i], ">=", 0));
            //If you don't add this constraint your are not following the same domain lower bounds in two models, because the delayedmakespan takes its lower bound as
            //the delayedEnding time of one task, while the makespan takes as lower bound the ending tme of one task
            solver.post(IntConstraintFactory.arithm(makespan, "-", endingTimesForNCumulative[i], ">=", 0));
            solver.post(IntConstraintFactory.arithm(makespanForNCumulative[i], "-", endingTimesForNCumulative[i], ">=", 0));
            for (int j = 0; j < numberOfTasks - 2; j++) {
                solver.post(IntConstraintFactory.arithm(makespanForNCumulative[i], "-", endingTimes[j], ">=", 0));
            }
        }

        for (int k = 0; k < numberOfTasks - 2; k++) {
            for (int l = 0; l < numberOfTasks - 2; l++) {
                if (precedences[k][l] == 1) {
                    solver.post(IntConstraintFactory.arithm(startingTimes[l], "-", startingTimes[k], ">", (processingTimes[k] - 1)));
                }
            }
        }

        //  solver.post(IntConstraintFactory.arithm(makespan, "=", 35));
        // Post Time-Tabling Constraint
        for (int m = 0; m < numberOfResources; m++) {
            //   System.out.println("this is machine " + m);
            IntVar[] starting_processing_ending_times_plus_height_variables_and_capacityOfResource = new IntVar[4 * countNonZerosOnEachResource[m] + 1];
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
                    d2[tempCounter] = d[g];
                    tempCounter++;
                }
            }
            for (int g = numberOfTasks - 2; g < 2 * numberOfTasks - 4; g++) {
                if (allHeights[g - (numberOfTasks - 2)][m].getLB() != 0) {
                    starting_processing_ending_times_plus_height_variables_and_capacityOfResource[tempCounter] = processingTimeVariables[g - (numberOfTasks - 2)];
                    tempCounter++;
                }
            }
            for (int g = 2 * numberOfTasks - 4; g < 3 * numberOfTasks - 6; g++) {
                if (allHeights[g - 2 * (numberOfTasks - 2)][m].getLB() != 0) {
                    starting_processing_ending_times_plus_height_variables_and_capacityOfResource[tempCounter] = endingTimes[g - 2 * (numberOfTasks - 2)];
                    tempCounter++;
                }
            }
            for (int g = 3 * numberOfTasks - 6; g < 4 * numberOfTasks - 8; g++) {
                if (allHeights[g - 3 * (numberOfTasks - 2)][m].getLB() != 0) {
                    starting_processing_ending_times_plus_height_variables_and_capacityOfResource[tempCounter] = allHeights[g - 3 * (numberOfTasks - 2)][m];
                    tempCounter++;
                }
            }
            starting_processing_ending_times_plus_height_variables_and_capacityOfResource[4 * countNonZerosOnEachResource[m]] = capacityVar;
            if (mode1 == MODE_NO_OVERLOADCHECK && mode2 == MODE_EDGEFINDING) {
                solver.post(new PropTTDynamicSweep_RobustConstraint(starting_processing_ending_times_plus_height_variables_and_capacityOfResource, countNonZerosOnEachResource[m], 1, capacityVector, d2));

            }
        }

        if (mode3 == MODE_NO_N_CUMULATIVE) {

            for (int m = 0; m < numberOfResources; m++) {

                int[] zeroDelays = new int[numberOfTasks];
                //   System.out.println("this is machine " + m);
                //  IntVar[] starting_processing_ending_times_plus_height_variables_and_capacityOfResource = new IntVar[4 * countNonZerosOnEachResource[m] + 1];
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
                        //  starting_processing_ending_times_plus_height_variables_and_capacityOfResource[tempCounter] = startingTimes[g];
                        starting_processing_ending_times_plus_height_variables_and_capacityOfResource_plus_makeSpan[tempCounter] = startingTimes[g];
                        d2[tempCounter] = d[g];
                        tempCounter++;
                    }
                }
                for (int g = numberOfTasks - 2; g < 2 * numberOfTasks - 4; g++) {
                    if (allHeights[g - (numberOfTasks - 2)][m].getLB() != 0) {
                        //   starting_processing_ending_times_plus_height_variables_and_capacityOfResource[tempCounter] = processingTimeVariables[g - (numberOfTasks - 2)];
                        starting_processing_ending_times_plus_height_variables_and_capacityOfResource_plus_makeSpan[tempCounter] = processingTimeVariables[g - (numberOfTasks - 2)];
                        tempCounter++;
                    }
                }
                for (int g = 2 * numberOfTasks - 4; g < 3 * numberOfTasks - 6; g++) {
                    if (allHeights[g - 2 * (numberOfTasks - 2)][m].getLB() != 0) {
                        //  starting_processing_ending_times_plus_height_variables_and_capacityOfResource[tempCounter] = endingTimes[g - 2 * (numberOfTasks - 2)];
                        starting_processing_ending_times_plus_height_variables_and_capacityOfResource_plus_makeSpan[tempCounter] = endingTimes[g - 2 * (numberOfTasks - 2)];
                        tempCounter++;
                    }
                }
                for (int g = 3 * numberOfTasks - 6; g < 4 * numberOfTasks - 8; g++) {
                    if (allHeights[g - 3 * (numberOfTasks - 2)][m].getLB() != 0) {
                        //  starting_processing_ending_times_plus_height_variables_and_capacityOfResource[tempCounter] = allHeights[g - 3 * (numberOfTasks - 2)][m];
                        starting_processing_ending_times_plus_height_variables_and_capacityOfResource_plus_makeSpan[tempCounter] = allHeights[g - 3 * (numberOfTasks - 2)][m];
                        tempCounter++;
                    }
                }
                //  starting_processing_ending_times_plus_height_variables_and_capacityOfResource[4 * countNonZerosOnEachResource[m]] = capacityVar;
                starting_processing_ending_times_plus_height_variables_and_capacityOfResource_plus_makeSpan[4 * countNonZerosOnEachResource[m]] = capacityVar;
                tempCounter++;
                starting_processing_ending_times_plus_height_variables_and_capacityOfResource_plus_makeSpan[4 * countNonZerosOnEachResource[m] + 1] = makespan;
                tempCounter++;

                if (mode1 == MODE_NO_OVERLOADCHECK && mode2 == MODE_EDGEFINDING) {
                    // solver.post(new tempEdgeFindingConstraint(starting_processing_ending_times_plus_height_variables_and_capacityOfResource_plus_makeSpan, countNonZerosOnEachResource[m], 1, capacityVector, d2, true));
                    // solver.post(new PropTTDynamicSweep_RobustConstraint(starting_processing_ending_times_plus_height_variables_and_capacityOfResource, countNonZerosOnEachResource[m], 1, capacityVector, d2));
                    solver.post(new edgeFindingConstraint(starting_processing_ending_times_plus_height_variables_and_capacityOfResource_plus_makeSpan, countNonZerosOnEachResource[m], 1, capacityVector, d2, false, filterLowerBound, filterUpperBound, activateImprovingDetectionLowerBound, false, 0, CAPProblemConsidered, 1));
                }
            }
        } else {
            this.zeroDelays = new int[numberOfTasks - 2];
            for (int cumulativeConstraintCounter = 0; cumulativeConstraintCounter < numberOfTasks - 2; cumulativeConstraintCounter++) {
                for (int m = 0; m < numberOfResources; m++) {
                    //   System.out.println("this is machine " + m);
                    IntVar[] starting_processing_ending_time_height_variables_and_capacityOfResource_plus_makeSpan_plus_extra_variables_for_NCumulative = new IntVar[4 * countNonZerosOnEachResource[m] + 2];
                    int C = capacities[m];
                    IntVar capacityVar = VariableFactory.fixed(C, solver);
                    int[] capacityVector = new int[1];
                    capacityVector[0] = C;
                    allCapacityVars[m] = VariableFactory.fixed(C, solver);
                    int tempCounter;
                    tempCounter = 0;
                    for (int g = 0; g < numberOfTasks - 2; g++) {
                        if (allHeights[g][m].getLB() != 0) {
                            starting_processing_ending_time_height_variables_and_capacityOfResource_plus_makeSpan_plus_extra_variables_for_NCumulative[tempCounter] = startingTimes[g];
                            //System.out.println("g = " + g + " -> " + starting_processing_ending_times_plus_height_variables_and_capacityOfResource_plus_makeSpan[g].getLB() + ", " + starting_processing_ending_times_plus_height_variables_and_capacityOfResource_plus_makeSpan[g].getUB());
                            tempCounter++;
                        }
                    }
                    for (int g = numberOfTasks - 2; g < 2 * numberOfTasks - 4; g++) {
                        if (allHeights[g - (numberOfTasks - 2)][m].getLB() != 0) {
                            if (cumulativeConstraintCounter == g - (numberOfTasks - 2)) {
                                starting_processing_ending_time_height_variables_and_capacityOfResource_plus_makeSpan_plus_extra_variables_for_NCumulative[tempCounter] = processingTimeVariablesForNCumulative[cumulativeConstraintCounter];
                            } else {
                                starting_processing_ending_time_height_variables_and_capacityOfResource_plus_makeSpan_plus_extra_variables_for_NCumulative[tempCounter] = processingTimeVariables[g - (numberOfTasks - 2)];
                            }
                            // System.out.println("g = " + g + " -> " + starting_processing_ending_times_plus_height_variables_and_capacityOfResource_plus_makeSpan[tempCounter].getLB() + ", " + starting_processing_ending_times_plus_height_variables_and_capacityOfResource_plus_makeSpan[tempCounter].getUB());
                            tempCounter++;
                        }
                    }
                    for (int g = 2 * numberOfTasks - 4; g < 3 * numberOfTasks - 6; g++) {
                        if (allHeights[g - 2 * (numberOfTasks - 2)][m].getLB() != 0) {
                            if (cumulativeConstraintCounter == g - 2 * (numberOfTasks - 2)) {
                                starting_processing_ending_time_height_variables_and_capacityOfResource_plus_makeSpan_plus_extra_variables_for_NCumulative[tempCounter] = endingTimesForNCumulative[cumulativeConstraintCounter];
                            } else {
                                starting_processing_ending_time_height_variables_and_capacityOfResource_plus_makeSpan_plus_extra_variables_for_NCumulative[tempCounter] = endingTimes[g - 2 * (numberOfTasks - 2)];
                            }
                            //System.out.println("g = " + g + " -> " + starting_processing_ending_time_height_variables_and_capacityOfResource_plus_makeSpan_plus_extra_variables_for_NCumulative[tempCounter].getLB() + ", " + starting_processing_ending_time_height_variables_and_capacityOfResource_plus_makeSpan_plus_extra_variables_for_NCumulative[tempCounter].getUB());
                            tempCounter++;
                        }
                    }
                    for (int g = 3 * numberOfTasks - 6; g < 4 * numberOfTasks - 8; g++) {
                        if (allHeights[g - 3 * (numberOfTasks - 2)][m].getLB() != 0) {
                            starting_processing_ending_time_height_variables_and_capacityOfResource_plus_makeSpan_plus_extra_variables_for_NCumulative[tempCounter] = allHeights[g - 3 * (numberOfTasks - 2)][m];
                            tempCounter++;
                        }
                    }
                    starting_processing_ending_time_height_variables_and_capacityOfResource_plus_makeSpan_plus_extra_variables_for_NCumulative[4 * countNonZerosOnEachResource[m]] = capacityVar;
                    starting_processing_ending_time_height_variables_and_capacityOfResource_plus_makeSpan_plus_extra_variables_for_NCumulative[4 * countNonZerosOnEachResource[m] + 1] = makespanForNCumulative[cumulativeConstraintCounter];

                    //starting_processing_ending_time_height_variables_and_capacityOfResource_plus_makeSpan_plus_extra_variables_for_NCumulative[4 * countNonZerosOnEachResource[m] + 1] = makespan;
                    // System.out.println("here are your variables before posting constraint");
                    //for (int j = 0; j < starting_processing_ending_time_height_variables_and_capacityOfResource_plus_makeSpan_plus_extra_variables_for_NCumulative.length; j++)
                    //  System.out.println(" j = " + j + " " + starting_processing_ending_time_height_variables_and_capacityOfResource_plus_makeSpan_plus_extra_variables_for_NCumulative[j].getLB() + " " + starting_processing_ending_time_height_variables_and_capacityOfResource_plus_makeSpan_plus_extra_variables_for_NCumulative[j].getUB());
                    if (mode1 == MODE_NO_OVERLOADCHECK && mode2 == MODE_EDGEFINDING) {
                        // solver.post(new tempEdgeFindingConstraint(starting_processing_ending_times_plus_height_variables_and_capacityOfResource_plus_makeSpan, countNonZerosOnEachResource[m], 1, capacityVector, d2, true));
                        //  System.out.println("posting constraint " + cumulativeConstraintCounter);

                        solver.post(new edgeFindingConstraint(starting_processing_ending_time_height_variables_and_capacityOfResource_plus_makeSpan_plus_extra_variables_for_NCumulative, countNonZerosOnEachResource[m], 1, capacityVector, zeroDelays, false, filterLowerBound, filterUpperBound, activateImprovingDetectionLowerBound, true, cumulativeConstraintCounter, CAPProblemConsidered, 1));

                        //   solver.post(new ModifiedTempEdgeFindingConstraint(starting_processing_ending_times_plus_height_variables_and_capacityOfResource_plus_makeSpan, countNonZerosOnEachResource[m], 1, capacityVector, d2, zeroDelays, false, filterLowerBound, filterUpperBound, false, printStuffForDebugging, 0));
                    }
                }
            }
        }

        IntVar[] arrayOfAllVariables = new IntVar[3 * (numberOfTasks - 2) + countNonZeros + numberOfResources + 1];
        // IntVar[] arrayOfAllVariables = new IntVar[3 * (numberOfTasks - 2) + countNonZeros + numberOfResources];
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
        /*
            for (int j = 0; j < numberOfTasks - 2; j++) {
            arrayOfAllVariables[g] = processingTimeVariablesForNCumulative[j];
            g++;
            }
            for (int j = 0; j < numberOfTasks - 2; j++) {
            arrayOfAllVariables[g] = endingTimesForNCumulative[j];
            g++;
            }   for (int j = 0; j < numberOfTasks - 2; j++) {
            arrayOfAllVariables[g] = makespanForNCumulative[j];
            g++;
            }
         */
        switch (heuristic) {
            case HEURISTIQUE_LEXICOGRAPHIC:
                solver.set(IntStrategyFactory.lexico_LB(arrayOfAllVariables));
                break;
            case HEURISTIQUE_LEXICO_SPLIT:
                // solver.set(IntStrategyFactory.lexico_Split(arrayOfAllVariables));
                break;
            case HEURISTIQUE_LEXICO_NEQ_LB:
                solver.set(IntStrategyFactory.lexico_Neq_LB(arrayOfAllVariables));
                break;
            default:
                break;
        }

        /*       
            counterSolutions = 0;
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
        solver.findAllOptimalSolutions(ResolutionPolicy.MINIMIZE, makespan, true);

        /*
                int co = 0;
                        if (solver.findSolution()){
                    do {
                        co++;
         //if (
                 //co >= 5570
              //    startingTimes[0].getValue() == 5
        //        &&  startingTimes[16].getValue() == 20
        //                && startingTimes[17].getValue() == 19
                    //   )
         {
                        System.out.println();
                        System.out.println("Solution number " + co);
                        for (int k = 0; k < numberOfTasks - 2; k++) {
                            System.out.print("s[" + k + "] = " + startingTimes[k].getValue() + ", ");
                        }
                                        }
                      //  if (co >= 7)
                           // System.out.println("fhf");
                    } while(solver.nextSolution());
                }
         */
        Chatterbox.printStatistics(solver);
        makespa = makespan.getValue();
        System.out.println("makespan = " + makespa);
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
        return backtracksNum;
    }

    public int makespan() {
        // System.out.println("Backtrack number = "+ backtracksNum);
        return makespa;
    }

    public static void main(String[] args) throws Exception {

        //        PrintStream out = new PrintStream(new FileOutputStream("output.txt"));
        //System.setOut(out);
        // for (int seedValue = 20; seedValue < 50; seedValue++)  {
        /// for (int k = 0; k < 29; k++)  {
        //     System.out.println("k = " + k);
      //  long seedValue = System.currentTimeMillis();

        //    long seedValue = 6;
       long seedValue = 1664169656005L;
        System.out.println("seedValue " + seedValue);

        //long seedValue = 8;
        //  System.out.println("k "+ k);
        //   System.out.println("seedValue " + seedValue);
        //  System.out.println(seedValue/ 1000L);
        //  String dataName = "bug.rcp";
        //   double delayFactor = 0.1;
        double delayFactor = 0.1;

        String dataName = "test20.rcp";

        //   String dataName = "bl20_1.rcp";
        BLBenchmarkInstances A = new BLBenchmarkInstances(dataName);
        int[] processingTimes = A.processingTimes();
        int[] d = new int[processingTimes.length];
        Random randomNumbers = new Random();
        randomNumbers.setSeed(seedValue);
        for (int i = 0; i < processingTimes.length; i++) {
            d[i] = randomNumbers.nextInt((int) Math.ceil(delayFactor * (processingTimes[i]) + 1));

            //   d[i] = randomNumbers.nextInt( (2*processingTimes[i]) + 1);
            //  d[i] = 0;
            //    System.out.println((int) Math.ceil( 0.99 * (processingTimes[i]) + 1));
            //  System.out.println("d " + d[i]);
        }

        boolean f = false;
        // false,  true
        boolean findAllOptimalSolutions = true;
        boolean filterLowerBound = true;
        boolean filterUpperBound = false;
//int heuristic = HEURISTIQUE_LEXICO_SPLIT;
        int heuristic = HEURISTIQUE_LEXICOGRAPHIC;
        //int heuristic = HEURISTIQUE_LEXICO_NEQ_LB;
        int r = 1;

        new benchmarkBLModelAdaptedWithAlbanFrameworkForNCumulative(dataName, d, MODE_NO_OVERLOADCHECK, MODE_EDGEFINDING, MODE_NO_N_CUMULATIVE, heuristic, filterLowerBound, filterUpperBound, findAllOptimalSolutions, f, r);
        new benchmarkBLModelAdaptedWithAlbanFrameworkForNCumulative(dataName, d, MODE_NO_OVERLOADCHECK, MODE_EDGEFINDING, MODE_N_CUMULATIVE, heuristic, filterLowerBound, filterUpperBound, findAllOptimalSolutions, f, r);

        System.out.println();
    }

    //  }
}
