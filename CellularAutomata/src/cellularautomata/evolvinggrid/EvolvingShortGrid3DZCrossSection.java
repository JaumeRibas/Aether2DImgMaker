package cellularautomata.evolvinggrid;

import java.io.FileNotFoundException;
import java.io.IOException;

import cellularautomata.grid3d.ShortGrid3DZCrossSection;

public class EvolvingShortGrid3DZCrossSection extends ShortGrid3DZCrossSection implements EvolvingShortGrid2D {

	public EvolvingShortGrid3DZCrossSection(EvolvingShortGrid3D source, int z) {
		super(source, z);
	}

	@Override
	public boolean nextStep() throws Exception {
		return ((EvolvingShortGrid3D) source).nextStep();
	}

	@Override
	public long getStep() {
		return ((EvolvingShortGrid3D) source).getStep();
	}

	@Override
	public String getName() {
		return ((EvolvingShortGrid3D) source).getName() + "_z=" + z;
	}

	@Override
	public String getSubFolderPath() {
		return ((EvolvingShortGrid3D) source).getSubFolderPath() + "/z=" + z;
	}

	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		((EvolvingShortGrid3D) source).backUp(backupPath, backupName);
	}

}
