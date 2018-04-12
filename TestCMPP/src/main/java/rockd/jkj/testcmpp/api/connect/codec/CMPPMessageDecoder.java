/**
 *
 * Copyright 2006 Jason Zou.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package rockd.jkj.testcmpp.api.connect.codec;

import org.apache.mina.common.ByteBuffer;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.demux.MessageDecoder;
import org.apache.mina.filter.codec.demux.MessageDecoderResult;

import common.Logger;
import rockd.jkj.testcmpp.api.message.AbstractMessage;
import rockd.jkj.testcmpp.api.message.ActiveTestMessage;
import rockd.jkj.testcmpp.api.message.ActiveTestRespMessage;
import rockd.jkj.testcmpp.api.message.ConnectRespMessage;
import rockd.jkj.testcmpp.api.message.DeliverMessage;
import rockd.jkj.testcmpp.api.message.NackRespMessage;
import rockd.jkj.testcmpp.api.message.SubmitRespMessage;
import rockd.jkj.testcmpp.api.message.TerminateMessage;
import rockd.jkj.testcmpp.api.message.TerminateRespMessage;
import rockd.jkj.testcmpp.api.sys.CommandID;
import rockd.jkj.testcmpp.api.sys.DefaultConfig;
import rockd.jkj.testcmpp.api.sys.MessageInfo;
import rockd.jkj.testcmpp.api.util.ByteUtil;

/**
 */
public class CMPPMessageDecoder implements MessageDecoder
{
	private static Logger logger = Logger.getLogger( CMPPMessageDecoder.class );

	private String lSep = System.getProperty( "line.separator" );


	public MessageDecoderResult decodable(IoSession session, ByteBuffer in)
	{
		// Return NEED_DATA if the whole header is not read yet.
		if (in.remaining() < MessageInfo.CMPP_HEADER_LEN)
		{
			return MessageDecoderResult.NEED_DATA;
		}

		// TODO: This may be a bug.
		int totalLength = in.getInt();
		// logger.debug("totalLength: " + totalLength + "--" + in.remaining());
		if (in.remaining() < totalLength - 4)
			return MessageDecoderResult.NEED_DATA;

		int commandId = in.getInt();
		if (commandId == CommandID.CMPP_NACK_RESP || commandId == CommandID.CMPP_CONNECT_RESP
				|| commandId == CommandID.CMPP_ACTVIE_TEST || commandId == CommandID.CMPP_ACTVIE_TEST_RESP
				|| commandId == CommandID.CMPP_TERMINATE || commandId == CommandID.CMPP_TERMINATE_RESP
				|| commandId == CommandID.CMPP_SUBMIT_RESP || commandId == CommandID.CMPP_DELIVER)
		{
			return MessageDecoderResult.OK;
		}

		// Return NOT_OK if not matches.
		return MessageDecoderResult.NOT_OK;
	}


	public MessageDecoderResult decode(IoSession session, ByteBuffer in, ProtocolDecoderOutput out) throws Exception
	{

		int headerLength = AbstractMessage.getHeaderLength();

		// Try to decode body
		int totalLength = in.getInt();
		int commandId = in.getInt();
		int commandStatus = 0;

		// SMIAS_NOTE:
		if (DefaultConfig.isSMIAS())
		{
			commandStatus = in.getInt();
		}

		int sequenceId = in.getInt();
		logger.debug( "commandId" + commandId );
		byte[] body = null;

		// get body
		if (totalLength > headerLength)
		{
			body = new byte[totalLength - headerLength];
			in.get( body );
		}

		logger.debug( lSep + "-------- Received Message -------" + lSep + "[Total Length:" + totalLength + "] [Command ID(HEX):"
				+ Integer.toHexString( commandId ).toUpperCase() + "] [Sequence Id(HEX):"
				+ Integer.toHexString( sequenceId ).toUpperCase() + "][Command Status(HEX):"
				+ Integer.toHexString( commandStatus ).toUpperCase() + "]" + lSep + ByteUtil.toHexForLog( body ) + lSep
				+ "-------- Received Message -------" );

		AbstractMessage m = null;// decodeBody(session, in);

		if (commandId == CommandID.CMPP_CONNECT_RESP)
		{
			m = new ConnectRespMessage();
		}
		else
			if (commandId == CommandID.CMPP_NACK_RESP)
			{
				m = new NackRespMessage();
			}
			else
				if (commandId == CommandID.CMPP_ACTVIE_TEST)
				{
					m = new ActiveTestMessage();
				}
				else
					if (commandId == CommandID.CMPP_ACTVIE_TEST_RESP)
					{
						m = new ActiveTestRespMessage();
					}
					else
						if (commandId == CommandID.CMPP_TERMINATE)
						{
							m = new TerminateMessage();
						}
						else
							if (commandId == CommandID.CMPP_TERMINATE_RESP)
							{
								m = new TerminateRespMessage();
							}
							else
								if (commandId == CommandID.CMPP_SUBMIT_RESP)
								{
									m = new SubmitRespMessage();
								}
								else
									if (commandId == CommandID.CMPP_DELIVER)
									{
										m = new DeliverMessage();
									}
									else
									{
										logger.debug( "Can't decode the message" );
										System.exit( 1 );
									}

		m.setTotalLength( totalLength );
		m.setSequenceId( sequenceId );
		m.setCommandStatus( commandStatus );

		// m.decodeBody(body);

		out.write( m );

		return MessageDecoderResult.OK;
	}


	@Override
	public void finishDecode(IoSession session, ProtocolDecoderOutput out) throws Exception
	{
		//
	}


	@Override
	public MessageDecoderResult decodable(IoSession arg0, IoBuffer arg1)
	{
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public MessageDecoderResult decode(IoSession arg0, IoBuffer arg1, ProtocolDecoderOutput arg2) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}
}
