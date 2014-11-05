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

package de.monoped.efile;

import de.monoped.utils.*;
import java.io.*;
import java.util.*;
import java.util.zip.*;

/** An EFile representing Zip Entries. 
 *  @author Bernd Eggink (monoped@users.sourceforge.net)  
 */

public class ZipEntryFile
    implements EFile, Cloneable
{
    private ZipFile     zip;
    private ZipNode     rootNode, node;
    private String      path;

    //----------------------------------------------------------------------

    public ZipEntryFile(ZipFile zip)
    {
        this(zip, "/");
    }

    //----------------------------------------------------------------------

    public ZipEntryFile(ZipFile zip, String path)
    {
        this.zip = zip;
        this.path = path;

        rootNode = new ZipNode(null, null, "");

        // build tree

        for (Enumeration entries = zip.entries(); entries.hasMoreElements(); )
        {
            ZipEntry    entry = (ZipEntry)entries.nextElement();
            String[]    comps = entry.getName().split("/");
            Node        base = rootNode;
            int         icomp;

            boolean found = false;

            for (icomp = 0; icomp < comps.length; ++icomp)
            {
                // Find path component in current base's entries

                String comp = comps[icomp];

                found = false;

                for (Iterator it = base.iterator(); it.hasNext(); )
                {
                    Node    child = (Node)it.next(); 
                    String  childName = child.getName();

                    if (childName.equals(comp))
                    {
                        base = child;
                        found = true;
                        break;
                    }
                }

                if (! found)
                    break;
            }

            if (! found)
            {
                // Not yet present, add dir parts

                for (; icomp < comps.length - 1; ++icomp)
                    base = base.addChild(new ZipNode(null, base, comps[icomp]));

                // and file part

                base.addChild(new ZipNode(entry, base, comps[comps.length - 1]));
            }
        }

        node = getNode();
    }

    //----------------------------------------------------------------------

    public Object clone()
    { 
        Object  f = null;

        try
        {
            f = super.clone();
            node = getNode();
        }
        catch (CloneNotSupportedException ex)
        { }
        
        return f;
    }

    //----------------------------------------------------------------------

    public void close()
    { }

    //----------------------------------------------------------------------

    public void copyFrom(File src) 
        throws IOException, FileNotFoundException
    {
        throw new IOException(zip.getName() + ": can't write");
    }

    //----------------------------------------------------------------------

    public boolean delete()
    {
        return false;
    }

    //----------------------------------------------------------------------

    public boolean delete(boolean recursive)
    {
        return false;
    }

    //----------------------------------------------------------------------

    public boolean equals(Object x)
    {
        return (x instanceof ZipEntryFile) && zip.getName().equals(((ZipEntryFile)x).zip.getName());
    }

    //----------------------------------------------------------------------

    public boolean exists()
    {
        return getNode() != null;
    }

    //----------------------------------------------------------------------

    public String getBase()
    {
        return zip.getName();
    }

    //----------------------------------------------------------------------

    public String getAbsolutePath()
    {
        return "/" + path;
    }

    //----------------------------------------------------------------------

    public byte[] getBytes()
        throws IOException
    {
        InputStream             in = getInputStream();
        ByteArrayOutputStream   out = new ByteArrayOutputStream();
        byte[]                  buf = new byte[2048];
        int                     n;

        while ((n = in.read(buf)) >= 0)
            out.write(buf, 0, n);

        in.close();
        return out.toByteArray();
    }

    //----------------------------------------------------------------------

    public InputStream getInputStream()
        throws IOException, FileNotFoundException
    {
        if (node == null)
            throw new FileNotFoundException(path + " (No such file or directory)");

        if (node.isDirectory())
            throw new FileNotFoundException(path + " (Is a directory)");

        return zip.getInputStream(node.getEntry());
    }

    //----------------------------------------------------------------------

    public String getName()
    {
        return Utils.getPathName(path);
    }

    //----------------------------------------------------------------------

    private ZipNode getNode(String path)
    {
        String[]    components = path.split("/");
        ZipNode     dir = rootNode;

        if (components.length == 0)
            return rootNode;

        for (int i = 0; i < components.length; ++i)
        {
            String comp = components[i];

            if (comp.length() > 0)
            {
                ZipNode child = (ZipNode)dir.getChild(comp);

                if (child == null)
                    return null;
                
                dir = child;
            }
        }

        return dir;
    }

    //----------------------------------------------------------------------

    private ZipNode getNode()
    {
        return getNode(path);
    }

    //----------------------------------------------------------------------

    public OutputStream getOutputStream()
         throws IOException, FileNotFoundException
    {
        throw new IOException(zip.getName() + ": can't write");
    }

    //----------------------------------------------------------------------

    public String getParent()
    {
        return Utils.getParentPath(path);
    }

    //----------------------------------------------------------------------

    public String getPath()
    {
        return path;
    }

    //----------------------------------------------------------------------

    public int hashCode()
    {
        return (zip.getName() + path).hashCode();
    }

    //----------------------------------------------------------------------

    public boolean isDirectory()
    {
        return node != null && node.isDirectory();
    }

    //----------------------------------------------------------------------

    public Iterator iterator()
    {
        return new ZipEntryFileIterator(list());
    }

    //----------------------------------------------------------------------

    public Iterator iterator(String filter)
    {
        return new ZipEntryFileIterator(list(filter));
    }

    //----------------------------------------------------------------------

    public String[] list()
    {
        if (node == null)
            return null;

        int         n = node.children.size();
        String[]    names = new String[n];
        int         i = 0;

        for (Iterator it = node.iterator(); it.hasNext(); )
        {
            Node nod = (Node)it.next();

            names[i++] = nod.getName();
        }

        return names;
    }

    //----------------------------------------------------------------------

    public String[] list(String filter)
    {
        if (node == null)
            return null;

        ArrayList   names = new ArrayList();

        if (filter.equals("**"))
            return (String[])node.treeList().toArray(new String[0]);
        
        OSFilter    osfilter = new OSFilter(filter.replace(File.separatorChar, '/'));
        File        parent = new File(Utils.getParentPath(path));

        for (Iterator it = node.iterator(); it.hasNext(); )
        {
            Node child = (Node)it.next();

            if (osfilter.accept(parent, child.getName()))
                names.add(child.getName());
        }

        return (String[])names.toArray(new String[0]);
    }

    //----------------------------------------------------------------------

    public boolean mkdirs()
    {
        return false;   
    }

    //----------------------------------------------------------------------

    public void putBytes(byte[] bytes)
         throws IOException, FileNotFoundException
    {
        throw new IOException(path + ": can't write");
    }

    //----------------------------------------------------------------------

    public EFile setName(String name)
    {
        if (name.indexOf("/") >= 0)
            throw new IllegalArgumentException(name);

        return new ZipEntryFile(zip, Utils.replaceName(path, name));
    }

    //----------------------------------------------------------------------

    public EFile setPath(String path)
        throws IOException
    {
        ZipEntryFile newfile = (ZipEntryFile)clone();

        newfile.path = path;
        newfile.node = getNode(path);
        return newfile;
    }

    //----------------------------------------------------------------------

    public String toString()
    {
        return "ZIP (" + zip.getName() + ") " + path;
    }

    //----------------------------------------------------------------------

    class ZipEntryFileIterator
        implements Iterator
    {
        String[]    list;
        int         i;

        //----------------------------------------------------------------------

        ZipEntryFileIterator(String[] list)
        {
            i = 0;
            this.list = list;
        }

        //----------------------------------------------------------------------

        public boolean hasNext()
        {
            return list != null && i < list.length;
        }

        //----------------------------------------------------------------------

        public Object next()
            throws NoSuchElementException
        {
            if (list == null || i >= list.length)
                throw new NoSuchElementException(String.valueOf(i));

            try
            {
                return setPath((path.equals("/") ? path : path + "/") + list[i++]);
            }
            catch (IOException ex)
            {
                return null;
            }
        }

        //----------------------------------------------------------------------

        public void remove()
            throws UnsupportedOperationException
        {
            throw new UnsupportedOperationException();
        }
    }

}

