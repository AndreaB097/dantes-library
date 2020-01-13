package danteslibrary.model;

/**
 * Bean che definisce l'entità Tessera
 * @author Andrea Buongusto
 * @author Marco Salierno
 */
public class CardsBean {
	
	private int card_id;
	private String codice_fiscale;
	private boolean associated;
	private String name;
	private String surname;
	private String email;
	
	public int getCard_id() {
		return card_id;
	}
	public void setCard_id(int card_id) {
		this.card_id = card_id;
	}
	public String getCodice_fiscale() {
		return codice_fiscale;
	}
	public void setCodice_fiscale(String codice_fiscale) {
		this.codice_fiscale = codice_fiscale;
	}
	public boolean isAssociated() {
		return associated;
	}
	public void setAssociated(boolean associated) {
		this.associated = associated;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSurname() {
		return surname;
	}
	public void setSurname(String surname) {
		this.surname = surname;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
}
