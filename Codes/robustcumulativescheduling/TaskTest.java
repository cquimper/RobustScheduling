package robustcumulativescheduling;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;


public class TaskTest {
    
    public TaskTest() {
    }
    
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
     * Test of earliestStartingTime method, of class Task.
     */
    @Test
    public void testEarliestStartingTime() {
         Task tester = new Task (6, 12, 13, 6, 1, 1);
        assertEquals(6, tester.earliestStartingTime());

    }

    
    
    
    /**
     * Test of latestCompletionTime method, of class Task.
     */
    @Test
    public void testLatestCompletionTime() {
       Task tester = new Task(1, 10, 11, 5, 1, 1);
        assertEquals(10, tester.latestCompletionTime());
    }
    
     @Test
    public void testerprocessingTime() {
        Task tester = new Task(1, 10, 11, 5, 1, 1);
        assertEquals(5, tester.processingTime());	
    }
    
    @Test
    public void testerAlloweddelay() {
        Task tester = new Task(1, 10, 11, 5, 1, 1);
        assertEquals(1, tester.allowedDelay());	
    }

    @Test
    public void testdelayedProcessingTime() {
        Task tester = new Task(3, 13, 17, 5, 4, 1);
        assertEquals(9, tester.delayedProcessingTime());	
    }
    
    @Test
    public void testHeight() {
        Task tester = new Task(6, 18, 21, 8, 3, 3);
        assertEquals(3, tester.height());	
    }
    
    @Test
    public void testerearliestCompletionTime() {
        Task tester = new Task(1, 10, 11, 4, 1, 1);
        assertEquals(5, tester.earliestCompletionTime());
    }

    @Test
    public void testerlatestStartingTime() {
        Task tester = new Task(1, 10, 11, 4, 1, 1);
        assertEquals(6, tester.latestStartingTime());	
    }

    @Test
    public void testerEnergy() {
        Task tester = new Task(1, 10, 11, 4, 1, 2);
        assertEquals(8, tester.energy());	
        Task tester2 = new Task(2, 9, 10, 4, 1, 2);
        assertEquals(8, tester.energy());	

    }
    
    @Test
    public void testerEnvelope() {
        Task tester = new Task(9, 28, 31, 7, 3, 4);
        assertEquals(82, tester.envelope(6));	
    }
    
    @Test
    public void testerDelayedEnergy() {
        Task tester = new Task(1, 10, 11, 4, 1, 2);
        assertEquals(10, tester.delayedEnergy());
        Task tester2 = new Task(2, 9, 10, 4, 1, 2);
        assertEquals(10, tester.delayedEnergy());	
    }  
    
        @Test
    public void testerenergyOnlyForTheDelayedPortion() {
        Task tester = new Task(1, 10, 11, 4, 1, 2);
        assertEquals(2, tester.energyOnlyForTheDelayedPortion());
      	
    } 

}