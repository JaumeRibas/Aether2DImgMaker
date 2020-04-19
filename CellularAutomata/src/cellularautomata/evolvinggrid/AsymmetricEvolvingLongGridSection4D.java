package cellularautomata.evolvinggrid;

import java.io.FileNotFoundException;
import java.io.IOException;

import cellularautomata.grid4d.AsymmetricLongGridSection4D;

public class AsymmetricEvolvingLongGridSection4D extends AsymmetricLongGridSection4D<SymmetricEvolvingLongGrid4D> implements EvolvingLongGrid4D {

	public AsymmetricEvolvingLongGridSection4D(SymmetricEvolvingLongGrid4D source) {
		super(source);
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
