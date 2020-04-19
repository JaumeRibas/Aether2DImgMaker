package cellularautomata.evolvinggrid;

import java.io.FileNotFoundException;
import java.io.IOException;

import cellularautomata.grid4d.LongGrid4DZCrossSection;

public class EvolvingLongGrid4DZCrossSection extends LongGrid4DZCrossSection<EvolvingLongGrid4D> implements EvolvingLongGrid3D {

	public EvolvingLongGrid4DZCrossSection(EvolvingLongGrid4D source, int z) {
		super(source, z);
	}

	@Override
	public boolean nextStep() throws Exception {
		return source.nextStep();
	}

	@Override
	public long getStep() {
		return source.getStep();
	}

	@Override
	public String getName() {
		return source.getName() + "_z=" + z;
	}

	@Override
	public String getSubFolderPath() {
		return source.getSubFolderPath() + "/z=" + z;
	}

	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		source.backUp(backupPath, backupName);
	}

}
