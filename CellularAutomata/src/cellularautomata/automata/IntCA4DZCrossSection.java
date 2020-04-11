package cellularautomata.automata;

import java.io.FileNotFoundException;
import java.io.IOException;

import cellularautomata.grid4d.IntGrid4DZCrossSection;

public class IntCA4DZCrossSection extends IntGrid4DZCrossSection implements IntCellularAutomaton3D {

	public IntCA4DZCrossSection(IntCellularAutomaton4D source, int z) {
		super(source, z);
	}

	@Override
	public boolean nextStep() throws Exception {
		return ((IntCellularAutomaton4D) source).nextStep();
	}

	@Override
	public long getStep() {
		return ((IntCellularAutomaton4D) source).getStep();
	}

	@Override
	public String getName() {
		return ((IntCellularAutomaton4D) source).getName() + "_z=" + z;
	}

	@Override
	public String getSubFolderPath() {
		return ((IntCellularAutomaton4D) source).getSubFolderPath() + "/z=" + z;
	}

	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		((IntCellularAutomaton4D) source).backUp(backupPath, backupName);
	}

}
