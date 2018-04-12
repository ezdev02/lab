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

package rockd.jkj.testcmpp.api.message;

import java.io.UnsupportedEncodingException;

import org.apache.mina.common.ByteBuffer;

import common.Logger;
import rockd.jkj.testcmpp.api.sys.CommandID;
import rockd.jkj.testcmpp.api.sys.DefaultConfig;
import rockd.jkj.testcmpp.api.util.ByteUtil;

/**
 * 
 * @author luomingjie (luomingjie@users.sourceforge.net ; luyiluo@126.com)
 * @version $Id: DeliverMessage.java,v 0.2 2007/05/15 13:45:29 
 */
public class DeliverMessage extends AbstractMessage
{
	private static final long serialVersionUID = CommandID.CMPP_DELIVER;

	private static Logger logger = Logger.getLogger( DeliverMessage.class );

	private static byte protocol_version = DefaultConfig.getProtocolVersion();

	private static int LEN_SRC_ID = 21; // 21 for CMPP 2.x and 32 for CMPP 3.0

	private static int LEN_DEST_ID = 21;

	static
	{
		if (protocol_version == 0x30)
		{
			// LEN_Fee_terminal_Id = 32;
			LEN_SRC_ID = 32;
			LEN_DEST_ID = 32;
		}
	}

	// //////////////////////////////////////////////////////
	private byte[] Msg_id = ByteUtil.intArray( 8 ); // 8 bytes

	private byte Registered_Delivery = 0;

	private byte[] Service_Id = ByteUtil.getByteFillSuffix( DefaultConfig.getServiceId(), 10 ); // 10

	// CMPP 3.0

	// private byte Fee_terminal_type = 0; //for CMPP 3.0 only

	private byte pid;

	private byte TP_udhi = 0x01;

	private byte dcs;

	private byte Msg_Mode;

	private String srcAddress = null;

	private String destAddress = null;

	// private byte Dest_terminal_type = 0; //CMPP 3.0 only

	// private byte msgLength; // TO_FILL

	private byte[] msgContent = null; // TO_FILL

	private byte[] reserved = ByteUtil.intArray( 8 ); // CMPP 2.x only

	private String serviceId = "";

	// private byte[] LinkID = null; //CMPP 3.0 only, 20 or 0

	// //////////////////////////////////////////////////////


	// private int LEN_Fee_terminal_Id = 21; //21 for CMPP 2.x and 32 for CMPP
	// 3.0

	public DeliverMessage()
	{
		// this.Msg_Content = msg_Content;
		// this.Dest_terminal_Id = Dest_terminal_Id;

		commandId = CommandID.CMPP_DELIVER;

		if (protocol_version != 0x30 && protocol_version != 0x20)
		{
			logger.error( "Version Not Supported!" );
			System.exit( 1 );
		}

	}


	@Override
	public void encodeBody(ByteBuffer bb)
	{
		logger.info( "CMPPDeliverMessage encodebody Not Supported" );
		System.exit( 1 );
	}


	/**
	 * Get next zero word position. If not found, return -1;
	 * 
	 * @param bt
	 * @param start
	 * @return
	 */
	public int getNextZero(byte[] bt, int start)
	{
		for ( int i = start; i < bt.length; i++ )
		{
			if (bt[i] == 0)
				return i;
		}

		return -1;
	}


	public void decodeBodySMIAS(byte[] body)
	{

		// Source address
		int currPos = 0;
		int end = getNextZero( body, currPos );
		srcAddress = new String( body, currPos, end - currPos );

		// Destination address
		currPos = end + 1;
		end = getNextZero( body, currPos );
		destAddress = new String( body, currPos, end - currPos );

		// Service Type
		currPos = end + 1;
		end = getNextZero( body, currPos );
		serviceId = new String( body, currPos, end - currPos );

		// Protocol ID
		currPos = end + 1;
		pid = body[currPos];

		// Message Mode
		currPos++;
		Msg_Mode = body[currPos];

		// Priority
		currPos++;

		// Data coding
		currPos++;
		dcs = body[currPos];

		// Msg_Length
		currPos++;
		int msgLength = (byte) (body[currPos] & 0xff);

		// Short message.
		currPos++;
		msgContent = new byte[msgLength];
		System.arraycopy( body, currPos, msgContent, 0, msgLength );
	}


	@Override
	public void decodeBody(byte[] body)
	{
		// SMIAS_NOTE:
		if (DefaultConfig.isSMIAS())
		{
			decodeBodySMIAS( body );
			return;
		}

		ByteBuffer bb = ByteBuffer.allocate( body.length );
		bb.put( body );
		bb.flip();

		bb.get( Msg_id );

		// Dest_Id
		byte[] Dest_Id = new byte[LEN_DEST_ID];
		bb.get( Dest_Id );
		destAddress = ByteUtil.decOctetString( Dest_Id );

		bb.get( Service_Id );
		pid = bb.get();
		TP_udhi = bb.get();
		dcs = bb.get();

		// Src_terminal_Id
		byte[] Src_terminal_Id = new byte[LEN_SRC_ID];
		bb.get( Src_terminal_Id );
		srcAddress = ByteUtil.decOctetString( Src_terminal_Id );

		Registered_Delivery = bb.get();
		byte msgLength = bb.get();

		int iMsg_Length = msgLength & 0xff;

		msgContent = new byte[iMsg_Length];
		bb.get( msgContent );

		bb.get( reserved );
	}


	@Override
	public String toString()
	{
		StringBuffer sb = new StringBuffer();

		sb.append( "TOTALLENGTH: " + totalLength );

		sb.append( " COMMAND ID:" + commandId );

		// SMIAS_NOTE:
		if (DefaultConfig.isSMIAS())
			sb.append( " COMMAND Status:" + commandStatus );

		sb.append( " SEQ:" + sequenceId );
		try
		{
			sb.append( " CONTENT:" + new String( msgContent, "GB2312" ) );
		}
		catch (UnsupportedEncodingException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return sb.toString();
	}


	/**
	 * @return Returns the msg_id.
	 */
	public byte[] getMsg_id()
	{
		return Msg_id;
	}


	public byte getPid()
	{
		return pid;
	}


	public byte getTP_udhi()
	{
		return TP_udhi;
	}


	public byte[] getMsg_Content()
	{
		return msgContent;
	}


	/**
	 * Get Source Address.
	 * 
	 * @return
	 */
	public String getSrcAddress()
	{
		return srcAddress;
	}


	public String getDestAddress()
	{
		return destAddress;
	}


	public byte getDcs()
	{
		return dcs;
	}


	public byte getRegistered_Delivery()
	{
		return Registered_Delivery;
	}


	public byte getMsg_Mode()
	{
		return Msg_Mode;
	}


	public String getServiceId()
	{
		return serviceId;
	}
}
