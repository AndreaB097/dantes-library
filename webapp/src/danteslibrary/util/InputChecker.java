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
		if (email == null)
			throw new NullPointerException("Errore: il campo email è null");
		String regex = "\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*(\\.\\w{2,3})";
    	if(Pattern.matches(regex, email) && email.length() <= 100 && email.length() >= 5)
    		return true;
    	
    	return false;
	}
	
	/**
	 * Controlla se la password passata come parametro ha un formato valido
	 * @param password
	 * @return Restituisce true se il formato è valido, altrimenti false
	 */
	public static boolean checkPassword(String password) {
		if (password == null)
			throw new NullPointerException("Errore: il campo password è null");
		String regex = ".{6,20}";
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
		if (name == null)
			throw new NullPointerException("Errore: il campo name è null");
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
		if (surname == null)
			throw new NullPointerException("Errore: il campo surname è null");
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
		if (address == null)
			throw new NullPointerException("Errore: il campo address è null");
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
		if (phone == null)
			throw new NullPointerException("Errore: il campo phone è null");
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
		if (codice_fiscale == null)
			throw new NullPointerException("Errore: il campo codice_fiscale è null");
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
		if (card_id == null)
			throw new NullPointerException("Errore: il campo card_id è null");
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
		if (booking_id == null)
			throw new NullPointerException("Errore: il campo booking_id è null");
		String regex = "[0-9]*";
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
		if (book_id == null)
			throw new NullPointerException("Errore: il campo book_id è null");
		String regex = "[0-9]*";
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
		if (title == null)
			throw new NullPointerException("Errore: il campo title è null");
		String regex = ".{1,100}";
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
		if (author == null)
			throw new NullPointerException("Errore: il campo author è null");
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
		if (publisher == null)
			throw new NullPointerException("Errore: il campo publisher è null");
		String regex = ".{1,100}";
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
		if (genre == null)
			throw new NullPointerException("Errore: il campo genre è null");
		String regex = "[A-zÀ-ú0-9 ]{1,30}";
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
		if (description == null)
			throw new NullPointerException("Errore: il campo description è null");
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
		if (quantity == null)
			throw new NullPointerException("Errore: il campo quantity è null");
		String regex = "[0-9]{1,3}";
		int num_quantity = Integer.parseInt(quantity);
    	if(Pattern.matches(regex, quantity) && num_quantity >= 0 && num_quantity < 1000)
    		return true;
    	
    	return false;
	}
	
	/*Sezione Biblioteca*/
	
	/**
	 * Controlla se il nome biblioteca passata come parametro ha un formato valido
	 * @param library_name
	 * @return Restituisce true se il formato è valido, false altrimenti.
	 */
	public static boolean checkLibraryName(String library_name) {
		if (library_name == null)
			throw new NullPointerException("Errore: il campo library_name è null");
		String regex = "[A-zÀ-ú ]{1,100}";
    	if(Pattern.matches(regex, library_name))
    		return true;
    	
    	return false;
	}
	
	/**
	 * Controlla se contatti biblioteca passato come parametro ha un formato valido
	 * @param contacts
	 * @return Restituisce true se il formato è valido, false altrimenti.
	 */
	public static boolean checkContacts(String contacts) {
		if (contacts == null)
			throw new NullPointerException("Errore: il campo contacts è null");
		String regex = "[\\s\\S]{0,300}";
    	if(Pattern.matches(regex, contacts))
    		return true;
    	
    	return false;
	}
	
}