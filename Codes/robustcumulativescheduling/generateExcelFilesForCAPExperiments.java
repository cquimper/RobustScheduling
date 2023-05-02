package robustcumulativescheduling;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Paths;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class generateExcelFilesForCAPExperiments {

    public static final int MODE_NO_OVERLOADCHECK = 0;
    public static final int MODE_NO_EDGEFINDING = 0;
    public static final int MODE_NO_NOTFIRSTNOTLAST = 0;
    public static final int MODE_OVERLOADCHECK = 1;
    public static final int MODE_EDGEFINDING = 1;
    public static final int MODE_NOTFIRSTNOTLAST = 1;
    public static final int LEXICOGRAPHIC_LB_HEURISTIQUE = 1;
    //     public static final int LEXICO_SPLIT_HEURISTIQUE = 2;
    //        public static final int LEXICO_NEQ_LB_HEURISTIQUE = 3;
    public static final int IMPACT_BASED_SEARCH_HEURISTIQUE = 2;
    public static final int DOMOVERWDEG_HEURISTIQUE = 3;
    public static final int SMALLEST_HEURISTIQUE = 4;
    public static int HEURISTIC_MODE;
    public static long SEED;
    public static double DELAY_COEFFICIENT;
    public static int FILTER_LB;
    public static int FILTER_UB;
    public static int FOUND_ONE_OPTIMAL_SOLUTION;
    public static int FOUND_ALL_OPTIMAL_SOLUTIONS;
    public static boolean activateImprovingDetectionUpperBound;
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
    private static Scanner input;

    public static void main(String[] args) throws Exception {
        openFile();
        readBenchmarkAndGenerateResults();
        closeFile();
    }

    public static void openFile() {
        try {
            // input = new Scanner(Paths.get("generatedCAPBenchmark.txt"));
            //   input = new Scanner(Paths.get("generatedCAPBenchmark(EASY).txt"));
            input = new Scanner(Paths.get("generatedCAPBenchmark(new).txt"));

        } catch (IOException ioException) {
            System.err.println("Error opening file. Terminating.");
            System.exit(1);
        }
    }

    public static void readBenchmarkAndGenerateResults() throws UnsupportedEncodingException, FileNotFoundException, Exception {

        int lineCounter = 1;
        String filename1 = "myCAPResultsRandom.xls";
        HSSFWorkbook workbook1 = new HSSFWorkbook();
        HSSFSheet sheet1 = workbook1.createSheet("FirstSheet");
        HSSFRow rowhead1 = sheet1.createRow((short) 0);
        rowhead1.createCell(0).setCellValue("Data ID.");
        rowhead1.createCell(1).setCellValue("NA");
        rowhead1.createCell(2).setCellValue("NC");
        rowhead1.createCell(3).setCellValue("min_p");
        rowhead1.createCell(4).setCellValue("max_p");
        rowhead1.createCell(5).setCellValue("Seed");
        //    rowhead1.createCell(6).setCellValue("Heuristic");
        //    rowhead1.createCell(7).setCellValue("LB Improving Detection is activated");
        rowhead1.createCell(6).setCellValue("FilterLB");
        rowhead1.createCell(7).setCellValue("FilterUB");
        rowhead1.createCell(8);
        rowhead1.createCell(9).setCellValue("TT(bt)");
        rowhead1.createCell(10).setCellValue("OC&TT(bt)");
        rowhead1.createCell(11).setCellValue("EF&&TT(bt)");
        rowhead1.createCell(12).setCellValue("TT(t)");
        rowhead1.createCell(13).setCellValue("OC&TT(t)");
        rowhead1.createCell(14).setCellValue("EF&&TT(t)");
        rowhead1.createCell(15).setCellValue("TT(makespan)");
        rowhead1.createCell(16).setCellValue("OC&TT(makespan)");
        rowhead1.createCell(17).setCellValue("EF&&TT(makespan)");
        rowhead1.createCell(18).setCellValue("TT case number");
        rowhead1.createCell(19).setCellValue("OC&TT case number");
        rowhead1.createCell(20).setCellValue("EF&&TT case number");

        int instanceId;
        long seed;
        int heuristic;
        heuristic = IMPACT_BASED_SEARCH_HEURISTIQUE;

        int nA;
        int nC;
        int min_processingTime;
        int max_processingTime;
        int nR = 1;
        float ttelapsedTiem = 0;
        int ttbacktrackNum = 0;
        float ocelapsedTiem = 0;
        int ocbacktrackNum = 0;
        float efelapsedTiem;
        int efbacktrackNum;
        int ttmakespan = 0;
        int ocmakespan = 0;
        int efmakespan;

        final int case1 = 1; //corresponds to the case that an optimal solution is found within the time limit.
        final int case2 = 2; //corresponds to the case that a sub optimal solution is found within the time limit.
        final int case3 = 3; //corresponds to the case that no  solution is found within the time limit.

        int ttdecidedCase = 0;
        int ocdecidedCase = 0;
        int efdecidedCase = 0;

        boolean findAllOptimalSolutions = false;
        boolean printSolutionInformatioin = false;
        boolean activateImprovingDetectionLowerBound = false;
        boolean filterLowerBound;
        boolean filterUpperBound;

        //  modelOfCraneAssignmentProblem timeTabling;
        //    modelOfCraneAssignmentProblem overloadChecking;
        //   modelOfCraneAssignmentProblem edgeFinding;
        //   modelOfCraneAssignmentProblemForTemporalProtection timeTabling;
        //  modelOfCraneAssignmentProblemForTemporalProtection overloadChecking;
        //  modelOfCraneAssignmentProblemForTemporalProtection edgeFinding;
        modelOfCraneAssignmentProblemIncludingLNSApproach timeTabling;
        modelOfCraneAssignmentProblemIncludingLNSApproach overloadChecking;
        modelOfCraneAssignmentProblemIncludingLNSApproach edgeFinding;
        boolean findOptimalSolution = true;
        boolean runningTTAndOCIsRequired = true;

        try {
            while (input.hasNext()) {

                instanceId = input.nextInt();
//                    if (instanceId == previousInstanceId)
//                        runningTTAndOCIsRequired = false;
//                    else
//                        runningTTAndOCIsRequired = true;
                System.out.println("instanceId = " + instanceId);
                nA = input.nextInt();
                System.out.println("nA = " + nA);
                nC = input.nextInt();
                System.out.println("nC = " + nC);
                min_processingTime = input.nextInt();
                System.out.println("min_processingTime = " + min_processingTime);
                max_processingTime = input.nextInt();
                System.out.println("max_processingTime = " + max_processingTime);
                seed = input.nextLong();
                System.out.println("seed = " + seed);

                for (int q1 = 1; q1 < 2; q1++) {
                    //for (int q1 = 1; q1 < 5; q1++) {
/*
                        switch (q1) {
                            case 1:
                                heuristic = LEXICOGRAPHIC_LB_HEURISTIQUE;
                                //  System.out.println("heuristic = " + heuristic);
                                break;
                            case 2:
                                heuristic = IMPACT_BASED_SEARCH_HEURISTIQUE;
                                //   System.out.println("heuristic = " + heuristic);
                                break;
                            case 3:
                                heuristic = DOMOVERWDEG_HEURISTIQUE;
                                //  System.out.println("heuristic = " + heuristic);
                                break;
                            case 4:
                                heuristic = SMALLEST_HEURISTIQUE;
                                //  System.out.println("heuristic = " + heuristic);
                                break;
                            default:
                                break;
                        }
                     */
                    System.out.println("heuristic = " + heuristic);

                    for (int q2 = 0; q2 < 1; q2++) {
                        // for (int q2 = 0; q2 < 2; q2++) {
                        /*
                            if (q2 == 0)
                                activateImprovingDetectionLowerBound = false;
                            else
                                activateImprovingDetectionLowerBound = true;
                         */
                        System.out.println("activateImprovingDetectionLowerBound = " + activateImprovingDetectionLowerBound);
                        for (int q3 = 1; q3 > -1; q3--) {
                            if (q3 == 0) {
                                filterLowerBound = false;
                            } else {
                                filterLowerBound = true;
                            }
                            System.out.println("filterLowerBound = " + filterLowerBound);

                            for (int q4 = 1; q4 > -1; q4--) {
                                if (q4 == 0) {
                                    filterUpperBound = false;
                                } else {
                                    filterUpperBound = true;
                                }
                                if (!(q3 == 0 && q4 == 1 && activateImprovingDetectionLowerBound)
                                        && (q3 == 1 || q4 == 1)) {
                                    System.out.println("filterUpperBound = " + filterUpperBound);

                                    lineCounter++;
                                    HSSFRow newRow = sheet1.createRow((short) lineCounter);

                                    newRow.createCell(0).setCellValue(instanceId);
                                    newRow.createCell(1).setCellValue(nA);
                                    newRow.createCell(2).setCellValue(nC);
                                    newRow.createCell(3).setCellValue(min_processingTime);
                                    newRow.createCell(4).setCellValue(max_processingTime);
                                    newRow.createCell(5).setCellValue(seed);
                                    /*
                    switch (heuristic) {
                        case 1:
                            newRow.createCell(6).setCellValue("LEXICOGRAPHIC_LB_HEURISTIQUE");
                            break;
                        case 2:
                            newRow.createCell(6).setCellValue("IMPACT_BASED_SEARCH_HEURISTIQUE");
                            break;
                        case 3:
                            newRow.createCell(6).setCellValue("DOMOVERWDEG_HEURISTIQUE");
                            break;
                        case 4:
                            newRow.createCell(6).setCellValue("SMALLEST_HEURISTIQUE");
                            break;
                        default:
                            break;
                    }
                    if (activateImprovingDetectionLowerBound)
                        newRow.createCell(7).setCellValue("TRUE");
                    else
                        newRow.createCell(7).setCellValue("FALSE");
                                     */
                                    if (filterLowerBound) {
                                        newRow.createCell(6).setCellValue("TRUE");
                                    } else {
                                        newRow.createCell(6).setCellValue("FALSE");
                                    }
                                    if (filterUpperBound) {
                                        newRow.createCell(7).setCellValue("TRUE");
                                    } else {
                                        newRow.createCell(7).setCellValue("FALSE");
                                    }
                                    int numberOfVariablesToFix = 1;
                                    if (runningTTAndOCIsRequired) {
                                        // timeTabling = new modelOfCraneAssignmentProblem(nA, nC, nR, min_processingTime, max_processingTime, MODE_NO_OVERLOADCHECK, MODE_NO_EDGEFINDING, seed, heuristic, filterLowerBound, filterUpperBound, activateImprovingDetectionLowerBound, findAllOptimalSolutions, printSolutionInformatioin, 1);
                                        //  timeTabling = new modelOfCraneAssignmentProblemForTemporalProtection(nA, nC, nR, min_processingTime, max_processingTime, MODE_NO_OVERLOADCHECK, MODE_NO_EDGEFINDING, seed, heuristic, filterLowerBound, filterUpperBound, activateImprovingDetectionLowerBound, findAllOptimalSolutions, printSolutionInformatioin, 1);
                                        timeTabling = new modelOfCraneAssignmentProblemIncludingLNSApproach(nA, nC, nR, min_processingTime, max_processingTime, MODE_NO_OVERLOADCHECK, MODE_NO_EDGEFINDING, seed, heuristic, filterLowerBound, filterUpperBound, activateImprovingDetectionLowerBound, printSolutionInformatioin, 1, numberOfVariablesToFix, findOptimalSolution);
                                        ttelapsedTiem = timeTabling.howMuchTime();
                                        ttbacktrackNum = timeTabling.howManyBacktracks();
                                        ttmakespan = timeTabling.makespan();
                                        if (!timeTabling.aSubOptimalSolutionIsFound()) {
                                            ttdecidedCase = case3;
                                        } else if (!timeTabling.timeLimitHasBypassedTheSearchProcess()) {
                                            ttdecidedCase = case1;
                                        } else if (timeTabling.timeLimitHasBypassedTheSearchProcess()) {
                                            ttdecidedCase = case2;
                                        }

                                    }
                                    System.out.println("ttelapsedTiem = " + ttelapsedTiem);
                                    System.out.println("ttbacktrackNum = " + ttbacktrackNum);

                                    if (runningTTAndOCIsRequired) {
                                        //  overloadChecking = new modelOfCraneAssignmentProblem(nA, nC, nR, min_processingTime, max_processingTime, MODE_OVERLOADCHECK, MODE_NO_EDGEFINDING, seed, heuristic, filterLowerBound, filterUpperBound, activateImprovingDetectionLowerBound, findAllOptimalSolutions, printSolutionInformatioin, 1);
                                        // overloadChecking = new modelOfCraneAssignmentProblemForTemporalProtection(nA, nC, nR, min_processingTime, max_processingTime, MODE_OVERLOADCHECK, MODE_NO_EDGEFINDING, seed, heuristic, filterLowerBound, filterUpperBound, activateImprovingDetectionLowerBound, findAllOptimalSolutions, printSolutionInformatioin, 1);
                                        overloadChecking = new modelOfCraneAssignmentProblemIncludingLNSApproach(nA, nC, nR, min_processingTime, max_processingTime, MODE_OVERLOADCHECK, MODE_NO_EDGEFINDING, seed, heuristic, filterLowerBound, filterUpperBound, activateImprovingDetectionLowerBound, printSolutionInformatioin, 1, numberOfVariablesToFix, findOptimalSolution);

                                        ocelapsedTiem = overloadChecking.howMuchTime();
                                        ocbacktrackNum = overloadChecking.howManyBacktracks();
                                        ocmakespan = overloadChecking.makespan();
                                        if (!overloadChecking.aSubOptimalSolutionIsFound()) {
                                            ocdecidedCase = case3;
                                        } else if (!overloadChecking.timeLimitHasBypassedTheSearchProcess()) {
                                            ocdecidedCase = case1;
                                        } else if (overloadChecking.timeLimitHasBypassedTheSearchProcess()) {
                                            ocdecidedCase = case2;
                                        }
                                    }
                                    System.out.println("ocelapsedTiem = " + ocelapsedTiem);
                                    System.out.println("ocbacktrackNum = " + ocbacktrackNum);

                                    // edgeFinding = new modelOfCraneAssignmentProblem(nA, nC, nR, min_processingTime, max_processingTime, MODE_NO_OVERLOADCHECK, MODE_EDGEFINDING, seed, heuristic, filterLowerBound, filterUpperBound, activateImprovingDetectionLowerBound, findAllOptimalSolutions, printSolutionInformatioin, 1);
                                    //   edgeFinding = new modelOfCraneAssignmentProblemForTemporalProtection(nA, nC, nR, min_processingTime, max_processingTime, MODE_NO_OVERLOADCHECK, MODE_EDGEFINDING, seed, heuristic, filterLowerBound, filterUpperBound, activateImprovingDetectionLowerBound, findAllOptimalSolutions, printSolutionInformatioin, 1);
                                    edgeFinding = new modelOfCraneAssignmentProblemIncludingLNSApproach(nA, nC, nR, min_processingTime, max_processingTime, MODE_NO_OVERLOADCHECK, MODE_EDGEFINDING, seed, heuristic, filterLowerBound, filterUpperBound, activateImprovingDetectionLowerBound, printSolutionInformatioin, 1, numberOfVariablesToFix, findOptimalSolution);

                                    efelapsedTiem = edgeFinding.howMuchTime();
                                    efbacktrackNum = edgeFinding.howManyBacktracks();
                                    efmakespan = edgeFinding.makespan();
                                    if (!edgeFinding.aSubOptimalSolutionIsFound()) {
                                        efdecidedCase = case3;
                                    } else if (!edgeFinding.timeLimitHasBypassedTheSearchProcess()) {
                                        efdecidedCase = case1;
                                    } else if (edgeFinding.timeLimitHasBypassedTheSearchProcess()) {
                                        efdecidedCase = case2;
                                    }
                                    System.out.println("efelapsedTiem = " + efelapsedTiem);
                                    System.out.println("efbacktrackNum = " + efbacktrackNum);

                                    newRow.createCell(8);

                                    newRow.createCell(9).setCellValue(ttbacktrackNum);
                                    newRow.createCell(10).setCellValue(ocbacktrackNum);
                                    newRow.createCell(11).setCellValue(efbacktrackNum);
                                    newRow.createCell(12).setCellValue(ttelapsedTiem);
                                    newRow.createCell(13).setCellValue(ocelapsedTiem);
                                    newRow.createCell(14).setCellValue(efelapsedTiem);
                                    newRow.createCell(15).setCellValue(ttmakespan);
                                    newRow.createCell(16).setCellValue(ocmakespan);
                                    newRow.createCell(17).setCellValue(efmakespan);
                                    newRow.createCell(18).setCellValue(ttdecidedCase);
                                    newRow.createCell(19).setCellValue(ocdecidedCase);
                                    newRow.createCell(20).setCellValue(efdecidedCase);

                                    FileOutputStream fileOut1 = new FileOutputStream(filename1);
                                    workbook1.write(fileOut1);
                                    fileOut1.close();
                                    System.out.println("fj");
                                    runningTTAndOCIsRequired = false;
                                }

                            }
                        }
                    }
                    runningTTAndOCIsRequired = true;
                }
            }
        } catch (NoSuchElementException elementException) {
            System.err.println("File improperly formed. Terminating.");
        } catch (IllegalStateException stateException) {
            System.err.println("Error reading from file. Terminating.");
        }
        //   System.out.println("Your excel file has been generated!");
    }

    public static void closeFile() {
        if (input != null) {
            input.close();
        }
    }
}
