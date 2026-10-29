package com.atos.arp.models;

import java.io.Serializable;
import java.util.Date;

public class JourFerie implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private int id;
	private String description;
	private Date jour;
	
	public JourFerie() {
	}
	
	public JourFerie(int id, String description, Date jour) {
		
		this.id = id;
		this.description = description;
		this.jour = jour;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getJour() {
		return jour;
	}

	public void setJour(Date jour) {
		this.jour = jour;
	}
	
	public String toString() {
		return "Jour [id=" + id + ", description=" + description + ", jour=" + jour + "]";
	}
}
