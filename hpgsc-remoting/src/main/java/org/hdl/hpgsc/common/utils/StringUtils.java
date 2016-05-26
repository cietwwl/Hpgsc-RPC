package org.hdl.hpgsc.common.utils;

import java.io.PrintWriter;

import org.hdl.hpgsc.common.io.UnsafeStringWriter;

/**
 * 
 * @author qiuhd
 * @since  2014-8-1
 * @version V1.0.0
 */
public final class StringUtils {

	/**
	 * is empty string.
	 * 
	 * @param str source string.
	 * @return is empty.
	 */
	public static boolean isEmpty(String str){
		if( str == null || str.length() == 0 )
			return true;
		return false;
	}

	/**
	 * is not empty string.
	 * 
	 * @param str source string.
	 * @return is not empty.
	 */
    public static boolean isNotEmpty(String str){
        return str != null && str.length() > 0;
    }
    
    /**
     * 
     * @param e
     * @return string
     */
    public static String toString(Throwable e) {
    	UnsafeStringWriter w = new UnsafeStringWriter();
        PrintWriter p = new PrintWriter(w);
        p.print(e.getClass().getName());
        if (e.getMessage() != null) {
            p.print(": " + e.getMessage());
        }
        p.println();
        try {
            e.printStackTrace(p);
            return w.toString();
        } finally {
            p.close();
        }
    }
	
	private StringUtils() {} ;
}

