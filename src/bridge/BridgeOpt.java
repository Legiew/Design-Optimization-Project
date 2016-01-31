package bridge;

import org.mopack.sopti.problems.ProblemType1;
import org.mopack.sopti.ui.swing.minife.ModelProvider;
import org.mopack.sopti.ui.swing.minife.OptViewer;

import inf.minife.fe.Model;

public class BridgeOpt extends ProblemType1 implements ModelProvider
{
	private BridgeStructure structure;

	public static void main(String[] args)
	{
		new OptViewer(new BridgeOpt()).setVisible(true);
	}

	public BridgeOpt()
	{
		this.structure = new BridgeStructure();

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
		return null;
	}
}