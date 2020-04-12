package cellularautomata.evolvinggrid;

import java.io.FileNotFoundException;
import java.io.IOException;

import cellularautomata.grid4d.AsymmetricLongGridSection4D;

public class AsymmetricEvolvingLongGridSection4D extends AsymmetricLongGridSection4D implements EvolvingLongGrid4D {

	public AsymmetricEvolvingLongGridSection4D(SymmetricEvolvingLongGrid4D source) {
		super(source);
	}

	@Override
	public boolean nextStep() throws Exception {
		return ((SymmetricEvolvingLongGrid4D) source).nextStep();
	}

	@Override
	public long getStep() {
		return ((SymmetricEvolvingLongGrid4D) source).getStep();
	}

	@Override
	public String getName() {
		return ((SymmetricEvolvingLongGrid4D) source).getName() + "_Asymmetric";
	}

	@Override
	public String getSubFolderPath() {
		return ((SymmetricEvolvingLongGrid4D) source).getSubFolderPath() + "/Asymmetric";
	}

	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		((SymmetricEvolvingLongGrid4D) source).backUp(backupPath, backupName);
	}

}
