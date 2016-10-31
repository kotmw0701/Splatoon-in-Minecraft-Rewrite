package jp.kotmw.splatoon.gamedatas;

import jp.kotmw.splatoon.gamedatas.DataStore.BombType;

import org.bukkit.Material;

public class SubWeaponData {
	private String name;
	private Material itemtype;
	private BombType type;
	private int damage;
	private float cost;
	private int cooltime;

	public SubWeaponData(String param1, Material param2, BombType param3, int param4, double param5, int param6) {
		this.name = param1;
		this.itemtype = param2;
		this.type = param3;
		this.damage = param4;
		this.cost = (float) (param5/100);
		this.cooltime = param6;
	}

	public String getName() {return name;}
	public Material getItemtype() {return itemtype;}
	public BombType getType() {return type;}
	public int getDamage() {return damage;}
	public float getCost() {return cost;}
	public int getCooltime() {return cooltime;}

	/*public void setName(String name) {this.name = name;}
	public void setItemtype(Material itemtype) {this.itemtype = itemtype;}
	public void setType(BombType type) {this.type = type;}
	public void setDamage(int damage) {this.damage = damage;}
	public void setCost(float cost) {this.cost = cost;}
	public void setCooltime(int cooltime) {this.cooltime = cooltime;}*/
}
