package cellularautomata.evolvinggrid;

import java.io.FileNotFoundException;
import java.io.IOException;

import cellularautomata.grid3d.BigIntGrid3DZCrossSection;

public class EvolvingBigIntGrid3DZCrossSection extends BigIntGrid3DZCrossSection<EvolvingBigIntGrid3D> implements EvolvingBigIntGrid2D {

	public EvolvingBigIntGrid3DZCrossSection(EvolvingBigIntGrid3D source, int z) {
		super(source, z);
	}

	@Override
	public boolean nextStep() throws Exception {
		boolean changed = source.nextStep();
		if (z > source.getMaxZ() || z < source.getMinZ()) {
			throw new UnsupportedOperationException("Z coordinate outside of grid bounds.");
		}
		return changed;
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
