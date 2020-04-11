package cellularautomata.automata;

import java.io.FileNotFoundException;
import java.io.IOException;

import cellularautomata.grid3d.AsymmetricLongGridSection3D;

public class AsymmetricLongCASection3D extends AsymmetricLongGridSection3D implements LongCellularAutomaton3D {

	public AsymmetricLongCASection3D(SymmetricLongCellularAutomaton3D source) {
		super(source);
	}

	@Override
	public boolean nextStep() throws Exception {
		return ((SymmetricLongCellularAutomaton3D) source).nextStep();
	}

	@Override
	public long getStep() {
		return ((SymmetricLongCellularAutomaton3D) source).getStep();
	}

	@Override
	public String getName() {
		return ((SymmetricLongCellularAutomaton3D) source).getName() + "_Asymmetric";
	}

	@Override
	public String getSubFolderPath() {
		return ((SymmetricLongCellularAutomaton3D) source).getSubFolderPath() + "/Asymmetric";
	}

	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		((SymmetricLongCellularAutomaton3D) source).backUp(backupPath, backupName);
	}

}
