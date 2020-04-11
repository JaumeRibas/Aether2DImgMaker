package cellularautomata.automata;

import java.io.FileNotFoundException;
import java.io.IOException;

import cellularautomata.grid4d.AsymmetricLongGridSection4D;

public class AsymmetricLongCASection4D extends AsymmetricLongGridSection4D implements LongCellularAutomaton4D {

	public AsymmetricLongCASection4D(SymmetricLongCellularAutomaton4D source) {
		super(source);
	}

	@Override
	public boolean nextStep() throws Exception {
		return ((SymmetricLongCellularAutomaton4D) source).nextStep();
	}

	@Override
	public long getStep() {
		return ((SymmetricLongCellularAutomaton4D) source).getStep();
	}

	@Override
	public String getName() {
		return ((SymmetricLongCellularAutomaton4D) source).getName() + "_Asymmetric";
	}

	@Override
	public String getSubFolderPath() {
		return ((SymmetricLongCellularAutomaton4D) source).getSubFolderPath() + "/Asymmetric";
	}

	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		((SymmetricLongCellularAutomaton4D) source).backUp(backupPath, backupName);
	}

}
