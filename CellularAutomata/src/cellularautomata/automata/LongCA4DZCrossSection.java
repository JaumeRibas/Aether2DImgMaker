package cellularautomata.automata;

import java.io.FileNotFoundException;
import java.io.IOException;

import cellularautomata.grid4d.LongGrid4DZCrossSection;

public class LongCA4DZCrossSection extends LongGrid4DZCrossSection implements LongCellularAutomaton3D {

	public LongCA4DZCrossSection(LongCellularAutomaton4D source, int z) {
		super(source, z);
	}

	@Override
	public boolean nextStep() throws Exception {
		return ((LongCellularAutomaton4D) source).nextStep();
	}

	@Override
	public long getStep() {
		return ((LongCellularAutomaton4D) source).getStep();
	}

	@Override
	public String getName() {
		return ((LongCellularAutomaton4D) source).getName() + "_z=" + z;
	}

	@Override
	public String getSubFolderPath() {
		return ((LongCellularAutomaton4D) source).getSubFolderPath() + "/z=" + z;
	}

	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		((LongCellularAutomaton4D) source).backUp(backupPath, backupName);
	}

}
