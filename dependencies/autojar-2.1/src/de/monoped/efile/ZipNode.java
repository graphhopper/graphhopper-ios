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

import java.util.zip.*;

/** A node representing a zip entry. 
 *  @author Bernd Eggink (monoped@users.sourceforge.net)  
 */

class ZipNode
    extends Node
{
    private ZipEntry entry;

    //----------------------------------------------------------------------

    ZipNode(ZipEntry entry, Node parent, String name)
    {
        super(parent, name);
        this.entry = entry;
    }

    //----------------------------------------------------------------------

    ZipEntry getEntry()
    {
        return entry;
    }

    //----------------------------------------------------------------------

    boolean isDirectory()
    {
        return entry != null && entry.isDirectory() || super.isDirectory();
    }

}

