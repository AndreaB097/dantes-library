package danteslibrary.util;

import java.util.regex.Pattern;

/**
 * Classe contenente metodi per il controllo del formato degli input utilizzando
 * espressioni regolari.
 * @author Andrea Buongusto
 * @author Marco Salierno
 */
public class InputChecker {
	
	/*Campi Gestore*/
	
	/**
	 * Controlla se l'email passata come parametro ha un formato valido.
	 * @param email Email da controllare.
	 * @return Restituisce true se il formato è valido, false altrimenti.
	 */
	public static boolean checkEmail(String email) {
		String regex = "\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*(\\.\\w{2,3})";
    	if(Pattern.matches(regex, email))
    		return true;
    	
    	return false;
	}
	
	/**
	 * Controlla se la password passata come parametro ha un formato valido
	 * @param password
	 * @return Restituisce true se il formato è valido, altrimenti false
	 */
	public static boolean checkPassword(String password) {
		String regex = "\\w{6,20}";
    	if(Pattern.matches(regex, password))
    		return true;
    	
    	return false;
	}
	
	/**
	 * Controlla se il nome passato come parametro ha un formato valido
	 * @param name
	 * @return Restituisce true se il formato è valido, altrimenti false
	 */
	public static boolean checkName(String name) {
		String regex = "[A-zÀ-ú ]{1,30}";
    	if(Pattern.matches(regex, name))
    		return true;
    	
    	return false;
	}
	
	/**
	 * Controlla se il cognome passato come parametro ha un formato valido
	 * @param surname
	 * @return Restituisce true se il formato è valido, altrimenti false
	 */
	public static boolean checkSurname(String surname) {
		String regex = "[A-zÀ-ú ]{1,30}";
    	if(Pattern.matches(regex, surname))
    		return true;
    	
    	return false;
	}
	
	/**
	 * Controlla se l'indirizzo stradale passato come parametro ha un formato valido
	 * @param address
	 * @return Restituisce true se il formato è valido, altrimenti false
	 */
	public static boolean checkAddress(String address) {
		String regex = "[A-zÀ-ú0-9 ,]{5,100}";
    	if(Pattern.matches(regex, address))
    		return true;
    	
    	return false;
	}
	
	/**
	 * Controlla se il numero di telefono passato come parametro ha un formato valido
	 * @param phone
	 * @return Restituisce true se il formato è valido, altrimenti false
	 */
	public static boolean checkPhone(String phone) {
		String regex = "[0-9]{7,10}";
    	if(Pattern.matches(regex, phone))
    		return true;
    	
    	return false;
	}
	
	
	/*Campi Utente*/
		
	/**
	 * Controlla se il codice fiscale passato come parametro ha un formato valido
	 * @param codice_fiscale
	 * @return Restituisce true se il formato è valido, altrimenti false
	 */
	public static boolean checkCodice_fiscale(String codice_fiscale) {
		String regex = "[a-zA-Z]{6}\\d\\d[a-zA-Z]\\d\\d[a-zA-Z]\\d\\d\\d[a-zA-Z]";
    	if(Pattern.matches(regex, codice_fiscale))
    		return true;
    	
    	return false;
	}
	
	/**
	 * Controlla se il card_id passato come parametro ha un formato valido.
	 * @param card_id
	 * @return Restituisce true se il formato è valido, false altrimenti.
	 */
	public static boolean checkCard_id(String card_id) {
		String regex = "[0-9]{5}";
    	if(Pattern.matches(regex, card_id))
    		return true;
    	
    	return false;
	}
	
	/*Campi Prenotazione*/
	
	/**
	 * Controlla se il booking_id passato come parametro ha un formato valido.
	 * @param card_id
	 * @return Restituisce true se il formato è valido, false altrimenti.
	 */
	public static boolean checkBooking_id(String booking_id) {
		String regex = "[0-9]{5}";
    	if(Pattern.matches(regex, booking_id))
    		return true;
    	
    	return false;
	}
	
	/*Campi Libro*/	
	
	/**
	 * Controlla se il book_id passato come parametro ha un formato valido.
	 * @param book_id
	 * @return Restituisce true se il formato è valido, false altrimenti.
	 */
	public static boolean checkBook_id(String book_id) {
		String regex = "[A-zÀ-ú0-9 _.,:?!]{1,100}";
    	if(Pattern.matches(regex, book_id))
    		return true;
    	
    	return false;
	}
	
	/**
	 * Controlla se il titolo passato come parametro ha un formato valido.
	 * @param title
	 * @return Restituisce true se il formato è valido, false altrimenti.
	 */
	public static boolean checkTitle(String title) {
		String regex = "[.]{1,100}";
    	if(Pattern.matches(regex, title))
    		return true;
    	
    	return false;
	}
	
	
	/**
	 * Controlla se l'autore passato come parametro ha un formato valido.
	 * @param author
	 * @return Restituisce true se il formato è valido, false altrimenti.
	 */
	public static boolean checkAuthor(String author) {
		String regex = "[A-zÀ-ú ]{1,100}";
    	if(Pattern.matches(regex, author))
    		return true;
    	
    	return false;
	}
	
	/**
	 * Controlla se la casa editrice passata come parametro ha un formato valido
	 * @param publisher
	 * @return Restituisce true se il formato è valido, false altrimenti.
	 */
	public static boolean checkPublisher(String publisher) {
		String regex = "[A-zÀ-ú0-9 _.,:?!]{1,100}";
    	if(Pattern.matches(regex, publisher))
    		return true;
    	
    	return false;
	}
	
	
	/**
	 * Controlla se il genere passato come parametro ha un formato valido
	 * @param genre
	 * @return Restituisce true se il formato è valido, false altrimenti.
	 */
	public static boolean checkGenre(String genre) {
		String regex = "[A-zÀ-ú0-9 _.,:?!]{1,100}";
    	if(Pattern.matches(regex, genre))
    		return true;
    	
    	return false;
	}
	
	/**
	 * Controlla se la descrizione passata come parametro ha un formato valido
	 * @param description
	 * @return Restituisce true se il formato è valido, false altrimenti.
	 */
	public static boolean checkDescription(String description) {
		String regex = "[\\s\\S]{1,1000}";
    	if(Pattern.matches(regex, description))
    		return true;
    	
    	return false;
	}
	
	/**
	 * Controlla se la quantità passata come parametro ha un formato valido
	 * @param quantity
	 * @return Restituisce true se il formato è valido, false altrimenti.
	 */
	public static boolean checkQuantity(String quantity) {
		String regex = "[0-9]";
    	if(Pattern.matches(regex, quantity))
    		return true;
    	
    	return false;
	}
	
	
}
