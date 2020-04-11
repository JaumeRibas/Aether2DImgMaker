package cellularautomata.automata;

import java.io.FileNotFoundException;
import java.io.IOException;

import cellularautomata.grid1d.AsymmetricLongGridSection1D;

public class AsymmetricLongCASection1D extends AsymmetricLongGridSection1D implements LongCellularAutomaton1D {

	public AsymmetricLongCASection1D(SymmetricLongCellularAutomaton1D source) {
		super(source);
	}

	@Override
	public boolean nextStep() throws Exception {
		return ((SymmetricLongCellularAutomaton1D) source).nextStep();
	}

	@Override
	public long getStep() {
		return ((SymmetricLongCellularAutomaton1D) source).getStep();
	}

	@Override
	public String getName() {
		return ((SymmetricLongCellularAutomaton1D) source).getName() + "_Asymmetric";
	}

	@Override
	public String getSubFolderPath() {
		return ((SymmetricLongCellularAutomaton1D) source).getSubFolderPath() + "/Asymmetric";
	}

	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		((SymmetricLongCellularAutomaton1D) source).backUp(backupPath, backupName);
	}

}
