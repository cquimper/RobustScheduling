    package robustcumulativescheduling;
    import java.io.FileOutputStream;
    import java.io.PrintStream;
    import java.io.BufferedReader;
    import java.io.FileReader;
    import java.io.IOException;
    import java.text.DateFormat;
    import java.text.SimpleDateFormat;
    import java.util.Date;
    import java.util.Random;
    import org.apache.poi.hssf.usermodel.HSSFRow;
    import org.apache.poi.hssf.usermodel.HSSFSheet;
    import org.apache.poi.hssf.usermodel.HSSFWorkbook;
    import org.apache.poi.ss.usermodel.Cell;
    import org.apache.poi.xssf.usermodel.XSSFRow;
    import org.apache.poi.xssf.usermodel.XSSFSheet;
    import org.apache.poi.xssf.usermodel.XSSFWorkbook;

    /**
     *
     * @author hamedfahimi
     */
    public class generateExcelFilesForExperiments {

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
        public static final int HEURISTIQUE_DOMOVERWDEG = 4;
        public static int HEURISTIC_MODE;
        public static int SEED;
        public static double DELAY_COEFFICIENT;
        public static int FILTER_LB;
        public static int FILTER_UB;
        public static int FOUND_ONE_OPTIMAL_SOLUTION;
        public static int FOUND_ALL_OPTIMAL_SOLUTIONS;
public static boolean activateImprovingDetectionLowerBound;
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



        public static void main(String[] args) throws Exception  {
            //	PrintStream out = new PrintStream(new FileOutputStream("myOutput10.txt"));
            //	System.setOut(out);
activateImprovingDetectionLowerBound = false;
activateImprovingDetectionUpperBound = false;

            benchmarkBLModelAdaptedWithAlbanFramework timeTabling = null;
            benchmarkBLModelAdaptedWithAlbanFramework overloadChecking = null;
            benchmarkBLModelAdaptedWithAlbanFramework edgeFinding = null;
            int instance_code = BL_INSTANCE;
            String filename1 = "myResultsRandom.xls" ;
            HSSFWorkbook workbook1 = new HSSFWorkbook();
            HSSFSheet sheet1 = workbook1.createSheet("FirstSheet");
            HSSFRow rowhead1 = sheet1.createRow((short) 0);
            rowhead1.createCell(0).setCellValue("Data ID.");
            rowhead1.createCell(1);
            rowhead1.createCell(2).setCellValue("TT(bt)");
            rowhead1.createCell(3).setCellValue("OC&TT(bt)");
            rowhead1.createCell(4).setCellValue("EF&&TT(bt)");
            rowhead1.createCell(5);
            rowhead1.createCell(6).setCellValue("TT(t)");
            rowhead1.createCell(7).setCellValue("OC&TT(t)");
            rowhead1.createCell(8).setCellValue("EF&&TT(t)");
            rowhead1.createCell(9);
            rowhead1.createCell(10).setCellValue("Heuristic");
            rowhead1.createCell(11).setCellValue("Seed");
            rowhead1.createCell(12).setCellValue("Delay factor");
            rowhead1.createCell(13).setCellValue("FilterLB");
            rowhead1.createCell(14).setCellValue("FilterUB");
            //	rowhead1.createCell(15).setCellValue("FoundOneOptimalSolution");
            //rowhead1.createCell(16).setCellValue("FoundAllOptimalSolutions");

            BufferedReader reader1 = null;
            try {
                //	reader1 = new BufferedReader(new FileReader("FileNamesForTests.txt"));
                reader1 = new BufferedReader(new FileReader("DOM-bl25.txt"));
                String read = null;
                int lineCounter = 1;
                while ((read = reader1.readLine()) != null) {
                    // if ((read = reader1.readLine()) != null) {
                    int i = 0;
                    String dataName = null;
                    String configurationFile;
                    String[] splited = read.split("\\s+");
                    for (String part : splited) {
                        if (i == 0) {
                            dataName = part;
                            //System.out.println(dataName);
                        }
                        if (i == 2) {
                            configurationFile = part;
                            // System.out.println(configurationFile);
                            String[] tokens = configurationFile.split( "," );
                            int j = 0;
                            for (String token : tokens) {
                                if (j == 0)
                                    HEURISTIC_MODE = Integer.parseInt(token);
                                if (j == 1)
                                    SEED =  Integer.parseInt(token);
                                if (j == 2)
                                    DELAY_COEFFICIENT =  Double.parseDouble(token);
                                if (j == 3)
                                    FILTER_LB = Integer.parseInt(token);
                                if (j == 4)
                                    FILTER_UB = Integer.parseInt(token);
                                //	if (j == 5)
                                //	FOUND_ONE_OPTIMAL_SOLUTION = Integer.parseInt(token);

                                //	if (j == 6)
                                //FOUND_ALL_OPTIMAL_SOLUTIONS = Integer.parseInt(token);

                                //   System.out.println(token);
                                j++;
                            }
                        }
                        i++;
                    }

                    int[] d;

                    if (instance_code == BL_INSTANCE)
                    {
                        BLBenchmarkInstances A = new BLBenchmarkInstances(dataName);
                        int[] processingTimes = A.processingTimes();
                        d = new int[processingTimes.length];
                        Random randomNumbers = new Random();
                        randomNumbers.setSeed(SEED);
                        for (int l = 0; l < processingTimes.length; l++) {
                            d[l] = randomNumbers.nextInt((int) Math.ceil(DELAY_COEFFICIENT * (processingTimes[l]) + 1));
                            //   d[i] = 0;
                        }
                    }

                    else {
                        PSPLibBenchmarkInstances A = new PSPLibBenchmarkInstances(dataName);
                        int[] processingTimes = A.processingTimes();
                        d = new int[processingTimes.length];
                        Random randomNumbers = new Random();
                        randomNumbers.setSeed(SEED);
                        for (int i2 = 0; i2 < processingTimes.length; i2++) {
                            d[i2] = randomNumbers.nextInt((int) Math.ceil(DELAY_COEFFICIENT * (processingTimes[i2]) + 1));
                            // d[i] = 0;
                        }

                    }

                    boolean t = true;
                    boolean f = false;
                    //HEURISTIQUE_LEXICOGRAPHIC
                    //  HEURISTIQUE_IMPACT_BASED_SEARCH
                    //HEURISTIQUE_LEXICO_SPLIT
                    boolean findAllOptimalSolutions;
                    boolean filterLB;
                    boolean filterUB;

                    if (FILTER_LB == 1)
                        filterLB = true;
                    else
                        filterLB = false;
                    if (FILTER_UB == 1)
                        filterUB = true;
                    else
                        filterUB = false;

                    //	if (FOUND_ONE_OPTIMAL_SOLUTION == 1)
                    findAllOptimalSolutions = false;
                    //else
                    //findAllOptimalSolutions = true;

                    if (lineCounter % 3 == 1) {
                        timeTabling = new benchmarkBLModelAdaptedWithAlbanFramework(instance_code, dataName, d, MODE_NO_OVERLOADCHECK, MODE_NO_EDGEFINDING,  HEURISTIC_MODE, filterLB, filterUB, activateImprovingDetectionLowerBound, findAllOptimalSolutions, f, 1);

                        DateFormat dateFormat1 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        Date date1 = new Date();
                        System.out.println("Finished TT on the instance " + dataName + " with seed = " + SEED + " and delay factor " + DELAY_COEFFICIENT + " at this date:");
                        System.out.println(dateFormat1.format(date1));
                        System.out.println();

                    }



                    if (lineCounter % 3 == 1) {
                        overloadChecking = new benchmarkBLModelAdaptedWithAlbanFramework(instance_code, dataName, d, MODE_OVERLOADCHECK, MODE_NO_EDGEFINDING,  HEURISTIC_MODE, filterLB, filterUB, activateImprovingDetectionLowerBound, findAllOptimalSolutions, f, 1);


                        DateFormat dateFormat2 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        Date date2 = new Date();
                        System.out.println("Finished OC&&TT on the instance " + dataName + " with seed = " + SEED + " and delay factor " + DELAY_COEFFICIENT + " at this date:");
                        System.out.println(dateFormat2.format(date2));
                        System.out.println();
                    }



                    edgeFinding =  new benchmarkBLModelAdaptedWithAlbanFramework(instance_code, dataName, d, MODE_NO_OVERLOADCHECK, MODE_EDGEFINDING,  HEURISTIC_MODE, filterLB, filterUB, activateImprovingDetectionLowerBound, findAllOptimalSolutions, f, 1);

                    DateFormat dateFormat3 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    Date date3 = new Date();
                    System.out.println("Finished EF&&TT on the instance " + dataName + " with seed = " + SEED + " and delay factor " + DELAY_COEFFICIENT + " at this date:");
                    System.out.println(dateFormat3.format(date3));
                    System.out.println();


                    HSSFRow row1 = sheet1.createRow((short)lineCounter);
                    row1.createCell(0).setCellValue(dataName);
                    row1.createCell(1);
                    row1.createCell(2).setCellValue(timeTabling.backtracksNum);
                    row1.createCell(3).setCellValue(overloadChecking.backtracksNum);
                    row1.createCell(4).setCellValue(edgeFinding.backtracksNum);
                    row1.createCell(5);
                    row1.createCell(6).setCellValue(timeTabling.elapsedTime);
                    row1.createCell(7).setCellValue(overloadChecking.elapsedTime);
                    row1.createCell(8).setCellValue(edgeFinding.elapsedTime);
                    row1.createCell(9);


                    if (HEURISTIC_MODE == 0)
                        row1.createCell(10).setCellValue("LEXICO_NEQ_LB");
                    else if (HEURISTIC_MODE == 1)
                        row1.createCell(10).setCellValue("LEXICOGRAPHIC_LB");
                    else if (HEURISTIC_MODE == 2)
                        row1.createCell(10).setCellValue("DOMOVERWDEG");
                    else if (HEURISTIC_MODE == 3)
                        row1.createCell(10).setCellValue("IMPACT_BASED_SEARCH");

                    row1.createCell(11).setCellValue(SEED);
                    row1.createCell(12).setCellValue(DELAY_COEFFICIENT);
                    if (FILTER_LB == 0)
                        row1.createCell(13).setCellValue("NO");
                    else
                        row1.createCell(13).setCellValue("YES");
                    if (FILTER_UB == 0)
                        row1.createCell(14).setCellValue("NO");
                    else
                        row1.createCell(14).setCellValue("YES");

    //				if (FOUND_ONE_OPTIMAL_SOLUTION == 0)
    //				{	row1.createCell(15).setCellValue("NO");
    //				row1.createCell(16).setCellValue("YES");
    //
    //				}
    //				else
    //					{
    //					row1.createCell(15).setCellValue("YES");
    //
    //					row1.createCell(16).setCellValue("NO");
    //
    //
    //					}
    //				if (FOUND_ALL_OPTIMAL_SOLUTIONS == 0)
    //					row1.createCell(16).setCellValue("NO");
    //				else
    //					row1.createCell(16).setCellValue("YES");



    FileOutputStream fileOut1 = new FileOutputStream(filename1);
    workbook1.write(fileOut1);
    fileOut1.close();




    lineCounter++;
                }
            } catch (IOException e) {
                System.out.println("There was a problem: " + e);
                e.printStackTrace();
            } finally {
                try {
                    reader1.close();
                } catch (Exception e) {
                }
            }
    //		FileOutputStream fileOut1 = new FileOutputStream(filename1);
    //		workbook1.write(fileOut1);
    //		fileOut1.close();


    System.out.println("Your excel file has been generated!");
        }
    }
