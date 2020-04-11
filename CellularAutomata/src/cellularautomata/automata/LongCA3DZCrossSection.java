package cellularautomata.automata;

import java.io.FileNotFoundException;
import java.io.IOException;

import cellularautomata.grid3d.LongGrid3DZCrossSection;

public class LongCA3DZCrossSection extends LongGrid3DZCrossSection implements LongCellularAutomaton2D {

	public LongCA3DZCrossSection(LongCellularAutomaton3D source, int z) {
		super(source, z);
	}

	@Override
	public boolean nextStep() throws Exception {
		return ((LongCellularAutomaton3D) source).nextStep();
	}

	@Override
	public long getStep() {
		return ((LongCellularAutomaton3D) source).getStep();
	}

	@Override
	public String getName() {
		return ((LongCellularAutomaton3D) source).getName() + "_z=" + z;
	}

	@Override
	public String getSubFolderPath() {
		return ((LongCellularAutomaton3D) source).getSubFolderPath() + "/z=" + z;
	}

	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		((LongCellularAutomaton3D) source).backUp(backupPath, backupName);
	}

}
