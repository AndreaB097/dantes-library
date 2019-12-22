package danteslibrary.model;

public class CardBean {
	
	private int card_id;
	private String codice_fiscale;
	private boolean associated;
	
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
	
}
