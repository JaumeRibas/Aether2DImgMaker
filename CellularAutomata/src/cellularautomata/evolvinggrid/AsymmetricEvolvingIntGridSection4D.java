package cellularautomata.evolvinggrid;

import java.io.FileNotFoundException;
import java.io.IOException;

import cellularautomata.grid4d.AsymmetricIntGridSection4D;

public class AsymmetricEvolvingIntGridSection4D extends AsymmetricIntGridSection4D implements EvolvingIntGrid4D {

	public AsymmetricEvolvingIntGridSection4D(SymmetricEvolvingIntGrid4D source) {
		super(source);
	}

	@Override
	public boolean nextStep() throws Exception {
		return ((SymmetricEvolvingIntGrid4D) source).nextStep();
	}

	@Override
	public long getStep() {
		return ((SymmetricEvolvingIntGrid4D) source).getStep();
	}

	@Override
	public String getName() {
		return ((SymmetricEvolvingIntGrid4D) source).getName() + "_Asymmetric";
	}

	@Override
	public String getSubFolderPath() {
		return ((SymmetricEvolvingIntGrid4D) source).getSubFolderPath() + "/Asymmetric";
	}

	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		((SymmetricEvolvingIntGrid4D) source).backUp(backupPath, backupName);
	}

}
