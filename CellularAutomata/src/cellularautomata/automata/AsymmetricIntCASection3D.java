package cellularautomata.automata;

import java.io.FileNotFoundException;
import java.io.IOException;

import cellularautomata.grid3d.AsymmetricIntGridSection3D;

public class AsymmetricIntCASection3D extends AsymmetricIntGridSection3D implements IntCellularAutomaton3D {

	public AsymmetricIntCASection3D(SymmetricIntCellularAutomaton3D source) {
		super(source);
	}

	@Override
	public boolean nextStep() throws Exception {
		return ((SymmetricIntCellularAutomaton3D) source).nextStep();
	}

	@Override
	public long getStep() {
		return ((SymmetricIntCellularAutomaton3D) source).getStep();
	}

	@Override
	public String getName() {
		return ((SymmetricIntCellularAutomaton3D) source).getName() + "_Asymmetric";
	}

	@Override
	public String getSubFolderPath() {
		return ((SymmetricIntCellularAutomaton3D) source).getSubFolderPath() + "/Asymmetric";
	}

	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		((SymmetricIntCellularAutomaton3D) source).backUp(backupPath, backupName);
	}

}
