package robustcumulativescheduling;

import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.variables.IntVar;

@SuppressWarnings("serial")
public class overloadCheckingConstraint extends Constraint {

	public overloadCheckingConstraint(IntVar[] vars, int nbTasks, int nbResources, int[] capacities, int[] ka, boolean filterMakespan, int r) {

		super("robustness", new Propagator_overloadCheckingConstraint(vars, nbTasks, nbResources,  capacities, ka, filterMakespan, r));
	}

}
