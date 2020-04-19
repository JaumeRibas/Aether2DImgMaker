package cellularautomata.evolvinggrid;

import java.io.FileNotFoundException;
import java.io.IOException;

import cellularautomata.grid4d.AsymmetricIntGridSection4D;

public class AsymmetricEvolvingIntGridSection4D extends AsymmetricIntGridSection4D<SymmetricEvolvingIntGrid4D> implements EvolvingIntGrid4D {

	public AsymmetricEvolvingIntGridSection4D(SymmetricEvolvingIntGrid4D source) {
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
