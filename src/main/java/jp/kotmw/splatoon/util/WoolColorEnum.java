package jp.kotmw.splatoon.util;

import java.math.BigDecimal;

public enum WoolColorEnum {

	WHITE(0xff,0xff,0xff),
	ORANGE(0xff,0xa5,0x00),
	MAGENTA(0xff,0x00,0xff),
	AQUA(0x00,0xff,0xff),
	YELLOW(0xff,0xff,0x00),
	LIME(0x00,0xff,0x00),
	PINK(0xff,0xc0,0xcb),
	GRAY(0x80,0x80,0x80),
	SLATE_GRAY(0x70,0x80,0x90),
	TEAL(0x00,0x80,0x80),
	PURPLE(0x80,0x00,0x80),
	BLUE(0x00,0x00,0xff),
	BROWN(0xa5,0x2a,0x2a),
	GREEN(0x00,0x80,0x00),
	RED(0xff,0x00,0x00),
	BLACK(0x00,0x00,0x00);

	private final float red;
	private final float green;
	private final float blue;

	private WoolColorEnum(final float red, final float green, final float blue) {
		this.red = Adjustmentparam(red/255);
		this.green = Adjustmentparam(green/255);
		this.blue = Adjustmentparam(blue/255);
	}

	public float getRed() {return red;}
	public float getGreen() {return green;}
	public float getBlue() {return blue;}

	private float Adjustmentparam(float rgb) {
		if(rgb == 0) {
			return 0.1f;
		} else if(rgb == 1) {
			return -0.1f;
		}
		BigDecimal bd = new BigDecimal(rgb);
		return bd.setScale(2, BigDecimal.ROUND_DOWN).floatValue();
	}
}
