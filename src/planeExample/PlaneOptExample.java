package planeExample;

import inf.minife.fe.Element;
import inf.minife.fe.IsoPlane;
import inf.minife.fe.Model;

import org.mopack.sopti.problems.ProblemType1;
import org.mopack.sopti.ui.swing.minife.ModelProvider;
import org.mopack.sopti.ui.swing.minife.OptViewer;

public class PlaneOptExample extends ProblemType1 implements ModelProvider
{
	// the array f containing
	// - the object function f[0] and
	// - the constraint values f[1], f[2], ...
	double[] f;

	double sigmaMax = 250; // N/mm^2

	Model model;

	public static void main(String[] args)
	{
		new OptViewer(new PlaneOptExample()).setVisible(true);
	}

	// double bucklingCoefficient = 4;

	public PlaneOptExample()
	{

		model = new PlaneExample().getModel();

		addDesignVariable("thickness", 0.1, 10, 20);

		addFunctionName(0, "total mass [kg]");

		for (int i = 0; i < countConstraints(); i++)
		{
			addFunctionName("stress member " + (i + 1));
		}

		// update
		evaluate(getInitial());
	}

	void computeStressConstraints()
	{
		Element[] elements = model.getElements();
		int n = elements.length;
		double sigma;

		for (int i = 0; i < n; i++)
		{
			sigma = elements[i].getResult(IsoPlane.RS_STRESS_EQV);
			f[i + 1] = Math.abs(sigma) / sigmaMax - 1.0;
		}
	}

	@Override
	public double[] evaluate(double[] x)
	{
		f = new double[1 + countConstraints()];

		model.getRealtable(1).setValue(IsoPlane.RT_T, x[0]);

		// run analysis
		model.solve();

		f[0] = model.getTotalMass();
		computeStressConstraints();

		return f;
	}

	@Override
	public int countConstraints()
	{
		return model.getElements().length;
	}

	@Override
	public Model getModel()
	{
		return model;
	}
}
