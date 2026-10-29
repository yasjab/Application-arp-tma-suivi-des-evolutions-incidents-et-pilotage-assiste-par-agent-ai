package com.atos.arp.models;

import java.io.Serializable;

public class HoraireJourneeTravail implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String heureDebutMatin;
	private String minuteDebutMatin;
	private String secondeDebutMatin;
	
	private String heureFinMatin;
	private String minuteFinMatin;
	private String secondeFinMatin;
	
	private String heureDebutSoir;
	private String minuteDebutSoir;
	private String secondeDebutSoir;
	
	private String heureFinSoir;
	private String minuteFinSoir;
	private String secondeFinSoir;
	
	public HoraireJourneeTravail() {
	}
	
	public HoraireJourneeTravail(String heureDebutMatin, String minuteDebutMatin, String secondeDebutMatin, String heureFinMatin, String minuteFinMatin,
                                 String secondeFinMatin, String heureDebutSoir, String minuteDebutSoir, String secondeDebutSoir, String heureFinSoir, String minuteFinSoir,
                                 String secondeFinSoir) {
		
		this.heureDebutMatin = heureDebutMatin;
		this.minuteDebutMatin = minuteDebutMatin;
		this.secondeDebutMatin = secondeDebutMatin;
		this.heureFinMatin = heureFinMatin;
		this.minuteFinMatin = minuteFinMatin;
		this.secondeFinMatin = secondeFinMatin;
		this.heureDebutSoir = heureDebutSoir;
		this.minuteDebutSoir = minuteDebutSoir;
		this.secondeDebutSoir = secondeDebutSoir;
		this.heureFinSoir = heureFinSoir;
		this.minuteFinSoir = minuteFinSoir;
		this.secondeFinSoir = secondeFinSoir;
	}

	public String getHeureDebutMatin() {
		return heureDebutMatin;
	}

	public void setHeureDebutMatin(String heureDebutMatin) {
		this.heureDebutMatin = heureDebutMatin;
	}

	public String getMinuteDebutMatin() {
		return minuteDebutMatin;
	}

	public void setMinuteDebutMatin(String minuteDebutMatin) {
		this.minuteDebutMatin = minuteDebutMatin;
	}

	public String getSecondeDebutMatin() {
		return secondeDebutMatin;
	}

	public void setSecondeDebutMatin(String secondeDebutMatin) {
		this.secondeDebutMatin = secondeDebutMatin;
	}

	public String getHeureFinMatin() {
		return heureFinMatin;
	}

	public void setHeureFinMatin(String heureFinMatin) {
		this.heureFinMatin = heureFinMatin;
	}

	public String getMinuteFinMatin() {
		return minuteFinMatin;
	}

	public void setMinuteFinMatin(String minuteFinMatin) {
		this.minuteFinMatin = minuteFinMatin;
	}

	public String getSecondeFinMatin() {
		return secondeFinMatin;
	}

	public void setSecondeFinMatin(String secondeFinMatin) {
		this.secondeFinMatin = secondeFinMatin;
	}

	public String getHeureDebutSoir() {
		return heureDebutSoir;
	}

	public void setHeureDebutSoir(String heureDebutSoir) {
		this.heureDebutSoir = heureDebutSoir;
	}

	public String getMinuteDebutSoir() {
		return minuteDebutSoir;
	}

	public void setMinuteDebutSoir(String minuteDebutSoir) {
		this.minuteDebutSoir = minuteDebutSoir;
	}

	public String getSecondeDebutSoir() {
		return secondeDebutSoir;
	}

	public void setSecondeDebutSoir(String secondeDebutSoir) {
		this.secondeDebutSoir = secondeDebutSoir;
	}

	public String getHeureFinSoir() {
		return heureFinSoir;
	}

	public void setHeureFinSoir(String heureFinSoir) {
		this.heureFinSoir = heureFinSoir;
	}

	public String getMinuteFinSoir() {
		return minuteFinSoir;
	}

	public void setMinuteFinSoir(String minuteFinSoir) {
		this.minuteFinSoir = minuteFinSoir;
	}

	public String getSecondeFinSoir() {
		return secondeFinSoir;
	}

	public void setSecondeFinSoir(String secondeFinSoir) {
		this.secondeFinSoir = secondeFinSoir;
	}
	
	public String toString() {
		return "HoraireJourneeTravail [heureDebutMatin=" + heureDebutMatin + ", minuteDebutMatin=" + minuteDebutMatin + ", secondeDebutMatin=" + secondeDebutMatin
				+ ", heureFinMatin=" + heureFinMatin + ", minuteFinMatin=" + minuteFinMatin + ", secondeFinMatin=" + secondeFinMatin + ", heureDebutSoir=" 
				+ heureDebutSoir + ", minuteDebutSoir=" + minuteDebutSoir + ", secondeDebutSoir=" + secondeDebutSoir + ", heureFinSoir=" + heureFinSoir 
				+ ", minuteFinSoir=" + minuteFinSoir + ", secondeFinSoir=" + secondeFinSoir + "]";
	}

	public String get(int index){
		
		switch (index) {
			case 1:
				return heureDebutMatin;
			case 2:
				return minuteDebutMatin + ":" + secondeDebutMatin;
			case 3:
				return heureFinMatin;
			case 4:
				return minuteFinMatin + ":" + secondeFinMatin;
			case 5:
				return heureDebutSoir;
			case 6:
				return minuteDebutSoir + ":" + secondeDebutSoir;
			case 7:
				return heureFinSoir;
			case 8:
				return minuteFinSoir + ":" + secondeFinSoir;
		}
		
		return "";
	}
}
