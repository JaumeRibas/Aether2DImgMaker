package cellularautomata.automata;

import java.io.FileNotFoundException;
import java.io.IOException;

import cellularautomata.grid2d.AsymmetricLongGridSection2D;

public class AsymmetricLongCASection2D extends AsymmetricLongGridSection2D implements LongCellularAutomaton2D {

	public AsymmetricLongCASection2D(SymmetricLongCellularAutomaton2D source) {
		super(source);
	}

	@Override
	public boolean nextStep() throws Exception {
		return ((SymmetricLongCellularAutomaton2D) source).nextStep();
	}

	@Override
	public long getStep() {
		return ((SymmetricLongCellularAutomaton2D) source).getStep();
	}

	@Override
	public String getName() {
		return ((SymmetricLongCellularAutomaton2D) source).getName() + "_Asymmetric";
	}

	@Override
	public String getSubFolderPath() {
		return ((SymmetricLongCellularAutomaton2D) source).getSubFolderPath() + "/Asymmetric";
	}

	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		((SymmetricLongCellularAutomaton2D) source).backUp(backupPath, backupName);
	}

}
