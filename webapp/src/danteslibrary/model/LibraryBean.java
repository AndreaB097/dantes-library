package danteslibrary.model;

/**
 * Bean che definisce l'entità Biblioteca
 * @author Andrea Buongusto
 * @author Marco Salierno
 */
public class LibraryBean {
	
	private String name;
	private String logo;
	private String contacts;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLogo() {
		return logo;
	}
	public void setLogo(String logo) {
		this.logo = logo;
	}
	public String getContacts() {
		return contacts;
	}
	public void setContacts(String contacts) {
		this.contacts = contacts;
	}

}
