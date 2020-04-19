package cellularautomata.evolvinggrid;

import java.io.FileNotFoundException;
import java.io.IOException;

import cellularautomata.grid2d.AsymmetricIntGridSection2D;

public class AsymmetricEvolvingIntGridSection2D extends AsymmetricIntGridSection2D<SymmetricEvolvingIntGrid2D> implements EvolvingIntGrid2D {

	public AsymmetricEvolvingIntGridSection2D(SymmetricEvolvingIntGrid2D grid) {
		super(grid);
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
		return source.getName() + "_asymmetric_section";
	}

	@Override
	public String getSubFolderPath() {
		return source.getSubFolderPath() + "/asymmetric_section";
	}

	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		source.backUp(backupPath, backupName);
	}

}
