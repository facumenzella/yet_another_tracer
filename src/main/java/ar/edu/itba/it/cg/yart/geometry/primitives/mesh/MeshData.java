package ar.edu.itba.it.cg.yart.geometry.primitives.mesh;

import java.util.Arrays;

import ar.edu.itba.it.cg.yart.geometry.Point3d;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;

public class MeshData {

    private final int[] triindices;
    private final Point3d[] vertices;
    private final Vector3d[] normals;
    
    public MeshData(final int[] triindices, final Point3d[] vertices, final Vector3d[] normals) {
        this.triindices = triindices;
        this.vertices = vertices;
        this.normals = normals;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(normals);
        result = prime * result + Arrays.hashCode(triindices);
        result = prime * result + Arrays.hashCode(vertices);
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MeshData other = (MeshData) obj;
        if (!Arrays.equals(normals, other.normals))
            return false;
        if (!Arrays.equals(triindices, other.triindices))
            return false;
        if (!Arrays.equals(vertices, other.vertices))
            return false;
        return true;
    }
}