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
import org.apache.log4j.*;

/** Expand wildcards in file names
 *  @author      Bernd Eggink (monoped@users.sourceforge.net)
 */

public class FileExpand
{
    static Logger       logger = Logger.getLogger(FileExpand.class);

    //----------------------------------------------------------------------

    private FileExpand()
    { }

    //----------------------------------------------------------------------

    /** Get List of files matching a wildcard expression, relative to basedir
     *
     *  @param basedir  Directory part, may be null.
     *  @param expr     relative path, last component may contain wildcards
     *  @return         Array of matching file names
     */

    static public String[] getList(File basedir, String expr)
    {
        try
        {
            File exprFile = new File(expr),
                 dir = new File(basedir, expr);

            dir = dir.getAbsoluteFile().getParentFile();

            if (! dir.exists())
                return null;

            String[]    names = dir.list(new OSFilter(exprFile.getName()));
            String      base = exprFile.getParent();      // Base of expr part, may be null
            
            if (base != null)
            {
                base += File.separator;

                for (int i = 0; i < names.length; ++i)
                    names[i] = base + names[i];
            }

            return names;
        }
        catch (Exception ex)
        {
            logger.fatal(ex);
            return null;
        }
    }
}
