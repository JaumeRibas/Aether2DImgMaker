package cellularautomata.evolvinggrid;

import java.io.FileNotFoundException;
import java.io.IOException;

import cellularautomata.grid3d.AsymmetricLongGridSection3D;

public class AsymmetricEvolvingLongGridSection3D extends AsymmetricLongGridSection3D<SymmetricEvolvingLongGrid3D> implements EvolvingLongGrid3D {

	public AsymmetricEvolvingLongGridSection3D(SymmetricEvolvingLongGrid3D source) {
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
