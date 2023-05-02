package robustcumulativescheduling;

/**
 *
 * @author hamedfahimi
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
import org.chocosolver.solver.ResolutionPolicy;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.constraints.ICF;
import org.chocosolver.solver.constraints.IntConstraintFactory;
import org.chocosolver.solver.constraints.LogicalConstraintFactory;
import org.chocosolver.solver.constraints.SatFactory;
import org.chocosolver.solver.constraints.nary.cnf.LogOp;
import org.chocosolver.solver.search.strategy.IntStrategyFactory;
import org.chocosolver.solver.trace.Chatterbox;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.VF;
import org.chocosolver.solver.variables.VariableFactory;

public final class newModelForFlexConstraint {
    public static final int MODE_NO_OVERLOADCHECK = 0;
    public static final int MODE_NO_EDGEFINDING = 0;
    public static final int MODE_OVERLOADCHECK = 1;
    public static final int MODE_EDGEFINDING = 1;
    public int numberOfTasks;
    public int resourceCapacity;
    public boolean filterLowerBound;
    public boolean filterUpperBound;
    public boolean filterMakespan;
        public static boolean activateImprovingDetectionLowerBound;
        public static boolean activateImprovingDetectionUpperBound;

    public newModelForFlexConstraint(String dataName, int mode1, int mode2, boolean filterLowerBound, boolean filterUpperBound, boolean filterMakespan, int r) throws FileNotFoundException {
        
        this.filterLowerBound = filterLowerBound;
        this.filterUpperBound = filterUpperBound;
        this.activateImprovingDetectionLowerBound = false;
                this.activateImprovingDetectionUpperBound = false;
boolean CAPProblemConsidered = false;
        Task[] T = readRandomizedData(dataName);
      //  printTaskks(T);
        Arrays.sort(T, new Task.TaskByHeight());
        int[] Cap = new int[1];
        Cap[0] = resourceCapacity;
        int[] d = new int[numberOfTasks];
        for (int i = 0; i < numberOfTasks; i++)
            d[i] = T[i].allowedDelay();
        Solver solver = new Solver();
        IntVar[] startingTimes = new IntVar[numberOfTasks];
        for (int i = 0; i < numberOfTasks; i++) {
            startingTimes[i] = VariableFactory.bounded("startingTimes1[" + i + "]", T[i].earliestStartingTime(), T[i].latestStartingTime(), solver);
// System.out.println("[" + startingTimes[i].getLB() + ", " + startingTimes[i].getUB() + "]");       
        }
        IntVar[] processingTimeVariables = new IntVar[numberOfTasks];
        for (int i = 0; i < numberOfTasks; i++) {
            processingTimeVariables[i] = VariableFactory.fixed(T[i].processingTime(), solver);
            //   System.out.println(processingTimeVariables[i].getLB());
        }
        IntVar[] endingTimes = new IntVar[numberOfTasks];
        for (int i = 0; i < numberOfTasks; i++) {
            endingTimes[i] = VariableFactory.bounded("startingTimes1[" + i + "]", T[i].earliestCompletionTime(), T[i].latestCompletionTime(), solver);
        }
        IntVar[] heights = new IntVar[numberOfTasks];
        for (int i = 0; i < numberOfTasks; i++) {
            heights[i] = VariableFactory.fixed(T[i].height(), solver);
        }
        IntVar makespan = VariableFactory.bounded("objective", 0, 100000, solver);
        IntVar CapSum = VariableFactory.fixed("capacity", resourceCapacity, solver);
        IntVar[] capacityVar =  new IntVar[1];
        capacityVar[0] = VariableFactory.fixed(Cap[0], solver);
        BoolVar[][] A = VariableFactory.boolMatrix("A", numberOfTasks, numberOfTasks, solver);
        IntVar[][] B = new IntVar[numberOfTasks][numberOfTasks];
        for (int i = 0; i < numberOfTasks; i++) {
            for (int j = 0; j < numberOfTasks; j++) {
                B[i][j] = VariableFactory.enumerated("B[" + i + ", " + j + "]", new int[]{0, T[i].height()}, solver);
            }
        }
        Constraint[] arrayOfConstraints1 = new Constraint[2];
        
        for (int i = 0; i < numberOfTasks; i++) {
            for (int j = 0; j < numberOfTasks; j++) {
                arrayOfConstraints1[0] = IntConstraintFactory.arithm(startingTimes[i], "<=", startingTimes[j], "-", T[i].processingTime());
                arrayOfConstraints1[1] = IntConstraintFactory.arithm(startingTimes[j], "<", startingTimes[i], "+", T[i].delayedProcessingTime());
                Constraint c = LogicalConstraintFactory.and(arrayOfConstraints1);
                LogicalConstraintFactory.ifThen(c, IntConstraintFactory.arithm(A[i][j], "=", 1));
                LogicalConstraintFactory.ifThen(IntConstraintFactory.arithm(A[i][j], "=", 1), c);
            }
        }
        
        Constraint[] arrayOfConstraints2 = new Constraint[2];
        Constraint[] arrayOfConstraints3 = new Constraint[2];
        Constraint[] arrayOfConstraints4 = new Constraint[2];
        for (int j = 0; j < numberOfTasks; j++) {
            for (int i = 0; i < numberOfTasks; i++) {
                arrayOfConstraints2[0] = ICF.arithm(startingTimes[i], "<=", startingTimes[j]);
                arrayOfConstraints2[1] = ICF.arithm(startingTimes[j], "<", startingTimes[i], "+", T[i].processingTime());
                Constraint c = LogicalConstraintFactory.and(arrayOfConstraints2);
                arrayOfConstraints4[0] = c;
                arrayOfConstraints3[0] = IntConstraintFactory.arithm(A[i][j], "=", 1);
                IntVar[] DD = new IntVar[i + 1];
                for (int k = 0; k <= i; k++) {
                    DD[k] = A[k][j];
                }
                Constraint L = ICF.sum(DD, "<=", VariableFactory.fixed(r, solver));
                arrayOfConstraints3[1] = L;
                Constraint f = LogicalConstraintFactory.and(arrayOfConstraints3);
                arrayOfConstraints4[1] = f;
                Constraint g = LogicalConstraintFactory.or(arrayOfConstraints4);
                LogicalConstraintFactory.ifThen(g, IntConstraintFactory.arithm(B[i][j], "=", T[i].height()));
                LogicalConstraintFactory.ifThen(IntConstraintFactory.arithm(B[i][j], "=", T[i].height()), g);
            }
        }
        for (int i = 0; i < numberOfTasks; i++) {
            solver.post(IntConstraintFactory.arithm(B[i][i], "=", T[i].height()));
            solver.post(IntConstraintFactory.arithm(A[i][i], "=", 0));
        }
        IntVar[] DD = new IntVar[numberOfTasks];
        for (int j = 0; j < numberOfTasks; j++) {
            // solver.post(ICF.sum(B[j], "<=", CapSum));
            for (int i = 0; i < numberOfTasks; i++) {
                DD[i] = B[i][j];
            }
            solver.post(ICF.sum(DD, "<=", CapSum));
        }
        //  solver.post(IntConstraintFactory.arithm(A[0][1], "=", 1));
        // solver.post(IntConstraintFactory.arithm(A[2][0], "=", 1));
        
        for (int i = 0; i < numberOfTasks; i++) {
            solver.post(IntConstraintFactory.arithm(endingTimes[i], "-", startingTimes[i], "=", T[i].processingTime()));
        }
        for (int i = 0; i < numberOfTasks; i++) {
            solver.post(IntConstraintFactory.arithm(makespan, "-", endingTimes[i], ">=", 0));
        }
        int tempCounter = 0;
        IntVar[] starting_processing_ending_times_plus_height_variables_and_capacityOfResource_plus_makeSpan = new IntVar[4 * numberOfTasks + 2];
        for (int g = 0; g < numberOfTasks; g++) {
            starting_processing_ending_times_plus_height_variables_and_capacityOfResource_plus_makeSpan[tempCounter] = startingTimes[g];
            tempCounter++;
            
        }
        for (int g = numberOfTasks; g < 2 * numberOfTasks;g++) {
            starting_processing_ending_times_plus_height_variables_and_capacityOfResource_plus_makeSpan[tempCounter] = processingTimeVariables[g - (numberOfTasks)];
            tempCounter++;
        }
        for (int g = 2 * numberOfTasks; g < 3 * numberOfTasks;g++) {
            starting_processing_ending_times_plus_height_variables_and_capacityOfResource_plus_makeSpan[tempCounter] = endingTimes[g - 2 * (numberOfTasks)];
            tempCounter++;
        }
        for (int g = 3 * numberOfTasks; g < 4 * numberOfTasks;g++) {
            starting_processing_ending_times_plus_height_variables_and_capacityOfResource_plus_makeSpan[tempCounter] = heights[g - 3 * (numberOfTasks)];
            tempCounter++;
        }
        starting_processing_ending_times_plus_height_variables_and_capacityOfResource_plus_makeSpan[4 * numberOfTasks] = capacityVar[0];
        starting_processing_ending_times_plus_height_variables_and_capacityOfResource_plus_makeSpan[4 * numberOfTasks + 1] = makespan;
        
        if (mode1 == MODE_OVERLOADCHECK && mode2 == MODE_NO_EDGEFINDING) {
            solver.post(new overloadCheckingConstraint(starting_processing_ending_times_plus_height_variables_and_capacityOfResource_plus_makeSpan, numberOfTasks, 1, Cap, d, filterMakespan, r));
        }
        else if (mode1 == MODE_NO_OVERLOADCHECK && mode2 == MODE_EDGEFINDING) {
            solver.post(new edgeFindingConstraint(starting_processing_ending_times_plus_height_variables_and_capacityOfResource_plus_makeSpan, numberOfTasks, 1, Cap, d, filterMakespan, filterLowerBound, filterUpperBound, activateImprovingDetectionLowerBound, false, 0, CAPProblemConsidered, r));
        }
        IntVar[] arrayOfAllVariables = new IntVar[4 * (numberOfTasks) + 2];
        for (int g = 0; g < numberOfTasks; g++) {
            arrayOfAllVariables[g] = startingTimes[g];
        }
        for (int g = numberOfTasks; g < 2 * numberOfTasks; g++) {
            arrayOfAllVariables[g] = processingTimeVariables[g - (numberOfTasks)];
        }
        for (int g = 2 * numberOfTasks; g < 3 * numberOfTasks; g++) {
            arrayOfAllVariables[g] = endingTimes[g - 2 * (numberOfTasks)];
        }
        for (int g = 3 * numberOfTasks; g < 4 * numberOfTasks; g++) {
            arrayOfAllVariables[g] = heights[g - 3 * (numberOfTasks)];
        }
        arrayOfAllVariables[4 * numberOfTasks] = capacityVar[0];
        arrayOfAllVariables[4 * numberOfTasks + 1] = makespan;
        solver.set(IntStrategyFactory.lexico_LB(arrayOfAllVariables));
        solver.findSolution();
        for (int i = 0; i < numberOfTasks; i++) {
          //  System.out.println("s["+ i + "] = " + startingTimes[i].getValue());
            //  System.out.println("e["+ i + "] = " + endingTimes[i].getValue());
        }
        
        /*
        for (int i = 0; i < numberOfTasks; i++) {
            for (int j = 0; j < numberOfTasks; j++) {
                System.out.println("A["+ i + ", " + j + "] = " + A[i][j].getValue());
            }
        }
        
        for (int i = 0; i < numberOfTasks; i++) {
            for (int j = 0; j < numberOfTasks; j++) {
                System.out.println("B["+ i + ", " + j + "] = " + B[i][j].getValue());
            }
        }
        */
        
        
//   System.out.println();

// int co = 0;
//
//      while (solver.solve) {
//         System.out.println("solution number " + co);
//
//    for (int i = 0; i < numberOfTasks; i++) {
//    System.out.println("s["+ i + "] = " + startingTimes[i].getValue());
//                //  System.out.println("e["+ i + "] = " + endingTimes[i].getValue());
//     }
//    co++;
//      }


//    solver.findAllOptimalSolutions(ResolutionPolicy.MINIMIZE, makespan, true);

//            if(solver.findSolution()){ do
//            {
//                System.out.println("solution number " + co);
//
//            for (int i = 0; i < numberOfTasks; i++) {
//                  System.out.println("s["+ i + "] = " + startingTimes[i].getValue());
//                //  System.out.println("e["+ i + "] = " + endingTimes[i].getValue());
//            }
//                co++;
//            } while(solver.nextSolution());
//            }

System.out.println("makespan = " + makespan.getValue());
Chatterbox.printStatistics(solver);
    }
    
    public Task[] readRandomizedData(String fileName) throws FileNotFoundException {
        Scanner s = new Scanner (new File (fileName)).useDelimiter("\\s+");
        s.nextLine();
        this.numberOfTasks = s.nextInt();
        this.resourceCapacity = s.nextInt();
        Task[] T = new Task[numberOfTasks];
        int num_generated = 0;
        while (s.hasNextInt()) {
            int a = s.nextInt();
            int b = s.nextInt();
            int c = s.nextInt();
            int d = s.nextInt();
            int e = s.nextInt();
            int f = s.nextInt();
            T[num_generated] = new Task(a, b, c, d, e, f);
            num_generated++;
        }
        return T;
    }
    
    
    public void printTaskks(Task[] T) {
        
        for (int j = 0; j < T.length; j++)
            System.out.println("j - > " + j + " " + T[j]);
        
    }
    
    public static void main(String[] args) throws FileNotFoundException {
        
        boolean filterLow = true;
        boolean filterUp = false;
        boolean filterMakespan = false;
        String f = "randomRobustTasks.txt";
        int r = 1;
       newModelForFlexConstraint F = new newModelForFlexConstraint(f, MODE_NO_OVERLOADCHECK, MODE_NO_EDGEFINDING, filterLow, filterUp, filterMakespan, r);
        newModelForFlexConstraint G = new newModelForFlexConstraint(f, MODE_OVERLOADCHECK, MODE_NO_EDGEFINDING, filterLow, filterUp, filterMakespan, r);
          newModelForFlexConstraint H = new newModelForFlexConstraint(f, MODE_NO_OVERLOADCHECK, MODE_EDGEFINDING, filterLow, filterUp, filterMakespan, r);
    }
}
