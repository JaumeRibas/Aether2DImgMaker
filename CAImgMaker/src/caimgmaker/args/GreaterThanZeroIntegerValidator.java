package caimgmaker.args;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.ParameterException;

public class GreaterThanZeroIntegerValidator implements IParameterValidator {
	
	@Override
	public void validate(String name, String value)
			throws ParameterException {
		int n = Integer.parseInt(value);
		if (n < 1) {
			throw new ParameterException("Parameter " + name + " must be greater than zero (found " + value +")");
		}
	}
	
}