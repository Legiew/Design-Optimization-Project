package threebarTruss2D;

import inf.minife.fe.Element;
import inf.minife.fe.Model;
import inf.minife.fe.Node;
import inf.minife.fe.RectangleS;
import inf.minife.fe.Truss2D;

import org.mopack.sopti.problems.ProblemType1;
import org.mopack.sopti.ui.swing.minife.ModelProvider;
import org.mopack.sopti.ui.swing.minife.OptViewer;

/**
 * @author kl
 * 
 *         Optimize a ThreeBar object.
 * 
 *         - optimization variables are - the support distance b and - the side
 *         lengths of bars 1, 2 and 3 (assume square shape)
 * 
 *         - the objective function is the total mass
 * 
 *         - constraints are stresses in each element
 * 
 */
public class ThreeBarOpt extends ProblemType1 implements ModelProvider {

	// the array f containing
	// - the object function f[0] and
	// - the constraint values f[1], f[2], ...
	double[] f;

	double sigmaMax = 250; // N/mm^2

	ThreeBar threebar;
	Model model;

	public static void main(String[] args) {
		new OptViewer(new ThreeBarOpt()).setVisible(true);
	}

	// double bucklingCoefficient = 4;

	public ThreeBarOpt() {

		threebar = new ThreeBar();
		model = threebar.getModel();

		addDesignVariable("support distance b [mm]", 400, 500, 2000);
		addDesignVariable("side length of bar #1 [mm]", 1, 10, 400);
		addDesignVariable("side length of bar #2 [mm]", 1, 10, 400);
		addDesignVariable("side length of bar #3 [mm]", 1, 10, 400);

		addFunctionName(0, "total mass [kg]");
		for (int i = 0; i < countConstraints(); i++) {
			addFunctionName("stress member " + (i + 1));
		}

		// update
		evaluate(getInitial());

		// Realtable rt[] = model.getRealtables();
		// System.out.println("rt length=" + rt.length);
		// for (int i = 0; i < rt.length; i++) {
		// System.out.println(rt[i]);
		// }
		// System.out.println();
	}

	void computeStressConstraints() {

		Element[] elements = model.getElements();
		int n = elements.length;
		double sigma;

		for (int i = 0; i < n; i++) {
			sigma = elements[i].getResult(Truss2D.RS_STRESS);
			f[i + 1] = Math.abs(sigma) / sigmaMax - 1.0;
		}
	}

	@Override
	public double[] evaluate(double[] x) {

		f = new double[1 + countConstraints()];

		// the support width
		double b = x[0];

		model.getNode(1).setCoordinate(Node.X, -b);
		model.getNode(3).setCoordinate(Node.X, b);

		for (int i = 1; i <= 3; i++) {
			RectangleS r = (RectangleS) model.getRealtable(i);
			r.setTKY(x[i]);
			r.setTKZ(x[i]);
		}

		// run analysis
		model.solve();

		f[0] = model.getTotalMass();
		computeStressConstraints();

		return f;
	}

	@Override
	public int countConstraints() {
		return model.getElements().length;
	}

	@Override
	public Model getModel() {
		return model;
	}
}
