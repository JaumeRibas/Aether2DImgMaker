package cellularautomata.automata;

import java.io.FileNotFoundException;
import java.io.IOException;

import cellularautomata.grid3d.ShortGrid3DZCrossSection;

public class ShortCA3DZCrossSection extends ShortGrid3DZCrossSection implements ShortCellularAutomaton2D {

	public ShortCA3DZCrossSection(ShortCellularAutomaton3D source, int z) {
		super(source, z);
	}

	@Override
	public boolean nextStep() throws Exception {
		return ((ShortCellularAutomaton3D) source).nextStep();
	}

	@Override
	public long getStep() {
		return ((ShortCellularAutomaton3D) source).getStep();
	}

	@Override
	public String getName() {
		return ((ShortCellularAutomaton3D) source).getName() + "_z=" + z;
	}

	@Override
	public String getSubFolderPath() {
		return ((ShortCellularAutomaton3D) source).getSubFolderPath() + "/z=" + z;
	}

	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		((ShortCellularAutomaton3D) source).backUp(backupPath, backupName);
	}

}
