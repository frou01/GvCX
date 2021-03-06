package handmadeguns.emb_modelloader;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class MQO_Vertex
{

	public float x, y, z;

	public MQO_Vertex(float x, float y)
	{
		this(x, y, 0F);
	}

	public MQO_Vertex(float x, float y, float z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public void normalize()
	{
		double d = length();
		this.x /= d;
		this.y /= d;
		this.z /= d;
	}
	public double length(){
		return Math.sqrt(x*x + y*y + z*z);
	}
	
	public void add(MQO_Vertex v)
	{
		this.x += v.x;
		this.y += v.y;
		this.z += v.z;
	}
	
	public boolean equal(MQO_Vertex v)
	{
		return this.x==v.x && this.y==v.y && this.z==v.z;
	}
	public double angle(MQO_Vertex v){
		return this.dot(v)/this.length()/v.length();
	}
	public double dot(MQO_Vertex v){
		return this.x * v.x + this.y * v.y + this.z * v.z;
	}
}
