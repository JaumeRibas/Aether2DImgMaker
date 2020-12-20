package cellularautomata.evolvinggrid;

import java.io.FileNotFoundException;
import java.io.IOException;

import cellularautomata.grid3d.AsymmetricBigIntGridSection3D;

public class AsymmetricEvolvingBigIntGridSection3D extends AsymmetricBigIntGridSection3D<SymmetricEvolvingBigIntGrid3D> implements EvolvingBigIntGrid3D {

	public AsymmetricEvolvingBigIntGridSection3D(SymmetricEvolvingBigIntGrid3D source) {
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
