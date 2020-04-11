package cellularautomata.automata;

import java.io.FileNotFoundException;
import java.io.IOException;

import cellularautomata.grid4d.AsymmetricShortGridSection4D;

public class AsymmetricShortCASection4D extends AsymmetricShortGridSection4D implements ShortCellularAutomaton4D {

	public AsymmetricShortCASection4D(SymmetricShortCellularAutomaton4D source) {
		super(source);
	}

	@Override
	public boolean nextStep() throws Exception {
		return ((SymmetricShortCellularAutomaton4D) source).nextStep();
	}

	@Override
	public long getStep() {
		return ((SymmetricShortCellularAutomaton4D) source).getStep();
	}

	@Override
	public String getName() {
		return ((SymmetricShortCellularAutomaton4D) source).getName() + "_Asymmetric";
	}

	@Override
	public String getSubFolderPath() {
		return ((SymmetricShortCellularAutomaton4D) source).getSubFolderPath() + "/Asymmetric";
	}

	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		((SymmetricShortCellularAutomaton4D) source).backUp(backupPath, backupName);
	}

}
