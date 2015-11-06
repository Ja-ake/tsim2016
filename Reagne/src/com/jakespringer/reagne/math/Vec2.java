package com.jakespringer.reagne.math;

public class Vec2 {

	public final double x;
	public final double y;

	public Vec2() {
		x = 0;
		y = 0;
	}

	public Vec2(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public Vec2 add(Vec2 other) {
		return new Vec2(x + other.x, y + other.y);
	}

	public double cross(Vec2 other) {
		return x * other.y - y * other.x;
	}

	public double direction() {
		return Math.atan2(y, x);
	}

	public double dot(Vec2 other) {
		return x * other.x + y * other.y;
	}

	public double length() {
		return Math.sqrt(lengthSquared());
	}

	public double lengthSquared() {
		return x * x + y * y;
	}

	public Vec2 multiply(double d) {
		return new Vec2(x * d, y * d);
	}

	public Vec2 normal() {
		return new Vec2(-y, x);
	}

	public Vec2 normalize() {
	    final double len = length();
	    if (len == 0) return new Vec2(1.0, 0.0);
	    return multiply(1 / len);
	}

	public Vec2 reverse() {
		return new Vec2(-x, -y);
	}

	public Vec2 withLength(double l) {
	    if (l == 0.0) return new Vec2();
		return multiply(l / length());
	}

	public Vec2 withX(double newx) {
		return new Vec2(newx, y);
	}

	public Vec2 withY(double newy) {
		return new Vec2(x, newy);
	}

	public Vec2 subtract(Vec2 other) {
		return new Vec2(x - other.x, y - other.y);
	}

	@Override
	public String toString() {
		return "(" + (float) x + ", " + (float) y + ")";
	}
}
