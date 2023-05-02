package robustcumulativescheduling;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import org.chocosolver.solver.ResolutionPolicy;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.constraints.IntConstraintFactory;
import org.chocosolver.solver.constraints.LogicalConstraintFactory;
import org.chocosolver.solver.search.loop.monitors.SearchMonitorFactory;
import org.chocosolver.solver.search.strategy.IntStrategyFactory;
import org.chocosolver.solver.search.strategy.selectors.values.IntDomainMin;
import org.chocosolver.solver.search.strategy.selectors.variables.Smallest;
import org.chocosolver.solver.search.strategy.strategy.AbstractStrategy;
import org.chocosolver.solver.trace.Chatterbox;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.VariableFactory;
import org.chocosolver.util.ESat;

/**
 *
 * @author hamed
 */
public class modelOfCraneAssignmentProblemForNCumulative {

  private static final int MODE_NO_OVERLOADCHECK = 0;
    private static final int MODE_NO_EDGEFINDING = 0;
    private static final int MODE_OVERLOADCHECK = 1;
    private static final int MODE_EDGEFINDING = 1;
    public static final int LEXICOGRAPHIC_LB_HEURISTIQUE = 1;
    public static final int IMPACT_BASED_SEARCH_HEURISTIQUE = 2;
    public static final int DOMOVERWDEG_HEURISTIQUE = 3;
    public static final int SMALLEST_HEURISTIQUE = 4;
    public float elapsedTime;
    public int backtracksNum;
    private final int makespanValue;
    private final int nomSolutions;
    private final int numberOfActivities;
  //  private final int numberOfResources;
    private final int numberOfCranes;
    private int[] pos;
    private int[][] tt;
    private static final int MINUSINFINITY = -1073741824;
    public static final int TEMPORAL_PROTECTION = 2;
    private final boolean limitReachedAndNoSolutionFound;
    private boolean atLeastOneSolutionHasBeenFound;
    private static int counterSolutions;
public static final int MODE_N_CUMULATIVE = 1;
    public static final int MODE_NO_N_CUMULATIVE = 0;
    public modelOfCraneAssignmentProblemForNCumulative(int nA, int nC, int min_processingTime, int max_processingTime, int mode1, int mode2, int mode3, long seedValue, int heuristic, boolean filterLowerBound, boolean filterUpperBound, boolean activateImprovingDetectionLowerBound, boolean findAllOptimalSolutions, boolean printSolutionInformatioin, int r) throws UnsupportedEncodingException, FileNotFoundException {

        boolean CAPProblemConsidered = true;
        this.numberOfActivities = nA;
        this.numberOfCranes = nC;
    //    this.numberOfResources = nR;
        int[] capacitiesOfResources = new int[1];
        for (int y = 0; y < capacitiesOfResources.length; y++) {
            capacitiesOfResources[y] = numberOfCranes;
        }
        boolean filterMakespan = true;
        Task[] T;
        int[] ub = new int[numberOfActivities];
        for (int o = 0; o < numberOfActivities; o++) {
            ub[o] = max_processingTime;
        }
        int[] processingTimes = generateRandomArray(numberOfActivities, min_processingTime, ub, seedValue);

 //printArray(processingTimes);
        int horizon = 0;
        for (int o = 0; o < numberOfActivities; o++) {
            horizon += processingTimes[o];
        }
        int[] ub_d = new int[numberOfActivities];
        for (int s = 0; s < numberOfActivities; s++) {
            ub_d[s] = (int) Math.floor(0.25 * (processingTimes[s]) + 1);
        }
        //  printArray(ub_d);
        int[] d = generateRandomArray(numberOfActivities, 0, ub_d, seedValue);
        //    printArray(d);

        for (int i = 0; i < numberOfActivities; i++) {
            horizon += d[i];
        }
        int max_delay = maxOfArray(d);
        //    int[] ub_tt = new int[numberOfActivities];
        //    for (int o = 0; o < numberOfActivities; o++)
        //       ub_tt[o] = numberOfActivities;
        //    int X[];
        //    X = generateRandomArray(numberOfActivities, 1, ub_tt, seedValue);
        this.pos = new int[numberOfActivities];    //la position dans les baies de chaque activitÃ©    
        int[] ub_pos = new int[numberOfActivities];
        for (int o = 0; o < numberOfActivities; o++) {
            ub_pos[o] = numberOfActivities;
        }
        pos = generateRandomArray(numberOfActivities, 0, ub_pos, seedValue);//LOOK!
        //printArray(pos);
        double factor = 0.25;
        this.tt = new int[numberOfActivities][numberOfActivities];//Les temps de transition sont calculÃ©s proportionnellement Ã  la distance entre leurs positions dans la baie
        for (int i = 0; i < numberOfActivities; i++) {
            int temp_max = -1;
            for (int j = 0; j < numberOfActivities; j++) {
                //  tt[i][j] = Math.abs(X[i] - X[j]);//This conventions does not lead OC or EF to do their jobs, namely, number of backtrack are the same at the end!
                tt[i][j] = (int) (factor * Math.abs(pos[i] - pos[j]));
                if (tt[i][j] > temp_max) {
                    temp_max = tt[i][j];
                }
            }
            horizon += temp_max;
        }
        int[] heights = new int[numberOfActivities];
        for (int i = 0; i < numberOfActivities; i++) {
            heights[i] = 1;
        }
     //   horizon += max_delay;
        int numberOfTooAdjacentPositions = 0;
        for (int i = 0; i < numberOfActivities; i++) {
            for (int j = 0; j < numberOfActivities; j++) {
                if (i < j && Math.abs(pos[i] - pos[j]) <= 2) {
                    numberOfTooAdjacentPositions++;
                }
            }
        }
        horizon += numberOfTooAdjacentPositions;
        T = new Task[numberOfActivities];
        for (int i = 0; i < numberOfActivities; i++) {
            T[i] = new Task(0, horizon, horizon + d[i], processingTimes[i], d[i], heights[i]);
      //    System.out.println("Task " + i + ": " + T[i]);
        }
        int numberOfRegularPrecedences = numberOfActivities / 2;
        int[][] precedences = generatePrecedences(numberOfRegularPrecedences);//Pour chacune des baies, il existe une relation de prÃ©cÃ©dence entre les activitÃ©s en-dessous et au-dessus du pont
        Solver m = new Solver();
        IntVar[] startingTimes = new IntVar[numberOfActivities];
        for (int i = 0; i < numberOfActivities; i++) {
            startingTimes[i] = VariableFactory.bounded("startingTimes1[" + i + "]", T[i].earliestStartingTime(), T[i].latestStartingTime(), m);
        }
        IntVar[] processingTimeVariables = new IntVar[numberOfActivities];
        for (int i = 0; i < numberOfActivities; i++) {
            processingTimeVariables[i] = VariableFactory.fixed(processingTimes[i], m);
        }

        IntVar[] endingTimes = new IntVar[numberOfActivities];
        for (int i = 0; i < numberOfActivities; i++) {
            endingTimes[i] = VariableFactory.bounded("endingTimes1[i]", processingTimes[i], T[i].latestCompletionTime(), m);
        }

        IntVar[] h = new IntVar[numberOfActivities];
        for (int i = 0; i < numberOfActivities; i++) {
            h[i] = VariableFactory.fixed(heights[i], m);
        }
        IntVar[] c = new IntVar[numberOfActivities];
        for (int i = 0; i < numberOfActivities; i++) {
            c[i] = VariableFactory.bounded("c[i]", 0, numberOfCranes - 1, m);
        }
        int[] temp_arr = new int[numberOfActivities];
        for (int o = 0; o < numberOfActivities; o++) {
            temp_arr[o] = T[o].delayedLatestCompletionTime();
        }
        int upperBoundOfHorizon = maxOfArray(temp_arr);
        IntVar makespan = VariableFactory.bounded("objective", 0, 10000000, m);
        IntVar[] processingTimeVariablesForNCumulative = new IntVar[numberOfActivities];
        IntVar[] endingTimesForNCumulative = new IntVar[numberOfActivities];
        IntVar[] makespanForNCumulative = new IntVar[numberOfActivities];

        for (int i = 0; i < numberOfActivities; i++) {
            processingTimeVariablesForNCumulative[i] = VariableFactory.fixed(processingTimes[i] + d[i], m);
            endingTimesForNCumulative[i] = VariableFactory.bounded("endingTimesForNCumulative1[i]", processingTimes[i] + d[i], T[i].delayedLatestCompletionTime(), m);
       makespanForNCumulative[i] = VariableFactory.bounded("objective", 0, 10000000, m);
        }
        for (int i = 0; i < numberOfActivities; i++) {
            m.post(IntConstraintFactory.arithm(endingTimes[i], "-", startingTimes[i], "=", processingTimes[i]));
            m.post(IntConstraintFactory.arithm(endingTimesForNCumulative[i], "-", startingTimes[i], "=", processingTimes[i] + d[i]));
            m.post(IntConstraintFactory.arithm(makespan, "-", endingTimes[i], ">=", d[i]));
            m.post(IntConstraintFactory.arithm(makespan, "-", endingTimesForNCumulative[i], ">=", 0));
            m.post(IntConstraintFactory.arithm(makespanForNCumulative[i], "-", endingTimesForNCumulative[i], ">=", 0));       
                    for (int j = 0; j < numberOfActivities; j++) {
                m.post(IntConstraintFactory.arithm(makespanForNCumulative[i], "-", endingTimes[j], ">=", 0));
            }
        }
        for (int i = 0; i < numberOfActivities; i++) {
            for (int j = 0; j < numberOfActivities; j++) {
                if (precedences[i][j] == 1) {
                    m.post(IntConstraintFactory.arithm(startingTimes[j], "-", endingTimes[i], ">=", d[i]));
               //     m.post(IntConstraintFactory.arithm(startingTimes[j], "-", endingTimesForNCumulative[i], ">", 0));

                }
            }
        }
//les contraintes du problÃ¨me de dÃ©charge-ment de grues:si la distance dans le temps entre deux activitÃ©s
//ne permet pas quâ€™une mÃªme 
//grue sâ€™en occupe (condition de la ligne 8) alors lâ€™activitÃ© dâ€™indice la plus petite doit Ãªtre affectÃ©e Ã  
//la grue dâ€™indice la plus petite, (les activitÃ©s comme les grues sont triÃ©es de la gauche vers la droite)         

//The constraint implies that if i and j overlap, but s_i starts first and ends after 
        //j has started (in other words j starts while i is underway), then c_i < c_j       
        for (int i = 0; i < numberOfActivities; i++) {
            for (int j = 0; j < numberOfActivities; j++) {
                if (i != j && pos[i] < pos[j]) {
                    Constraint mc1 = IntConstraintFactory.arithm(startingTimes[i], "-", endingTimes[j], "<", (tt[i][j] + d[j]));
                    Constraint mc2 = IntConstraintFactory.arithm(startingTimes[j], "-", endingTimes[i], "<", (tt[i][j] + d[i]));
                    Constraint mc3 = LogicalConstraintFactory.and(mc1, mc2);
                    Constraint mc4 = IntConstraintFactory.arithm(c[i], "-", c[j], "<", 0);
                    LogicalConstraintFactory.ifThen(mc3, mc4);
                }
            }
        }
        
         for (int i = 0; i < numberOfActivities; i++) {
            for (int j = 0; j < numberOfActivities; j++) {
                if (i != j && pos[i] < pos[j]) {
                    Constraint mc1 = IntConstraintFactory.arithm(startingTimes[i], "-", endingTimesForNCumulative[j], "<", tt[i][j]);
                    Constraint mc2 = IntConstraintFactory.arithm(startingTimes[j], "-", endingTimesForNCumulative[i], "<", tt[i][j]);
                    Constraint mc3 = LogicalConstraintFactory.and(mc1, mc2);
                    Constraint mc4 = IntConstraintFactory.arithm(c[i], "-", c[j], "<", 0);
                    LogicalConstraintFactory.ifThen(mc3, mc4);
                }
            }
        }
        
        
        //une contrainte de distance temporelle entre activitÃ©s ayant une distance physique faible. Ceci permet dâ€™Ã©viter une collision
        //lors du dÃ©chargement simultanÃ© de deux conteneurs trop proches.
        for (int i = 0; i < numberOfActivities; i++) {
            for (int j = 0; j < numberOfActivities; j++) {
                if (i < j && Math.abs(pos[i] - pos[j]) <= 2) {
                    Constraint a = IntConstraintFactory.arithm(startingTimes[i], "-", endingTimes[j], ">", d[j]);
                    Constraint b = IntConstraintFactory.arithm(startingTimes[j], "-", endingTimes[i], ">", d[i]);
                    m.post(LogicalConstraintFactory.or(a, b));
                }
            }
        }
        
                for (int i = 0; i < numberOfActivities; i++) {
            for (int j = 0; j < numberOfActivities; j++) {
                if (i < j && Math.abs(pos[i] - pos[j]) <= 2) {
                    Constraint a = IntConstraintFactory.arithm(startingTimes[i], "-", endingTimesForNCumulative[j], ">", 0);
                    Constraint b = IntConstraintFactory.arithm(startingTimes[j], "-", endingTimesForNCumulative[i], ">", 0);
                    m.post(LogicalConstraintFactory.or(a, b));
                }
            }
        }
        
        
        IntVar[] allCapacityVars = new IntVar[1];
    
            IntVar[] starting_processing_ending_times_plus_height_variables_and_capacityOfResource = new IntVar[4 * numberOfActivities + 1];
            int C = capacitiesOfResources[0];
            IntVar capacityVar = VariableFactory.fixed(C, m);
            int[] capacityVector = new int[1];
            capacityVector[0] = C;
            allCapacityVars[0] = VariableFactory.fixed(C, m);
            int tempCounter;
            tempCounter = 0;
            for (int g = 0; g < numberOfActivities; g++) {
                starting_processing_ending_times_plus_height_variables_and_capacityOfResource[tempCounter] = startingTimes[g];
                tempCounter++;
            }
            for (int g = numberOfActivities; g < 2 * numberOfActivities; g++) {
                starting_processing_ending_times_plus_height_variables_and_capacityOfResource[tempCounter] = processingTimeVariables[g - (numberOfActivities)];
                tempCounter++;
            }
            for (int g = 2 * numberOfActivities; g < 3 * numberOfActivities; g++) {
                starting_processing_ending_times_plus_height_variables_and_capacityOfResource[tempCounter] = endingTimes[g - 2 * (numberOfActivities)];
                tempCounter++;
            }
            for (int g = 3 * numberOfActivities; g < 4 * numberOfActivities; g++) {
                starting_processing_ending_times_plus_height_variables_and_capacityOfResource[tempCounter] = h[g - 3 * (numberOfActivities)];
                tempCounter++;
            }
            starting_processing_ending_times_plus_height_variables_and_capacityOfResource[4 * numberOfActivities] = capacityVar;
        if (mode1 == MODE_NO_OVERLOADCHECK && mode2 == MODE_EDGEFINDING) 
                        m.post(new PropTTDynamicSweep_RobustConstraint(starting_processing_ending_times_plus_height_variables_and_capacityOfResource, numberOfActivities, 1, capacityVector, d));


    IntVar[] starting_processing_ending_time_height_variables_and_capacityOfResource_plus_makeSpan = new IntVar[4 * numberOfActivities + 2];;
    
    if (mode3 == MODE_NO_N_CUMULATIVE) {
       //     for (int f = 0; f < numberOfResources; f++) {
                //    IntVar[] starting_processing_ending_times_plus_height_variables_and_capacityOfResource = new IntVar[4 * numberOfActivities + 1];
           //     IntVar[] starting_processing_ending_times_plus_height_variables_and_capacityOfResource_plus_makeSpan = new IntVar[4 * numberOfActivities + 2];
               
                tempCounter = 0;
                for (int g = 0; g < numberOfActivities; g++) {
                    //    starting_processing_ending_times_plus_height_variables_and_capacityOfResource[tempCounter] = startingTimes[g];
                    starting_processing_ending_time_height_variables_and_capacityOfResource_plus_makeSpan[tempCounter] = startingTimes[g];
                    tempCounter++;
                }
                for (int g = numberOfActivities; g < 2 * numberOfActivities; g++) {
                    //    starting_processing_ending_times_plus_height_variables_and_capacityOfResource[tempCounter] = processingTimeVariables[g - (numberOfActivities)];
                    starting_processing_ending_time_height_variables_and_capacityOfResource_plus_makeSpan[tempCounter] = processingTimeVariables[g - (numberOfActivities)];
                    tempCounter++;
                }
                for (int g = 2 * numberOfActivities; g < 3 * numberOfActivities; g++) {
                   //     starting_processing_ending_times_plus_height_variables_and_capacityOfResource[tempCounter] = endingTimes[g - 2 * (numberOfActivities)];
                    starting_processing_ending_time_height_variables_and_capacityOfResource_plus_makeSpan[tempCounter] = endingTimes[g - 2 * (numberOfActivities)];
                    tempCounter++;
                }
                for (int g = 3 * numberOfActivities; g < 4 * numberOfActivities; g++) {
                     //  starting_processing_ending_times_plus_height_variables_and_capacityOfResource[tempCounter] = h[g - 3 * (numberOfActivities)];
                    starting_processing_ending_time_height_variables_and_capacityOfResource_plus_makeSpan[tempCounter] = h[g - 3 * (numberOfActivities)];
                    tempCounter++;
                }
                //  starting_processing_ending_times_plus_height_variables_and_capacityOfResource[4 * numberOfActivities] = capacityVar;
                starting_processing_ending_time_height_variables_and_capacityOfResource_plus_makeSpan[4 * numberOfActivities] = capacityVar;
                starting_processing_ending_time_height_variables_and_capacityOfResource_plus_makeSpan[4 * numberOfActivities + 1] = makespan;
//               if (mode1 == MODE_NO_OVERLOADCHECK && mode2 == MODE_NO_EDGEFINDING) {
//                m.post(new PropTTDynamicSweep_RobustConstraint(starting_processing_ending_times_plus_height_variables_and_capacityOfResource, numberOfActivities, 1, capacityVector, d));
//            } else
//                   if (mode1 == MODE_OVERLOADCHECK && mode2 == MODE_NO_EDGEFINDING) {
//                    m.post(new overloadCheckingConstraint(starting_processing_ending_time_height_variables_and_capacityOfResource_plus_makeSpan, numberOfActivities, 1, capacityVector, d, filterMakespan, r));
//                      //  m.post(new PropTTDynamicSweep_RobustConstraint(starting_processing_ending_times_plus_height_variables_and_capacityOfResource, numberOfActivities, 1, capacityVector, d));
//
//                } else
                       if (mode1 == MODE_NO_OVERLOADCHECK && mode2 == MODE_EDGEFINDING) {
                    m.post(new edgeFindingConstraint(starting_processing_ending_time_height_variables_and_capacityOfResource_plus_makeSpan, numberOfActivities, 1, capacityVector, d, filterMakespan, filterLowerBound, filterUpperBound, activateImprovingDetectionLowerBound, false, 0, CAPProblemConsidered, r));
                   //    m.post(new PropTTDynamicSweep_RobustConstraint(starting_processing_ending_times_plus_height_variables_and_capacityOfResource, numberOfActivities, 1, capacityVector, d));
                }
       //     }
        } else if (mode3 == MODE_N_CUMULATIVE) {

                 //   int tempCounter;
                 //   tempCounter = 0;
                    for (int g = 0; g < numberOfActivities; g++) {
                         //  starting_processing_ending_times_plus_height_variables_and_capacityOfResource[g] = startingTimes[g];
                        starting_processing_ending_time_height_variables_and_capacityOfResource_plus_makeSpan[g] = startingTimes[g];
                    //    tempCounter++;
                    }
                    
                    
                  for (int g = 3 * numberOfActivities; g < 4 * numberOfActivities; g++) {
                          //  starting_processing_ending_times_plus_height_variables_and_capacityOfResource[g] = h[g - 3 * (numberOfActivities)];
                        starting_processing_ending_time_height_variables_and_capacityOfResource_plus_makeSpan[g] = h[g - 3 * (numberOfActivities)];
                     //   tempCounter++;
                    }
                     //  starting_processing_ending_times_plus_height_variables_and_capacityOfResource[4 * numberOfActivities] = capacityVar;
                    starting_processing_ending_time_height_variables_and_capacityOfResource_plus_makeSpan[4 * numberOfActivities] = capacityVar;
  
                    
            for (int cumulativeConstraintCounter = 0; cumulativeConstraintCounter < numberOfActivities; cumulativeConstraintCounter++) {

                    
                   
                    for (int g = numberOfActivities; g < 2 * numberOfActivities; g++) {
                        if (cumulativeConstraintCounter == g - (numberOfActivities)) {
                           starting_processing_ending_time_height_variables_and_capacityOfResource_plus_makeSpan[g] = processingTimeVariablesForNCumulative[cumulativeConstraintCounter];
                         //   starting_processing_ending_time_height_variables_and_capacityOfResource_plus_makeSpan[g] = VariableFactory.fixed(processingTimes[cumulativeConstraintCounter] + d[cumulativeConstraintCounter], m);
                        } else {
                                                 //     starting_processing_ending_times_plus_height_variables_and_capacityOfResource[g] = processingTimeVariables[g - (numberOfActivities)];
 starting_processing_ending_time_height_variables_and_capacityOfResource_plus_makeSpan[g] = processingTimeVariables[g - (numberOfActivities)];

                        }

                    //    tempCounter++;
                    }
                    for (int g = 2 * numberOfActivities; g < 3 * numberOfActivities; g++) {

                        if (cumulativeConstraintCounter == g - 2 * numberOfActivities) {
                           starting_processing_ending_time_height_variables_and_capacityOfResource_plus_makeSpan[g] = endingTimesForNCumulative[cumulativeConstraintCounter];
                        //    starting_processing_ending_time_height_variables_and_capacityOfResource_plus_makeSpan[g] = VariableFactory.bounded("endingTimesForNCumulative1[i]", T[cumulativeConstraintCounter].earliestStartingTime() + processingTimes[cumulativeConstraintCounter] + d[cumulativeConstraintCounter], T[cumulativeConstraintCounter].delayedLatestCompletionTime(), m);
                    
                        
                        
                        } else {
                                                   //    starting_processing_ending_times_plus_height_variables_and_capacityOfResource[g] = endingTimes[g - 2 * (numberOfActivities)];
 starting_processing_ending_time_height_variables_and_capacityOfResource_plus_makeSpan[g] = endingTimes[g - 2 * (numberOfActivities)];

                        }
                      //  tempCounter++;
                    }
                                        starting_processing_ending_time_height_variables_and_capacityOfResource_plus_makeSpan[4 * numberOfActivities + 1] = makespanForNCumulative[cumulativeConstraintCounter];

//  if (mode1 == MODE_NO_OVERLOADCHECK && mode2 == MODE_NO_EDGEFINDING) {
//                m.post(new PropTTDynamicSweep_RobustConstraint(starting_processing_ending_times_plus_height_variables_and_capacityOfResource, numberOfActivities, 1, capacityVector, d));
//  }
//                  else
//      if (mode1 == MODE_OVERLOADCHECK && mode2 == MODE_NO_EDGEFINDING) {
//                        m.post(new overloadCheckingConstraint(starting_processing_ending_time_height_variables_and_capacityOfResource_plus_makeSpan, numberOfActivities, 1, capacityVector, d, filterMakespan, r));
//                       //    m.post(new PropTTDynamicSweep_RobustConstraint(starting_processing_ending_times_plus_height_variables_and_capacityOfResource, numberOfActivities, 1, capacityVector, d));
//
//                    } else
          if (mode1 == MODE_NO_OVERLOADCHECK && mode2 == MODE_EDGEFINDING) {
                        m.post(new edgeFindingConstraint(starting_processing_ending_time_height_variables_and_capacityOfResource_plus_makeSpan, numberOfActivities, 1, capacityVector, d, filterMakespan, filterLowerBound, filterUpperBound, activateImprovingDetectionLowerBound, false, cumulativeConstraintCounter, CAPProblemConsidered, r));
                       //     m.post(new PropTTDynamicSweep_RobustConstraint(starting_processing_ending_times_plus_height_variables_and_capacityOfResource, numberOfActivities, 1, capacityVector, d));
                    }
           //     }

            }

        }
      /*  else if (mode3 == TEMPORAL_PROTECTION) {
            
            
                tempCounter = 0;
                for (int g = 0; g < numberOfActivities; g++) {
                    //    starting_processing_ending_times_plus_height_variables_and_capacityOfResource[tempCounter] = startingTimes[g];
                    starting_processing_ending_time_height_variables_and_capacityOfResource_plus_makeSpan[tempCounter] = startingTimes[g];
                    tempCounter++;
                }
                for (int g = numberOfActivities; g < 2 * numberOfActivities; g++) {
 //starting_processing_ending_times_plus_height_variables_and_capacityOfResource[tempCounter] =  VariableFactory.fixed(processingTimes[g - (numberOfActivities)] + d[g - (numberOfActivities)], m);
                    starting_processing_ending_time_height_variables_and_capacityOfResource_plus_makeSpan[tempCounter] = 
                            VariableFactory.fixed(processingTimes[g - (numberOfActivities)] + d[g - (numberOfActivities)], m);
                    tempCounter++;
                }
                for (int g = 2 * numberOfActivities; g < 3 * numberOfActivities; g++) {
                    //    starting_processing_ending_times_plus_height_variables_and_capacityOfResource[tempCounter] = VariableFactory.bounded("endingTimesForNCumulative1[i]", T[g - 2 * (numberOfActivities)].earliestStartingTime() + processingTimes[g - 2 * (numberOfActivities)] + d[g - 2 * (numberOfActivities)], T[g - 2 * (numberOfActivities)].delayedLatestCompletionTime(), m);
                    starting_processing_ending_time_height_variables_and_capacityOfResource_plus_makeSpan[tempCounter] = 
                    VariableFactory.bounded("endingTimesForNCumulative1[i]", T[g - 2 * (numberOfActivities)].earliestStartingTime() + processingTimes[g - 2 * (numberOfActivities)] + d[g - 2 * (numberOfActivities)], T[g - 2 * (numberOfActivities)].delayedLatestCompletionTime(), m);
                    tempCounter++;
                }
                for (int g = 3 * numberOfActivities; g < 4 * numberOfActivities; g++) {
                    //   starting_processing_ending_times_plus_height_variables_and_capacityOfResource[tempCounter] = h[g - 3 * (numberOfActivities)];
                    starting_processing_ending_time_height_variables_and_capacityOfResource_plus_makeSpan[tempCounter] = h[g - 3 * (numberOfActivities)];
                    tempCounter++;
                }
               //   starting_processing_ending_times_plus_height_variables_and_capacityOfResource[4 * numberOfActivities] = capacityVar;
                starting_processing_ending_time_height_variables_and_capacityOfResource_plus_makeSpan[4 * numberOfActivities] = capacityVar;
                starting_processing_ending_time_height_variables_and_capacityOfResource_plus_makeSpan[4 * numberOfActivities + 1] = makespan;
//               if (mode1 == MODE_NO_OVERLOADCHECK && mode2 == MODE_NO_EDGEFINDING) {
//                m.post(new PropTTDynamicSweep_RobustConstraint(starting_processing_ending_times_plus_height_variables_and_capacityOfResource, numberOfActivities, 1, capacityVector, d));
//            } else
                   if (mode1 == MODE_OVERLOADCHECK && mode2 == MODE_NO_EDGEFINDING) {
                    m.post(new overloadCheckingConstraint(starting_processing_ending_time_height_variables_and_capacityOfResource_plus_makeSpan, numberOfActivities, 1, capacityVector, d, filterMakespan, r));
                    //    m.post(new PropTTDynamicSweep_RobustConstraint(starting_processing_ending_times_plus_height_variables_and_capacityOfResource, numberOfActivities, 1, capacityVector, d));

                } else if (mode1 == MODE_NO_OVERLOADCHECK && mode2 == MODE_EDGEFINDING) {
                    m.post(new edgeFindingConstraint(starting_processing_ending_time_height_variables_and_capacityOfResource_plus_makeSpan, numberOfActivities, 1, capacityVector, d, filterMakespan, filterLowerBound, filterUpperBound, activateImprovingDetectionLowerBound, false, 0, CAPProblemConsidered, r));
                  //     m.post(new PropTTDynamicSweep_RobustConstraint(starting_processing_ending_times_plus_height_variables_and_capacityOfResource, numberOfActivities, 1, capacityVector, d));
                } 
               
            }  
        */
int yo = 0;
        //  IntVar[] arrayOfStartingTimeVariables = new IntVar[numberOfActivities];
        IntVar[] arrayOfAllVariablesExcludingStartingTimes = new IntVar[4 * (numberOfActivities) + 1 + 1];

        //    IntVar[] arrayOfAllVariablesExcludingCranes = new IntVar[4 * (numberOfActivities) + numberOfResources + 1];
        for (int g = 0; g < numberOfActivities; g++) {
            ///   arrayOfAllVariablesExcludingCranes[g] = startingTimes[g];
            //  arrayOfStartingTimeVariables[g] = startingTimes[g];
            arrayOfAllVariablesExcludingStartingTimes[g] = processingTimeVariables[g];
    //   yo = g + numberOfActivities;
   //    System.out.println("g = " + g + ", yo = " + yo);
                 //   arrayOfAllVariablesExcludingStartingTimes[g] = starting_processing_ending_time_height_variables_and_capacityOfResource_plus_makeSpan[yo];

        
        }
        for (int g = numberOfActivities; g < 2 * numberOfActivities; g++) {
            //  arrayOfAllVariablesExcludingCranes[g] = processingTimeVariables[g - (numberOfActivities)];
            arrayOfAllVariablesExcludingStartingTimes[g] = endingTimes[g - (numberOfActivities)];
   //    yo = g + numberOfActivities;
    //   System.out.println("g = " + g + ", yo = " + yo);
 //arrayOfAllVariablesExcludingStartingTimes[g] = starting_processing_ending_time_height_variables_and_capacityOfResource_plus_makeSpan[yo];

        
        }
     /*   for (int g = 2 * numberOfActivities; g < 3 * numberOfActivities; g++) {
            //  arrayOfAllVariablesExcludingCranes[g] = processingTimeVariables[g - (numberOfActivities)];
            arrayOfAllVariablesExcludingStartingTimes[g] = endingTimesForNCumulative[g -  2 * (numberOfActivities)];
        }*/
        for (int g = 2 * numberOfActivities; g < 3 * numberOfActivities; g++) {
            //   arrayOfAllVariablesExcludingCranes[g] = endingTimes[g - 2 * (numberOfActivities)];
            arrayOfAllVariablesExcludingStartingTimes[g] = h[g - 2 * (numberOfActivities)];
               
            //     yo = g + numberOfActivities;;
     //  System.out.println("g = " + g + ", yo = " + yo);

       //  arrayOfAllVariablesExcludingStartingTimes[g] = starting_processing_ending_time_height_variables_and_capacityOfResource_plus_makeSpan[yo];

        
        }
        for (int g = 3 * numberOfActivities; g < 4 * numberOfActivities; g++) {
            //  arrayOfAllVariablesExcludingCranes[g] = h[g - 3 * (numberOfActivities)];
          //  arrayOfAllVariablesExcludingStartingTimes[g] = c[g - 4 * (numberOfActivities)];
     //  System.out.println("g = " + g + ", yo = " + yo);
                    arrayOfAllVariablesExcludingStartingTimes[g] = c[g - 3 * (numberOfActivities)];

        
        }
        int g = 4 * numberOfActivities;
     //   for (int j = 0; j < numberOfResources; j++) {
            //  arrayOfAllVariablesExcludingCranes[g] = allCapacityVars[j];
            arrayOfAllVariablesExcludingStartingTimes[g] = allCapacityVars[0];
            g++;
      //  }
        //  arrayOfAllVariablesExcludingCranes[4 * (numberOfActivities) + numberOfResources] = makespan;         
        arrayOfAllVariablesExcludingStartingTimes[4 * (numberOfActivities) + 1] = makespan;
        //for (int w = 0; w < arrayOfAllVariables.length; w++) {
        // System.out.println("domain of variable " + w + ": [" + arrayOfAllVariables[w].getLB() + ", " + arrayOfAllVariables[w].getUB() + "]");
        //}
        AbstractStrategy[] strategies = new AbstractStrategy[2];
        switch (heuristic) {
            case LEXICOGRAPHIC_LB_HEURISTIQUE:
                strategies[0] = IntStrategyFactory.custom(new Smallest(), new IntDomainMin(), startingTimes);
                strategies[1] = IntStrategyFactory.lexico_LB(arrayOfAllVariablesExcludingStartingTimes);
                m.set(IntStrategyFactory.sequencer(strategies));
                break;
            case IMPACT_BASED_SEARCH_HEURISTIQUE:
                strategies[0] = IntStrategyFactory.custom(new Smallest(), new IntDomainMin(), startingTimes);
                strategies[1] = IntStrategyFactory.impact(arrayOfAllVariablesExcludingStartingTimes, 1);
                m.set(IntStrategyFactory.sequencer(strategies));
                break;
            case DOMOVERWDEG_HEURISTIQUE:
                strategies[0] = IntStrategyFactory.custom(new Smallest(), new IntDomainMin(), startingTimes);
                strategies[1] = IntStrategyFactory.domOverWDeg(arrayOfAllVariablesExcludingStartingTimes, 1);
                m.set(IntStrategyFactory.sequencer(strategies));
                break;
            case SMALLEST_HEURISTIQUE:
                strategies[0] = IntStrategyFactory.custom(new Smallest(), new IntDomainMin(), startingTimes);
                strategies[1] = IntStrategyFactory.custom(new Smallest(), new IntDomainMin(), arrayOfAllVariablesExcludingStartingTimes);
                m.set(IntStrategyFactory.sequencer(strategies));
                break;
            default:
                break;
        }
        SearchMonitorFactory.limitTime(m, "600s");
        m.getSearchLoop().hasReachedLimit();
 
        /*
         counterSolutions = 0;
        final LinkedList<Solution> optSols = new LinkedList<>();
        m.getSearchLoop().plugSearchMonitor(new IMonitorSolution() {
            int bestObj=1073741824;
            @Override
            public void onSolution() {
                //   System.out.println("solution "+a + ":" + b+" has been found");
                     System.out.print("solution number " + ++counterSolutions + ": ");             
                     for (int k = 0; k < numberOfActivities; k++) {
                    System.out.print("s[" + k + "] = " + startingTimes[k].getValue() + ", ");
                }
             //   System.out.print("\nmakespan = " + makespan.getValue() );
              //  System.out.print("\nmakespan in [" + makespan.getLB() + "]");
             //   System.out.print("\nmakespan in [" + makespan.getUB() + "]");

System.out.println();
                int currentObj = makespan.getValue();
                // try to get better or equal solutions
                m.post(ICF.arithm(makespan,"<=",currentObj));
                // only keeps the best solutions found so far
                if(bestObj>currentObj){
                    bestObj = currentObj;
                    optSols.clear();
                }
                Solution sol = new Solution();
                sol.record(m);
                optSols.add(sol);
            }
        });
      //  solver.findAllSolutions();
m.findAllOptimalSolutions(ResolutionPolicy.MINIMIZE, makespan, true);
 */       
        if (findAllOptimalSolutions) {
            m.findAllOptimalSolutions(ResolutionPolicy.MINIMIZE, makespan, true);
        } else {
            m.findOptimalSolution(ResolutionPolicy.MINIMIZE, makespan);
        }

        //  m.findSolution();
        if (m.isFeasible() == ESat.TRUE) {
            //  System.out.println("hhhiooo");
            this.atLeastOneSolutionHasBeenFound = true;
        }

        for (int i = 0; i < numberOfActivities; i++) {
            //  System.out.println("s["+ i + "] = " + startingTimes[i].getValue());
            //  System.out.println("e["+ i + "] = " + endingTimes[i].getValue());
        }
        for (int i = 0; i < numberOfActivities; i++) {
            // System.out.println("c["+ i + "] = " + c[i].getValue());
        }
//   System.out.println();

        this.limitReachedAndNoSolutionFound = m.hasReachedLimit();

//Esat atLeastOneSolutionHasBeenFound = new Esat();
//m.i
//int y = m.i
//this.atLeastOneSolutionHasBeenFound = m.i
        if (printSolutionInformatioin) {
            Chatterbox.printStatistics(m);
            //  Chatterbox.printVersion(m);
            //   Chatterbox.showSolutions(m);

        }
        this.makespanValue = makespan.getValue();
        this.backtracksNum = (int) m.getMeasures().getBackTrackCount();
        this.elapsedTime = m.getMeasures().getTimeCount();
        this.nomSolutions = (int) m.getMeasures().getSolutionCount();

        //    boolean temp1 = m.getMeasures().isObjectiveOptimal();
        //  System.out.println(temp1);
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

    public boolean timeLimitHasBypassedTheSearchProcess() {
        return limitReachedAndNoSolutionFound;
    }

    public boolean aSubOptimalSolutionIsFound() {
        return atLeastOneSolutionHasBeenFound;
    }

    public static void main(String[] args) throws Exception {

        long seedValue;
        int r = 1;
        int[] d;
        //     seedValue = 1;
        int k = 0;

      //    for (k = 1; k < 20; k++) {
        System.out.println("k = " + k);

          seedValue = System.currentTimeMillis();
//returns the difference measured in milliseconds between the current time and midnight, January 1970 UTC
   //    seedValue = 1593191695494L;
      
     System.out.println("seedValue: " + seedValue + "L");
        int heuristic;
          heuristic = LEXICOGRAPHIC_LB_HEURISTIQUE;
        //    heuristic = LEXICO_SPLIT_HEURISTIQUE;
        //     heuristic = LEXICO_NEQ_LB_HEURISTIQUE;
      //  heuristic = IMPACT_BASED_SEARCH_HEURISTIQUE;
        //   heuristic = DOMOVERWDEG_HEURISTIQUE;
        //    heuristic = SMALLEST_HEURISTIQUE;
        boolean activateImprovingDetectionLowerBound = !true;
        boolean printSolutionInformatioin = true;
        boolean findAllOptimalSolutions = true;
        boolean filterLowerBound = true;
        boolean filterUpperBound = true;
        int numberOfActivities = 7;
        int numberOfCranes = 2;
        int numberOfResources = 1;
        // int min_processingTime = 1;
        int min_processingTime = 1;
        int max_processingTime = 12;

        boolean nCumulativePosted = false;
        //  int max_processingTime = 796;
        /*
          Scanner input = new Scanner(System.in);
          System.out.print("Enter the number of activities: "); // prompt
          int numberOfActivities = input.nextInt(); // read first number from user
          System.out.print("Enter the number of cranes: "); // prompt
          int numberOfCranes = input.nextInt(); // read first number from user
         */
modelOfCraneAssignmentProblemForNCumulative X;
modelOfCraneAssignmentProblemForNCumulative Y;
modelOfCraneAssignmentProblemForNCumulative Z; 
   
printStuffForYourInformation(filterLowerBound, filterUpperBound, numberOfActivities, numberOfCranes, numberOfResources, min_processingTime, max_processingTime, heuristic, findAllOptimalSolutions);
             System.out.println("EF-TT WITHOUT N_CUMULATIVE POSTED");
         Z = new modelOfCraneAssignmentProblemForNCumulative(numberOfActivities, numberOfCranes, min_processingTime, max_processingTime, MODE_NO_OVERLOADCHECK, MODE_EDGEFINDING, MODE_NO_N_CUMULATIVE, seedValue, heuristic, filterLowerBound, filterUpperBound, activateImprovingDetectionLowerBound, findAllOptimalSolutions, printSolutionInformatioin, r);
                   

         System.out.println("TT-EF a time limit has bypassed the search process? " + Z.timeLimitHasBypassedTheSearchProcess());

         System.out.println("TT-EF a solution was found within the timeout? = " + Z.aSubOptimalSolutionIsFound());

       System.out.println("TT-EF makespan = " + Z.makespan());
System.out.println();
             System.out.println("EF-TT WITH N_CUMULATIVE POSTED");
         Z = new modelOfCraneAssignmentProblemForNCumulative(numberOfActivities, numberOfCranes, min_processingTime, max_processingTime, MODE_NO_OVERLOADCHECK, MODE_EDGEFINDING, MODE_N_CUMULATIVE, seedValue, heuristic, filterLowerBound, filterUpperBound, activateImprovingDetectionLowerBound, findAllOptimalSolutions, printSolutionInformatioin, r);
                   

        System.out.println("TT-EF a time limit has bypassed the search process? " + Z.timeLimitHasBypassedTheSearchProcess());

        System.out.println("TT-EF a solution was found within the timeout? = " + Z.aSubOptimalSolutionIsFound());

        System.out.println("TT-EF makespan = " + Z.makespan());

        
        
        
        
        
        
        
        
        /*
        System.out.println();
        printStuffForYourInformation(filterLowerBound, filterUpperBound, numberOfActivities, numberOfCranes, numberOfResources, min_processingTime, max_processingTime, heuristic, findAllOptimalSolutions);
        System.out.println("TT");
         X = new modelOfCraneAssignmentProblemForTemporalProtection(numberOfActivities, numberOfCranes, min_processingTime, max_processingTime, MODE_NO_OVERLOADCHECK, MODE_NO_EDGEFINDING, TEMPORAL_PROTECTION, seedValue, heuristic, filterLowerBound, filterUpperBound, activateImprovingDetectionLowerBound, findAllOptimalSolutions, printSolutionInformatioin, r);
        System.out.println("OC-TT");
         Y = new modelOfCraneAssignmentProblemForTemporalProtection(numberOfActivities, numberOfCranes, min_processingTime, max_processingTime, MODE_OVERLOADCHECK, MODE_NO_EDGEFINDING, TEMPORAL_PROTECTION, seedValue, heuristic, filterLowerBound, filterUpperBound, activateImprovingDetectionLowerBound, findAllOptimalSolutions, printSolutionInformatioin, r);
        System.out.println("EF-TT");
         Z = new modelOfCraneAssignmentProblemForTemporalProtection(numberOfActivities, numberOfCranes, min_processingTime, max_processingTime, MODE_NO_OVERLOADCHECK, MODE_EDGEFINDING, TEMPORAL_PROTECTION, seedValue, heuristic, filterLowerBound, filterUpperBound, activateImprovingDetectionLowerBound, findAllOptimalSolutions, printSolutionInformatioin, r);
                   

        System.out.println("TT a time limit has bypassed the search process? " + X.timeLimitHasBypassedTheSearchProcess());
        System.out.println("TT-OC a time limit has bypassed the search process? " + Y.timeLimitHasBypassedTheSearchProcess());
        System.out.println("TT-EF a time limit has bypassed the search process? " + Z.timeLimitHasBypassedTheSearchProcess());

        System.out.println("TT a solution was found within the timeout? = " + X.aSubOptimalSolutionIsFound());
        System.out.println("TT-OC a solution was found within the timeout? = " + Y.aSubOptimalSolutionIsFound());
        System.out.println("TT-EF a solution was found within the timeout? = " + Z.aSubOptimalSolutionIsFound());

        System.out.println("TT makespan = " + X.makespan());
        System.out.println("TT-OC makespan = " + Y.makespan());
        System.out.println("TT-EF makespan = " + Z.makespan());
        */
        
        
        
        
        
      //    } 
    }

    public static int[] generateRandomArray(int numberOfTasks, int lb, int[] ub, long seedValue) {

        int[] randomlyGeneratedArray = new int[numberOfTasks];
        Random randomNumbers = new Random();
        randomNumbers.setSeed(seedValue);
        for (int i = 0; i < numberOfTasks; i++) {
            if (ub[i] != 0) {
                randomlyGeneratedArray[i] = lb + randomNumbers.nextInt(ub[i]);
            }
        }
        return randomlyGeneratedArray;
    }

    public static void shuffleArray(int[] array) {
        List<Integer> list = new ArrayList<>();
        for (int i : array) {
            list.add(i);
        }
        Collections.shuffle(list);
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }
    }

    public static void printArray(int A[]) {
        for (int i = 0; i < A.length; i++) {
            System.out.println("A[" + i + "] = " + A[i]);
        }

    }

    private int maxOfArray(int[] H) {
        int max = MINUSINFINITY;
        for (int i = 0; i < numberOfActivities; i++) {
            if (max < H[i]) {
                max = H[i];
            }
        }
        return max;
    }

    private int[][] generatePrecedences(int numberOfRegularPrecedences) {

        int[][] precedences = new int[numberOfActivities][numberOfActivities];
        for (int i = 0; i <= numberOfRegularPrecedences - 1; i++) {
            precedences[2 * i][2 * i + 1] = 1;
        }
        /*
        int size = numberOfActivities / 2;
        int[] ak = new int[size];
        for (int i = 0; i < size; i++)
            ak[i] = 2 * i;
        int[] bk = new int[size];
        for (int i = 0; i < size; i++)
            bk[i] = 2 * i + 1;
        shuffleArray(ak);
        shuffleArray(bk);
       for (int i = 0; i < size; i++) {
            precedences[ak[i]][bk[i]] = 1;
        }
         */
 /*
        int numberOfSuffledInOrder = 2 * (5 * numberOfRegularPrecedences / 100);
        Random random = new Random();
        boolean[] generatedNumbers = new boolean[numberOfRegularPrecedences];
        int generatedCount = 0;
        while (generatedCount < numberOfSuffledInOrder){
            int newNumber = random.nextInt(numberOfRegularPrecedences);
            if (generatedNumbers[newNumber] == false) {
                generatedNumbers[newNumber] = true;
                generatedCount++; 
            }
        }
        int[] sortedUniqueArray = new int[numberOfSuffledInOrder];
        int selectedNumbers = 0;
        for (int i = 0; i < generatedNumbers.length; i++)
        {
            if (generatedNumbers[i] == true){
                sortedUniqueArray[selectedNumbers] = i; 
                selectedNumbers++; 
            }
        }
        //   System.out.println(Arrays.toString(sortedUniqueArray));
        for (int i = 0; i <= numberOfSuffledInOrder / 2 - 1; i++) {
            precedences[sortedUniqueArray[2 * i]][sortedUniqueArray[2 * i + 1]] = 1;
        }
        for (int i = 0; i < numberOfActivities; i++) {
            for (int j = 0; j < numberOfActivities; j++) {
                if (precedences[i][j] == 1 && precedences[j][i] == 1) {
                    System.out.println("The precedences are inconsistent");
                return null;
                }
            }
        }
         */
        return precedences;
    }

    public static void printStuffForYourInformation(boolean filterLowerBound, boolean filterUpperBound, int numberOfActivities, int numberOfCranes, int numberOfResources, int min_processingTime, int max_processingTime, int heuristic, boolean findAllOptimalSolutions) {
        System.out.println("numberOfActivities = " + numberOfActivities);
        System.out.println("numberOfCranes = " + numberOfCranes);
        System.out.println("numberOfResources = " + numberOfResources);
        System.out.println("min_processingTime = " + min_processingTime);
        System.out.println("max_processingTime = " + max_processingTime);
        if (filterLowerBound && filterUpperBound) {
            System.out.println("filters lower and upper bounds");
        }
        if (filterLowerBound && !filterUpperBound) {
            System.out.println("filters only lower bounds");
        }
        if (!filterLowerBound && filterUpperBound) {
            System.out.println("filters only upper bounds");
        }

        if (findAllOptimalSolutions) {
            System.out.println("found all optimal Solutions");
        } else {
            System.out.println("found one optimal Solution");
        }
        switch (heuristic) {
            case LEXICOGRAPHIC_LB_HEURISTIQUE:
                System.out.println("heuristic == LEXICOGRAPHIC_LB");
                break;
            case IMPACT_BASED_SEARCH_HEURISTIQUE:
                System.out.println("heuristic == IMPACT_BASED_SEARCH");
                break;
            case DOMOVERWDEG_HEURISTIQUE:
                System.out.println("heuristic == DOMOVERWDEG");
                break;
            case SMALLEST_HEURISTIQUE:
                System.out.println("heuristic == Smallest");
                break;
            default:
                break;
        }

    }
}
