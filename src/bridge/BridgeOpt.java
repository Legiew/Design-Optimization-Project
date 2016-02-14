package bridge;

import org.mopack.sopti.problems.ProblemType1;
import org.mopack.sopti.ui.swing.minife.ModelProvider;
import org.mopack.sopti.ui.swing.minife.OptViewer;

import inf.minife.fe.Beam3D;
import inf.minife.fe.Element;
import inf.minife.fe.Model;

public class BridgeOpt extends ProblemType1 implements ModelProvider
{
	public static void main(String[] args)
	{
		new OptViewer(new BridgeOpt()).setVisible(true);
	}

	private final BridgeStructure structure;

	private double[] f;

	public BridgeOpt()
	{
		this.structure = new BridgeStructure();

		final double minDiameter = 0.1; // m
		final double maxDiameter = 0.5; // m

		this.addDesignVariable("diameter for normal beams [m]", minDiameter, 0.15, maxDiameter);
		this.addDesignVariable("diameter for angular beams [m]", minDiameter, 0.15, maxDiameter);

		this.addFunctionName(0, "total mass [kg]");

		for (int i = 0; i < this.countConstraints(); i++)
		{
			this.addFunctionName("member " + (i + 1));
		}

		this.evaluate(this.getInitial());
	}

	@Override
	public int countConstraints()
	{
		return this.structure.getModel().getElements().length;
	}

	@Override
	public double[] evaluate(double[] x)
	{
		final double maxStress = 10e6; // kN/m^2

		this.f = new double[1 + this.countConstraints()];

		this.structure.getNormalSection().setDiameter(x[0]);
		this.structure.getAngularSection().setDiameter(x[1]);

		this.structure.getModel().solve();

		this.f[0] = this.structure.getModel().getTotalMass();

		final Element[] elements = this.structure.getModel().getElements();

		for (int i = 0; i < elements.length; i++)
		{
			final double stress = elements[i].getResult(Beam3D.RS_SMAX_I);
			this.f[i + 1] = (Math.abs(stress) / maxStress) - 1.0;
		}

		return this.f;
	}

	@Override
	public Model getModel()
	{
		return this.structure.getModel();
	}
}