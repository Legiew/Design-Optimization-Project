package girder;

import inf.minife.fe.Element;
import inf.minife.fe.Model;
import inf.minife.fe.Node;
import inf.minife.fe.Truss2D;

import org.mopack.sopti.problems.ProblemType1;
import org.mopack.sopti.ui.swing.minife.ModelProvider;
import org.mopack.sopti.ui.swing.minife.OptViewer;

public class GirderOpt extends ProblemType1 implements ModelProvider {

	Model model;
	GirderStructure gs;

	double[] f;

	double maxStress = 240e3; // kN/m^2
	double minHeight = 0.1; // m
	double maxHeight = 5.0; // m
	double minDiameter = 0.01; // m
	double maxDiameter = 0.3; // m

	public static void main(String[] args) {
		new OptViewer(new GirderOpt()).setVisible(true);
	}

	public GirderOpt() {
		gs = new GirderStructure();
		model = gs.getModel();

		addDesignVariable("diameter for lower members [m^2]", minDiameter, gs
				.getDiameter(), maxDiameter);
		addDesignVariable("diameter for diagonal members [m^2]", minDiameter,
				gs.getDiameter(), maxDiameter);
		addDesignVariable("diameter for upper members [m^2]", minDiameter, gs
				.getDiameter(), maxDiameter);

		addDesignVariable("height 1 [m]", minHeight, gs.getHeight(), maxHeight);
		addDesignVariable("height 2 [m]", minHeight, gs.getHeight(), maxHeight);
		addDesignVariable("height 3 [m]", minHeight, gs.getHeight(), maxHeight);

		addFunctionName(0, "total mass [kg]");
		for (int i = 0; i < countConstraints(); i++) {
			addFunctionName("member " + (i + 1));
		}

		// update
		evaluate(getInitial());
	}

	public double[] evaluate(double[] x) {

		f = new double[1 + countConstraints()];
		
		gs.getSectionForLowerMembers().setDiameter(x[0]);
		gs.getSectionForDiagonalMembers().setDiameter(x[1]);
		gs.getSectionForUpperMembers().setDiameter(x[2]);

		model.getNode(7).setCoordinate(Node.Y, x[3]);
		model.getNode(11).setCoordinate(Node.Y, x[3]);
		model.getNode(8).setCoordinate(Node.Y, x[4]);
		model.getNode(10).setCoordinate(Node.Y, x[4]);
		model.getNode(9).setCoordinate(Node.Y, x[5]);

		model.solve();

		f[0] = model.getTotalMass();

		Element[] elements = model.getElements();
		for (int i = 0; i < elements.length; i++) {
			double stress = elements[i].getResult(Truss2D.RS_STRESS);
			f[i + 1] = Math.abs(stress) / maxStress - 1;
		}

		return f;
	}

	public Model getModel() {
		return model;
	}

	@Override
	public int countConstraints() {
		return model.getElements().length;
	}
}
