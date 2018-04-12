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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.mina.common.ByteBuffer;

import common.Logger;
import rockd.jkj.testcmpp.api.sys.CommandID;
import rockd.jkj.testcmpp.api.sys.DefaultConfig;
import rockd.jkj.testcmpp.api.util.ByteUtil;

/**
 * 
 * @author luomingjie (luomingjie@users.sourceforge.net ; luyiluo@126.com)
 * @version $Id: SubmitMessage.java,v 0.2 2007/05/15 13:45:29 
 */
public class SubmitMessage extends AbstractMessage
{
	private static final long serialVersionUID = CommandID.CMPP_SUBMIT;

	private static Logger logger = Logger.getLogger( SubmitMessage.class );

	private static String tpda = DefaultConfig.getTpda();

	private static int protocol_version = DefaultConfig.getProtocolVersion();

	private static String spid = DefaultConfig.getSpid();

	// //////////////////////////////////////////////////////
	private byte[] Msg_id = ByteUtil.intArray( 8 ); // 8 bytes

	private byte PK_total = 1;

	private byte PK_number = 1;

	private byte Registered_Delivery = 0;

	private byte Message_level = 0;

	private String service_id = DefaultConfig.getServiceId();

	// .getServiceId()
	// private byte[] Service_Id = ByteUtil.getByteFillSuffix(DefaultConfig
	// .getServiceId(), 10); // 10

	// bytes

	private byte Fee_UserType = DefaultConfig.getFeeUserType();

	private byte[] Fee_terminal_Id; // 21 bytes for CMPP2.x and 32 bytes for

	// CMPP 3.0

	private byte Fee_terminal_type = 0; // for CMPP 3.0 only

	private byte pid = 0x00; //

	private byte TP_udhi = 0x00;

	private byte Msg_Fmt = (byte) 0x00;

	private byte[] Msg_src = ByteUtil.getByteFillSuffix( spid, 6 ); // 6 bytes.

	private byte[] FeeType = new String( "01" ).getBytes(); // 2 bytes.

	private byte[] FeeCode = ByteUtil.getByteFillSuffix( "0", 6 ); // 6

	private byte[] Valid_Time = ByteUtil.intArray( 17 ); // 17

	private byte[] At_Time = ByteUtil.intArray( 17 ); // 17

	private byte[] Src_Id;//

	private byte DestUsr_tl; // to compute according to Dest_terminal_Id

	private List Dest_terminal_Id = null; // TO_FILL

	private byte Dest_terminal_type = 0; // CMPP 3.0 only

	private byte Msg_Length; // TO_FILL

	private byte[] msgContent = null; // TO_FILL

	private byte[] reserved = ByteUtil.intArray( 8 ); // CMPP 2.x only

	// private byte[] LinkID = null; // CMPP 3.0 only, 20 or 0

	// //////////////////////////////////////////////////////

	private int LEN_Fee_terminal_Id = 21; // 21 for CMPP 2.x and 32 for CMPP

	// 3.0

	private int LEN_Src_Id = 21; // 21 for CMPP 2.x and 32 for CMPP 3.0

	private int LEN_DEST_TERMINAL_ID = 21;


	public SubmitMessage(List Dest_terminal_Id, byte[] msg_Content)
	{
		this.msgContent = msg_Content;
		this.Dest_terminal_Id = Dest_terminal_Id;
		Fee_terminal_Id = ByteUtil.intArray( LEN_Fee_terminal_Id );
		Src_Id = ByteUtil.getByteFillSuffix( tpda, LEN_Src_Id );

		commandId = CommandID.CMPP_SUBMIT;

		if (protocol_version != 0x30 && protocol_version != 0x20)
		{
			logger.error( "Version Not Supported!" );
			System.exit( 1 );
		}

		if (protocol_version == 0x30)
		{
			LEN_Fee_terminal_Id = 32;
			LEN_Src_Id = 32;
			LEN_DEST_TERMINAL_ID = 32;
		}

	}


	/**
	 * SMIAS encoding body.
	 * 
	 * @param bb
	 */
	public void encodeBodySMIAS(ByteBuffer bb1)
	{

		ByteBuffer bb = ByteBuffer.allocate( 300 );

		// SP ID
		bb.put( ByteUtil.getCOctetBytes( spid ) );
		// Service Type
		bb.put( ByteUtil.getCOctetBytes( service_id ) );

		bb.put( (byte) 01 ); // TODO: Fee type
		bb.putInt( 0 ); // TODO: Info fee
		bb.put( pid ); // Protocol ID
		bb.put( (byte) 0 ); // TODO: Message mode
		bb.put( Message_level ); // Priority

		// TODO: Validity period
		SimpleDateFormat dateFormat = new SimpleDateFormat( "yyMMddHHmmss" );
		Date now = new Date();
		String strNow = dateFormat.format( now ) + "012+";
		bb.put( ByteUtil.getCOctetBytes( strNow ) );

		// TODO: Schedule
		bb.put( (byte) 0 );

		bb.put( Fee_UserType );
		bb.put( ByteUtil.getCOctetBytes( DefaultConfig.getFeeUser() ) );

		bb.put( ByteUtil.getCOctetBytes( spid ) );

		DestUsr_tl = (byte) Dest_terminal_Id.size(); // Count of destination
		// addresses
		bb.put( DestUsr_tl );

		// Destination address(es)
		for ( int i = 0; i < Dest_terminal_Id.size(); i++ )
		{
			String dest = (String) Dest_terminal_Id.get( i );
			bb.put( ByteUtil.getCOctetBytes( dest ) );
		}

		bb.put( Msg_Fmt ); // Data coding

		Msg_Length = (byte) msgContent.length;
		bb.put( Msg_Length );
		bb.put( msgContent );

		// copy bb to bb1.
		bb.flip();
		byte[] bts = new byte[bb.limit()];
		logger.info( " ------------ bb.limit(): " + bb.limit() + "  --- " + bts.length );
		bb.get( bts );
		bb1.put( bts );

		// set body Length.
		// super.bodyLength = bts.length;

	}


	@Override
	public void encodeBody(ByteBuffer bb)
	{

		// SMIAS_NOTE: specific.
		if (DefaultConfig.isSMIAS())
		{
			encodeBodySMIAS( bb );
			return;
		}

		bb.put( Msg_id );
		bb.put( PK_total );
		bb.put( PK_number );
		bb.put( Registered_Delivery );
		bb.put( Message_level );
		bb.put( ByteUtil.getByteFillSuffix( service_id, 10 ) );
		bb.put( Fee_UserType );
		bb.put( Fee_terminal_Id );

		if (protocol_version == 0x30)
		{
			bb.put( Fee_terminal_type );
		}

		bb.put( pid );
		bb.put( TP_udhi );
		bb.put( Msg_Fmt );
		bb.put( Msg_src );
		bb.put( FeeType );
		bb.put( FeeCode );
		bb.put( Valid_Time );
		bb.put( At_Time );
		bb.put( Src_Id );

		DestUsr_tl = (byte) Dest_terminal_Id.size();
		bb.put( DestUsr_tl );

		for ( int i = 0; i < Dest_terminal_Id.size(); i++ )
		{
			String dest = (String) Dest_terminal_Id.get( i );
			bb.put( ByteUtil.getByteFillSuffix( dest, LEN_DEST_TERMINAL_ID ) );
		}

		if (protocol_version == 0x30)
		{
			bb.put( Dest_terminal_type );
		}

		Msg_Length = (byte) msgContent.length;
		bb.put( Msg_Length );
		bb.put( msgContent );

		if (protocol_version == 0x20)
		{
			bb.put( reserved );
		}

	}


	@Override
	public void decodeBody(byte[] body)
	{
		logger.error( "NOT Supported!" );
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


	public void setPid(byte pid)
	{
		this.pid = pid;
	}
}
