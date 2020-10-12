package cellularautomata.evolvinggrid;

import java.io.FileNotFoundException;
import java.io.IOException;

import cellularautomata.grid3d.ShortGrid3DXCrossSection;

public class EvolvingShortGrid3DXCrossSection extends ShortGrid3DXCrossSection<EvolvingShortGrid3D> implements EvolvingShortGrid2D {

	public EvolvingShortGrid3DXCrossSection(EvolvingShortGrid3D source, int x) {
		super(source, x);
	}

	@Override
	public boolean nextStep() throws Exception {
		boolean changed = source.nextStep();
		if (x > source.getMaxX() || x < source.getMinX()) {
			throw new UnsupportedOperationException("X coordinate outside of grid bounds.");
		}
		return changed;
	}

	@Override
	public long getStep() {
		return source.getStep();
	}

	@Override
	public String getName() {
		return source.getName() + "_x=" + x;
	}

	@Override
	public String getSubFolderPath() {
		return source.getSubFolderPath() + "/x=" + x;
	}

	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		source.backUp(backupPath, backupName);
	}

}
