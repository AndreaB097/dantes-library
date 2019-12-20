package danteslibrary.model;

public class Card {
	
	private int card_code;
	private String codice_fiscale;
	private boolean associated;
	
	public int getCard_code() {
		return card_code;
	}
	public void setCard_code(int card_code) {
		this.card_code = card_code;
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
	
}
