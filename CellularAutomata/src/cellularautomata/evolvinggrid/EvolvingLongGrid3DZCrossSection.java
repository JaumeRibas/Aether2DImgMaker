package cellularautomata.evolvinggrid;

import java.io.FileNotFoundException;
import java.io.IOException;

import cellularautomata.grid3d.LongGrid3DZCrossSection;

public class EvolvingLongGrid3DZCrossSection extends LongGrid3DZCrossSection implements EvolvingLongGrid2D {

	public EvolvingLongGrid3DZCrossSection(EvolvingLongGrid3D source, int z) {
		super(source, z);
	}

	@Override
	public boolean nextStep() throws Exception {
		return ((EvolvingLongGrid3D) source).nextStep();
	}

	@Override
	public long getStep() {
		return ((EvolvingLongGrid3D) source).getStep();
	}

	@Override
	public String getName() {
		return ((EvolvingLongGrid3D) source).getName() + "_z=" + z;
	}

	@Override
	public String getSubFolderPath() {
		return ((EvolvingLongGrid3D) source).getSubFolderPath() + "/z=" + z;
	}

	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		((EvolvingLongGrid3D) source).backUp(backupPath, backupName);
	}

}
