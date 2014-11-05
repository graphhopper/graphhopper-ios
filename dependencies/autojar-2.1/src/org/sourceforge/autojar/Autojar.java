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

import de.monoped.utils.*;
import java.io.*;
import java.util.*;
import org.apache.log4j.*;

/** Autojar main class 
 *  @author Bernd Eggink (monoped@users.sourceforge.net)  
 */

public class Autojar
{
    static Logger               logger = Logger.getLogger(Autojar.class);
    static final String         VERSION = 
                                    "Autojar " + Version.VERSION + ", Copyright (C) 2004 Bernd Eggink\n" +
                                    "http://autojar.sourceforge.net/\n" +
                                    "using BCEL 5.1 (http://jakarta.apache.org/bcel/)\n" +
                                    "Autojar comes with ABSOLUTELY NO WARRANTY.\n" +
                                    "This is free software, and you may redistribute it\n" +
                                    "under the conditions of the GNU General Public License.\n",

                                HELP = 
                                    "Usage:    java -jar autojar.jar [option...] file...\n" +
                                    "Options:  -c classpath    where to look for classes\n" +
                                    "          -b              use classpath for normal files\n" +
                                    "          -C dir          change to dir temporarily\n" +
                                    "          -d              look for dynamic loading\n" +
                                    "          -a              add dynamically loaded classes if possible (implies d)\n" +
                                    "          -A              add resources if possible\n" +
                                    "          -e              search jar files in extension dirs\n" +
                                    "          -h              output help and exit\n" +
                                    "          -m manifest     manifest file\n" +
                                    "          -o outfile      output file (mandatory)\n" +
                                    "          -p filepath     where to look for non-class files\n" +
                                    "          -q              be quiet\n" +
                                    "          -v              be verbose\n" +
                                    "          -V              output version information and exit\n" +
                                    "          -x prefix       skip bases starting with prefix\n" +
                                    "Files:    name            class name\n" +
                                    "          path            file or directory path\n" +
                                    "          -C dir          change to dir temporarily\n" +
                                    "          -X pattern      skip files matching pattern\n" +
                                    "          -X -            clear pattern list";
        static int                  level = Ajar.STD_LEVEL;

    //----------------------------------------------------------------------

    /** Split path by pathSeparator and add results to list.
     *
     *  @param path         Holds path fragments 
     *  @param list         List to which append.
     */

    private static void addPathComponents(String path, List list)
    {
        String[] comps = path.split(File.pathSeparator);

        for (int i = 0; i < comps.length; ++i)
        {
            String comp = comps[i];

            if (comp.indexOf('*') >= 0 || comp.indexOf('?') >= 0)
            {
                // Wildcard, perform expansion
                   
                String[] wlist = FileExpand.getList(null, comp);

                if (wlist == null)
                    logger.warn("Directory " + path + " does not exist");
                else
                    for (int j = 0; j < wlist.length; ++j)
                        list.add(wlist[j]);
            }
            else
                list.add(comp);
        }
    }

    //----------------------------------------------------------------------

    /** Output exception. */

    static void error(Exception ex)
    {
        if (level == Ajar.DEBUG_LEVEL)
            logger.fatal(ex.getMessage(), ex);
        else
            logger.fatal(ex);

        ex.printStackTrace();
    }

    //----------------------------------------------------------------------

    /** Output message and exit. */

    static void abort(String msg)
    {
        logger.fatal(msg);
        System.exit(1);
    }

    //----------------------------------------------------------------------

    /** Output exception and exit. */

    static void abort(Exception ex)
    {
        error(ex);
        System.exit(1);
    }

    //----------------------------------------------------------------------

    /** Main class. Creates an Ajar object and processes program options and file parameters. */

    static public void main(String[] args)
    {
        String      basedirName = "",
                    outfileName = null;
        boolean     bothpaths = false,
                    searchExtensions = false,
                    dynresource = false;
        ArrayList   excludes = new ArrayList(),
                    classPathList = new ArrayList(),
                    filePathList = null;
        int         dynamic = Ajar.DYN_OFF;
        File        basedir = null, 
                    manifest = null, 
                    outfile = null;
        Ajar        autojar = null;
        Getopt      opts = null;

        // Parse options

        try
        {
            opts = new Getopt(args, "aAbc:dDehm:o:p:qvVx:");
        }
        catch (IOException ex)
        {
            abort(ex);
        }

        int     opt;
        
        while ((opt = opts.getOption()) >= 0)
        {
            switch (opt)
            {
                case 'a':   // add dynamically loaded classes automatically
                            
                            dynamic = Ajar.DYN_AUTO;
                            break;
                            
                case 'A':   // add dynamically loaded resources (by ClassLoader.getResource() etc.) automatically
    
                            dynresource = true;

                            if (dynamic == Ajar.DYN_OFF)
                                dynamic = Ajar.DYN_WARN;

                            break;

                case 'b':   // use class path for normal files
                            
                            bothpaths = true;
                            break;
                            
                case 'c':   // add to classpath
                            
                            addPathComponents(opts.getOptarg(), classPathList);
                            break;

                case 'd':   // dynamic loading

                            dynamic = Ajar.DYN_WARN;
                            break;

                case 'D':   // debug mode, print stack trace in case of exceptions
                            
                            level = Ajar.DEBUG_LEVEL;
                            break;

                case 'e':   // search extension

                            searchExtensions = true;
                            break;

                case 'h':   // print help
                    
                            logger.warn(VERSION);
                            logger.warn(HELP);
                            System.exit(0);
                            break;
                            
                case 'm':   // manifest file
                            
                            manifest = new File(opts.getOptarg());
                            break;

                case 'o':   // output file
                            
                            outfileName = opts.getOptarg();
                            break;

                case 'p':   // add to file path
                            
                            if (filePathList == null)
                                filePathList = new ArrayList();

                            addPathComponents(opts.getOptarg(), filePathList);
                            break;

                case 'q':   // be quiet

                            level = Ajar.QUIET_LEVEL;
                            break;

                case 'v':   // verbose mode
                            
                            level = Ajar.VERBOSE_LEVEL;
                            break;

                case 'V':   // Version information

                            logger.warn(VERSION);
                            System.exit(0);
                            break;

                case 'x':   // exclude
                            
                            excludes.add(opts.getOptarg());
                            break;

                default:    // wrong option
                            
                            logger.warn(HELP);
                            System.exit(1);
            }
        }

        // Output file must be present

        if (outfileName == null)
            abort("Output file missing");

        outfile = new File(outfileName);

        // show version information

        logger.info(Autojar.VERSION);

        try
        {
            // Create an Ajar object

            autojar = new Ajar(outfile, classPathList, excludes, filePathList, 
                    manifest, dynamic, dynresource, searchExtensions, bothpaths, level);
        }
        catch (IOException ex)
        {
            logger.fatal(ex.getMessage(), ex);
            System.exit(1);
        }

        // Process file parameters. 
           
        String[] files = opts.getParms();

        if (files.length == 0 && autojar.getMainClass() == null)
            abort("No input files");

        try
        {
            boolean haveBasedir = false,
                    haveExclude = false,
                    haveNotSource = false;

            for (int i = 0; i < files.length; ++i)
            {
                String name = files[i];
                
                // handle pseudo options

                if (name.equals("-C"))
                {
                    // basedir pseudo option

                    haveBasedir = true;   
                    haveExclude = false;
                    continue;
                }

                if (name.equals("-X"))
                {
                    // exclude pseudo option

                    haveExclude = true;   
                    haveBasedir = false;   
                    continue;
                }

                if (name.equals("-X-"))
                {
                    // clear excludes pseudo option

                    autojar.clearFileExcludes();
                    haveExclude = false;   
                    haveBasedir = false;   
                    continue;
                }

                if (name.equals("-Y"))
                {
                    // don't copy source classes

                    haveNotSource = true;
                    haveExclude = false;
                    continue;
                }

                // not a pseudo option

                if (haveBasedir)
                {
                    // the basedir 

                    basedirName = files[i];
                    basedir = new File(basedirName); // .getCanonicalFile();
                    logger.info("Entering directory '" + basedirName + "'");
                    haveBasedir = false;
                    continue;
                }

                // check if excluded 

                if (haveExclude)
                {
                    if (files[i].equals("-"))
                        autojar.clearFileExcludes();
                    else
                        autojar.addFileExcludes(files[i]);

                    haveExclude = false;
                    continue;
                }

                logger.info("Argument: " + name);
                
                boolean hasWildcards = name.indexOf('*') >= 0 || name.indexOf('?') >= 0,
                        isClass = ! name.startsWith(File.separator) && name.endsWith(".class");

                if (name.equals("."))
                {
                    // treat "." like "*" 
                       
                    if (basedir == null)        // Hm...
                        basedir = new File(System.getProperty("user.dir"));

                    autojar.handleFileList(basedir, basedir.list());
                }
                else if (isClass)
                {
                    // lookup class path

                    autojar.lookupClass(name.substring(0, name.length() - 6));  
                }
                else 
                {
                    // normal file or pattern

                    File file = new File(basedir, name);

                    if (! hasWildcards && file.exists())
                    {
                        // not a pattern and file exists: include without lookup

                        autojar.handleFile(basedir, name, ! haveNotSource);
                    }
                    else if (hasWildcards)
                    {
                        // expand wildcards
                    
                        String[] list = FileExpand.getList(basedir, name);

                        if (list != null && list.length > 0)
                        {
                            // some files match, include the list

                            autojar.handleFileList(basedir, list);
                        }
                        else
                        {
                            // no match, lookup if not behind -C

                            if (basedir == null)
                                autojar.lookupFile(name);
                            else
                                autojar.addMissing(file.getPath());
                        }
                    }
                    else
                    {
                        // Not a pattern, lookup if relative

                        if (file.isAbsolute())
                            autojar.addMissing(file.getPath());
                        else
                            autojar.lookupFile(name);
                    }
                }

                if (basedir != null)
                    logger.info("Leaving directory '" + basedirName + "'");

                basedir = null;
                haveNotSource = false;
            }
        }
        catch (Exception ex)
        {
            error(ex);
        }
        finally
        {
            autojar.close();
        }

        autojar.reportUnusedPathComponents();
        autojar.reportDynamicLoading();
        autojar.reportMissingFiles();
    }
}



