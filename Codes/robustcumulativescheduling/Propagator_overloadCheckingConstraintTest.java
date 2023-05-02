package robustcumulativescheduling;

import java.util.Comparator;
import java.util.Vector;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.exception.ContradictionException;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.VariableFactory;
import org.chocosolver.util.ESat;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class Propagator_overloadCheckingConstraintTest {

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of main method, of class newOverloadChecking3.
     */
    @Test
    public void testChecking_E_Feasibility() {
        System.out.println(" working directory = " + System.getProperty("user.dir"));

        Task[] T;
        int delayNumber;
        int resourceCapacity;
        newOverloadChecking instance;
        boolean expResult;
        boolean result;
        int numberOfTasks;
        int[] d;

      
        
       
        
        
        
        Task MyTask214 = new Task(0, 4, 8, 4, 4, 11);//A
        Task MyTask215 = new Task(8, 12, 16, 4, 4, 9);//B
        Task MyTask216 = new Task(26, 27, 28, 1, 1, 7);//C
        Task MyTask217 = new Task(16, 20, 26, 4, 6, 6);//D
        Task MyTask218 = new Task(21, 22, 23, 1, 1, 5);//E
        Task MyTask219 = new Task(22, 25, 26, 2, 1, 5);//F
        Task MyTask220 = new Task(16, 21, 23, 5, 2, 3);//G
        Task MyTask221 = new Task(8, 21, 23, 10, 2, 2);//H
        T = new Task[8];
        T[0] = MyTask214;
        T[1] = MyTask215;
        T[2] = MyTask216;
        T[3] = MyTask217;
        T[4] = MyTask218;
        T[5] = MyTask219;
        T[6] = MyTask220;
        T[7] = MyTask221;
        delayNumber = 1;
        resourceCapacity = 11;
        instance = new newOverloadChecking();
        numberOfTasks = T.length;
        d = new int[numberOfTasks];
        result = instance.checking_E_Feasibility(T, delayNumber, resourceCapacity);
        expResult = true;
        assertEquals(expResult, result); 

        
        Task MyTask206 = new Task(0, 4, 8, 4, 4, 11);//A
        Task MyTask207 = new Task(8, 12, 16, 4, 4, 9);//B
        Task MyTask208 = new Task(16, 17, 18, 1, 1, 7);//C
        Task MyTask209 = new Task(18, 22, 28, 4, 6, 6);//D
        Task MyTask210 = new Task(20, 21, 22, 1, 1, 5);//E
        Task MyTask211 = new Task(22, 30, 31, 2, 1, 5);//E
        Task MyTask212 = new Task(22, 29, 31, 5, 2, 3);//E
        Task MyTask213 = new Task(8, 18, 20, 10, 2, 2);//E
        T = new Task[8];
        T[0] = MyTask206;
        T[1] = MyTask207;
        T[2] = MyTask208;
        T[3] = MyTask209;
        T[4] = MyTask210;
        T[5] = MyTask211;
        T[6] = MyTask212;
        T[7] = MyTask213;
        delayNumber = 1;
        resourceCapacity = 11;
        instance = new newOverloadChecking();
        numberOfTasks = T.length;
        d = new int[numberOfTasks];
        result = instance.checking_E_Feasibility(T, delayNumber, resourceCapacity);
        expResult = true;
        assertEquals(expResult, result); 
        
         Task MyTask1570 = new Task(1, 13, 14, 4, 1, 6);//A
        Task MyTask158 = new Task(6, 23, 24, 9, 1, 7);//B
        Task MyTask159 = new Task(16, 36, 46, 8, 10, 2);//C
        Task MyTask160 = new Task(14, 28, 29, 12, 1, 6);//D
        //Delaying B causes an overload
        T = new Task[4];
        T[0] = MyTask1570;
        T[1] = MyTask158;
        T[2] = MyTask159;
        T[3] = MyTask160;
        delayNumber = 1;
        resourceCapacity = 7;
        instance = new newOverloadChecking();
        result = instance.checking_E_Feasibility(T, delayNumber, resourceCapacity);
        expResult = true;
        assertEquals(expResult, result);
        
        Task MyTask123 = new Task(12, 40, 56, 3, 16, 4);//A
        Task MyTask124 = new Task(6, 47, 54, 28, 7, 9);//B  
        Task MyTask125 = new Task(19, 46, 53, 20, 7, 4);//C
        Task MyTask126 = new Task(19, 35, 36, 11, 1, 7);//D
        //Delaying B causes an overload
        T = new Task[4];
        T[0] = MyTask123;
        T[1] = MyTask124;
        T[2] = MyTask125;
        T[3] = MyTask126;
        delayNumber = 1;
        resourceCapacity = 10;
        instance = new newOverloadChecking();
        result = instance.checking_E_Feasibility(T, delayNumber, resourceCapacity);
        expResult = false;
        assertEquals(expResult, result);
        
        
        
        
        Task MyTask200 = new Task(1, 20, 21, 5, 1, 2);//A
        Task MyTask201 = new Task(4, 23, 25, 7, 2, 4);//B
        Task MyTask202 = new Task(0, 14, 14, 4, 0, 3);//C
        Task MyTask203 = new Task(0, 21, 24, 8, 3, 4);//D
        Task MyTask204 = new Task(9, 26, 27, 2, 1, 1);//E
        T = new Task[5];
        T[0] = MyTask200;
        T[1] = MyTask201;
        T[2] = MyTask202;
        T[3] = MyTask203;
        T[4] = MyTask204;
        delayNumber = 2;
        resourceCapacity = 4;
        instance = new newOverloadChecking();
        numberOfTasks = T.length;
        d = new int[numberOfTasks];
        result = instance.checking_E_Feasibility(T, delayNumber, resourceCapacity);
        expResult = false;
        assertEquals(expResult, result);

        Task MyTask138 = new Task(1, 5, 6, 4, 1, 6);//A
        Task MyTask139 = new Task(3, 4, 4, 1, 0, 1);//B
        Task MyTask140 = new Task(6, 12, 13, 1, 1, 8);//C
        Task MyTask141 = new Task(6, 13, 13, 2, 0, 6);//D
        Task MyTask142 = new Task(10, 23, 25, 5, 2, 2);//E
        Task MyTask143 = new Task(10, 23, 25, 4, 2, 6);//F
        Task MyTask144 = new Task(0, 23, 24, 2, 1, 2);//G
        Task MyTask145 = new Task(6, 13, 13, 4, 0, 8);//H
        Task MyTask146 = new Task(10, 23, 29, 10, 6, 2);//I
        T = new Task[9];
        T[0] = MyTask138;
        T[1] = MyTask139;
        T[2] = MyTask140;
        T[3] = MyTask141;
        T[4] = MyTask142;
        T[5] = MyTask143;
        T[6] = MyTask144;
        T[7] = MyTask145;
        T[8] = MyTask146;
        resourceCapacity = 10;
        delayNumber = 1;
        instance = new newOverloadChecking();
        result = instance.checking_E_Feasibility(T, delayNumber, resourceCapacity);
        expResult = true;
        assertEquals(expResult, result);

        
        
        
        
        

        //all good
        Task MyTask310 = new Task(14, 23, 25, 9, 2, 4);//A
        Task MyTask311 = new Task(0, 5, 6, 5, 1, 3);//D
        Task MyTask312 = new Task(19, 24, 28, 4, 4, 4);//A
        Task MyTask313 = new Task(6, 24, 25, 4, 1, 4);//D

        T = new Task[4];
        T[0] = MyTask310;
        T[1] = MyTask311;
        T[2] = MyTask312;
        T[3] = MyTask313;
        resourceCapacity = 4;
        delayNumber = 1;
        instance = new newOverloadChecking();
        result = instance.checking_E_Feasibility(T, delayNumber, resourceCapacity);
        expResult = false;
        assertEquals(expResult, result);

        Task MyTask3 = new Task(4, 10, 10, 4, 0, 2);//A
        Task MyTask4 = new Task(1, 8, 10, 6, 2, 3);//C
        Task MyTask5 = new Task(2, 7, 8, 4, 1, 1);//D
        Task MyTask6 = new Task(1, 5, 5, 1, 0, 1);//B
        //Delaying B in the whole set causes overload. 
        T = new Task[4];
        T[0] = MyTask3;
        T[1] = MyTask4;
        T[2] = MyTask5;
        T[3] = MyTask6;
        delayNumber = 1;
        resourceCapacity = 4;
        instance = new newOverloadChecking();
        result = instance.checking_E_Feasibility(T, delayNumber, resourceCapacity);
        expResult = false;
        assertEquals(expResult, result);

        Task MyTask11 = new Task(1, 9, 9, 3, 0, 1);//A
        Task MyTask12 = new Task(1, 7, 9, 4, 2, 1);//B
        T = new Task[2];
        T[0] = MyTask11;
        T[1] = MyTask12;
        delayNumber = 1;
        resourceCapacity = 1;
        instance = new newOverloadChecking();
        result = instance.checking_E_Feasibility(T, delayNumber, resourceCapacity);
        expResult = false;
        assertEquals(expResult, result);

        Task MyTask356 = new Task(0, 14, 18, 5, 4, 3);//D
        Task MyTask357 = new Task(0, 8, 12, 4, 4, 4);//E
        Task MyTask359 = new Task(5, 14, 16, 4, 2, 4);//G
        T = new Task[3];
        T[0] = MyTask356;
        T[1] = MyTask357;
        T[2] = MyTask359;
        delayNumber = 1;
        resourceCapacity = 1;
        instance = new newOverloadChecking();
        result = instance.checking_E_Feasibility(T, delayNumber, resourceCapacity);
        expResult = false;
        assertEquals(expResult, result);

        Task MyTask0 = new Task(1, 6, 8, 4, 2, 2);//A
        Task MyTask1 = new Task(1, 8, 8, 2, 0, 3);//B
        Task MyTask2 = new Task(1, 8, 9, 2, 1, 2);//C
        //Delaying A in the whole set causes overload. 
        T = new Task[3];
        T[0] = MyTask0;
        T[1] = MyTask1;
        T[2] = MyTask2;
        delayNumber = 1;
        resourceCapacity = 3;
        instance = new newOverloadChecking();
        result = instance.checking_E_Feasibility(T, delayNumber, resourceCapacity);
        expResult = false;
        assertEquals(expResult, result);

        Task MyTask8 = new Task(1, 10, 10, 2, 0, 2);//A
        Task MyTask9 = new Task(1, 9, 10, 6, 1, 3);//B
        Task MyTask10 = new Task(2, 11, 12, 2, 1, 2);//C
        T = new Task[3];
        T[0] = MyTask8;
        T[1] = MyTask9;
        T[2] = MyTask10;
        resourceCapacity = 3;
        delayNumber = 1;
        instance = new newOverloadChecking();
        result = instance.checking_E_Feasibility(T, delayNumber, resourceCapacity);
        expResult = true;
        assertEquals(expResult, result);

        Task MyTask50 = new Task(1, 10, 11, 1, 1, 2);//A
        Task MyTask51 = new Task(1, 10, 10, 8, 0, 3);//B
        Task MyTask52 = new Task(1, 11, 12, 2, 1, 2);//C
        T = new Task[3];
        T[0] = MyTask50;
        T[1] = MyTask51;
        T[2] = MyTask52;
        resourceCapacity = 11;
        delayNumber = 1;
        instance = new newOverloadChecking();
        result = instance.checking_E_Feasibility(T, delayNumber, resourceCapacity);
        expResult = true;
        assertEquals(expResult, result);

        Task MyTask13 = new Task(4, 15, 15, 6, 0, 4);//A
        Task MyTask14 = new Task(2, 14, 14, 4, 0, 3);//B
        Task MyTask15 = new Task(2, 14, 14, 3, 0, 4);//C
        Task MyTask16 = new Task(3, 15, 15, 5, 0, 2);//D
        Task MyTask17 = new Task(2, 17, 17, 5, 0, 2);//D
        T = new Task[5];
        T[0] = MyTask13;
        T[1] = MyTask14;
        T[2] = MyTask15;
        T[3] = MyTask16;
        T[4] = MyTask17;
        delayNumber = 1;
        resourceCapacity = 5;
        instance = new newOverloadChecking();
        result = instance.checking_E_Feasibility(T, delayNumber, resourceCapacity);
        expResult = true;
        assertEquals(expResult, result);

        Task MyTask118 = new Task(4, 11, 12, 3, 1, 2);//A
        Task MyTask119 = new Task(2, 13, 15, 5, 2, 3);//B
        Task MyTask120 = new Task(1, 9, 10, 5, 1, 3);//C
        Task MyTask121 = new Task(1, 8, 10, 4, 2, 3);//D
        Task MyTask122 = new Task(3, 9, 11, 4, 2, 1);//D
        T = new Task[5];
        T[0] = MyTask118;
        T[1] = MyTask119;
        T[2] = MyTask120;
        T[3] = MyTask121;
        T[4] = MyTask122;
        delayNumber = 1;
        resourceCapacity = 4;
        instance = new newOverloadChecking();
        result = instance.checking_E_Feasibility(T, delayNumber, resourceCapacity);
        expResult = false;
        assertEquals(expResult, result);

        Task MyTask168 = new Task(-22, -13, -11, 9, 2, 4);//A
        Task MyTask169 = new Task(-13, -9, -9, 4, 0, 4);//B
        Task MyTask170 = new Task(-9, -7, -7, 2, 0, 3);//C
        T = new Task[3];
        T[0] = MyTask168;
        T[1] = MyTask169;
        T[2] = MyTask170;

        resourceCapacity = 4;
        delayNumber = 1;
        instance = new newOverloadChecking();
        result = instance.checking_E_Feasibility(T, delayNumber, resourceCapacity);
        expResult = false;
        assertEquals(expResult, result);

        Task MyTask101 = new Task(26, 37, 38, 4, 1, 4);
        Task MyTask102 = new Task(21, 37, 38, 4, 1, 4);
        Task MyTask103 = new Task(21, 33, 34, 5, 1, 3);
        Task MyTask104 = new Task(21, 37, 37, 4, 0, 4);
        //  Task MyTask105 = new Task (19, 21, 21, 2, 0, 3  );
        // Task MyTask106 = new Task (7, 16, 17, 9, 1, 4 );
        //  Task MyTask107 = new Task (0, 6, 7, 6, 1, 1);
        T = new Task[4];
        T[0] = MyTask101;
        T[1] = MyTask102;
        T[2] = MyTask103;
        T[3] = MyTask104;
        // T[4] = MyTask105;
        //  T[5] = MyTask106;
        //   T[6] = MyTask107;
        resourceCapacity = 4;
        delayNumber = 1;
        instance = new newOverloadChecking();
        result = instance.checking_E_Feasibility(T, delayNumber, resourceCapacity);
        expResult = false;
        assertEquals(expResult, result);

        Task MyTask148 = new Task(3, 11, 11, 2, 0, 1);
        Task MyTask149 = new Task(2, 15, 15, 5, 0, 1);
        Task MyTask150 = new Task(5, 9, 9, 1, 0, 1);
        Task MyTask151 = new Task(7, 20, 20, 3, 0, 1);
        Task MyTask152 = new Task(4, 20, 20, 2, 0, 1);
        Task MyTask153 = new Task(9, 35, 35, 7, 0, 1);
        Task MyTask154 = new Task(3, 19, 19, 1, 0, 1);
        Task MyTask155 = new Task(1, 22, 22, 2, 0, 1);
        Task MyTask156 = new Task(11, 28, 28, 6, 0, 1);
        Task MyTask157 = new Task(1, 10, 10, 5, 0, 1);
        T = new Task[10];
        T[0] = MyTask148;
        T[1] = MyTask149;
        T[2] = MyTask150;
        T[3] = MyTask151;
        T[4] = MyTask152;
        T[5] = MyTask153;
        T[6] = MyTask154;
        T[7] = MyTask155;
        T[8] = MyTask156;
        T[9] = MyTask157;
        delayNumber = 1;
        resourceCapacity = 1;
        instance = new newOverloadChecking();
        result = instance.checking_E_Feasibility(T, delayNumber, resourceCapacity);
        expResult = true;
        assertEquals(expResult, result);

        Task MyTask110 = new Task(19, 23, 24, 4, 1, 6);
        Task MyTask111 = new Task(14, 31, 34, 10, 3, 2);
        Task MyTask112 = new Task(14, 31, 32, 4, 1, 8);
        Task MyTask113 = new Task(14, 31, 32, 4, 1, 6);
        Task MyTask114 = new Task(14, 31, 32, 3, 1, 2);
        Task MyTask115 = new Task(14, 29, 29, 5, 0, 2);
        Task MyTask116 = new Task(14, 31, 31, 2, 0, 6);
        Task MyTask117 = new Task(19, 31, 31, 2, 0, 2);
        T = new Task[8];
        T[0] = MyTask110;
        T[1] = MyTask111;
        T[2] = MyTask112;
        T[3] = MyTask113;
        T[4] = MyTask114;
        T[5] = MyTask115;
        T[6] = MyTask116;
        T[7] = MyTask117;
        resourceCapacity = 8;
        delayNumber = 1;
        instance = new newOverloadChecking();
        result = instance.checking_E_Feasibility(T, delayNumber, resourceCapacity);
        expResult = false;
        assertEquals(expResult, result);

        Task MyTask500 = new Task(22, 34, 36, 10, 2, 2);//A
        Task MyTask501 = new Task(11, 22, 25, 8, 3, 5);//B
        Task MyTask502 = new Task(5, 11, 11, 2, 0, 9);//C
        Task MyTask503 = new Task(1, 30, 30, 5, 0, 3);//D
        Task MyTask504 = new Task(9, 32, 32, 2, 0, 6);//E
        Task MyTask505 = new Task(5, 11, 11, 4, 0, 10);//F
        T = new Task[6];
        T[0] = MyTask500;
        T[1] = MyTask501;
        T[2] = MyTask502;
        T[3] = MyTask503;
        T[4] = MyTask504;
        T[5] = MyTask505;
        resourceCapacity = 10;
        delayNumber = 1;
        instance = new newOverloadChecking();
        result = instance.checking_E_Feasibility(T, delayNumber, resourceCapacity);
        expResult = true;
        assertEquals(expResult, result);

        Task MyTask893 = new Task(0, 12, 13, 1, 1, 1);//A
        Task MyTask894 = new Task(0, 20, 21, 2, 1, 2);//B
        Task MyTask895 = new Task(0, 12, 14, 5, 2, 1);//C 
        Task MyTask896 = new Task(12, 17, 22, 5, 5, 2);//D
        Task MyTask897 = new Task(1, 15, 16, 3, 1, 1);//E
        Task MyTask898 = new Task(0, 18, 19, 4, 1, 2);//F
        Task MyTask899 = new Task(12, 20, 26, 5, 6, 2);//G 
        Task MyTask963 = new Task(4, 20, 22, 1, 2, 1);//H
        Task MyTask964 = new Task(0, 15, 17, 5, 2, 2);//I
        Task MyTask965 = new Task(16, 20, 23, 2, 3, 2);//J
        Task MyTask966 = new Task(11, 20, 28, 5, 8, 2);//K 
        Task MyTask967 = new Task(16, 20, 23, 2, 3, 2);//L
        Task MyTask968 = new Task(11, 20, 27, 4, 7, 1);//M
        Task MyTask969 = new Task(5, 20, 20, 1, 0, 1);//N  
        T = new Task[14];
        T[0] = MyTask893;
        T[1] = MyTask894;
        T[2] = MyTask895;
        T[3] = MyTask896;
        T[4] = MyTask897;
        T[5] = MyTask898;
        T[6] = MyTask899;
        T[7] = MyTask963;
        T[8] = MyTask964;
        T[9] = MyTask965;
        T[10] = MyTask966;
        T[11] = MyTask967;
        T[12] = MyTask968;
        T[13] = MyTask969;
        resourceCapacity = 4;
        delayNumber = 1;
        instance = new newOverloadChecking();
        result = instance.checking_E_Feasibility(T, delayNumber, resourceCapacity);
        expResult = false;
        assertEquals(expResult, result);

        
        
        
//        Task MyTask647 = new Task(5, 9, 12, 4, 3, 1);//A
//        Task MyTask648 = new Task(0, 1, 1, 1, 0, 6);//B
//        Task MyTask649 = new Task(16, 24, 26, 8, 2, 8);//C
//        Task MyTask650 = new Task(26, 28, 29, 2, 1, 6);//D
//        Task MyTask651 = new Task(29, 34, 37, 5, 3, 9);//E
//        Task MyTask652 = new Task(37, 39, 39, 2, 0, 2);//F
//        Task MyTask653 = new Task(11, 15, 16, 4, 1, 8);//G
//        Task MyTask654 = new Task(1, 11, 14, 10, 3, 7);//H
//        T = new Task[8];
//        T[0] = MyTask647;
//        T[1] = MyTask648;
//        T[2] = MyTask649;
//        T[3] = MyTask650;
//        T[4] = MyTask651;
//        T[5] = MyTask652;
//        T[6] = MyTask653;
//        T[7] = MyTask654;
//        resourceCapacity = 9;
//        delayNumber = 1;
//        instance = new newOverloadChecking();
//        result = instance.checking_E_Feasibility(T, delayNumber, resourceCapacity);
//        expResult = false;
//        assertEquals(expResult, result);
//Bug
//        Task MyTask300 = new Task (27, 35, 38, 8, 3, 4);//A
//        Task MyTask301 = new Task (38, 40, 41, 2, 1, 7);//B
//        Task MyTask302 = new Task (32, 40, 41, 8, 1, 1);//C
//        Task MyTask303 = new Task (16, 25, 27, 9, 2, 3);//D
//        Task MyTask304 = new Task (0, 12, 16, 12, 4, 3);//E
//        Task MyTask305 = new Task (41, 45, 47, 4, 2, 8);//E
//        Task MyTask306 = new Task (27, 32, 32, 5, 0, 2);//E
//        Task MyTask307 = new Task (47, 54, 57, 7, 3, 8);//E
//        T = new Task[8];
//        T[0] = MyTask300;
//        T[1] = MyTask301;
//        T[2] = MyTask302;
//        T[3] = MyTask303;
//        T[4] = MyTask304;
//        T[5] = MyTask305;
//        T[6] = MyTask306;
//        T[7] = MyTask307;
//        delayNumber = 1;
//        resourceCapacity = 8;
//        instance = new newOverloadChecking();
//         numberOfTasks = T.length;
//        d = new int[numberOfTasks];
//       result = instance.checking_E_Feasibility(T, delayNumber, resourceCapacity);
//        expResult = true;
//        assertEquals(expResult, result);
    }
//    public int[] postYourConstraintForOC(Task[] T, int C, int[] delays, int delayNumber) {
//
//        Solver solver = new Solver();
//        int[] resourceCapacity = new int[1];
//        resourceCapacity[0] = C;
//        int numberOfTasks = T.length;
//        IntVar[] startingTimes = new IntVar[numberOfTasks];
//        for (int i = 0; i < numberOfTasks; i++) {
//            startingTimes[i] = VariableFactory.bounded("startingTimes1[" + i + "]", T[i].earliestStartingTime(), T[i].latestStartingTime(), solver);
//        }
//        IntVar[] processingTimeVariables = new IntVar[numberOfTasks];
//        for (int i = 0; i < numberOfTasks; i++) {
//            processingTimeVariables[i] = VariableFactory.fixed(T[i].processingTime(), solver);
//        }
//        IntVar[] endingTimes = new IntVar[numberOfTasks];
//        for (int i = 0; i < numberOfTasks; i++) {
//         //   endingTimes[i] = VariableFactory.bounded("endingTimes1[i]", T[i].processingTime(), 1000, solver);
//                endingTimes[i] = VariableFactory.bounded("endingTimes1[i]", T[i].earliestStartingTime() + T[i].processingTime(), T[i].latestStartingTime() + T[i].processingTime(), solver);
//}
//        IntVar[] heightVars = new IntVar[numberOfTasks];
//        for (int i = 0; i < numberOfTasks; i++) {
//            heightVars[i] = VariableFactory.fixed(T[i].height(), solver);
//        }
//        IntVar capacityVar = VariableFactory.fixed(resourceCapacity[0], solver);
//        IntVar makespan = VariableFactory.bounded("objective", 0, 100000, solver);
//        IntVar[] vars = new IntVar[4 * numberOfTasks + 2];
//        for (int g = 0; g < numberOfTasks; g++) {
//            vars[g] = startingTimes[g];
//            vars[numberOfTasks + g] = processingTimeVariables[g];
//            vars[2 * numberOfTasks + g] = endingTimes[g];
//            vars[3 * numberOfTasks + g] = heightVars[g];
//            vars[4 * numberOfTasks] = capacityVar;
//            vars[4 * numberOfTasks + 1] = makespan;
//            
//        }
//
//        
//       overloadCheckingConstraint Ch;
//        Ch = new overloadCheckingConstraint(vars, T.length, 1, resourceCapacity, delays,  true, false, delayNumber);
//        
//                solver.post(Ch);
//
//        Assert.assertTrue(Ch.isConsistent());
//       
//
//
//
//int[] A = new int[numberOfTasks];
//        for (int a = 0; a < numberOfTasks; a++)
//            A[a] = vars[a].getLB();
//        return A;
//    }
}
