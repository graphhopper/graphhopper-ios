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

import java.util.*;
import java.util.zip.*;
import java.io.*;
import de.monoped.efile.*;
import de.monoped.utils.*;

/** Class representing a lookup path.
 *  @author Bernd Eggink (monoped@users.sourceforge.net)
 */
  
class FilePath
{
    private List componentList;

    //----------------------------------------------------------------------

    /** Constructor. */

    FilePath()
    {
        componentList = new ArrayList();
    }

    //----------------------------------------------------------------------

    /** Add a path component. 
     *
     *  @param path     Directory or archive.
     */

    void add(String path)
        throws IOException
    {
        StringTokenizer tok = new StringTokenizer(path, File.pathSeparator);
        EFile           comp;
        
        while (tok.hasMoreTokens())
        {
            String el = tok.nextToken();

            if (el.indexOf('*') >= 0 || el.indexOf('?') >= 0)
            {
                // Wildcard, perform expansion
                   
                String[] wlist = FileExpand.getList(null, el);
                
                for (int i = 0; i < wlist.length; ++i)
                    componentList.add(createEntry(wlist[i]));
            }
            else 
            {
                comp = createEntry(el);

                if (comp != null)
                    componentList.add(comp);
            }
        }
    }

    //----------------------------------------------------------------------

    /** Add a list of components.
     *
     *  @param clist    List of components (directories or archives).
     */

    void addList(List clist)
        throws IOException
    {
        for (Iterator it = clist.iterator(); it.hasNext(); )
        {
            EFile efile = createEntry((String)it.next());

            if (efile != null)
                componentList.add(efile);
        }
    }

    //----------------------------------------------------------------------

    /** Create entry from path. */

    private EFile createEntry(String path)
        throws IOException
    {
        File file = new File(path);

        // file may be directory, .jar or .zip

        if (file.exists())
        {
            if (file.isDirectory())
                return new LocalFile(path, "");
            
            return new ZipEntryFile(new ZipFile(file));
        }
        else
            return null;
    }

    //----------------------------------------------------------------------

    /** Get the component list. */

    List getList()
    {
        return componentList;
    }

    //----------------------------------------------------------------------

    /** Scan file path for filename.
     *
     *  @param      name    File name (may be a relative path).
     *  @return     File, wrapped in an EFile object.
     *
     *  @throws     IOException if not found.
     */

    EFile lookupFile(String name)
        throws IOException
    {
        for (Iterator it = componentList.iterator(); it.hasNext(); )
        {
            EFile comp = (EFile)it.next();

            if (comp == null) 
                continue;

            EFile efile = comp.setPath(name);

            if (efile.exists())
                return efile;
        }

        throw new IOException("Couldn't find: " + name);
    }

    //----------------------------------------------------------------------

    /** Scan file path for a pattern.
     *
     *  @param  path    Pattern (relative path with wildcards in the name part only).
     *  @return         List of Efiles found.
     */

    List lookupPattern(String path)
        throws IOException
    {
        File        file = new File(path);
        String      base = file.getParent();
        
        if (base != null) 
            base = base.replace(File.separatorChar, '/');

        String      pattern = file.getName();
        ArrayList   flist = new ArrayList();

        for (Iterator it = componentList.iterator(); it.hasNext(); )
        {
            EFile   comp = ((EFile)it.next());
            
            if (comp == null) 
                continue;

            if (base != null)
                comp = comp.setPath(base);

            for (Iterator pit = comp.iterator(pattern); pit.hasNext(); )
            {
                EFile f = (EFile)pit.next();

                flist.add(f);
            }
        }

        return flist;
    }

    //----------------------------------------------------------------------

    /** Path as an operation system path. */

    String toOSPath()
    {
        String p = "";

        for (Iterator it = componentList.iterator(); it.hasNext(); )
        {
            if (p.length() > 0)
                p += File.pathSeparator;

            p += ((EFile)it.next()).getBase();
        }

        return p;
    }

    //----------------------------------------------------------------------

    public String toString()
    {
        return toOSPath();
    }

}

