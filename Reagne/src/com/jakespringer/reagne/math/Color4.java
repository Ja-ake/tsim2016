package com.jakespringer.reagne.math;

public class Color4 {

	public final double r;
	public final double g;
	public final double b;
	public final double a;

	public Color4(double r, double g, double b) {
		this.r = r;
		this.g = g;
		this.b = b;
		a = 1;
	}

	public Color4(double r, double g, double b, double a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}
}
