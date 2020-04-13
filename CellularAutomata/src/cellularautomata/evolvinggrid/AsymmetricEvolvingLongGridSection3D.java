package cellularautomata.evolvinggrid;

import java.io.FileNotFoundException;
import java.io.IOException;

import cellularautomata.grid3d.AsymmetricLongGridSection3D;

public class AsymmetricEvolvingLongGridSection3D extends AsymmetricLongGridSection3D implements EvolvingLongGrid3D {

	public AsymmetricEvolvingLongGridSection3D(SymmetricEvolvingLongGrid3D source) {
		super(source);
	}

	@Override
	public boolean nextStep() throws Exception {
		return ((SymmetricEvolvingLongGrid3D) source).nextStep();
	}

	@Override
	public long getStep() {
		return ((SymmetricEvolvingLongGrid3D) source).getStep();
	}

	@Override
	public String getName() {
		return ((SymmetricEvolvingLongGrid3D) source).getName() + "_asymmetric_section";
	}

	@Override
	public String getSubFolderPath() {
		return ((SymmetricEvolvingLongGrid3D) source).getSubFolderPath() + "/asymmetric_section";
	}

	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		((SymmetricEvolvingLongGrid3D) source).backUp(backupPath, backupName);
	}

}
