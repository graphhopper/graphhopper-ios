package de.monoped.utils;

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

/** File filter for names with wildcards
 *  @author      Bernd Eggink (monoped@users.sourceforge.net)
 */

public class OSFilter
    implements FilenameFilter
{
    private String      expr;
    private ArrayList   excludes;

    //----------------------------------------------------------------------

    /** Constructor
     *
     *  @param expr File path  containing wildcards
     */

    public OSFilter(String expr)
    {
        this.expr = expr;
    }

    //----------------------------------------------------------------------

    public void setExcludes(ArrayList excludes)
    {
        this.excludes = excludes;
    }
    
    //----------------------------------------------------------------------

    public boolean accept(File dir, String path)
    {
        boolean ok = FileUtils.patternMatches(expr, path);

        if (! ok)
            return false;

        if (excludes != null)
            for (Iterator it = excludes.iterator(); it.hasNext(); )
                if (FileUtils.patternMatches((String)it.next(), path))
                    return false;

        return true;
    }
}

