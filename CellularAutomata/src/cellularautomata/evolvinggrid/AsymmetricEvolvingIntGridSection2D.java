package cellularautomata.evolvinggrid;

import java.io.FileNotFoundException;
import java.io.IOException;

import cellularautomata.grid2d.AsymmetricIntGridSection2D;

public class AsymmetricEvolvingIntGridSection2D extends AsymmetricIntGridSection2D implements EvolvingIntGrid2D {

	public AsymmetricEvolvingIntGridSection2D(SymmetricEvolvingIntGrid2D source) {
		super(source);
	}

	@Override
	public boolean nextStep() throws Exception {
		return ((SymmetricEvolvingIntGrid2D) source).nextStep();
	}

	@Override
	public long getStep() {
		return ((SymmetricEvolvingIntGrid2D) source).getStep();
	}

	@Override
	public String getName() {
		return ((SymmetricEvolvingIntGrid2D) source).getName() + "_Asymmetric";
	}

	@Override
	public String getSubFolderPath() {
		return ((SymmetricEvolvingIntGrid2D) source).getSubFolderPath() + "/Asymmetric";
	}

	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		((SymmetricEvolvingIntGrid2D) source).backUp(backupPath, backupName);
	}

}
