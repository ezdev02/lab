/*
 * ============================================================================
 * GNU Lesser General Public License
 * ============================================================================
 *
 * deer-cmpp  - Free Java cmpp library.
 * http://deer-cmpp.sourceforge.net
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307, USA.
 * 
 */

package rockd.jkj.testcmpp.api.util;

import org.apache.commons.codec.binary.Hex;
import org.apache.mina.common.ByteBuffer;

/**
 * 
 * @author luomingjie (luomingjie@users.sourceforge.net ; luyiluo@126.com)
 * @version $Id: ByteUtil.java,v 0.2 2007/05/15 13:45:29 
 */
public class ByteUtil
{
	// get 4 byte from byte[] and convert it to integer.
	public static int byte2Int(byte[] bt, int idx)
	{
		int result = bt[idx] * 256 * 256 * 256 + bt[idx + 1] * 256 * 256 + bt[idx + 2] * 256 + bt[idx];
		return result;
	}


	/**
	 * Create an array with 0 filled.
	 * 
	 * @param length
	 *            the length of the array returned.
	 * @return
	 */
	public static byte[] intArray(int length)
	{
		byte[] bt = new byte[length];
		for ( int i = 0; i < length; i++ )
		{
			bt[i] = 0;
		}
		return bt;
	}


	/**
	 * The ByteBuffer's initial position shou
	 * 
	 * @param bb
	 * @return
	 */
	public static String toHexForLog(ByteBuffer bb)
	{

		int originPos = bb.position();
		bb.position( 0 );

		byte[] arr = new byte[bb.limit()];
		bb.get( arr );
		String str = toHexForLog( arr );

		bb.position( originPos );
		return str;

	}


	public static String toHexForLog(byte[] bt)
	{
		StringBuffer sb = new StringBuffer();

		String lSep = System.getProperty( "line.separator" );
		char[] hexChar = Hex.encodeHex( bt );
		for ( int i = 0; i < (hexChar.length / 2); i++ )
		{
			sb.append( hexChar[2 * i] );
			sb.append( hexChar[2 * i + 1] );
			sb.append( ' ' );

			if ((i + 1) % 16 == 0)
			{
				sb.append( lSep );
			}
		}
		return sb.toString().toUpperCase();
	}


	/**
	 * get a string's byte code. If the length is not enough, fill 0.
	 * 
	 * @param str
	 * @param len
	 * @return
	 */
	public static byte[] getByteFillSuffix(String str, int len)
	{
		byte[] arr = new byte[len];
		byte[] bt = str.getBytes();
		for ( int i = 0; i < bt.length; i++ )
		{
			arr[i] = bt[i];
		}
		for ( int j = bt.length; j < arr.length; j++ )
		{
			arr[j] = 0;
		}
		return arr;
	}


	/**
	 * Get Variable Length String's byte, end with 0x00;
	 * 
	 * @param str
	 * @return
	 */
	public static byte[] getCOctetBytes(String str)
	{
		byte[] bSrc = str.getBytes();
		byte[] bts = new byte[bSrc.length + 1];
		System.arraycopy( bSrc, 0, bts, 0, bSrc.length );
		bts[bSrc.length] = 0;
		return bts;
	}


	/**
	 * The ByteBuffer's initial position shou
	 * 
	 * @param bb
	 * @return
	 */
	public static String decOctetString(ByteBuffer bb)
	{

		int originPos = bb.position();
		bb.position( 0 );

		byte[] arr = new byte[bb.limit()];
		bb.get( arr );
		String str = decOctetString( arr );

		bb.position( originPos );
		return str;

	}


	public static String decOctetString(byte[] bt)
	{
		int b = 0;
		int e = 0;

		// find the begin non 0 position;
		for ( int i = 0; i < bt.length; i++ )
		{
			if (bt[i] != 0)
			{
				b = i;
				break;
			}
		}

		// find the end non 0 position;
		for ( int i = bt.length - 1; i > 0; i-- )
		{
			if (bt[i] != 0)
			{
				e = i;
				break;
			}
		}

		return new String( bt, b, e - b + 1 );

	}

}
