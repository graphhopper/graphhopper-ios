package de.monoped.efile;

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

/** Static path methods for EFile. 
 *  @author Bernd Eggink (monoped@users.sourceforge.net)  
 */

public class Utils
{
    public static String normalizePath(String path)
    {
        String[]    comps = path.split("/");
        String[]    ap = new String[comps.length];
        int         j = 0;
        boolean     absolute = comps[0].length() == 0;

        for (int i = 0; i < comps.length; ++i)
        {
            String s = comps[i];

            if (s.equals(".") || s.length() == 0)
                continue;

            if (s.equals(".."))
            {
                if (j > 0)
                    --j;
            }
            else
                ap[j++] = s;
        }
        
        if (j == 0)
            return absolute ? "/" : ".";

        if (absolute)
            path = "/" + ap[0];
        else
            path = ap[0];

        for (int i = 1; i < j; ++i)
            path += "/" + ap[i];

        return path;
    }

    //----------------------------------------------------------------------

    static String getParentPath(String path)
    {
        int k = path.lastIndexOf("/");
        
        if (k == 0)
            return "/";

        if (k > 0)
            return path.substring(0, k);

        return "..";
    }

    //----------------------------------------------------------------------

    static String getPathName(String path)
    {
        int k = path.lastIndexOf("/");

        if (k >= 0)
            return path.substring(k + 1);
        
        return path;
    }

    //----------------------------------------------------------------------

    static String replaceName(String path, String name)
    {
        if (name.startsWith("/"))
            throw new IllegalArgumentException(name);

        int k = path.lastIndexOf("/");

        if (k >= 0)
            path = path.substring(0, k + 1) + name;
        else
            path = name;

        return normalizePath(path);
    }

}

