package cellularautomata.evolvinggrid;

import java.io.FileNotFoundException;
import java.io.IOException;

import cellularautomata.grid2d.AsymmetricLongGridSection2D;

public class AsymmetricEvolvingLongGridSection2D extends AsymmetricLongGridSection2D implements EvolvingLongGrid2D {

	public AsymmetricEvolvingLongGridSection2D(SymmetricEvolvingLongGrid2D source) {
		super(source);
	}

	@Override
	public boolean nextStep() throws Exception {
		return ((SymmetricEvolvingLongGrid2D) source).nextStep();
	}

	@Override
	public long getStep() {
		return ((SymmetricEvolvingLongGrid2D) source).getStep();
	}

	@Override
	public String getName() {
		return ((SymmetricEvolvingLongGrid2D) source).getName() + "_asymmetric_section";
	}

	@Override
	public String getSubFolderPath() {
		return ((SymmetricEvolvingLongGrid2D) source).getSubFolderPath() + "/asymmetric_section";
	}

	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		((SymmetricEvolvingLongGrid2D) source).backUp(backupPath, backupName);
	}

}
