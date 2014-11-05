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

/** Extended File interface. 
 *  @author Bernd Eggink (monoped@users.sourceforge.net)  
 */  

public interface EFile
{
    void            close();
    void            copyFrom(File src) throws IOException, FileNotFoundException;
    boolean         delete();
    boolean         delete(boolean recursive);
    boolean         exists();
    String          getBase();
    byte[]          getBytes() throws IOException, FileNotFoundException;
    String          getName();
    InputStream     getInputStream() throws IOException, FileNotFoundException;
    OutputStream    getOutputStream() throws IOException, FileNotFoundException;
    String          getParent();
    String          getPath();
    String          getAbsolutePath();
    boolean         isDirectory();
    String[]        list();
    String[]        list(String filter);
    Iterator        iterator();
    Iterator        iterator(String filter);
    boolean         mkdirs();
    void            putBytes(byte[] bytes) throws IOException, FileNotFoundException;
    EFile           setName(String name);
    EFile           setPath(String path) throws IOException;
    String          toString();
}

