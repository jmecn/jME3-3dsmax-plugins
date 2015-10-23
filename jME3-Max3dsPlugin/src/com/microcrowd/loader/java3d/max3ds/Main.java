/**
 * Make a donation http://sourceforge.net/donate/index.php?group_id=98797
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

package com.microcrowd.loader.java3d.max3ds;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import javax.media.j3d.Behavior;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.GraphicsConfigTemplate3D;
import javax.media.j3d.Interpolator;
import javax.media.j3d.Locale;
import javax.media.j3d.PhysicalBody;
import javax.media.j3d.PhysicalEnvironment;
import javax.media.j3d.RotPosPathInterpolator;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TransformInterpolator;
import javax.media.j3d.View;
import javax.media.j3d.ViewPlatform;
import javax.media.j3d.VirtualUniverse;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.filechooser.FileFilter;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Quat4f;
import com.sun.j3d.loaders.Scene;
import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.behaviors.keyboard.KeyNavigatorBehavior;


/**
 * Provides an example of loader usage with key navigation.
 * If loaded as an applet expects a 'url' parameter tag which
 * must be specified as a fully qualified url.  If there isn't a 
 * url tag it looks for an applet parameter called filename.
 * If this is run from the command line it will try to load
 * the file passed in at the prompt, unless a command line argument
 * 'url' is the first argument.  Then it will load the url represented
 * as the second command argument.
 */
public class Main extends Applet
{
    private BoundingSphere bounds = new BoundingSphere(new Point3d(0.0,0.0,0.0),3000.0);
    private Canvas3D canvas;
    private String modelLocation;
    private static final String DEFAULT_MODEL = "bounce.3DS";
    private BranchGroup universeBranch;

    /**
     * If loaded from the command line a modelFile must be provided.
     * args are <filename> or url <url>
     */
    public static void main(String args[])
    {
        Main self = new Main();
        if(args.length > 0)
            self.modelLocation = args[0];

        MainFrame mainFrame = new MainFrame(self, 750, 550);
    }

    /**
     * Initializes applet. If this is loaded from a command
     * line the model is loaded from the parameters provided.
     * If it is loaded from an applet the parameter tag 'model'
     * is checked for a path to a model. 
     */
    public void init()
    {
        try 
        {
            URL location = null;
            if(modelLocation == null)
            {
                location = findAFile(DEFAULT_MODEL);
            }
            else
            {
                location = findAFile(modelLocation);
            } 

            if(location == null)
                throw new IllegalArgumentException("No model was found when attempting to retrieve " +
                        (modelLocation == null ? DEFAULT_MODEL : modelLocation));

            GraphicsConfigTemplate3D config = new GraphicsConfigTemplate3D();
            config.setSceneAntialiasing(GraphicsConfigTemplate3D.PREFERRED);
            GraphicsDevice gd[] = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
            canvas = new Canvas3D(gd[0].getBestConfiguration(config));

            setLayout(new BorderLayout());
            add("Center", canvas);

            JMenuBar menuBar = new JMenuBar();
            JMenu fileMenu = new JMenu("File");
            JMenuItem menuItem = new JMenuItem("Open");
            add(menuBar, "North");
            menuBar.add(fileMenu);
            fileMenu.add(menuItem);
            menuItem.addActionListener(new BrowseListener());

            View view = new View();

            Scene scene = getScene(location);
            universeBranch = createUniverse(view, scene);
            view.addCanvas3D(canvas);

            addSceneToBranch(universeBranch, scene);
        }
        catch(Exception e){
            e.printStackTrace();
            usage(e.getMessage());
        }
    }

    /**
     * Adds the scene to the branch and turns on the scene's 
     * behaviors.
     */
    private void addSceneToBranch(BranchGroup branch, Scene scene)
    {
        turnOnBehaviors(scene.getBehaviorNodes());
        BranchGroup modelGroup = scene.getSceneGroup();
        modelGroup.compile();
        branch.addChild(modelGroup);
    }

    /**
     * This is called during initialization of the applet, 
     */
    public void Main()
    {
    }

    /**
     * load the scene.
     */
    public Scene getScene(URL location)
    {
        Scene scene = null;

        try 
        {
            return new Loader3DS().load(location);
        }
        catch(IOException e){
            e.printStackTrace();
            usage(e.getMessage());
        }
        return null;
    }


    /**
     * Turns on all the behaviors provided.
     * @param bahaviors the behaviors to enable.
     */
    public void turnOnBehaviors(Behavior[] behaviors)
    {
        if(behaviors == null)
            return;
        for(int i=0; i < behaviors.length; i++)
        {

            behaviors[i].setEnable(true);
            if(behaviors[i] instanceof Interpolator)
            {
                ((Interpolator)behaviors[i]).setSchedulingBounds(new BoundingSphere(new Point3d(), 3000));
            }
            if(behaviors[i] instanceof TransformInterpolator)
            {
                ((TransformInterpolator)behaviors[i]).getTarget().setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
            } 
        }
    }

    /**
     * Adds an interpolator that will be added to 
     * the view transform so that it may be used to
     * switched cameras later.
     * @param target the target that the interpolator will operate on
     */
    private void addCameraInterpolator(TransformGroup target)
    {
        Interpolator cameraInterpolator = new RotPosPathInterpolator(
                null, target, new Transform3D(), new float[]{0,1}, 
                new Quat4f[]{new Quat4f(), new Quat4f()}, new Point3f[]{new Point3f(), new Point3f()});
        cameraInterpolator.setSchedulingBounds(bounds);
        target.addChild(cameraInterpolator);
    }
    /**
     * Constructs a scene graph.
     * <ol>
     * <li>Creates a branch group
     * <li>Adds the parent transform group to it.
     * </ol>
     */
    public BranchGroup createSceneGraph() {
        BranchGroup    root           = new BranchGroup();
        TransformGroup parentGroup     = new TransformGroup();

        parentGroup.setCapability(TransformGroup.ENABLE_PICK_REPORTING);
        parentGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        parentGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        parentGroup.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
        parentGroup.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
        parentGroup.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);

        root.addChild(parentGroup);

        return root;
    } 

    /**
     * Creates a universe with a locale turns the scene graph.  
     * Builds a scene branch group and adds it to the locale.  
     * Builds a view platform that uses the provided view and adds
     * that to the locale.
     * @param view the view to use in the view platform
     * @param scene the scene to grab a camera from as a default view
     *
     * @return the root group of the scene branch of the universe. This
     * is what other groups for display are added to. 
     */
    public BranchGroup createUniverse(View view, Scene scene) 
    {
        VirtualUniverse universe         = new VirtualUniverse();
        Locale locale           = new Locale(universe);
        BranchGroup     sceneBranch = createSceneGraph();
        BranchGroup viewBranchGroup  = new BranchGroup();
        sceneBranch.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        viewBranchGroup.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        ViewPlatform    platform         = new ViewPlatform();

        TransformGroup viewTransformGroup = new TransformGroup();
        viewTransformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        viewTransformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        viewTransformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);

        TransformGroup[] viewGroups = scene.getViewGroups();
        Transform3D viewTransform = new Transform3D();
        if(viewGroups != null && viewGroups.length > 0)
        {
            viewGroups[0].getTransform(viewTransform);
        }

        addViewKeyBehavior(viewTransformGroup);
        viewTransformGroup.setTransform(viewTransform);

        addCameraInterpolator(viewTransformGroup);
        viewTransformGroup.addChild(platform);
        viewBranchGroup.addChild(viewTransformGroup);

        platform.setViewAttachPolicy(View.RELATIVE_TO_FIELD_OF_VIEW);
        platform.setActivationRadius(100);

        configureView(platform, view);
        sceneBranch.setCapability(BranchGroup.ALLOW_DETACH);

        viewBranchGroup.compile();
        sceneBranch.compile();
        locale.addBranchGraph(viewBranchGroup);
        locale.addBranchGraph(sceneBranch);
        return sceneBranch;
    }

    /**
     * Adds a behavior to the view that listens to the canvas.
     * This allows 1st person navigation.
     */
    public void addViewKeyBehavior(TransformGroup viewTransformGroup)
    {
        KeyNavigatorBehavior keyBehavior = new KeyNavigatorBehavior(canvas, viewTransformGroup);
        keyBehavior.setSchedulingBounds(bounds);
        //keyBehavior.setMovementRate(100.0f);
        viewTransformGroup.addChild(keyBehavior);
    }

    /**
     * Creates a physical environment and physical body and
     * adds it them the view which is configured to use
     * a regular screen display for configuration. The view is
     * attached to the platform.
     * @param platform the platform which will have the view attached to it.
     * @param view the view to which the body and environment will be added.
     * canvas 3d added to it.
     */
    protected void configureView(ViewPlatform platform, View view)
    {
        PhysicalBody        body        = new PhysicalBody();
        PhysicalEnvironment environment = new PhysicalEnvironment();

        view.setPhysicalEnvironment(environment);
        view.setPhysicalBody(body);
        view.attachViewPlatform(platform);
        view.setBackClipDistance(1000.0);
        view.setFrontClipDistance(1.0);


    }

    public static void usage()
    {
        usage("");
    }

    public static void usage(String message)
    {
        System.out.println(message);
        System.out.println("This is a sample program for the java3ds loader");
        System.out.println("usage java -jar Loader3DS <model> where model is the 3ds file");
        System.out.println("Textures for the file should be in the same directory as the model");
        System.out.println("If this is being run as an applet a parameter named model (a relative url) may be provided");
        System.exit(1);
    }

    /** 
     * Looks for a file to load.  If one
     * cannot be found looks for and loads 
     * the default one.
     * @param name, or path of the file to find
     * looks for the following:
     * <ol>
     * <li>a file with the path of fileName
     * <li>a resource in the classpath with fileName
     * <li>a resource in the classpath corresponding to 
     * the default file.
     * <li>a url specified in the applet parameter &quot;model&quot;
     * </ol>
     *
     **/
    private URL findAFile(String fileName)
    {
        URL location = null;
        if(fileName != null)
        {
            try 
            {
                File file = new File(fileName);
                if (file.exists())
                {
                    return file.toURL();
                }
            }
            catch(IOException e)
            {
                e.printStackTrace();
                usage(e.getMessage());
            }
        }

        if(fileName != null)
        {
            location = Main.class.getClassLoader().getResource(fileName);
        }
        if(location != null)
        {
            return location;
        }

        String relativeURL = getParameter("model");
        if(relativeURL != null)
        {
            try 
            {
                URL codeBase = getCodeBase();
                return new URL(codeBase.toString() + relativeURL);
            }
            catch(MalformedURLException e){
                e.printStackTrace();
                usage(e.getMessage());
            }
        }


        return location;
    }

    private class BrowseListener implements ActionListener
    {
        public void actionPerformed(ActionEvent event)
        {
            File file = null;
            try {
                JFileChooser chooser = new JFileChooser();
                Filter3DS filter = new Filter3DS();
                chooser.setFileFilter(filter);
                chooser.showDialog(Main.this, "wee");
                file = chooser.getSelectedFile();
                if(file == null)
                    return;
                Scene scene = getScene(file.toURL());
                addSceneToBranch(universeBranch, scene);
            }
            catch(Exception e){
                e.printStackTrace();
                System.out.println("file not loadable " + file);
            }
        }
    }

    private class Filter3DS extends FileFilter
    {
        public String getDescription()
        {
            return "3DS Files";
        }
        public boolean accept(File file)
        {
            if (file.isDirectory())
                return true;
            String fileName = file.getName();
            if(fileName.length() < 4)
                return false;
            
            String extension = fileName.substring(fileName.length() - 3, fileName.length());
            if(extension.equalsIgnoreCase("3ds"))
                return true;
            return false;
        }
    }
}
