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
package com.jme3.asset.max3ds.data;

import java.util.HashMap;
import java.util.List;

import com.jme3.animation.AnimControl;
import com.jme3.math.Matrix4f;
import com.jme3.math.Quaternion;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 * @author Josh DeFord 
 */
public class KeyFramer
{
    private HashMap lastGroupMap = new HashMap();
    private HashMap fatherMap = new HashMap();

    private Quaternion   rotation;
    private Vector3f  position;
    private Vector3f  pivotCenter;
    private Vector3f pivot;
    private Vector3f scale;
    private HashMap<String, Transform>  namedObjectCoordinateSystems = new HashMap<String, Transform>();

    private List positionKeys;
    private List orientationKeys;
    private List scaleKeys;

    private Integer id;
    private Node father;
    private Node dummyObject;


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
    public AnimControl createBehavior(String meshName, Node transformGroup, Object testObject)
    {
    	Node objectGroup = getObjectByName(meshName, transformGroup, testObject);
    	if (objectGroup == null) {
    		return null;
    	}
    	

    	/*
    	insertFather(objectGroup, meshName);
    	
        Transform coordinateSystem  = (Transform)namedObjectCoordinateSystems.get(meshName);

        //Gonna put these children back later.
        List<Spatial> children = removeChildren(objectGroup);

        Transform coordinateTransform = coordinateSystem == null ? new Transform() : coordinateSystem;

        Transform targetTransform = new Transform();
        Node targetGroup = new Node();
        targetGroup.setLocalTransform(targetTransform);

        Node localCoordinates = hasKeys() ? buildLocalCoordinates(coordinateSystem) : new Node();
        Node lastGroup = (Node)addGroups(objectGroup, new Node[]
                {
                    localCoordinates,
                    targetGroup,
                    buildPivotGroup(coordinateTransform, pivot),
                    buildKeysGroup(),
                });

        addChildren(children, lastGroup);
        lastGroupMap.put(objectGroup, lastGroup);


        AnimControl anim = buildInterpolator(targetGroup, coordinateSystem);
        if(anim != null)
        {
            anim.setEnabled(false);
            targetGroup.addControl(anim);

            // behavior.computeTransform(0f, targetTransform);
            // targetGroup.setLocalTransform(targetTransform);
        }
        return anim;
        */
    	return null;
    }

    private List<Spatial> removeChildren(Node group)
    {
    	List<Spatial> children = group.getChildren(); 
        group.detachAllChildren();
        return children;
    }

    private void addChildren(List<Spatial> children, Node group)
    {
        if(group == null)
            return;
        for(Spatial child : children) {
        	 group.attachChild(child);
        }
    }

    /**
     * Looks up the current object.
     * objectGroup is returned if it is the right one to return
     * otherwise a new dummy object may be returned.
     * If it isn't there it gets the dummy object
     * from the frames description chunk.
     */
    private Node getObjectByName(String objectName, Node objectGroup, Object testObject)
    {

        //This means its a dummy object.  It needs to be created.
        if(objectGroup == null && testObject == null)
        {
            namedObjectCoordinateSystems.put(objectName, new Transform());
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
    private void insertFather(Node parentGroup, String objectName)
    {
        if(father == null)
            return;
        Node topGroup = new Node(); 
        topGroup.attachChild(father);
        Node bottomGroup = (Node)lastGroupMap.get(father);

        Node fatherParent = (Node)topGroup.getParent();
        if(fatherParent != null)
            fatherParent.detachChild(topGroup);
        
        List<Spatial> originalChildren = removeChildren(parentGroup);
        parentGroup.attachChild(topGroup);
        addChildren(originalChildren, bottomGroup);
    }

    /**
     * Builds a transform group from the zeroth key of the 
     * position and rotation tracks.
     * @return transform group with position and rotation information
     */
    private Node buildKeysGroup()
    {
        Node node = new Node();
        node.setLocalTranslation(position);
        node.setLocalRotation(rotation);
        node.setLocalScale(scale);
        
        return node;
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
    private Node buildPivotGroup(Transform coordinateTransform, Vector3f pivot)
    {
            Transform pivotTransform = new Transform();
//            pivotTransform.mult(coordinateTransform);
            pivot = new Vector3f(pivot);
            pivot.negate();
            translatePivot(pivotTransform, pivot, pivotCenter);
            
//            Transform3D pivotTransform = new Transform3D();
//            pivotTransform.mulInverse(coordinateTransform);
//            pivot = new Vector3f(pivot);
//            pivot.negate();
//            translatePivot(pivotTransform, pivot, pivotCenter);
//            return new TransformGroup(pivotTransform);
            
            return null;
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
    private Node buildLocalCoordinates(Transform coordinateSystem)
    {
    	//Matrix4f coordMatrix = new Matrix4f();
    	//Vector3f translation = new Vector3f();

    	//translation = coordinateSystem.getTranslation();
    	//coordinateSystem.invert();

    	//coordinateSystem.get(coordMatrix);
    	//coordMatrix.m03 = translation.x;
    	//coordMatrix.m13 = translation.y;
    	//coordMatrix.m23 = translation.z;
    	//coordinateSystem.set(coordMatrix);
        //coordinateSystem.invert();
        Node systemGroup = new Node();
        //systemGroup.setLocalTransform(coordinateSystem);
        //coordinateSystem.invert();
        return systemGroup;
    }

   /**
     * Hierarchically adds the provided groups in order to parentGroup.
     * groups[0] is added to parentGroup, groups[1] is added to groups[0] etc.
     * @return the last group added (groups[groups.length - 1]).
     */
    private Node addGroups(Node parentGroup, Node[] groups)
    {
    	Node nextGroup = parentGroup;
        for(int i=0; i < groups.length; i++)
        {
            nextGroup.attachChild(groups[i]);
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
    private void translatePivot(Transform transform, Vector3f vector, Vector3f offset)
    {
        if(offset != null)
        {
            pivot.subtractLocal(offset);
        }
        Matrix4f matrix = new Matrix4f();
        matrix.setTransform(transform.getTranslation(), transform.getScale(), transform.getRotation().toRotationMatrix());

        matrix.m03 += (matrix.m00*vector.x + matrix.m01*vector.y + matrix.m02*vector.z);
        matrix.m13 += (matrix.m10*vector.x + matrix.m11*vector.y + matrix.m12*vector.z);
        matrix.m23 += (matrix.m20*vector.x + matrix.m21*vector.y + matrix.m22*vector.z);
        
        // transform.set(matrix);
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
    private AnimControl buildInterpolator(Node targetGroup, Transform axisOfTransform)
    {
        makeTwoListsTheSameSize(positionKeys, orientationKeys);
        int numKeys = positionKeys.size();

        Vector3f currentPoint = position; 
        Quaternion  currentQuat  = rotation; 
        if(numKeys > 1) 
        {
            float[]    knots = new float[numKeys];
            Vector3f[] points = new Vector3f[numKeys];
            Quaternion[]  quats  = new Quaternion[numKeys];

            for(int i=0; i < numKeys; i++) 
            {
                //Knots need to be between 0(beginning) and 1(end)
                knots[i]= (i==0?0:((float)i/((float)(numKeys-1))));
                if(positionKeys.size() > i)
                {
                    Vector3f newPoint = (Vector3f)positionKeys.get(i);
                    if(newPoint != null)
                    {
                        currentPoint = newPoint;
                    }

                    Quaternion newQuat = (Quaternion)orientationKeys.get(i);
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
            //Alpha alpha = new Alpha(-1, (long)(numKeys/.03));
            //alpha.setStartTime(System.currentTimeMillis());
            //alpha.setDuplicateOnCloneTree(true);

            //rotator = new RotPosPathInterpolator(alpha, targetGroup, axisOfTransform, knots, quats, points);
        }
        return null;
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
    public void setPivotCenter(Vector3f center)
    {
        this.pivotCenter = center;
    }

    /**
     * Called to set the coordinate system transform for an object named
     * objectName. 
     * This is the first t
     */
    public void setCoordinateSystem(String objectName, Transform coordinateSystem)
    {
        namedObjectCoordinateSystems.put(objectName, coordinateSystem);
    }

    /**
     * Sets the group that will be used to center rotations.
     * This is applied to the mesh after all other transforms
     * have been applied.
     * @param group the group that will act as the rotation transform.
     */
    public void setRotation(Quaternion rotation)
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
    public void setPosition(Vector3f position)
    {
        this.position = position;
    }

    /**
     * Sets the position information necessary for animation.s
     * @param positions a list of Vector3f, which may contain null elements,
     * containing a position for some keys.
     */
    public void setPositionKeys(List positions)
    {
        positionKeys = positions;
    }

    /**
     * Sets the orientation information necessary for animation.s
     * @param positions a list of Quaternion, which may contain null elements,
     * containing a orientation for some keys.
     */
    public void setOrientationKeys(List orientations)
    {
        orientationKeys = orientations;
    }

    /**
     *
     */
    public void setDummyObject(Node object)
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
    public void addFather(int fatherID, Node newFather)
    {
        if(fatherID < 0)
        {
            father = null;
        }
        else
        {
            father = (Node)(fatherMap.get(new Integer(fatherID)));
            //Remove the father's father because the father will
            //be inserted somewhere later.
            Node grandFather = (Node)father.getParent();
            if(grandFather != null)
            {
                grandFather.detachChild(father);
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
