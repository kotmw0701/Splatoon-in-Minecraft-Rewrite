package jp.kotmw.splatoon.util;

import java.math.BigDecimal;

public class DetailsColor {
	
	private float red;
	private float green;
	private float blue;
	
	public DetailsColor(DetailsColorType type) {setColor(type.getCode());}
	
	public DetailsColor(String code) {setColor(code);}
	
	public DetailsColor(int red, int green, int blue) {
		this.red = Adjustmentparam(red);
		this.green = Adjustmentparam(green);
		this.blue = Adjustmentparam(blue);
	}
	
	private void setColor(String code) {
		int colorvalue = Integer.decode(code).intValue();
		this.red = Adjustmentparam(((colorvalue >> 16) & 0xFF));
		this.green = Adjustmentparam(((colorvalue >> 8) & 0xFF));
		this.blue = Adjustmentparam((colorvalue & 0xFF));
	}

	public float getRed() {return red;}
	public float getGreen() {return green;}
	public float getBlue() {return blue;}

	private float Adjustmentparam(float rgb) {
		rgb /= 255.0;
		if(rgb == 0.0) return 0.1f;
		else if(rgb == 1.0) return -0.1f;
		BigDecimal bd = new BigDecimal(rgb);
		return bd.setScale(2, BigDecimal.ROUND_DOWN).floatValue();
	}
	
	public enum DetailsColorType {
		WoolColor_WHITE("0xffffff"),
		WoolColor_ORANGE("0xffa500"),
		WoolColor_MAGENTA("0xff00ff"),
		WoolColor_AQUA("0x00ffff"),
		WoolColor_YELLOW("0xffff00"),
		WoolColor_LIME("0x00ff00"),
		WoolColor_PINK("0xffc0cb"),
		WoolColor_GRAY("0x808080"),
		WoolColor_SLATE_GRAY("0x708090"),
		WoolColor_TEAL("0x008080"),
		WoolColor_PURPLE("0x800080"),
		WoolColor_BLUE("0x0000ff"),
		WoolColor_BROWN("0xa52a2a"),
		WoolColor_GREEN("0x008000"),
		WoolColor_RED("0xff0000"),
		WoolColor_BLACK("0x000000"),
		Silver_Platinum("#E5E4E2"),
		Silver_Lavender_Tint("#DADADA"),
		Silver_Pale_Silver("#C9C0BB"),
		Silver_Pink("#C4AEAD"),
		Silver("#C0C0C0"),
		Silver_Sand("#BFC1C2"),
		Silver_New_Silver("#BFB8A5"),
		Silver_Light_Sirocco("#B8C2C2"),
		Silver_Silver_Chalice("#ACACAC"),
		Silver_Quicksilver("#A6A6A6"),
		Silver_Roman_Silver("#838996"),
		Silver_Old_Silver("#848482"),
		Silver_Sonic_Silver("#757575"),
		Silver_Sirocco("#718080"),
		Navy_Medium_Ultramarine("#5A7CC2"),
		Navy_Star_Command_Blue("#007BB8"),
		Navy_Bright_Navy_Blue("#0066CC"),
		Navy_Medium_Blue("#0066CD"),
		Navy_Electric_Ultramarine("#3F00FF"),
		Navy_Royal_Azure("#0038A8"),
		Navy_Pigment_Blue("#333399"),
		Navy_Ultramarine("#120A8F"),
		Navy_Dark_Blue("#00008B"),
		Navy_Blue("#000080"),
		Navy_Persian_Indigo("#32127A"),
		Navy_Midnight_Blue("#191970"),
		Navy_Dark_Sapphire("#082567"),
		Navy_Space_Cadet("#1D2951");

		private final String code;

		private DetailsColorType(String code) {
			this.code = code;
		}
		
		public DetailsColor getColor() {
			return new DetailsColor(code);
		}
		
		public float getRed() {return getColor().red;}
		public float getGreen() {return getColor().green;}
		public float getBlue() {return getColor().blue;}
		
		public String getCode() {
			return code;
		}
	}

}