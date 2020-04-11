package cellularautomata.automata;

import java.io.FileNotFoundException;
import java.io.IOException;

import cellularautomata.grid4d.AsymmetricIntGridSection4D;

public class AsymmetricIntCASection4D extends AsymmetricIntGridSection4D implements IntCellularAutomaton4D {

	public AsymmetricIntCASection4D(SymmetricIntCellularAutomaton4D source) {
		super(source);
	}

	@Override
	public boolean nextStep() throws Exception {
		return ((SymmetricIntCellularAutomaton4D) source).nextStep();
	}

	@Override
	public long getStep() {
		return ((SymmetricIntCellularAutomaton4D) source).getStep();
	}

	@Override
	public String getName() {
		return ((SymmetricIntCellularAutomaton4D) source).getName() + "_Asymmetric";
	}

	@Override
	public String getSubFolderPath() {
		return ((SymmetricIntCellularAutomaton4D) source).getSubFolderPath() + "/Asymmetric";
	}

	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		((SymmetricIntCellularAutomaton4D) source).backUp(backupPath, backupName);
	}

}
