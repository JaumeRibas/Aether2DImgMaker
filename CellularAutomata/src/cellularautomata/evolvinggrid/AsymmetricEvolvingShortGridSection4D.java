package cellularautomata.evolvinggrid;

import java.io.FileNotFoundException;
import java.io.IOException;

import cellularautomata.grid4d.AsymmetricShortGridSection4D;

public class AsymmetricEvolvingShortGridSection4D extends AsymmetricShortGridSection4D implements EvolvingShortGrid4D {

	public AsymmetricEvolvingShortGridSection4D(SymmetricEvolvingShortGrid4D source) {
		super(source);
	}

	@Override
	public boolean nextStep() throws Exception {
		return ((SymmetricEvolvingShortGrid4D) source).nextStep();
	}

	@Override
	public long getStep() {
		return ((SymmetricEvolvingShortGrid4D) source).getStep();
	}

	@Override
	public String getName() {
		return ((SymmetricEvolvingShortGrid4D) source).getName() + "_Asymmetric";
	}

	@Override
	public String getSubFolderPath() {
		return ((SymmetricEvolvingShortGrid4D) source).getSubFolderPath() + "/Asymmetric";
	}

	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		((SymmetricEvolvingShortGrid4D) source).backUp(backupPath, backupName);
	}

}
