package cellularautomata.automata;

import java.io.FileNotFoundException;
import java.io.IOException;

import cellularautomata.grid3d.IntGrid3DZCrossSection;

public class IntCA3DZCrossSection extends IntGrid3DZCrossSection implements IntCellularAutomaton2D {

	public IntCA3DZCrossSection(IntCellularAutomaton3D source, int z) {
		super(source, z);
	}

	@Override
	public boolean nextStep() throws Exception {
		return ((IntCellularAutomaton3D) source).nextStep();
	}

	@Override
	public long getStep() {
		return ((IntCellularAutomaton3D) source).getStep();
	}

	@Override
	public String getName() {
		return ((IntCellularAutomaton3D) source).getName() + "_z=" + z;
	}

	@Override
	public String getSubFolderPath() {
		return ((IntCellularAutomaton3D) source).getSubFolderPath() + "/z=" + z;
	}

	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		((IntCellularAutomaton3D) source).backUp(backupPath, backupName);
	}

}
