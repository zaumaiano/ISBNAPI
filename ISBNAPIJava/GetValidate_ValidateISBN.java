import com.ibm.broker.javacompute.MbJavaComputeNode;
import com.ibm.broker.plugin.MbElement;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbMessageAssembly;
import com.ibm.broker.plugin.MbOutputTerminal;
import com.ibm.broker.plugin.MbUserException;

public class GetValidate_ValidateISBN extends MbJavaComputeNode {

	public void evaluate(MbMessageAssembly inAssembly) throws MbException {
		MbOutputTerminal fail = getOutputTerminal("failure");
		MbOutputTerminal out = getOutputTerminal("out");
		MbOutputTerminal alt = getOutputTerminal("alternate");

		MbMessage inMessage = inAssembly.getMessage();
		MbMessageAssembly outAssembly = null;
		
		try {
			MbMessage outMessage = new MbMessage(inMessage);
			outAssembly = new MbMessageAssembly(inAssembly, outMessage);
			
			MbElement env = inAssembly.getGlobalEnvironment().getRootElement();
			MbElement variables = env.getFirstElementByPath("Variables");
			
			boolean isValid = validateISBN13(variables.getFirstElementByPath("ISBNValue").getValueAsString());
			
			if(isValid) {
				out.propagate(outAssembly);
			} else {
				alt.propagate(outAssembly);
			}
			
		} catch (Exception e) {
			fail.propagate(outAssembly);
			throw new MbUserException(this, "evaluate()", "", "", e.toString(), null);
		}
	}
	
	public boolean validateISBN13(String isbnValue) {
		String value = isbnValue;
		
        if ( value == null )
            return false;        
        
        value = value.replaceAll( "-", "" );
        
        if (value.length() != 13 )
        	return false;
                 
        try	{
            int total = 0;
            
            for ( int i = 0; i < 12; i++ )	{
                int digit = Integer.parseInt(value.substring( i, i + 1 ) );
                total += (i % 2 == 0) ? digit * 1 : digit * 3;
            }
            
            int checksum = 10 - (total % 10);
            if ( checksum == 10 )
                checksum = 0;
                   
            return checksum == Integer.parseInt( value.substring( 12 ) );
        }
        catch ( NumberFormatException ex) {
            return false;
        }
	}
}
