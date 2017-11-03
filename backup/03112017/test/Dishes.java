package com.example.bot.spring;

import lombok.extern.slf4j.Slf4j;
import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.*;
import java.util.List;
import java.net.URISyntaxException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;

public class Dishes {
	String dishes_string;
	Items[] items;
	float weight_total = 0;
	int energy_total = 0;
	int sodium_total = 0;
	int fat_total = 0;
	
	Dishes (String text){
		this.dishes_string = text;
		String[] item_names = text.split(" ");
		for(int i = 0; i < dishes_string.length; i++) {
			items[i] = new Items(item_names[i]);
		}
		this.weight_total = 0;
		this.energy_total = 0;
		this.sodium_total = 0;
		this.fat_total = 0;
		
	}
	public float getWeightTotal() {
		return weight_total;
	}
	public int getEnergyTotal() {
		return energy_total;
	}
	public int getSodiumTotal() {
		return sodium_total;
	}
	public int getFatTotal() {
		return fat_total;
	}
	public void setWeightTotal(float w) {
		this.weight_total = w;
	}
	public void setEnergyTotal(int e) {
		this.energy_total = e;
	}
	public void setSodiumTotal(int s) {
		this.sodium_total = s;
	}
	public void setFatTotal(int f) {
		this.fat_total = f;
	}
}

public class Items{
	String name;
	float weight_avg = 0;
	int energy_avg = 0;
	int sodium_avg = 0;
	int fat_avg = 0;
	Items(String name){
		this.name = name;
		this.weight_avg = 0;
		this.energy_avg = 0;
		this.sodium_avg = 0;
		this.fat_avg = 0;
	}
	public String getName() {return name;}
	public float getWeightAvg() {
		return weight_avg;
	}
	public int getEnergyAvg() {
		return energy_avg;
	}
	public int getSodiumAvg() {
		return sodium_avg;
	}
	public int getFatAvg() {
		return fat_avg;
	}
	public void setWeightAvg(float w) {
		this.weight_avg = w;
	}
	public void setEnergyAvg(int e) {
		this.energy_avg = e;
	}
	public void setSodiumAvg(int s) {
		this.sodium_avg = s;
	}
	public void setFatAvg(int f) {
		this.fat_avg = f;
	}
}
