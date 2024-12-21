package tfc.btvr.math.util;

public class Quaternion {
	public float x, y, z, w;
	
	public Quaternion normalise(Quaternion quaternion) {
		double len = Math.sqrt(x * x + y * y + z * z + w * w);
		double ilen = 1 / len;
		x *= ilen;
		y *= ilen;
		z *= ilen;
		w *= ilen;
		return this;
	}
	
	public void set(float v, float v1, float v2, float v3) {
		this.x = v;
		this.y = v1;
		this.z = v2;
		this.w = v3;
	}
}
