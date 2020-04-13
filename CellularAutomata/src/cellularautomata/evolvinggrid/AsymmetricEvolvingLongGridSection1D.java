package cellularautomata.evolvinggrid;

import java.io.FileNotFoundException;
import java.io.IOException;

import cellularautomata.grid1d.AsymmetricLongGridSection1D;

public class AsymmetricEvolvingLongGridSection1D extends AsymmetricLongGridSection1D implements EvolvingLongGrid1D {

	public AsymmetricEvolvingLongGridSection1D(SymmetricEvolvingLongGrid1D source) {
		super(source);
	}

	@Override
	public boolean nextStep() throws Exception {
		return ((SymmetricEvolvingLongGrid1D) source).nextStep();
	}

	@Override
	public long getStep() {
		return ((SymmetricEvolvingLongGrid1D) source).getStep();
	}

	@Override
	public String getName() {
		return ((SymmetricEvolvingLongGrid1D) source).getName() + "_asymmetric_section";
	}

	@Override
	public String getSubFolderPath() {
		return ((SymmetricEvolvingLongGrid1D) source).getSubFolderPath() + "/asymmetric_section";
	}

	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		((SymmetricEvolvingLongGrid1D) source).backUp(backupPath, backupName);
	}

}
