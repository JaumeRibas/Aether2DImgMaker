package cellularautomata.evolvinggrid;

import java.io.FileNotFoundException;
import java.io.IOException;

import cellularautomata.grid3d.IntGrid3DZCrossSection;

public class EvolvingIntGrid3DZCrossSection extends IntGrid3DZCrossSection implements EvolvingIntGrid2D {

	public EvolvingIntGrid3DZCrossSection(EvolvingIntGrid3D source, int z) {
		super(source, z);
	}

	@Override
	public boolean nextStep() throws Exception {
		return ((EvolvingIntGrid3D) source).nextStep();
	}

	@Override
	public long getStep() {
		return ((EvolvingIntGrid3D) source).getStep();
	}

	@Override
	public String getName() {
		return ((EvolvingIntGrid3D) source).getName() + "_z=" + z;
	}

	@Override
	public String getSubFolderPath() {
		return ((EvolvingIntGrid3D) source).getSubFolderPath() + "/z=" + z;
	}

	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		((EvolvingIntGrid3D) source).backUp(backupPath, backupName);
	}

}
