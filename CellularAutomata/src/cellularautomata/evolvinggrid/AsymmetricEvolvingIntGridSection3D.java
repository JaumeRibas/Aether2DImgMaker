package cellularautomata.evolvinggrid;

import java.io.FileNotFoundException;
import java.io.IOException;

import cellularautomata.grid3d.AsymmetricIntGridSection3D;

public class AsymmetricEvolvingIntGridSection3D extends AsymmetricIntGridSection3D implements EvolvingIntGrid3D {

	public AsymmetricEvolvingIntGridSection3D(SymmetricEvolvingIntGrid3D source) {
		super(source);
	}

	@Override
	public boolean nextStep() throws Exception {
		return ((SymmetricEvolvingIntGrid3D) source).nextStep();
	}

	@Override
	public long getStep() {
		return ((SymmetricEvolvingIntGrid3D) source).getStep();
	}

	@Override
	public String getName() {
		return ((SymmetricEvolvingIntGrid3D) source).getName() + "_asymmetric_section";
	}

	@Override
	public String getSubFolderPath() {
		return ((SymmetricEvolvingIntGrid3D) source).getSubFolderPath() + "/asymmetric_section";
	}

	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		((SymmetricEvolvingIntGrid3D) source).backUp(backupPath, backupName);
	}

}
