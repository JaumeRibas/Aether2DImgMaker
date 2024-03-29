package caimgmaker.args;

import java.math.BigInteger;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.ParameterException;

import caimgmaker.AetherImgMaker;

public class NonNegativeIntegerValidator implements IParameterValidator {
	
	@Override
	public void validate(String name, String value)
			throws ParameterException {
		BigInteger n = new BigInteger(value);
		if (n.compareTo(BigInteger.ZERO) < 0) {
			throw new ParameterException(String.format(AetherImgMaker.messages.getString("param-cannot-be-negative-format"), name, value));
		}
	}
	
}