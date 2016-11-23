package Game.DataStructure;

/**
 * Created by Envy on 11/23/2016.
 */
public class Vector3 {
    public float x, y, z;
    public Vector3(float X, float Y, float Z){
        x=X;
        y=Y;
        z=Z;
    }

    public Vector3(Vector3 vec){
        this.x = vec.x;
        this.y = vec.y;
        this.z = vec.z;
    }

    public Vector3 add(float nX, float nY, float nZ) {
        x+=nX;
        y+=nY;
        z+=nZ;
        return this;
    }
}
