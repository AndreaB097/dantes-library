package danteslibrary.util;

import java.util.regex.Pattern;

/**
 * Classe contenente metodi per il controllo del formato degli input utilizzando
 * espressioni regolari.
 * @author Andrea Buongusto
 * @author Marco Salierno
 */
public class InputChecker {
	
	/**
	 * Controlla se l'email passata come parametro ha un formato valido.
	 * @param email Email da controllare.
	 * @return Restituisce true se il formato è valido, false altrimenti.
	 */
	public static boolean checkEmail(String email) {
		String regex = "\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*(\\.\\w{2,3})+";
    	if(Pattern.matches(regex, email))
    		return true;
    	
    	return false;
	}
	
	/**
	 * Controlla se il codice fiscale passato come parametro ha un formato valido.
	 * @param codice_fiscale Codice_fiscale da controllare.
	 * @return Restituisce true se il formato è valido, false altrimenti.
	 */
	public static boolean checkCodice_fiscale(String codice_fiscale) {
		String regex = "[a-zA-Z]{6}\\d\\d[a-zA-Z]\\d\\d[a-zA-Z]\\d\\d\\d[a-zA-Z]";
    	if(Pattern.matches(regex, codice_fiscale))
    		return true;
    	
    	return false;
	}
}
