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

import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.demux.DemuxingProtocolCodecFactory;

/**
 * A {@link ProtocolCodecFactory}that provides a protocol codec for SumUp
 * protocol.
 * 
 * @author The Apache Directory Project
 * @version $Rev: 355016 $, $Date: 2006/07/18 06:00:53 $
 */
public class CMPPProtocolCodecFactory extends DemuxingProtocolCodecFactory
{

	public CMPPProtocolCodecFactory(boolean server)
	{
		super.register( CMPPMessageDecoder.class );
		super.register( CMPPMessageEncoder.class );
	}
}
