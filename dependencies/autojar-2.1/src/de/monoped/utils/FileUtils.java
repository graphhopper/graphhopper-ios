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

public class FileUtils
{
    static public boolean patternMatches(String pattern, String name)
    {
        if (pattern == null)
            return true;
        
        int     iExpr = 0, 
                iPath = 0,
                lenPattern = pattern.length(),
                lenName = name.length();
        boolean ok = false;

loop:
        while (iExpr < lenPattern)
        {
            char charExpr = pattern.charAt(iExpr);

            switch (charExpr)
            {
                case '?':
                {
                    // any character
                    
                    if (iPath >= lenName)
                        break loop;

                    ++iExpr;
                    ++iPath;
                    break;
                }

                case '*':
                {
                    // character sequence 
                    
                    int nqu = 0;
                    
                    // find normal chars in pattern after '*', count '?'s

                    while (++iExpr < lenPattern)
                    {
                        char c = pattern.charAt(iExpr);

                        if (c == '?')
                            ++nqu;
                        else if (c != '*')
                            break;
                    }
                     
                    if (iExpr >= lenPattern)
                    {
                        // at end, no normal chars after *, just check '?'s
                           
                        ok = lenName - iPath >= nqu;
                        break loop;
                    }
                    else
                    {
                        // extract normal part

                        int jex = iExpr;

                        while (++jex < lenPattern)
                        {
                            char c = pattern.charAt(jex);

                            if (c == '*' || c == '?')
                                break;
                        }
                    
                        String  normal = pattern.substring(iExpr, jex);
                        int     ifound = name.indexOf(normal, iPath);

                        if (ifound < 0 || ifound - iPath < nqu)
                        {
                            // not found in name, or too early
                               
                            break loop;
                        }

                        // adjust indices

                        iPath = ifound + normal.length();
                        iExpr = jex;

                        break;
                    }
                }
                
                default:
                {
                    // normal char
                       
                    if (iPath >= lenName || charExpr != name.charAt(iPath))
                        break loop;

                    ++iExpr;
                    ++iPath;
                }
            }
        }

        if (! ok) 
            ok = iPath == lenName;

        return ok;
        
    }

    //----------------------------------------------------------------------

    static public boolean patternMatches(List patterns, String name)
    {
        if (patterns == null)
            return false;

        for (Iterator it = patterns.iterator(); it.hasNext(); )
        {
            String pat = (String)it.next();

            if (patternMatches(pat, name))
                return true;
        }

        return false;
    }
                    
    
}

