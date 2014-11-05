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

/** Handles program arguments like Unix getopt(). Example: <pre>
 *  void main(String[] argv)
 *  {
 *      int     opt;
 *      boolean x = false;
 *      Getopt  getopt = new Getopt(argv, "a:hx");
 *      String  argA = "";
 *
 *      // get Options
 *      
 *      while ((opt = getopt.getOption()) != -1)
 *      {
 *          switch (opt)
 *          {
 *              case 'a':   argA = getopt.getOptarg();
 *                          break;
 *              case 'h':   help();
 *                          break;
 *              case 'x':   x = true;
 *                          break;
 *              default:    System.out.println("wrong option: " + getOptopt());
 *          }
 *      }
 *      
 *      // handle non-option parameters
 *
 *      String[]   files = getopt.getParms();
 *      
 *      for (int i = 0; i < files.length; ++i)
 *          doSomethingWith(files[i]);
 *  }
 *
 *  Legal calls:
 *      java Mainclass -hx file1 file2
 *      java Mainclass -xa blurp -h -- file3
 *      
 *  Illegal calls:      
 *      java Mainclass -y f
 *      java Mainclass -a
 *
 *  The special argument -- denotes the end of the options. All Arguments 
 *  after this will be considered as non-options, even if starting with -.
 *
 *  If any command line argument is of the form "@filename", this file
 *  is read and each line is considered a program parameter.
 *  
 *  </pre>    
 *
 *  @author Bernd Eggink (monoped@users.sourceforge.net)
 */

public class Getopt
{
    private String[]    argv;
    private ArrayList   arglist;
    private String      optarg, opts;
    private int         ichr = 0, optind = 0;
    private char        optopt;
    private boolean     opterr = true;

    //----------------------------------------------------------------------

    /** Default constructor 
     *
     * @param opts      The possible options.
     */

    private Getopt(String opts)
    {
        this.opts = opts;
        arglist = new ArrayList();
    }
    
    //----------------------------------------------------------------------

    /** Constructs a Getopt object by reading all arguments from a file.
     * @param filename  Name of the file to read.
     * @param opts      The possible options.
     * @param opterr    If true, an error message will be printed if an illegal option character
     *                  is found.
     */

    public Getopt(String filename, String opts, boolean opterr)
        throws IOException
    {
        this(filename, opts);
        this.opterr = opterr;
    }

    //----------------------------------------------------------------------

    /** Like Getopt(filename, opts, true) */

    public Getopt(String filename, String opts)
        throws IOException
    {
        this(opts);
        addArgsFromFile(filename);
    }

    //----------------------------------------------------------------------

    /** Constructs a Getopt object using the main() parameter list.
     * @param argv      The arguments of main() 
     * @param opts      The possible options. Each option is a single character.
     *                  If followed by a colon, the option given in the command line
     *                  must be followed by an argument. 
     * @param opterr    If true, an error message will be printed if an illegal option character
     *                  is encountered.
     */

    public Getopt(String[] argv, String opts, boolean opterr)
        throws IOException
    {
        this(argv, opts);
        this.opterr = opterr;
    }

    //----------------------------------------------------------------------

    /** Like Getopt(argv, opts, true). */

    public Getopt(String[] argv, String opts)
        throws IOException
    {
        this(opts);

        for (int i = 0; i < argv.length; ++i)
        {
            String arg = argv[i];

            if (arg.startsWith("@"))
                addArgsFromFile(arg.substring(1));
            else
                arglist.add(arg);
        }
        
        this.argv = (String[])arglist.toArray(new String[0]);
    }

    //----------------------------------------------------------------------

    private void addArgsFromFile(String name)
        throws IOException
    {
        BufferedReader  reader = new BufferedReader(new FileReader(new File(name)));
        String          zeile;

        while ((zeile = reader.readLine()) != null)
            arglist.add(zeile);

        reader.close();
    }

    //----------------------------------------------------------------------

    /** Returns the current argument or null. */

    public String  getOptarg()
    {
        return optarg;
    }

    //----------------------------------------------------------------------

    /** Returns the next option as int value,
     *  -1 if no more options are available, '?' if the option is illegal.
     */

    public int getOption()
    {
        char    c;
        int     iopt;
        
        if (ichr == 0)
        {
            // beginning of word

            if (optind >= argv.length || argv[optind].charAt(0) != '-')
                return -1;
            
            if (argv[optind].equals("-") || argv[optind].equals("--"))
            {
                ++optind;
                return -1;
            }
        }

        // had -
           
        c = argv[optind].charAt(++ichr);

        if (c == ':' || (iopt = opts.indexOf(c)) < 0)
        {
            if (opterr)
                System.err.println("+++ Illegal option: " + c);

            if (++ichr >= argv[optind].length())
            {
                ++optind; 
                ichr = 0;
            }

            optopt = c;
            optarg = null;
            return '?';
        }

        if (iopt + 1 < opts.length() && opts.charAt(iopt + 1) == ':')
        {
            // must have optarg

            if (++ichr < argv[optind].length())
                optarg = argv[optind++].substring(ichr);
            else if (++optind >= argv.length)
            {
                if (opterr)
                    System.err.println("+++ Option " + c + " requires an argument");
        
                ichr = 0;
                optopt = c;
                return '?';
            }
            else
                optarg = argv[optind++];

            ichr = 0;
        }
        else
        {
            // no optarg
               
            if (ichr + 1 >= argv[optind].length())
            {
                ++optind;
                ichr = 0;
            }

            optarg = null;
        }

        return c;
    }

    //----------------------------------------------------------------------

    /** Returns the unrecognized option character. */
    
    public char getOptopt()
    {
        return optopt;
    }

    //----------------------------------------------------------------------

    /** Returns parameters not handled by getOption() as array of Strings. */
    
    public String[] getParms()
    {
        String[] parms = new String[argv.length - optind];

        for (int i = 0; optind + i < argv.length; ++i)
            parms[i] = argv[optind + i];

        return parms;
    }

    //----------------------------------------------------------------------

/*
    static public void main(String[] args)
    {
        try
        {
            Getopt g = new Getopt(args, "ab:c:d:e");

            int opt;
            
            while ((opt = g.getOption()) >= 0)
                System.out.println("opt = " + (char)opt + ", optarg = " + g.getOptarg());

            String[] words = g.getParms();

            for (int i = 0; i < words.length; ++i)
                System.out.println(words[i]);
        }
        catch (IOException ex)
        {
            System.err.println(ex);
        }
    }
*/

}

