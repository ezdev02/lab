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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.apache.mina.filter.codec.demux.MessageEncoder;

import common.Logger;
import rockd.jkj.testcmpp.api.message.AbstractMessage;
import rockd.jkj.testcmpp.api.message.ActiveTestMessage;
import rockd.jkj.testcmpp.api.message.ActiveTestRespMessage;
import rockd.jkj.testcmpp.api.message.ConnectMessage;
import rockd.jkj.testcmpp.api.message.ConnectRespMessage;
import rockd.jkj.testcmpp.api.message.DeliverRespMessage;
import rockd.jkj.testcmpp.api.message.SubmitMessage;
import rockd.jkj.testcmpp.api.message.TerminateMessage;
import rockd.jkj.testcmpp.api.message.TerminateRespMessage;
import rockd.jkj.testcmpp.api.util.ByteUtil;

/**
 * A {@link MessageEncoder}that encodes message header and forwards the
 * encoding of body to a subclass.
 * 
 * @author The Apache Directory Project
 * @version $Rev: 355016 $, $Date: 2006/07/18 06:00:53 $
 */
public class CMPPMessageEncoder implements MessageEncoder
{
	private static Logger logger = Logger.getLogger( CMPPMessageEncoder.class );

	String lSep = System.getProperty( "line.separator" );

	private static final Set TYPES;

	static
	{
		Set types = new HashSet();
		types.add( ConnectMessage.class );
		types.add( ConnectRespMessage.class );
		types.add( ActiveTestMessage.class );
		types.add( ActiveTestRespMessage.class );
		types.add( TerminateMessage.class );
		types.add( TerminateRespMessage.class );
		types.add( SubmitMessage.class );
		types.add( DeliverRespMessage.class );
		TYPES = Collections.unmodifiableSet( types );
	}


	public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception
	{
		AbstractMessage m = (AbstractMessage) message;
		ByteBuffer buf = ByteBuffer.allocate( 300 );
		buf.setAutoExpand( true ); // Enable auto-expand for easier encoding

		// Encode a header
		m.encodeHeader( buf );
		m.encodeBody( buf );
		int totalLength = buf.position();

		buf.putInt( 0, totalLength ); // rewrite the PDU's total length;

		buf.flip();

		// Log the buffer.
		logger.debug( lSep + "---------- Encoded Message--------------- " + lSep + ByteUtil.toHexForLog( buf ) + lSep
				+ "---------- End Encoded Message--------------- " );

		out.write( buf );
	}


	public Set getMessageTypes()
	{
		return TYPES;
	}


	@Override
	public void encode(org.apache.mina.core.session.IoSession session, Object message, ProtocolEncoderOutput out) throws Exception
	{
		// TODO Auto-generated method stub

	}

}
