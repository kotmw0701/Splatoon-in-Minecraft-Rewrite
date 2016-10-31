package jp.kotmw.splatoon.manager;

public class TeamCountManager {

	private boolean havearea = false;
	private int count = 100;
	private int penalty = 0;
	private int ensurecount = 0;

	public void sethavearea(boolean havearea) {this.havearea = havearea;}

	public boolean ishavearea() {return havearea;}

	public int getcount() {return count;}

	public int getpenalty() {return penalty;}

	public int setpenalty() {
		int penalty = (int)(ensurecount*0.75)+1;
		int beforepenalty = this.penalty;
		this.penalty += penalty;
		this.ensurecount = 0;
		return beforepenalty;
	}

	public void updatecount() {
		if(havearea) {
			if(penalty > 0) {
				penalty--;
				return;
			}
			count--;
			ensurecount++;
		}
	}
}
