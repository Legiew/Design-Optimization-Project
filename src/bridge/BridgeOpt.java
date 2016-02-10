package bridge;

import org.mopack.sopti.problems.ProblemType1;
import org.mopack.sopti.ui.swing.minife.ModelProvider;
import org.mopack.sopti.ui.swing.minife.OptViewer;

import inf.minife.fe.Beam3D;
import inf.minife.fe.Element;
import inf.minife.fe.Model;

public class BridgeOpt extends ProblemType1 implements ModelProvider
{
	private BridgeStructure structure;

	double[] f;

	public static void main(String[] args)
	{
		new OptViewer(new BridgeOpt()).setVisible(true);
	}

	public BridgeOpt()
	{
		this.structure = new BridgeStructure();

		double minDiameter = 0.1; // m
		double maxDiameter = 0.5; // m

		addDesignVariable("diameter for normal beams [m]", minDiameter, 0.15, maxDiameter);
		addDesignVariable("diameter for angular beams [m]", minDiameter, 0.15, maxDiameter);

		addFunctionName(0, "total mass [kg]");

		for (int i = 0; i < countConstraints(); i++)
		{
			addFunctionName("member " + (i + 1));
		}

		evaluate(getInitial());
	}

	@Override
	public int countConstraints()
	{
		return this.structure.getModel().getElements().length;
	}

	@Override
	public Model getModel()
	{
		return this.structure.getModel();
	}

	@Override
	public double[] evaluate(double[] x)
	{
		double maxStress = 10e6; // kN/m^2

		f = new double[1 + countConstraints()];

		this.structure.getNormalSection().setDiameter(x[0]);
		this.structure.getAngularSection().setDiameter(x[1]);

		this.structure.getModel().solve();

		f[0] = this.structure.getModel().getTotalMass();

		Element[] elements = this.structure.getModel().getElements();

		for (int i = 0; i < elements.length; i++)
		{
			double stress = elements[i].getResult(Beam3D.RS_SMAX_I);
			f[i + 1] = (Math.abs(stress) / maxStress) - 1.0;
		}

		return f;
	}
}