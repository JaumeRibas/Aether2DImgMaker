package cellularautomata.evolvinggrid;

import java.io.FileNotFoundException;
import java.io.IOException;

import cellularautomata.grid4d.ShortGrid4DZCrossSection;

public class EvolvingShortGid4DZCrossSection extends ShortGrid4DZCrossSection implements EvolvingShortGrid3D {

	public EvolvingShortGid4DZCrossSection(EvolvingShortGrid4D source, int z) {
		super(source, z);
	}

	@Override
	public boolean nextStep() throws Exception {
		return ((EvolvingShortGrid4D) source).nextStep();
	}

	@Override
	public long getStep() {
		return ((EvolvingShortGrid4D) source).getStep();
	}

	@Override
	public String getName() {
		return ((EvolvingShortGrid4D) source).getName() + "_z=" + z;
	}

	@Override
	public String getSubFolderPath() {
		return ((EvolvingShortGrid4D) source).getSubFolderPath() + "/z=" + z;
	}

	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		((EvolvingShortGrid4D) source).backUp(backupPath, backupName);
	}

}
