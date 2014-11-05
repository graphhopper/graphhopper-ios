package org.sourceforge.autojar;

/* This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * monoped@users.sourceforge.net
 */

import java.io.*;
import java.util.*;
import java.util.jar.*;
import java.util.zip.*;
import de.monoped.efile.*;
import de.monoped.utils.*;

import org.apache.bcel.*;
import org.apache.bcel.classfile.*;
import org.apache.bcel.generic.*;
import org.apache.bcel.util.*;
import org.apache.log4j.*;

/** Class providing a public interface (used by Autojar and Eclipse plugin).
 *  @author Bernd Eggink (monoped@users.sourceforge.net)  
 */

public class Ajar
{

                                    /** Don't check for dynamic loading. */
    static public final int         DYN_OFF = 0; 

                                    /** Warn if dynamic loading is detected. */
    static public final int         DYN_WARN = 1;

                                    /** Try to resolve dynamic loading (implies DYN_WARN). */
    static public final int         DYN_AUTO = 2;

                                    /** No output except errors. */
    static public final int         QUIET_LEVEL = 0; 

                                    /** Standard level, normal output. */
    static public final int         STD_LEVEL = 1;

                                    /** Verbose output. */
    static public final int         VERBOSE_LEVEL = 2;

                                    /** Debug output. */
    static public final int         DEBUG_LEVEL = 3;

    static private final String     JAVA_HOME = System.getProperty("java.home");

    private int                     dynamic;
    private boolean                 bothpaths,
                                    debug, 
                                    dynresource,
                                    quiet,
                                    searchExtensions;
    private String                  mainClass;
    private ZipOutputStream         jarout;
    private FilePath                classpath, filepath;
    private HashSet                 doneFiles,
                                    zipEntries,
                                    usedSources;
    private Level                   logLevel;
    private List                    classPathList,          // user supplied class path
                                    extensionList,      
                                    excludes,
                                    fileExcludes;
    private byte[]                  buffer;
    private TreeSet                 forNameDyn,
                                    getResourceDyn,
                                    missing;
    private Logger                  logger;
    private String[]                bootPathComps;
            
    //----------------------------------------------------------------------

    /** Create an Ajar object.
     *
     *  @param outfile          Output file (jar).
     *  @param classPathList    List containing user supplied class path components.
     *  @param excludes         List containing class prefixes to exclude.
     *  @param filePathList     List containing file path components.
     *  @param manifest         File containing additional manifest entries.
     *  @param dynamic          How to handle Class.ForName() etc. One of {@link #DYN_OFF DYN_OFF}, 
     *                              {@link #DYN_WARN DYN_WARN}, 
     *                              {@link #DYN_AUTO DYN_AUTO}. 
     *  @param dynresource      If true, try to find and add dynamically loaded resources.
     *  @param searchExtensions If true, search extdirs for classes.
     *  @param bothpaths        Use class path for file path.
     *  @param level            Verbosity level. One of {@link #QUIET_LEVEL QUIET_LEVEL},
     *                          {@link #STD_LEVEL STD_LEVEL},
     *                          {@link #VERBOSE_LEVEL VERBOSE_LEVEL},
     *                          {@link #DEBUG_LEVEL DEBUG_LEVEL}.
     */

    public Ajar(File outfile, List classPathList, List excludes, List filePathList,
            File manifest, int dynamic, boolean dynresource, 
            boolean searchExtensions, boolean bothpaths,
            int level)
        throws IOException
    { 
        this.excludes = excludes;
        this.dynamic = dynamic;
        this.dynresource = dynresource;
        this.searchExtensions = searchExtensions;
        this.bothpaths = bothpaths;
        this.classPathList = classPathList;
        extensionList = new ArrayList();

        logger = Logger.getRootLogger();

        switch (level)
        {
            case QUIET_LEVEL:   logLevel = Level.ERROR;
                                break;
            case VERBOSE_LEVEL: logLevel = Level.INFO;
                                break;
            case DEBUG_LEVEL:   logLevel = Level.DEBUG;
                                debug = true;
                                break;
            default:            logLevel = Level.WARN;
        }

        logger.setLevel(logLevel);

        doneFiles = new HashSet();
        zipEntries = new HashSet();
        usedSources = new HashSet();
        fileExcludes = new ArrayList();
        buffer = new byte[8192];
        forNameDyn = new TreeSet();
        getResourceDyn = new TreeSet();
        missing = new TreeSet();

        classpath = new FilePath();
        filepath = new FilePath();

        // Build classpath, starting with boot path
        
        String bootPath = System.getProperty("sun.boot.class.path");

        classpath.add(bootPath);
        bootPathComps = bootPath.split(File.pathSeparator);

        if (classPathList != null)
            for (Iterator it = classPathList.iterator(); it.hasNext(); )
            {
                String  comp = (String)it.next();
                File    file = new File(comp);

                if (file.isDirectory())
                    classpath.add(comp);
                else if (file.exists())
                {
                    try
                    {
                        new ZipFile(comp);
                        classpath.add(comp);
                    }
                    catch (ZipException ex)
                    {
                        logger.warn("Class path component '" + comp + "' is not a valid zip file.");
                    }
                }
                else
                    logger.warn("Class path component '" + comp + "' doesn't exist.");
            }
        else
            classpath.add(".");

        // append extension dirs

        String extdirs = System.getProperty("java.ext.dirs");

        if (extdirs != null)
        {
            // create list of extension archives

            String[] extcomp = extdirs.split(File.pathSeparator);

            for (int i = 0; i < extcomp.length; ++i)
            {
                File dir = new File(extcomp[i]);

                if (! dir.exists() || ! dir.isDirectory())
                    continue;

                String[] files = dir.list();

                for (int j = 0; j < files.length; ++j)
                    if (files[j].endsWith(".jar") || files[j].endsWith(".zip"))
                        extensionList.add(new File(dir, files[j]).toString());
            }
        }

        classpath.addList(extensionList);    // add extension archives to class path

        // file path

        if (filePathList != null)
            for (Iterator it = filePathList.iterator(); it.hasNext(); )
            {
                String comp = (String)it.next();

                if (new File(comp).exists())
                    filepath.add(comp);
                else
                    logger.warn("File path component '" + comp + "' doesn't exist.");
            }
        else if (bothpaths && classPathList != null)
            filepath.addList(classPathList);
    
        logger.info("Class path:  " + classpath.toOSPath());
        logger.info("File path:   " + filepath.toOSPath());
        logger.info("Output file: " + outfile.getPath());
                
        // create output file

        FileOutputStream    out = new FileOutputStream(outfile);

        if (! outfile.getPath().endsWith(".jar"))
            jarout = new ZipOutputStream(out);
        else if (manifest == null)           // without manifest
            jarout = new JarOutputStream(out);
        else
        {                               // with manifest
            logger.info("Manifest:    " + manifest.getPath());

            Manifest            mani = new Manifest();
            FileInputStream     maniIn = new FileInputStream(manifest);
            
            mani.getMainAttributes().putValue("Manifest-Version", "1.0");
            mani.read(maniIn);
            maniIn.close();
            jarout = new JarOutputStream(out, mani);

            // handle Main-Class if given
               
            mainClass = mani.getMainAttributes().getValue("Main-Class");

            if (mainClass != null && mainClass.length() > 0)
                lookupClass(mainClass);
        }
    }

    //----------------------------------------------------------------------
 
    /** Create an Ajar object with standard log level. */

    public Ajar(File outfile, List classPathList, List excludes, List filePathList,
            File manifest, int dynamic, boolean dynresource, 
            boolean searchExtensions, boolean bothpaths)
        throws IOException
    { 
        this(outfile, classPathList, excludes, filePathList,
            manifest, dynamic, dynresource, searchExtensions, bothpaths, STD_LEVEL);
    }

    //----------------------------------------------------------------------

    /** Add a class file to output (unconditionally). 
     *
     *  @param classFile    The class file.
     */

    private void addClass(EFile classFile)
        throws FileNotFoundException, IOException
    {
        String  base = classFile.getBase(),
                path = classFile.getPath();
        boolean explicit = false,
                copy = true;

        // test if library is in user class path

        for (Iterator it = classPathList.iterator(); it.hasNext(); )
        {
            String comp = (String)it.next();

            if (base.equals(comp))
            {
                explicit = true;
                break;
            }
        }

        if (! explicit)
        {
            boolean inExt = false;

            // check if base is an extension archive

            for (Iterator it = extensionList.iterator(); it.hasNext(); )
            {
                String file = (String)it.next();

                if (base.equals(file))
                {
                    inExt = true;       // in extension
                    break;
                }
            }

            if (inExt && ! searchExtensions) 
                copy = false;           // handle recursively, don't copy
            else if (! inExt)
            {
                if (base.startsWith(JAVA_HOME))
                    return;
                
                for (int i = 0; i < bootPathComps.length; ++i)
                    if (bootPathComps[i].equals(base))
                        return;
            }
        }

        // base excluded?
        
        for (Iterator it = excludes.iterator(); it.hasNext(); )
            if (base.startsWith((String)it.next()))
                return;

        if (copy)
            copyToJar(classFile);

        // handle referred classes recursively

        handleReferences(classFile.getInputStream(), path.substring(0, path.length() - 6));
    }

    //----------------------------------------------------------------------

    private void addResource(EFile resoFile)
        throws FileNotFoundException, IOException
    {
        String  base = resoFile.getBase(),
                path = resoFile.getPath();

        // base excluded?
        
        for (Iterator it = excludes.iterator(); it.hasNext(); )
            if (base.startsWith((String)it.next()))
                return;

        copyToJar(resoFile);
    }

    //----------------------------------------------------------------------

    /** Add a pattern for input file exclusion.
     *
     *  @param pattern  Pattern containing * and ? wildcards.
     */

    public void addFileExcludes(String pattern)
    {
        fileExcludes.add(pattern);
    }

    //----------------------------------------------------------------------

    /** Add an entry to the list of missing files.
     *
     *  @param name     Name of the file.
     */

    public void addMissing(String name)
    {
        missing.add(name);
    }

    //----------------------------------------------------------------------

    /** Clear exclusion pattern list. */

    public void clearFileExcludes()
    {
        fileExcludes.clear();
    }

    //----------------------------------------------------------------------

    /** Close output file. */

    public void close()
    {
        try
        {
            if (zipEntries.size() > 0)
                jarout.close();
        }
        catch (IOException e)
        {
            logger.fatal(e.getMessage(), e);
        }
    }

    //----------------------------------------------------------------------

    /** Copy file content to the output.
     *
     *  @param  file    File to be copied.
     */

    private void copyToJar(EFile file)
        throws FileNotFoundException, IOException
    {
        byte[]  bytes = file.getBytes();
        String  base = file.getBase(),
                path = file.getPath(),
                zipPath = path.replace(File.separatorChar, '/');

        if (putEntry(zipPath))
        {
            jarout.write(bytes);
            doneFiles.add(path);
            missing.remove(path);
            usedSources.add(base);
            logger.info("added: " + base + " => " + zipPath);
        }
    }

    //----------------------------------------------------------------------

    /** Found Class.forName, Class.getResource etc. in constant pool */

    private void forNameResource(JavaClass klass, ConstantMethodref refForName,
           ConstantMethodref refClassGetResource, ConstantMethodref refClassGetStream, 
           ConstantMethodref refLoaderGetResource, ConstantMethodref refLoaderGetStream)
        throws IOException
    {
        // Scan all methods 

        ConstantPool    pool = klass.getConstantPool();
        Method[]        methods = klass.getMethods();
        
        for (int i = 0; i < methods.length; ++i)
        {
            Method  method = methods[i];
            Code    code = method.getCode();

            if (code == null 
                    || method.getName().endsWith("$")           // javac
                    || method.getName().equals("class")         // jikes
                )
                continue;

            // Get line number table

            LineNumberTable lineNumbers = code.getLineNumberTable();

            // Wrap code into InstructionList
            
            InstructionList instructions = null;

            try
            {
                instructions = new InstructionList(code.getCode());
            }
            catch (ClassGenException ex)
            {
                logger.fatal(code.toString(), ex);
                System.exit(1);
            }

            instructions.setPositions();

            // Iterate through instructions

            for (Iterator it = instructions.iterator(); it.hasNext(); )
            {
                InstructionHandle   handle = (InstructionHandle)it.next();
                Instruction         instruction = handle.getInstruction();
                InstructionHandle   preHandle = handle.getPrev();

                if (preHandle == null)
                    continue;

                if (! (instruction instanceof InvokeInstruction))
                    continue;
                
                // get Instruction from Pool
                
                ConstantCP      constant  = (ConstantCP)pool.getConstant(((InvokeInstruction)instruction).getIndex());        
                Instruction     pre = preHandle.getInstruction();

                if (constant != null && refForName != null && constant.equals(refForName))
                {
                    // found Class.forName...

                    if (pre.getOpcode() == Constants.LDC)       // pre-instruction loads constant
                    {
                        // ... with constant (String) operand

                        LDC ldc = (LDC)pre;

                        String  operand = (String)ldc.getValue(new ConstantPoolGen(pool)),  
                                bcelop = operand.replace('.', '/') + ".class";
                        boolean found = getDynamic() == Ajar.DYN_AUTO && 
                                    lookupClassInternal(bcelop);        // Lookup and copy if found
                        String message = logstr(klass, method, lineNumbers, handle, 
                                    "Class.forName(\"" + operand + "\")", found);

                        logger.info("* " + message);
                    }
                    else
                    {
                        // ... with computed operand

                        String message = logstr(klass, method, lineNumbers, handle, null, false);

                        forNameDyn.add(message);
                    }
                }
                
                boolean useClass = false, useLoader = false, 
                        asStream = false;

                if (refClassGetResource != null && refClassGetResource.equals(constant))
                    useClass = true;
                else if (refClassGetStream != null && refClassGetStream.equals(constant))
                    useClass = asStream = true;
                else if (refLoaderGetResource != null && refLoaderGetResource.equals(constant))
                    useLoader = true;
                else if (refLoaderGetStream != null && refLoaderGetStream.equals(constant))
                    useLoader = asStream = true;
                else
                    continue;

                String  className = useLoader ? "ClassLoader" : "Class",
                        methodName = asStream ? "getResourceAsStream" : "getResource",
                        call;

                if (pre.getOpcode() == Constants.LDC)       // does pre-instruction load constant?
                {
                    // yes

                    LDC     ldc = (LDC)pre;
                    String  operand = (String)ldc.getValue(new ConstantPoolGen((pool)));   

                    call = methodName + "(\"" + operand + "\")";

                    if (dynresource)
                    {
                        // If the file can be found, add it

                        EFile   theFile = null;

                        try
                        {
                            String path; 
                            
                            if (useClass)
                                if (operand.startsWith("/"))
                                    path = operand.substring(1);
                                else
                                    path = klass.getPackageName().replace('.', '/') + "/" + operand;
                            else
                                path = operand;

                            theFile = classpath.lookupFile(path);
                        }
                        catch (IOException ex)
                        {
                            missing.add(operand);
                        }

                        if (theFile != null)
                        {
                            // found it

                            String message = logstr(klass, method, lineNumbers, handle, call, true);
                            logger.info("* " + message);
                            addResource(theFile);
                        }
                        else
                            logger.info("* " + logstr(klass, method, lineNumbers, handle, call, false));
                    }
                    else
                        logger.info("* " + logstr(klass, method, lineNumbers, handle, call, false));
                }
                else
                {
                    // sorry, pre-instruction doesn't load a constant

                    String message = logstr(klass, method, lineNumbers, handle, methodName + "()", false);
                    getResourceDyn.add(message);
                }
            }
        }
    }

    //----------------------------------------------------------------------

    public int getDynamic()
    {
        return dynamic;
    }

    //----------------------------------------------------------------------

    /** Get the log level. */

    public Level getLogLevel()
    {
        return logLevel;
    }

    //----------------------------------------------------------------------

    /** Get mainClass property. */

    public String getMainClass()
    {
        return mainClass;
    }

    //----------------------------------------------------------------------

    /** Handle a directory by adding it to the output.
     *
     *  @param base     Base directory or null.
     *  @param name     Directory name.
     */
    
    public void handleDirectory(File base, String name)
        throws IOException
    {
        File    dir = new File(base, name);
        String  zipPath = name.replace(File.separatorChar, '/');

        logger.info("added: " + zipPath);

        // create directory entry

        putEntry(zipPath + '/');

        // process all files recursively

        String[]  files = dir.list();
        
        for (int i = 0; i < files.length; ++i)
            handleFileNoLookup(base, new File(name, files[i]).getPath(), true);
    }

    //----------------------------------------------------------------------

    /** Handle a directory by adding it to the output.
     *
     *  @param path     Directory path.
     */
    
    public void handleDirectory(String path)
        throws IOException
    {
        handleDirectory(null, path);
    }

    //----------------------------------------------------------------------

    /** Handle file without lookup.
     *
     *  @param base     Base directory or null.
     *  @param name     File name.
     */

    public void handleFile(File base, String name, boolean includeSource)
        throws IOException
    {
        File    file = new File(base, name);
        String  path = file.getPath();

        // handle file according to suffix

        if (file.isDirectory())
        {
            // directory
               
            handleDirectory(base, name);
        }
        else if (path.endsWith(".jar") || path.endsWith(".war"))
        {
            // add jar file contents

            handleJarFile(file, includeSource);
        }
        else if (path.endsWith(".zip"))
        {
            // add zip file contents

            handleZipFile(file);
        }
        else
        {
            // normal file (no lookup)
               
            handleFileNoLookup(base, name, true);          
        }
    }

    //----------------------------------------------------------------------

    /** Handle a list of files.
     *
     *  @param base     Base directory or null.
     *  @param list     List containing the filenames (names may contain wildcards).
     */

    public void handleFileList(File base, String[] list)
        throws IOException
    {
        if (list == null)
            return;

        // iterate through list of files
           
        for (int iex = 0; iex < list.length; ++iex)
        {
            String  name = list[iex];

            if (! FileUtils.patternMatches(fileExcludes, name))
                handleFile(base, name, true);
        } 
    }

    //----------------------------------------------------------------------

    /** Handle a list of files.
     *
     *  @param list     List containing the filenames.
     */

    public void handleFileList(String[] list)
        throws IOException
    {
        handleFileList(null, list);
    }

    //----------------------------------------------------------------------

    /** Handle normal file without lookup.
     *
     *  @param base     Base directory or null.
     *  @param name     File name (may contain wildcards).
     */

    private void handleFileNoLookup(File base, String name, boolean includeSource)
        throws IOException
    {
        File    file = new File(base, name);
        String  path = file.getPath();
        String  zipPath = name.replace(File.separatorChar, '/');

        if (FileUtils.patternMatches(fileExcludes, file.getName()))
            return;

        if (file.isDirectory())
        {
            // process content of directory
               
            handleDirectory(base, name);
            return;
        }
        
        if (includeSource)
        {
            BufferedInputStream     in = new BufferedInputStream(new FileInputStream(file));
            int                     n;

            // Copy file to output

            if (putEntry(zipPath))
            {
                while((n = in.read(buffer)) >= 0)
                    jarout.write(buffer, 0, n);

                doneFiles.add(path);
                missing.remove(path);
                logger.info("added: " + zipPath);
            }
        }
    }

    //----------------------------------------------------------------------

    /** Handle a jar file. Extract everything except manifest. 
     *
     *  @param file             Jar file.
     *  @param includeSource    Add source to output
     */

    private void handleJarFile(File file, boolean includeSource)
        throws IOException
    {
        String  path = file.getPath();
        JarFile jarfile = new JarFile(file);

        for (Enumeration entries = jarfile.entries(); entries.hasMoreElements(); )
        {
            JarEntry    entry = (JarEntry)entries.nextElement();
            String      entryName = entry.getName();

            if (entryName.startsWith("META-INF"))
                continue;

            if (FileUtils.patternMatches(fileExcludes, entryName))
                continue;       // matches exclude pattern

            if (includeSource)
            {
                if (! putEntry(entry))
                    continue;       // already there
            
                InputStream in = jarfile.getInputStream(entry);
                int         n;
            
                while ((n = in.read(buffer)) >= 0)
                    jarout.write(buffer, 0, n);

                in.close();
                doneFiles.add(entryName);
            }
            
            if (entryName.endsWith(".class"))
                handleReferences(jarfile.getInputStream(entry), 
                    entryName.substring(entryName.length() - 6).replace(File.separatorChar, '.'));

            missing.remove(entryName);

            logger.info("added: " + path + " => " + entryName);
        }

        classPathList.add(path);
        usedSources.add(path);
    }

    //----------------------------------------------------------------------

    /** Scan class file for class references 
     *  
     *  @param in       Stream reading from class file
     *  @param bcelName BCEL class name in the form pack1/pack2/name
     */

    private void handleReferences(InputStream in, String bcelName)
        throws IOException
    {
        JavaClass       klass = new ClassParser(in, bcelName).parse();
        Avisitor        visitor = new Avisitor(this, klass);
        ConstantPoolGen pool = new ConstantPoolGen(klass.getConstantPool());

        new DescendingVisitor(klass, visitor).visit();
        in.close();

        if (dynamic == DYN_OFF)          // no -d option
            return;

        // handle references to Class.forName etc.

        if (visitor.hasClassReferences())
            forNameResource(klass, visitor.getRefClassForName(),
                    visitor.getRefClassGetResource(), 
                    visitor.getRefClassGetResourceAsStream(),
                    visitor.getRefLoaderGetResource(),
                    visitor.getRefLoaderGetResourceAsStream());
    }

    //----------------------------------------------------------------------

    /** Handle a zip file. Extract everything. 
     *
     * @param file  Zip file.
     */

    private void handleZipFile(File file)
        throws IOException
    {
        String  path = file.getPath();
        ZipFile zipfile = new ZipFile(file);

        for (Enumeration entries = zipfile.entries(); entries.hasMoreElements(); )
        {
            ZipEntry    entry = (ZipEntry)entries.nextElement();
            String      entryName = entry.getName();

            if (FileUtils.patternMatches(fileExcludes, entryName))
                continue;

            InputStream in = zipfile.getInputStream(entry);
            int         n;
            
            if (putEntry(entry))
            {
                while ((n = in.read(buffer)) >= 0)
                    jarout.write(buffer, 0, n);

                in.close();
                doneFiles.add(entryName);
                missing.remove(entryName);
                logger.info("added: " + path + " => " + entryName);
            }
        }
    }

    //----------------------------------------------------------------------

    /** Get bothpaths setting. */

    public boolean isBothpaths()
    {
        return bothpaths;
    }

    //----------------------------------------------------------------------

    /** Get debug setting. */

    public boolean isDebug()
    {
        return debug;
    }

    //----------------------------------------------------------------------

    /** Create message (Class.forName, getResource() etc.) */

    private String logstr(JavaClass klass, Method method, 
            LineNumberTable lineNumbers, InstructionHandle handle, 
            String call, boolean found)
    {
        StringBuffer message = new StringBuffer(klass.getClassName());
        
        if (lineNumbers != null)
            message.append(":").append(lineNumbers.getSourceLine(handle.getPosition()));

        message.append(" (method ").append(method.getName()).append(")");
        
        if (call != null)
            message.append(": ").append(call);

        if (found)
            message.append(" (RESOLVED)");

        return message.toString();
    }

    //----------------------------------------------------------------------

    /** Get quiet property. */

    public boolean isQuiet()
    {
        return quiet;
    }

    //----------------------------------------------------------------------

    /** Look up class in classpath. 
     *
     *  @param className    class name in the form pack1.pack2.name.
     *                      Name may contain wildcards.
     *  @return             true if class found and not in excluded base,
     *                      false otherwise
     */
    
    public boolean lookupClass(String className)
        throws IOException
    {
        if (className.endsWith(".class"))
            className = className.substring(0, className.length() - 6);

        className = className.replace('.', '/') + ".class";

        int     islash = className.lastIndexOf("/");
        String  last = className.substring(islash + 1);

        if (last.indexOf('?') >= 0 || last.indexOf('*') >= 0) 
        {
            /* Class name contains wildcards. '**.class' is handled specially:
             * We need the suffix .class to know that the classpath is to be
             * used, but have to remove it for the recursive include to work.
             */
    
            if (className.endsWith("**.class"))
                className = className.substring(0, className.length() - 6);

            List flist = classpath.lookupPattern(className);

            for (Iterator it = flist.iterator(); it.hasNext(); )
            {
                EFile   efile = (EFile)it.next();
                String  path = efile.getPath();

                if (path.endsWith(".class") && ! doneFiles.contains(path))
                    addClass(efile);
            }

            return true;
        }

        return lookupClassInternal(className);  
    }

    //----------------------------------------------------------------------

    /** Look up class in classpath. 
     *
     *  @param path     class or resource name in the form pack1/pack2/name.class
     *  @return         true if class found and not in excluded base,
     *                  false otherwise
     */
    
    boolean lookupClassInternal(String path)
    {
        if (doneFiles.contains(path))         // already done?
            return true;
        
        EFile classFile = null;
        
        try
        {
            classFile = classpath.lookupFile(path);
            doneFiles.add(path);
            addClass(classFile);
        }
        catch (IOException ex)
        {
            missing.add(path);
            return false;
        }

        return true;
    }

    //----------------------------------------------------------------------
    
    /** Look file in filepath.
     *
     *  @param name path name of the file.
     */

    public void lookupFile(String name)
        throws IOException
    {
        if (doneFiles.contains(name))
            return;

        int     islash = name.lastIndexOf(File.separator);
        String  last = name.substring(islash + 1);

        if (last.indexOf('?') >= 0 || last.indexOf('*') >= 0)
        {
            lookupFilePattern(name);
            return;
        }

        EFile theFile;

        try
        {
            theFile = filepath.lookupFile(name);
        }
        catch (IOException ex)
        {
            missing.add(name);
            return;
        }

        copyToJar(theFile);
    }

    //----------------------------------------------------------------------

    /** Lookup files from a pattern.
     *
     *  @param path  Path with wildcards in name part.
     */

    private void lookupFilePattern(String path)
        throws IOException
    {
        List flist = filepath.lookupPattern(path);

        for (Iterator it = flist.iterator(); it.hasNext(); )
        {
            EFile efile = (EFile)it.next();

            if (! doneFiles.contains(efile.getPath()))
                copyToJar(efile);
        }
    }

    //----------------------------------------------------------------------

    /** Put jar entry in output file, if not yet contained. 
     *
     *  @param entry    The jar entry.
     */

    private boolean putEntry(ZipEntry entry)
        throws IOException
    {
        if (zipEntries.contains(entry.getName()))
            return false;
        
        // Workaround for bug 4682202

        ZipEntry tmpEntry = new ZipEntry(entry);

        tmpEntry.setCompressedSize(-1);
        jarout.putNextEntry(tmpEntry);

        /* Original statement:
           jarout.putNextEntry(entry);
        */

        zipEntries.add(entry.getName());
        return true;
    }
    
    //----------------------------------------------------------------------

    /** Create new jar entry and put it into output file,
     *  if not yet contained.
     */

    private boolean putEntry(String name)
        throws IOException
    {
        return putEntry(new JarEntry(name));
    }

    //----------------------------------------------------------------------

    /** Report genuine dynamic loading. */

    public void reportDynamicLoading()
    {
        if (forNameDyn.size() > 0)
        {
            String message = "Dynamic loading, unknown classname:";
            
            for (Iterator it = forNameDyn.iterator(); it.hasNext(); )
                message += "\n    " + (String)it.next();

            logger.warn(message);
        }

        if (getResourceDyn.size() > 0)
        {
            String message = "Resource access, unknown resource name:";
        
            for (Iterator it = getResourceDyn.iterator(); it.hasNext(); )
                message += "\n    " + (String)it.next();

            logger.warn(message);
        }
    }

    //----------------------------------------------------------------------

    /** Report referenced files that couldn't be found. */
    
    public void reportMissingFiles()
    {
        if (missing.size() == 0 || quiet)
            return;

        String message = "Missing files:";

        for (Iterator it = missing.iterator(); it.hasNext(); )
            message += "\n    " + (String)it.next();

        logger.warn(message);
    }
    
    //----------------------------------------------------------------------

    /** Report unused jar files in classpath. */

    public void reportUnusedPathComponents()
    {
        Set             pathSet = new HashSet(classpath.getList());

        pathSet.addAll(filepath.getList());

        int             n = pathSet.size(),
                        nUnused = 0;
        String[]        unused = new String[n];

        for (Iterator it = pathSet.iterator(); it.hasNext(); )
        {
            EFile   file = (EFile)it.next();
            String  part = file.getBase();

            if (! part.startsWith(JAVA_HOME) && ! usedSources.contains(part))
                unused[nUnused++] = part;
        }

        if (nUnused > 0)
        {
            String message = "Unused path components:";

            Arrays.sort(unused, 0, nUnused);

            for (int i = 0; i < nUnused; ++i)
                message += "\n    " + unused[i];

            logger.info(message);
        }
    }
    
}

