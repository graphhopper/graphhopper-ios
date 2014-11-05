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

import java.io.*;
import java.util.*;
import de.monoped.utils.*;

public class LocalFile
    implements EFile
{
    private File    file;
    private String  root, path;

    //----------------------------------------------------------------------

    public LocalFile(String root, String path)
    {
        this.root = root;
        this.path = path;
        file = new File(root, path);
    }

    //----------------------------------------------------------------------

    public LocalFile(String path)
    {
        this(File.separator, path);
    }

    //----------------------------------------------------------------------

    public void close()
    { }

    //----------------------------------------------------------------------

    public void copyFrom(File src)
        throws IOException, FileNotFoundException
    {
        OutputStream    out = getOutputStream();
        InputStream     in = new FileInputStream(src);
        byte[]          buf = new byte[4096];
        int             n;

        while ((n = in.read(buf)) >= 0)
            out.write(buf, 0, n);

        in.close();
        out.close();
    }

    //----------------------------------------------------------------------

    public boolean delete()
    {
        return file.delete();
    }

    //----------------------------------------------------------------------

    public boolean delete(boolean recursive)
    {
        if (! recursive || ! file.isDirectory())
            return file.delete();

        return deleteDir(file);
    }

    //----------------------------------------------------------------------

    private boolean deleteDir(File dir)
    {
        File[]  files = dir.listFiles();

        for (int i = 0; i < files.length; ++i)
        {
            File f = files[i];

            if (f.isDirectory())
            {
                if (! deleteDir(f))
                    return false;
            }
            else if (! f.delete())
                return false;
        }

        dir.delete();
        return true;
    }

    //----------------------------------------------------------------------

    public boolean exists()
    {
        return file.exists();
    }

    //----------------------------------------------------------------------

    public boolean equals(Object x)
    {
        return (x instanceof LocalFile) && file.equals(((LocalFile)x).file);
    }

    //----------------------------------------------------------------------

    public String getBase()
    {
        return root;
    }

    //----------------------------------------------------------------------

    public int hashCode()
    {
        return file.hashCode();
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
        return new FileInputStream(file);
    }

    //----------------------------------------------------------------------

    public String getName()
    {
        return file.getName();
    }

    //----------------------------------------------------------------------

    public String getParent()
    {
        return file.getParent();
    }

    //----------------------------------------------------------------------

    public String getAbsolutePath()
    {
        return file.getAbsolutePath();
    }

    //----------------------------------------------------------------------

    public String getPath()
    {
        String p = file.getPath();

        return p.substring(root.length() + 1);
    }

    //----------------------------------------------------------------------

    public OutputStream getOutputStream()
         throws IOException, FileNotFoundException
    {
        return new FileOutputStream(file);
    }

    //----------------------------------------------------------------------

    public boolean isDirectory()
    {
        return file.isDirectory();
    }

    //----------------------------------------------------------------------

    public String[] list()
    {
        return file.list();
    }

    //----------------------------------------------------------------------

    public String[] list(String filter)
    {
        if (filter.startsWith("**"))
        {
            ArrayList list = new ArrayList();

            treeList(file, null, list);
            return (String[])list.toArray(new String[0]);
        }

        return file.list(new OSFilter(filter));
    }

    //----------------------------------------------------------------------

    public Iterator iterator()
    {
        return new LocalFileIterator(file.list());
    }

    //----------------------------------------------------------------------

    public Iterator iterator(String filter)
    {
        if (filter.equals("**"))
        {
            ArrayList list = new ArrayList();

            treeList(file, null, list);
            return list.iterator();
        }

        return new LocalFileIterator(file.list(new OSFilter(filter)));
    }

    //----------------------------------------------------------------------

    public boolean mkdirs()
    {
        return file.mkdirs();
    }

    //----------------------------------------------------------------------

    public void putBytes(byte[] bytes)
        throws IOException, FileNotFoundException
    {
        OutputStream out = getOutputStream();
        
        out.write(bytes);
        out.close();
    }

    //----------------------------------------------------------------------

    public EFile setName(String name)
    {
        if (name.indexOf(File.separator) >= 0)
            throw new IllegalArgumentException(name);

        return new LocalFile(root, path + File.separator + name);
    }

    //----------------------------------------------------------------------

    public EFile setPath(String path)
        throws IOException
    {
        return new LocalFile(root, path);
    }

    //----------------------------------------------------------------------

    public String toString()
    {
        return "LOCAL (" + root + ") " + file.getAbsolutePath();
    }

    //----------------------------------------------------------------------

    private void treeList(File file, String prefix, List list)
    {
        if (! file.exists())
            return;

        String name = file.getName();

        if (file.isDirectory())
        {
            if (prefix != null)
                prefix += name + File.separator;
            else 
                prefix = getPath() + File.separator;
       
            File[] files = file.listFiles();

            if (files != null)
                for (int i = 0; i < files.length; ++i)
                    treeList(files[i], prefix, list);
        }
        else
            list.add(new LocalFile(root, prefix != null ? prefix + name : name));
    }

    //----------------------------------------------------------------------
    //----------------------------------------------------------------------

    class LocalFileIterator
        implements Iterator
    {
        private int         i;
        private String[]    list;

        //----------------------------------------------------------------------

        LocalFileIterator(String[] list)
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

            return setName(list[i++]);
        }

        //----------------------------------------------------------------------

        public void remove()
            throws UnsupportedOperationException
        {
            throw new UnsupportedOperationException();
        }

    }
}

