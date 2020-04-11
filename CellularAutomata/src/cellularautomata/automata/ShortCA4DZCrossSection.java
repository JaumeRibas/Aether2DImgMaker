package cellularautomata.automata;

import java.io.FileNotFoundException;
import java.io.IOException;

import cellularautomata.grid4d.ShortGrid4DZCrossSection;

public class ShortCA4DZCrossSection extends ShortGrid4DZCrossSection implements ShortCellularAutomaton3D {

	public ShortCA4DZCrossSection(ShortCellularAutomaton4D source, int z) {
		super(source, z);
	}

	@Override
	public boolean nextStep() throws Exception {
		return ((ShortCellularAutomaton4D) source).nextStep();
	}

	@Override
	public long getStep() {
		return ((ShortCellularAutomaton4D) source).getStep();
	}

	@Override
	public String getName() {
		return ((ShortCellularAutomaton4D) source).getName() + "_z=" + z;
	}

	@Override
	public String getSubFolderPath() {
		return ((ShortCellularAutomaton4D) source).getSubFolderPath() + "/z=" + z;
	}

	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		((ShortCellularAutomaton4D) source).backUp(backupPath, backupName);
	}

}
