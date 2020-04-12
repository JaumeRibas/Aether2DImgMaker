package cellularautomata.evolvinggrid;

import java.io.FileNotFoundException;
import java.io.IOException;

import cellularautomata.grid4d.LongGrid4DZCrossSection;

public class EvolvingLongGrid4DZCrossSection extends LongGrid4DZCrossSection implements EvolvingLongGrid3D {

	public EvolvingLongGrid4DZCrossSection(EvolvingLongGrid4D source, int z) {
		super(source, z);
	}

	@Override
	public boolean nextStep() throws Exception {
		return ((EvolvingLongGrid4D) source).nextStep();
	}

	@Override
	public long getStep() {
		return ((EvolvingLongGrid4D) source).getStep();
	}

	@Override
	public String getName() {
		return ((EvolvingLongGrid4D) source).getName() + "_z=" + z;
	}

	@Override
	public String getSubFolderPath() {
		return ((EvolvingLongGrid4D) source).getSubFolderPath() + "/z=" + z;
	}

	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		((EvolvingLongGrid4D) source).backUp(backupPath, backupName);
	}

}
