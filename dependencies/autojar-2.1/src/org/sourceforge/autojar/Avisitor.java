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

import java.io.*;
import java.util.*;
import org.apache.bcel.*;
import org.apache.bcel.classfile.*;
import org.apache.bcel.generic.*;
import org.apache.bcel.util.*;
import org.apache.log4j.*;
import org.sourceforge.autojar.*;

/** Visitor, handles ConstantClass entries 
 * @author      Bernd Eggink, (monoped@users.sourceforge.net)
 */

class Avisitor
    extends org.apache.bcel.classfile.EmptyVisitor
{
    static Logger       logger = Logger.getLogger(Avisitor.class);

    private JavaClass   klass;
    private Ajar        ajar;
    
    private ConstantMethodref   refClassForName,
                                refClassGetResource, refClassGetResourceAsStream,
                                refLoaderGetResource, refLoaderGetResourceAsStream;

    //----------------------------------------------------------------------

    /** @param klass JavaClass object containing the code */
    
    Avisitor(Ajar ajar, JavaClass klass)
    {
        this.ajar = ajar;
        this.klass = klass;
    }

    //----------------------------------------------------------------------

    /** Get constant pool ref of Class.forName */

    public ConstantMethodref getRefClassForName()
    {
        return refClassForName;
    }

    //----------------------------------------------------------------------

    /** Get constant pool ref of Class.getResource */

    public ConstantMethodref getRefClassGetResource()
    {
        return refClassGetResource;
    }

    //----------------------------------------------------------------------

    /** Get constant pool ref of Class.getResourceAsStream */

    public ConstantMethodref getRefClassGetResourceAsStream()
    {
        return refClassGetResourceAsStream;
    }

    //----------------------------------------------------------------------

    /** Get constant pool ref of ClassLoader.getResource */

    public ConstantMethodref getRefLoaderGetResource()
    {
        return refLoaderGetResource;
    }

    //----------------------------------------------------------------------

    /** Get constant pool ref of ClassLoader.getResourceAsStream */

    public ConstantMethodref getRefLoaderGetResourceAsStream()
    {
        return refLoaderGetResourceAsStream;
    }

    //----------------------------------------------------------------------

    boolean hasClassReferences()
    {
        return refClassForName != null 
            || refClassGetResource != null
            || refClassGetResourceAsStream != null 
            || refLoaderGetResource != null
            || refLoaderGetResourceAsStream != null; 
    }

    //----------------------------------------------------------------------

    /** Class name in constant pool: Try to find class file and add it */

    public void visitConstantClass(ConstantClass cc)
    {
        String cstr = klass.getConstantPool().getConstant(cc.getNameIndex()).toString();

        int     ia = cstr.indexOf('"'),
                ie = cstr.lastIndexOf('"');
        String  name = cstr.substring(ia + 1, ie);
        
        // skip arrays
           
        if (name.startsWith("["))
            return;

        try
        {
            ajar.lookupClass(name);
        }
        catch (IOException ex)
        {
            logger.fatal(ex.getMessage());
        }
    }

    //----------------------------------------------------------------------

    /** Method reference in constant pool: Check if Class.forName(), getResource() etc.,
     *  try to resolve if necessary.
     */

    public void visitConstantMethodref(ConstantMethodref ref)
    {
        ConstantPool    pool = klass.getConstantPool();
        String          cstr = ref.getClass(pool);          // Class to which method belongs
        int             iname = ref.getNameAndTypeIndex();  // Pool index of method name and type
        String          name = ((ConstantNameAndType)pool.getConstant(iname)).getName(pool);    // method name

        if (cstr.equals("java.lang.Class"))
        {
            if (name.equals("forName"))
                refClassForName = ref;                      // save index of Class.forName()
            else if (name.equals("getResource"))
                refClassGetResource = ref;                  // Class.getResource
            else if (name.equals("getResourceAsStream"))
                refClassGetResourceAsStream = ref;          // Class.getResourceAsStream
        }
        else if (cstr.equals("java.lang.ClassLoader"))
        {
            if (name.equals("getResource"))
                refLoaderGetResource = ref;                 // ClassLoader.getResource
            else if (name.equals("getResourceAsStream"))    
                refLoaderGetResourceAsStream = ref;         // ClassLoader.getResourceAsStream
        }
    }

}


