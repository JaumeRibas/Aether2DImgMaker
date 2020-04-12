package cellularautomata.evolvinggrid;

import java.io.FileNotFoundException;
import java.io.IOException;

import cellularautomata.grid4d.IntGrid4DZCrossSection;

public class EvolvingIntGrid4DZCrossSection extends IntGrid4DZCrossSection implements EvolvingIntGrid3D {

	public EvolvingIntGrid4DZCrossSection(EvolvingIntGrid4D source, int z) {
		super(source, z);
	}

	@Override
	public boolean nextStep() throws Exception {
		return ((EvolvingIntGrid4D) source).nextStep();
	}

	@Override
	public long getStep() {
		return ((EvolvingIntGrid4D) source).getStep();
	}

	@Override
	public String getName() {
		return ((EvolvingIntGrid4D) source).getName() + "_z=" + z;
	}

	@Override
	public String getSubFolderPath() {
		return ((EvolvingIntGrid4D) source).getSubFolderPath() + "/z=" + z;
	}

	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		((EvolvingIntGrid4D) source).backUp(backupPath, backupName);
	}

}
