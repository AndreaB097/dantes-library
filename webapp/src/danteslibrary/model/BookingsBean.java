package danteslibrary.model;

import java.time.LocalDate;

public class BookingsBean {
	
	private int booking_code;
	private LocalDate start_date;
	private LocalDate end_date;
	private int state_id;
	
	public int getBooking_code() {
		return booking_code;
	}
	public void setBooking_code(int booking_code) {
		this.booking_code = booking_code;
	}
	public LocalDate getStart_date() {
		return start_date;
	}
	public void setStart_date(LocalDate start_date) {
		this.start_date = start_date;
	}
	public LocalDate getEnd_date() {
		return end_date;
	}
	public void setEnd_date(LocalDate end_date) {
		this.end_date = end_date;
	}
	public int getState_id() {
		return state_id;
	}
	public void setState_id(int state_id) {
		this.state_id = state_id;
	}
}
