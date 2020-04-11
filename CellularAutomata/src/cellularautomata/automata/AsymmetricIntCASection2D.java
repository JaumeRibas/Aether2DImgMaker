package cellularautomata.automata;

import java.io.FileNotFoundException;
import java.io.IOException;

import cellularautomata.grid2d.AsymmetricIntGridSection2D;

public class AsymmetricIntCASection2D extends AsymmetricIntGridSection2D implements IntCellularAutomaton2D {

	public AsymmetricIntCASection2D(SymmetricIntCellularAutomaton2D source) {
		super(source);
	}

	@Override
	public boolean nextStep() throws Exception {
		return ((SymmetricIntCellularAutomaton2D) source).nextStep();
	}

	@Override
	public long getStep() {
		return ((SymmetricIntCellularAutomaton2D) source).getStep();
	}

	@Override
	public String getName() {
		return ((SymmetricIntCellularAutomaton2D) source).getName() + "_Asymmetric";
	}

	@Override
	public String getSubFolderPath() {
		return ((SymmetricIntCellularAutomaton2D) source).getSubFolderPath() + "/Asymmetric";
	}

	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		((SymmetricIntCellularAutomaton2D) source).backUp(backupPath, backupName);
	}

}
