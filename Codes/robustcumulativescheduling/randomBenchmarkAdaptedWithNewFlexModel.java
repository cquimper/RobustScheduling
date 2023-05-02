package robustcumulativescheduling;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
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

public final class randomBenchmarkAdaptedWithNewFlexModel {

    public int numberOfTasks;
    public int resourceCapacity;
    public int numberOfResources;
    public int[] p;
    public int[] d;
    public int[] h;
    public int sumOfProcessingTimes;
    public static final int MODE_NO_OVERLOADCHECK = 0;
    public static final int MODE_NO_EDGEFINDING = 0;
    public static final int MODE_NO_NOTFIRSTNOTLAST = 0;
    public static final int MODE_OVERLOADCHECK = 1;
    public static final int MODE_EDGEFINDING = 1;
    public static final int MODE_NOTFIRSTNOTLAST = 1;
    public static final int HEURISTIQUE_LEXICOGRAPHIC_LB = 1;
    public static final int HEURISTIQUE_LEXICO_SPLIT = 2;
    public static final int HEURISTIQUE_IMPACT_BASED_SEARCH = 3;
    public static final int HEURISTIQUE_DOMOVERWDEG = 4;
    public static final int MODE_N_CUMULATIVE = 1;
    public static final int MODE_NO_N_CUMULATIVE = 0;
    public float elapsedTime;
    public static boolean activateImprovingDetectionLowerBound;

    public static boolean activateImprovingDetectionUpperBound;

    public randomBenchmarkAdaptedWithNewFlexModel(String f, int mode1, int mode2, int heuristic, boolean filterLowerBound, boolean filterUpperBound, boolean findOnlySolution, boolean findAllOptimalSolutions, boolean filterMakespan, int r) throws FileNotFoundException {
        Task[][] T;
        readYourRandomizedData(f, r);
        int[] capacities = new int[1];
        capacities[0] = resourceCapacity;
        int Horizon;
        Horizon = maxHorizon();
        activateImprovingDetectionLowerBound = false;
        activateImprovingDetectionUpperBound = false;
        boolean CAPProblemConsidered = false;
        //  Horizon = 200;
        T = new Task[numberOfTasks][numberOfResources];
        for (int i = 0; i < numberOfTasks; i++) {
            for (int j = 0; j < numberOfResources; j++) {
                // T[i][j] = new Task (0, Horizon, processingTimes[i], d[i], heights[j][i]);
                T[i][j] = new Task(0, Horizon, Horizon + d[i], p[i], d[i], h[i]);
                //  System.out.println("i:" + i + " j:" + j + " " + T[i][j]);
            }
        }
        Solver solver = new Solver();
        IntVar[] startingTimes = new IntVar[numberOfTasks];
        for (int i = 0; i < numberOfTasks; i++) {
            startingTimes[i] = VariableFactory.bounded("startingTimes1[" + i + "]", T[i][0].earliestStartingTime(), T[i][0].latestStartingTime(), solver);
        }
        IntVar[] processingTimeVariables = new IntVar[numberOfTasks];
        for (int i = 0; i < numberOfTasks; i++) {
            processingTimeVariables[i] = VariableFactory.fixed(p[i], solver);
        }
        IntVar[] endingTimes = new IntVar[numberOfTasks];
        for (int i = 0; i < numberOfTasks; i++) {
            //  endingTimes[i] = VariableFactory.bounded("endingTimes1[i]", processingTimes[i], Horizon, solver);
            endingTimes[i] = VariableFactory.bounded("endingTimes1[i]", p[i], T[i][0].latestCompletionTime(), solver);
        }
        IntVar[][] allHeights = new IntVar[numberOfTasks][numberOfResources];
        for (int i = 0; i < numberOfTasks; i++) {
            for (int j = 0; j < numberOfResources; j++) {
                allHeights[i][j] = VariableFactory.fixed(T[i][j].height(), solver);
            }
        }
        IntVar makespan = VariableFactory.bounded("objective", 0, Horizon, solver);

        for (int i = 0; i < numberOfTasks; i++) {
            solver.post(IntConstraintFactory.arithm(endingTimes[i], "-", startingTimes[i], "=", p[i]));
        }
        for (int i = 0; i < numberOfTasks; i++) {
            solver.post(IntConstraintFactory.arithm(makespan, "-", endingTimes[i], ">=", 0));
        }
        BoolVar[][] A = VariableFactory.boolMatrix("A", numberOfTasks, numberOfTasks, solver);
        for (int i = 0; i < numberOfTasks; i++) {
            for (int j = 0; j < numberOfTasks; j++) {
                // System.out.println("i = " + i + ", j = " + j + " -> [" + A[i][j].getLB() + ", " + A[i][j].getUB() + "]");
            }
        }
        Constraint[] arrayOfConstraints1 = new Constraint[2];
        for (int i = 0; i < numberOfTasks; i++) {
            for (int j = 0; j < numberOfTasks; j++) {
                arrayOfConstraints1[0] = IntConstraintFactory.arithm(startingTimes[i], "<=", startingTimes[j], "-", T[i][0].processingTime());
                arrayOfConstraints1[1] = IntConstraintFactory.arithm(startingTimes[j], "<", startingTimes[i], "+", T[i][0].delayedProcessingTime());
                Constraint c = LogicalConstraintFactory.and(arrayOfConstraints1);
                LogicalConstraintFactory.ifThen(c, IntConstraintFactory.arithm(A[i][j], "=", 1));
                LogicalConstraintFactory.ifThen(IntConstraintFactory.arithm(A[i][j], "=", 1), c);
            }
        }
        for (int i = 0; i < numberOfTasks; i++) {
            solver.post(IntConstraintFactory.arithm(A[i][i], "=", 0));
        }
        IntVar[][][] B = new IntVar[numberOfResources][numberOfTasks][numberOfTasks];
        for (int m = 0; m < numberOfResources; m++) {
            for (int i = 0; i < numberOfTasks; i++) {
                for (int j = 0; j < numberOfTasks; j++) {
                    B[m][i][j] = VariableFactory.enumerated("B[" + m + ", " + i + ", " + j + "]", new int[]{0, T[i][m].height()}, solver);
                    //  System.out.println("m = " + m + ", i = " + i + ", j = " + j + " -> [" + B[m][i][j].getLB() + ", " + B[m][i][j].getUB() + "]");
                }
            }
        }
        IntVar[][][] LL = new IntVar[numberOfResources][numberOfTasks][numberOfTasks];
        IntVar[][][][] DD = new IntVar[numberOfResources][numberOfTasks][numberOfTasks][numberOfTasks];
        Constraint[] arrayOfConstraints2 = new Constraint[2];
        Constraint[] arrayOfConstraints3 = new Constraint[2];
        Constraint[] arrayOfConstraints4 = new Constraint[2];
        IntVar[] allCapacityVars = new IntVar[numberOfResources];
        for (int m = 0; m < numberOfResources; m++) {
            allCapacityVars[m] = VariableFactory.fixed(capacities[m], solver);
            // IntVar capacityVar = VariableFactory.fixed(capacities[numberOfResources], solver);
            IntVar[] starting_processing_ending_times_plus_height_variables_and_capacityOfResource_plus_makeSpan = new IntVar[4 * numberOfTasks + 2];
            for (int j = 0; j < numberOfTasks; j++) {
                // solver.post(ICF.sum(B[j], "<=", CapSum));
                for (int i = 0; i < numberOfTasks; i++) {
                    LL[m][j][i] = B[m][i][j];
                    // System.out.println("m = " + m + ", j = " + j + ", i = " + i + " -> [" + LL[m][j][i].getLB() + ", " + LL[m][j][i].getUB() + "]");
                }
                solver.post(ICF.sum(LL[m][j], "<=", allCapacityVars[m]));
            }
            for (int i = 0; i < numberOfTasks; i++) {
                solver.post(IntConstraintFactory.arithm(B[m][i][i], "=", T[i][m].height()));
            }
            for (int j = 0; j < numberOfTasks; j++) {
                for (int i = 0; i < numberOfTasks; i++) {
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
                    for (int k = i + 1; k < numberOfTasks; k++) {
                        DD[m][j][i][k] = VariableFactory.fixed(0, solver);

                    }
                    Constraint L = ICF.sum(DD[m][j][i], "<=", VariableFactory.fixed(r, solver));
                    arrayOfConstraints3[1] = L;
                    Constraint f2 = LogicalConstraintFactory.and(arrayOfConstraints3);
                    arrayOfConstraints4[1] = f2;
                    Constraint g = LogicalConstraintFactory.or(arrayOfConstraints4);
                    LogicalConstraintFactory.ifThen(g, IntConstraintFactory.arithm(B[m][i][j], "=", T[i][m].height()));
                    LogicalConstraintFactory.ifThen(IntConstraintFactory.arithm(B[m][i][j], "=", T[i][m].height()), g);
                }
            }
            int tempCounter;
            int[] d2 = new int[numberOfTasks];
            tempCounter = 0;
            for (int g = 0; g < numberOfTasks; g++) {
                starting_processing_ending_times_plus_height_variables_and_capacityOfResource_plus_makeSpan[tempCounter] = startingTimes[g];
                d2[tempCounter] = d[g];
                tempCounter++;

            }
            for (int g = numberOfTasks; g < 2 * numberOfTasks; g++) {
                starting_processing_ending_times_plus_height_variables_and_capacityOfResource_plus_makeSpan[tempCounter] = processingTimeVariables[g - (numberOfTasks)];
                tempCounter++;

            }
            for (int g = 2 * numberOfTasks; g < 3 * numberOfTasks; g++) {
                starting_processing_ending_times_plus_height_variables_and_capacityOfResource_plus_makeSpan[tempCounter] = endingTimes[g - 2 * (numberOfTasks)];
                tempCounter++;

            }
            for (int g = 3 * numberOfTasks; g < 4 * numberOfTasks; g++) {
                starting_processing_ending_times_plus_height_variables_and_capacityOfResource_plus_makeSpan[tempCounter] = allHeights[g - 3 * (numberOfTasks)][m];
                tempCounter++;
            }
            starting_processing_ending_times_plus_height_variables_and_capacityOfResource_plus_makeSpan[4 * numberOfTasks] = allCapacityVars[m];
            starting_processing_ending_times_plus_height_variables_and_capacityOfResource_plus_makeSpan[4 * numberOfTasks + 1] = makespan;
            int[] capacityVector = new int[1];
            capacityVector[0] = capacities[m];
            if (mode1 == MODE_OVERLOADCHECK && mode2 == MODE_NO_EDGEFINDING) {
                solver.post(new overloadCheckingConstraint(starting_processing_ending_times_plus_height_variables_and_capacityOfResource_plus_makeSpan, numberOfTasks, 1, capacityVector, d2, filterMakespan, r));
            } else if (mode1 == MODE_NO_OVERLOADCHECK && mode2 == MODE_EDGEFINDING) {
                solver.post(new edgeFindingConstraint(starting_processing_ending_times_plus_height_variables_and_capacityOfResource_plus_makeSpan, numberOfTasks, 1, capacityVector, d2, filterMakespan, filterLowerBound, filterUpperBound, activateImprovingDetectionLowerBound, false, 0, CAPProblemConsidered, r));
            }
        }
        IntVar[] arrayOfAllVariables = new IntVar[4 * (numberOfTasks) + numberOfResources + 1
                + (numberOfTasks) * (numberOfTasks) + numberOfResources * (numberOfTasks) * (numberOfTasks)
                + numberOfResources * (numberOfTasks) * (numberOfTasks) * (numberOfTasks) + numberOfResources * (numberOfTasks) * (numberOfTasks)];
        for (int g = 0; g < numberOfTasks; g++) {
            arrayOfAllVariables[g] = startingTimes[g];
        }
        for (int g = numberOfTasks; g < 2 * numberOfTasks; g++) {
            arrayOfAllVariables[g] = processingTimeVariables[g - (numberOfTasks)];

        }
        for (int g = 2 * numberOfTasks; g < 3 * numberOfTasks; g++) {
            arrayOfAllVariables[g] = endingTimes[g - 2 * (numberOfTasks)];

        }
        int g = 3 * numberOfTasks;
        for (int j = 0; j < numberOfResources; j++) {
            for (int i = 0; i < numberOfTasks; i++) {
                arrayOfAllVariables[g] = allHeights[i][j];
                g++;

            }
        }
        for (int j = 0; j < numberOfResources; j++) {
            arrayOfAllVariables[g] = allCapacityVars[j];
            g++;
        }
        arrayOfAllVariables[4 * (numberOfTasks) + numberOfResources] = makespan;
        g++;
        for (int i = 0; i < numberOfTasks; i++) {
            for (int j = 0; j < numberOfTasks; j++) {
                arrayOfAllVariables[g] = A[i][j];
                g++;
            }
        }
        for (int m = 0; m < numberOfResources; m++) {
            for (int i = 0; i < numberOfTasks; i++) {
                for (int j = 0; j < numberOfTasks; j++) {

                    arrayOfAllVariables[g] = B[m][i][j];
                    g++;

                }
            }
        }
        for (int m = 0; m < numberOfResources; m++) {
            for (int i = 0; i < numberOfTasks; i++) {
                for (int j = 0; j < numberOfTasks; j++) {
                    arrayOfAllVariables[g] = LL[m][i][j];
                    g++;

                }
            }
        }
        for (int m = 0; m < numberOfResources; m++) {
            for (int i = 0; i < numberOfTasks; i++) {
                for (int j = 0; j < numberOfTasks; j++) {
                    for (int k = 0; k < numberOfTasks; k++) {
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
        switch (heuristic) {
            case HEURISTIQUE_LEXICOGRAPHIC_LB:
                solver.set(IntStrategyFactory.lexico_LB(arrayOfAllVariables));
                break;
            case HEURISTIQUE_LEXICO_SPLIT:
                solver.set(IntStrategyFactory.lexico_Split(arrayOfAllVariables));
                break;
            case HEURISTIQUE_IMPACT_BASED_SEARCH:
                solver.set(IntStrategyFactory.impact(arrayOfAllVariables, 1));
            case HEURISTIQUE_DOMOVERWDEG:
                solver.set(IntStrategyFactory.domOverWDeg(arrayOfAllVariables, 1));
                break;
            default:
                break;
        }

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
        if (findOnlySolution) {
            solver.findSolution();
        } else {
            if (findAllOptimalSolutions) {
                solver.findAllOptimalSolutions(ResolutionPolicy.MINIMIZE, makespan, true);
            } else {
                solver.findOptimalSolution(ResolutionPolicy.MINIMIZE, makespan);
            }
        }

        //  for (int i = 0; i < numberOfTasks; i++)
        //   System.out.println("s["+ i + "] = " + startingTimes[i].getValue());
        //              System.out.println("e["+ i + "] = " + endingTimes[i].getValue());
        // }
        //   System.out.println();
        Chatterbox.printStatistics(solver);
        // makespanValue = makespan.getValue();
        //  backtracksNum = (int) solver.getMeasures().getBackTrackCount();
        elapsedTime = solver.getMeasures().getTimeCount();
        //  nomSolutions = (int) solver.getMeasures().getSolutionCount();
        //  System.out.println("Solutions = " + nomSolutions);

        // System.out.println("backtrack numbers = " + backtracksNum);
        //   System.out.println("Elapsed time = " + elapsedTime);
        //  System.out.println("makespan = " + makespanValue);
        //  System.out.println();
        // nomSolutions = solver.findAllOptimalSolutions.
    }

    public void readYourRandomizedData(String fileName, int r) throws FileNotFoundException {
        Scanner s = new Scanner(new File(fileName)).useDelimiter("\\s+");
        this.numberOfTasks = s.nextInt();
        this.numberOfResources = s.nextInt();
        this.resourceCapacity = s.nextInt();
        this.p = new int[numberOfTasks];
        this.d = new int[numberOfTasks];
        this.h = new int[numberOfTasks];
        int i = 0;
        this.sumOfProcessingTimes = 0;
        Task[] T = new Task[numberOfTasks];
        while (s.hasNextInt()) {
            p[i] = s.nextInt();
            sumOfProcessingTimes += p[i];
            d[i] = s.nextInt();
            h[i] = s.nextInt();
            i++;
        }
        Arrays.sort(d);
        for (int y = 0; y < r; y++) {
            sumOfProcessingTimes += d[y];
        }
    }

    public int[] processingTimesArray() {
        return p;
    }

    public int[] delaysArray() {
        return d;
    }

    public int[] heightsArray() {
        return h;
    }

    public int maxHorizon() {
        return sumOfProcessingTimes;
    }

    public float howMuchTime() {
        // System.out.println("Backtrack number = "+ elapsedTime);
        return elapsedTime;
    }

    public static void runOnDifferentSizesOfTasks(int n, int heuristic, boolean filterLowerBound, boolean filterUpperBound, boolean findOnlySolution, boolean findAllOptimalSolutions, boolean filterMakespan, int r) throws FileNotFoundException, IOException {
        // String filename1 = "OCResults-n10.xls" ;
        String filename1 = "EGResults-n" + n + ".xls";
        int lineCounter = 1;
        HSSFWorkbook workbook1 = new HSSFWorkbook();
        HSSFSheet sheet1 = workbook1.createSheet("FirstSheet");
        HSSFRow rowhead1 = sheet1.createRow((short) 0);
        rowhead1.createCell(0).setCellValue("File name");
        rowhead1.createCell(1);
        rowhead1.createCell(2).setCellValue("Elapsed time");
        sheet1.createRow((short) lineCounter);
        lineCounter++;
        for (int l = 1; l < 51; l++) {
            System.out.println("l = " + l);
            String dataName = "newFile" + l + "-n" + n + ".txt";
            // randomBenchmarkAdaptedWithNewFlexModel A2 = new randomBenchmarkAdaptedWithNewFlexModel(dataName, MODE_OVERLOADCHECK, MODE_NO_EDGEFINDING,  heuristic, filterLowerBound, filterUpperBound, findOnlySolution, findAllOptimalSolutions, filterMakespan, r);
            randomBenchmarkAdaptedWithNewFlexModel A2 = new randomBenchmarkAdaptedWithNewFlexModel(dataName, MODE_NO_OVERLOADCHECK, MODE_EDGEFINDING, heuristic, filterLowerBound, filterUpperBound, findOnlySolution, findAllOptimalSolutions, filterMakespan, r);
            HSSFRow row1 = sheet1.createRow((short) lineCounter);
            row1.createCell(0).setCellValue(dataName);
            row1.createCell(1);
            row1.createCell(2).setCellValue(A2.elapsedTime);
            lineCounter++;
        }
        FileOutputStream fileOut1 = new FileOutputStream(filename1);
        workbook1.write(fileOut1);
        fileOut1.close();

    }

    public static void runOnDifferentSizesOfr(String dataName, int heuristic, boolean filterLowerBound, boolean filterUpperBound, boolean findOnlySolution, boolean findAllOptimalSolutions, boolean filterMakespan) throws FileNotFoundException, IOException {
        String filename1 = "ResultsFor-r-.xls";
        int lineCounter = 1;
        HSSFWorkbook workbook1 = new HSSFWorkbook();
        HSSFSheet sheet1 = workbook1.createSheet("FirstSheet");
        HSSFRow rowhead1 = sheet1.createRow((short) 0);
        rowhead1.createCell(0).setCellValue("r");
        rowhead1.createCell(1);
        rowhead1.createCell(2).setCellValue("Elapsed time");
        sheet1.createRow((short) lineCounter);
        lineCounter++;
        for (int r = 14; r < 15; r++) {
            System.out.println("r = " + r);
            // String dataName = "newFile" + r + ".txt";
            randomBenchmarkAdaptedWithNewFlexModel A2 = new randomBenchmarkAdaptedWithNewFlexModel(dataName, MODE_OVERLOADCHECK, MODE_NO_EDGEFINDING, heuristic, filterLowerBound, filterUpperBound, findOnlySolution, findAllOptimalSolutions, filterMakespan, r);
            //  randomBenchmarkAdaptedWithNewFlexModel A2 = new randomBenchmarkAdaptedWithNewFlexModel(dataName, MODE_NO_OVERLOADCHECK, MODE_EDGEFINDING,  heuristic, filterLowerBound, filterUpperBound, findOnlySolution, findAllOptimalSolutions, filterMakespan, r);

            HSSFRow row1 = sheet1.createRow((short) lineCounter);
            row1.createCell(0).setCellValue(r);
            row1.createCell(1);
            row1.createCell(2).setCellValue(A2.elapsedTime);
            lineCounter++;
        }
        FileOutputStream fileOut1 = new FileOutputStream(filename1);
        workbook1.write(fileOut1);
        fileOut1.close();

    }

    public static void main(String[] args) throws Exception {

        int r = 1;
        boolean findOnlySolution = true;
        boolean findAllOptimalSolutions = !true;
        boolean filterLowerBound = true;
        boolean filterUpperBound = true;
        boolean filterMakespan = true;
        //    int heuristic = HEURISTIQUE_IMPACT_BASED_SEARCH;
        //int heuristic = HEURISTIQUE_LEXICOGRAPHIC_LB;
        int heuristic = HEURISTIQUE_LEXICO_SPLIT;
        //  int heuristic =  HEURISTIQUE_DOMOVERWDEG;
        /*
            //HEURISTIQUE_LEXICO_SPLIT
            //HEURISTIQUE_LEXICOGRAPHIC_LB
            //HEURISTIQUE_IMPACT_BASED_SEARCH
            //  randomBenchmarkAdaptedWithNewFlexModel A1 = new randomBenchmarkAdaptedWithNewFlexModel(b, MODE_NO_OVERLOADCHECK, MODE_NO_EDGEFINDING,  heuristic, filterLowerBound, filterUpperBound, findOnlySolution, findAllOptimalSolutions, filterMakespan, r);
            randomBenchmarkAdaptedWithNewFlexModel A2 = new randomBenchmarkAdaptedWithNewFlexModel(b, MODE_OVERLOADCHECK, MODE_NO_EDGEFINDING,  heuristic, filterLowerBound, filterUpperBound, findOnlySolution, findAllOptimalSolutions, filterMakespan, r);
            randomBenchmarkAdaptedWithNewFlexModel A3 = new randomBenchmarkAdaptedWithNewFlexModel(b, MODE_NO_OVERLOADCHECK, MODE_EDGEFINDING,  heuristic, filterLowerBound, filterUpperBound, findOnlySolution, findAllOptimalSolutions, filterMakespan, r);
         */
        String b = "newFile14-n100.txt";

           for (int taskSize = 80; taskSize < 90; taskSize += 10) 
           runOnDifferentSizesOfTasks(taskSize, heuristic, filterLowerBound, filterUpperBound, findOnlySolution, findAllOptimalSolutions, filterMakespan, r);
//        for (int i = 0; i < 5; i++) {
//            runOnDifferentSizesOfr(b, heuristic, filterLowerBound, filterUpperBound, findOnlySolution, findAllOptimalSolutions, filterMakespan);
//        }
    }
}
