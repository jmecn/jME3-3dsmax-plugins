/**
 * Make a donation http://sourceforge.net/donate/index.php?group_id=98797
 *
 * Microcrowd.com
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Contact Josh DeFord jdeford@microcrowd.com
 */
package com.microcrowd.loader.java3d.max3ds.data;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import javax.media.j3d.Alpha;
import javax.media.j3d.Behavior;
import javax.media.j3d.Group;
import javax.media.j3d.Node;
import javax.media.j3d.RotPosPathInterpolator;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TransformInterpolator;
import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

/**
 * @author Josh DeFord 
 */
public class KeyFramer
{
    private HashMap lastGroupMap = new HashMap();
    private HashMap fatherMap = new HashMap();

    private Quat4f   rotation;
    private Point3f  position;
    private Point3f  pivotCenter;
    private Vector3f pivot;
    private Vector3f scale;
    private HashMap  namedObjectCoordinateSystems = new HashMap();

    private List positionKeys;
    private List orientationKeys;
    private List scaleKeys;

    private Integer id;
    private Group father;
    private Group dummyObject;


    /**
     * Retrieves the named object for the current key framer
     * inserts the rotation, position and pivot transformations for frame 0
     * and assigns the coordinate system to it.
     *
     * The inverse of the local coordinate system converts from 3ds 
     * semi-absolute coordinates (what is in the file) to local coordinates.
     *
     * Then these local coordinates are converted with matrix 
     * that will instantiate them to absolute coordinates:
     * Xabs = sx a1 (Xl-Px) + sy a2 (Yl-Py) + sz a3 (Zl-Pz) + Tx
     * Yabs = sx b1 (Xl-Px) + sy b2 (Yl-Py) + sz b3 (Zl-Pz) + Ty
     * Zabs = sx c1 (Xl-Px) + sy c2 (Yl-Py) + sz c3 (Zl-Pz) + Tz
     * Where:
     * (Xabs,Yabs,Zabs) = absolute coordinate
     * (Px,Py,Pz) = mesh pivot (constant)
     * (X1,Y1,Z1) = local coordinates
     *
     */
    public Behavior createBehavior(String meshName, Group transformGroup, Object testObject)
    {
        Group objectGroup = getObjectByName(meshName, transformGroup, testObject);
        //System.out.println("mesh " + meshName + " scale " + scale);
        if(objectGroup == null)
            return null;

        insertFather(objectGroup, meshName);

        TransformInterpolator behavior = null;
        Transform3D coordinateSystem  = (Transform3D)namedObjectCoordinateSystems.get(meshName);

        //Gonna put these children back later.
        Enumeration children = removeChildren(objectGroup);

        Transform3D coordinateTransform = coordinateSystem == null ? new Transform3D() : new Transform3D(coordinateSystem);

        Transform3D targetTransform = new Transform3D();
        TransformGroup targetGroup = new TransformGroup(targetTransform);

        TransformGroup localCoordinates = hasKeys() ? buildLocalCoordinates(coordinateSystem) : new TransformGroup();
        TransformGroup lastGroup = (TransformGroup)addGroups(objectGroup, new Group[]
                {
                    localCoordinates,
                    targetGroup,
                    buildPivotGroup(coordinateTransform, pivot),
                    buildKeysGroup(),
                });

        addChildren(children, lastGroup);
        lastGroupMap.put(objectGroup, lastGroup);


        behavior = buildInterpolator(targetGroup, coordinateSystem);
        if(behavior != null)
        {
            behavior.setEnable(false);
            targetGroup.addChild(behavior);

            behavior.computeTransform(0f, targetTransform);
            targetGroup.setTransform(targetTransform);
        }
        return behavior;
    }

    private Enumeration removeChildren(Group group)
    {
        Enumeration children = group.getAllChildren(); 
        group.removeAllChildren();
        return children;
    }

    private void addChildren(Enumeration children, Group group)
    {
        if(group == null)
            return;
        while(children.hasMoreElements())
        {
            Node node = (Node)(children.nextElement());
            group.addChild(node);
        }
    }

    /**
     * Looks up the current object.
     * objectGroup is returned if it is the right one to return
     * otherwise a new dummy object may be returned.
     * If it isn't there it gets the dummy object
     * from the frames description chunk.
     */
    private Group getObjectByName(String objectName, Group objectGroup, Object testObject)
    {

        //This means its a dummy object.  It needs to be created.
        if(objectGroup == null && testObject == null)
        {
            namedObjectCoordinateSystems.put(objectName, new Transform3D());
            objectGroup = dummyObject;
        }

        return objectGroup;
    }

    /**
     * Locates the father for the object named objectName and inserts 
     * its transform cluster between the parent and all
     * the parent's children. This only occurs in a hierarchical
     * model. like bones and stuff. The fatherGroup, if found,
     * is removed from whatever parent belonged to on before insertion.
     */
    private void insertFather(Group parentGroup, String objectName)
    {
        if(father == null)
            return;
        Group topGroup = new TransformGroup(); 
        topGroup.addChild(father);
        Group bottomGroup = (Group)lastGroupMap.get(father);

        if(topGroup == null)
            return;

        Group fatherParent = (Group)topGroup.getParent();
        if(fatherParent != null)
            fatherParent.removeChild(topGroup);
        
        Enumeration originalChildren = removeChildren(parentGroup);
        parentGroup.addChild(topGroup);
        addChildren(originalChildren, bottomGroup);
    }

    /**
     * Builds a transform group from the zeroth key of the 
     * position and rotation tracks.
     * @return transform group with position and rotation information
     */
    private TransformGroup buildKeysGroup()
    {
        Transform3D positionTransform   = new Transform3D();
        positionTransform.set(new Vector3f(position));

        Transform3D rotationTransform   = new Transform3D();
        rotationTransform.set(rotation);

        Transform3D scaleTransform = new Transform3D();
        scaleTransform.setScale(new Vector3d(scale));
        TransformGroup scaleGroup = new TransformGroup(scaleTransform);

        Transform3D keyTransform = new Transform3D(positionTransform);
        keyTransform.mul(scaleTransform);
        keyTransform.mul(rotationTransform);
        return new TransformGroup(keyTransform);
    }

    /**
     * Builds a pivot group that will allow the objects
     * to be positioned properly according to their rotations
     * and positions.
     * @param coordinateTransform the coordinate system defining the 
     * location and orientation of the local axis. This is not modified.
     * @param pivot the pivot defined in the 3ds file loaded by pivot chunk.
     * This is not changed.
     */
    private TransformGroup buildPivotGroup(Transform3D coordinateTransform, Vector3f pivot)
    {
            Transform3D pivotTransform = new Transform3D();
            pivotTransform.mulInverse(coordinateTransform);
            pivot = new Vector3f(pivot);
            pivot.negate();
            translatePivot(pivotTransform, pivot, pivotCenter);
            return new TransformGroup(pivotTransform);
    }

    /**
     * Builds a coordinate group that will allow the objects
     * to be positioned properly according to their rotations
     * and positions.
     * @param coordinateSystem the coordinate system defining the 
     * location and orientation of the local axis. This is modified 
     * so it will be useful during the construction
     * of the animations.
     */
    private TransformGroup buildLocalCoordinates(Transform3D coordinateSystem)
    {
        Matrix4f coordMatrix = new Matrix4f();
        Vector3f translation = new Vector3f();

        coordinateSystem.get(translation);
        coordinateSystem.invert();

        coordinateSystem.get(coordMatrix);
        coordMatrix.m03 = translation.x;
        coordMatrix.m13 = translation.y;
        coordMatrix.m23 = translation.z;
        coordinateSystem.set(coordMatrix);
        coordinateSystem.invert();
        TransformGroup systemGroup = new TransformGroup(coordinateSystem);
        coordinateSystem.invert();
        return systemGroup;
    }

   /**
     * Hierarchically adds the provided groups in order to parentGroup.
     * groups[0] is added to parentGroup, groups[1] is added to groups[0] etc.
     * @return the last group added (groups[groups.length - 1]).
     */
    private Group addGroups(Group parentGroup, Group[] groups)
    {
        Group nextGroup = parentGroup;
        for(int i=0; i < groups.length; i++)
        {
            nextGroup.addChild(groups[i]);
            nextGroup = groups[i];
        }
        return groups[groups.length - 1];
    }

    /**
     * Does a pre rotational translation of the pivot.
     * @param transform the matrix that will have a translation concatenated to it.
     * @param vector the vector which will be used to translate the matrix.
     * @param offset the offset used to offset the pivot. 
     */
    private void translatePivot(Transform3D transform, Vector3f vector, Point3f offset)
    {
        if(offset != null)
        {
            pivot.sub(offset);
        }
        Matrix4f matrix = new Matrix4f();
        transform.get(matrix);

        matrix.m03 += (matrix.m00*vector.x + matrix.m01*vector.y + matrix.m02*vector.z);
        matrix.m13 += (matrix.m10*vector.x + matrix.m11*vector.y + matrix.m12*vector.z);
        matrix.m23 += (matrix.m20*vector.x + matrix.m21*vector.y + matrix.m22*vector.z);

        transform.set(matrix);
    }


    /**
     * Builds a rotation position interpolator for use on this mesh using position and rotation information
     * adds it to targetGroup.
     * This does not set the capability bits that need to be set for the animation  
     * to be used. The capability bits of the targetGroup must be set by the client application.
     * The alpha object on the Interpolator must also be enabled. 
     * The Interpolator must also have its scheduling bounds set.
     * @param pivotGroup transform group which will be operated on by the interpolator.
     * @param interpolatorAxis the axis that about which rotations will be centered.
     */
    //TODO... This needs to use both a rotation interpolator and a position interpolator
    //in case there are keys with no position information but position information and 
    //vice versa right now its using RotPosPathInterpolator
    private TransformInterpolator buildInterpolator(TransformGroup targetGroup, Transform3D axisOfTransform)
    {
        makeTwoListsTheSameSize(positionKeys, orientationKeys);
        int numKeys = positionKeys.size();

        Point3f currentPoint = position; 
        Quat4f  currentQuat  = rotation; 
        RotPosPathInterpolator rotator = null; 
        if(numKeys > 1) 
        {
            float[]    knots = new float[numKeys];
            Point3f[] points = new Point3f[numKeys];
            Quat4f[]  quats  = new Quat4f[numKeys];

            for(int i=0; i < numKeys; i++) 
            {
                //Knots need to be between 0(beginning) and 1(end)
                knots[i]= (i==0?0:((float)i/((float)(numKeys-1))));
                if(positionKeys.size() > i)
                {
                    Point3f newPoint = (Point3f)positionKeys.get(i);
                    if(newPoint != null)
                    {
                        currentPoint = newPoint;
                    }

                    Quat4f newQuat = (Quat4f)orientationKeys.get(i);
                    if(newQuat != null)
                    {
                        currentQuat = newQuat;
                    }
                }

                points[i] = currentPoint;
                quats[i] = currentQuat;
                quats[i].inverse();
            }

            //This gives a continuous loop at a rate of 30 fps
            Alpha alpha = new Alpha(-1, (long)(numKeys/.03));
            alpha.setStartTime(System.currentTimeMillis());
            alpha.setDuplicateOnCloneTree(true);

            rotator = new RotPosPathInterpolator(alpha, targetGroup, 
                    axisOfTransform, knots, 
                    quats, points);
        }
        return rotator;
    }

    public void makeTwoListsTheSameSize(List list1, List list2)
    {
        growList(list2.size() - 1, list1);
        growList(list1.size() - 1, list2);
    }

    /**
     * Make sure the list is at least able to
     * hold a value at index.
     * @param index an int specifying the initial size
     * @parame the list that may need to grow
     */
    public void growList(int index, List list)
    {
        int numNeeded = (index + 1) - list.size();
        while(numNeeded-- > 0)
        {
            list.add(null);
        }
    }

    /**
     * Sets the center of the bounding box that the pivot
     * should offset.
     */
    public void setPivotCenter(Point3f center)
    {
        this.pivotCenter = center;
    }

    /**
     * Called to set the coordinate system transform for an object named
     * objectName. 
     * This is the first t
     */
    public void setCoordinateSystem(String objectName, Transform3D coordinateSystem)
    {
        namedObjectCoordinateSystems.put(objectName, coordinateSystem);
    }

    /**
     * Sets the group that will be used to center rotations.
     * This is applied to the mesh after all other transforms
     * have been applied.
     * @param group the group that will act as the rotation transform.
     */
    public void setRotation(Quat4f rotation)
    {
        this.rotation = rotation;
    }

    /**
     * Sets the pivot that will be used to as a pivot for
     * these transfomations.
     * @param group the group that will act as the pivot. 
     */
    public void setPivot(Vector3f pivot)
    {
        this.pivot = pivot;
    }

    /**
     * Sets the scale for x y and z axis for objects. 
     * This is applied to the mesh before the rotation transform 
     * has been applied.
     * @param group the group that will act as the scale 
     */
    public void setScale(Vector3f scale)
    {
        this.scale = scale;
    }

    /**
     * Sets the scale information necessary for animation.s
     * @param scaleKeys a list of Vector3f, which may contain null elements,
     * containing a position for some keys.
     */
    public void setScaleKeys(List scaleKeys)
    {
        this.scaleKeys = scaleKeys;
    }

    /**
     * Sets the group that will be used to translate the mesh..
     * This is applied to the mesh just before the rotation transform 
     * has been applied.
     * @param group the group that will act as the position transform.
     */
    public void setPosition(Point3f position)
    {
        this.position = position;
    }

    /**
     * Sets the position information necessary for animation.s
     * @param positions a list of Point3f, which may contain null elements,
     * containing a position for some keys.
     */
    public void setPositionKeys(List positions)
    {
        positionKeys = positions;
    }

    /**
     * Sets the orientation information necessary for animation.s
     * @param positions a list of Quat4f, which may contain null elements,
     * containing a orientation for some keys.
     */
    public void setOrientationKeys(List orientations)
    {
        orientationKeys = orientations;
    }

    /**
     *
     */
    public void setDummyObject(Group object)
    {
        dummyObject = object;
    }

    /**
     * returns true if position keys and orientation
     * keys are longer than one element each.
     */
    public boolean hasKeys()
    {
        return (positionKeys.size() > 1 || orientationKeys.size() > 1);
    }

    /**
     */
    public void addFather(int fatherID, TransformGroup newFather)
    {
        if(fatherID < 0)
        {
            father = null;
        }
        else
        {
            father = (TransformGroup)(fatherMap.get(new Integer(fatherID)));
            //Remove the father's father because the father will
            //be inserted somewhere later.
            Group grandFather = (Group)father.getParent();
            if(grandFather != null)
            {
                grandFather.removeChild(father);
            }
        }
        fatherMap.put(id, newFather);
    }

    /**
     * Sets the id for these frames.
     * @param id the id for these frames.
     */
    public void setID(int id)
    {
        this.id = new Integer(id);
    }
}